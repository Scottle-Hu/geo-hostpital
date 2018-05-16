package com.charles.geo.service.process.impl;

import com.charles.geo.mapper.HospitalMapper;
import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.mapper.UniversityMapper;
import com.charles.geo.model.*;
import com.charles.geo.service.process.IMainQueryService;
import com.charles.geo.service.rank.IPaperCatchService;
import com.charles.geo.service.rank.IRankService;
import com.charles.geo.utils.Constant;
import com.charles.geo.utils.DBUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 推荐主方法，根据封装的请求信息计算返回推荐信息的封装
 *
 * @author huqj
 * @since 1.0
 */
@Service("mainQueryService")
public class MainQueryServiceImpl implements IMainQueryService {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(MainQueryServiceImpl.class);

    /**
     * 医疗机构最大推荐数目
     */
    private final int HOS_MAX_RECOMMAND_NUM = 5;

    /**
     * 医疗机构最小推荐数目
     */
    private final int HOS_MIN_RECOMMAND_NUM = 1;

    /**
     * 学校最大推荐数目
     */
    private final int COLLE_MAX_RECOMMAND_NUM = 3;

    /**
     * 学校最小推荐数目
     */
    private final int COLLE_MIN_RECOMMAND_NUM = 1;

    /**
     * 按照经纬度查询的时候取附近的经纬度范围
     */
    private double geoRank = 0.036; // 2KM

    @Autowired
    private IRankService rankService;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private IPaperCatchService paperCatchService;

    @Autowired
    private DBUtil dbUtil;

    private Map<String, List<String>> hospitalPlace2Id = new HashMap<String, List<String>>();

    @PostConstruct
    public void init() {
        System.out.println("==========读取医院信息到内存==========");
        List<String> placeList = hospitalMapper.findAllPlace();
        int index = 1;
        for (String p : placeList) {
            System.out.println(index++);
            List<String> ids = hospitalMapper.findIdsByPlace(p);
            hospitalPlace2Id.put(p, ids);
        }
        System.out.println("==========读取医院信息到内存==========");
    }

    /**
     * @param request
     * @return
     */
    public InformationResponse handler(QueryRequest request) {
        InformationResponse response = new InformationResponse();
        // 获取推荐的医疗机构
        List<Hospital> hospitals = getHospitals(request);
        // 填充专家
        for (Hospital hospital : hospitals) {
            hospital.setExpertList(queryExpertByHospital(hospital.getId()));
        }
        // 获取推荐的大学及研究信息
        List<Colleage> colleages = getColleages(request);
        // 封装信息
        response.setColleageList(colleages);
        response.setHospitalList(hospitals);
        return response;
    }

    /**
     * 根据请求返回推荐医院（包括专家）的相关信息，是有序结果
     *
     * @param request
     * @return
     */
    private List<Hospital> getHospitals(QueryRequest request) {
        System.out.println("开始题名医院:" + System.currentTimeMillis());
        List<Hospital> hospitalList = new ArrayList<Hospital>();
        Set<Hospital> hospitalSet = new HashSet<Hospital>();
        List<Region> regionList = request.getRegionList(); // 行政区划集合
        List<GeoPoint> pointList = request.getPointList(); // 经纬度点集合
        // 获取每个行政区下面的医疗机构，取并集
        for (Region region : regionList) {
            String q = null;
            // 构造查询条件
            if (region.getType().equals(Constant.COUNTY)) { // 区县级别
                Region father = placeMapper.findRegionById(region.getFatherId());
                q = father.getName() + " " + region.getName();
                List<String> ids = hospitalPlace2Id.get(q);
                hospitalSet.addAll(queryHospitalByIds(ids));
            } else if (region.getType().equals(Constant.CITY)) { // 市级别
                // 效率考量，用多次精确查询替代模糊查询
                List<Region> sons = placeMapper.findRegionByFather(region.getId());
                for (Region s : sons) {
                    q = region.getName() + " " + s.getName();
                    List<String> ids = hospitalPlace2Id.get(q);
                    hospitalSet.addAll(queryHospitalByIds(ids));
                }
            } else { // 省级别,找到所有的市然后查询
                List<Region> sons = placeMapper.findRegionByFather(region.getId());
                for (Region s : sons) {
                    List<Region> gsons = placeMapper.findRegionByFather(s.getId());
                    for (Region g : gsons) {
                        q = s.getName() + " " + g.getName();
                        List<String> ids = hospitalPlace2Id.get(q);
                        hospitalSet.addAll(queryHospitalByIds(ids));
                    }
                }
            }
        }
        System.out.println("结束行政区题名医院:" + System.currentTimeMillis());
        // 获取每个经纬度节点附近的医疗机构，取并集
        for (GeoPoint point : pointList) {
            double longitude = point.getLongitude();
            double latitude = point.getLatitude();
            // 计算选择医院的经纬度范围
            Map<String, Double> map = new HashMap<String, Double>();
            map.put("longitudeMin", longitude - geoRank);
            map.put("longitudeMax", longitude + geoRank);
            map.put("latitudeMin", latitude - geoRank);
            map.put("latitudeMax", latitude + geoRank);
            hospitalSet.addAll(queryHospitalByRange(map));
        }
        int level = 1;
        System.out.println("结束经纬度题名医院:" + System.currentTimeMillis());
        // 如果医院候选列表过少，将行政区划向上扩张
        while (hospitalSet.size() < HOS_MIN_RECOMMAND_NUM) {
            if (level > 2) {
                LOGGER.error("所提名的推荐医院候选列表元素个数太少,可能是数据原因。");
                break;
            }
            switch (level) {
                case 1: { // 扩充区县级别到市
                    for (Region region : regionList) {
                        if (region.getType().equals(Constant.COUNTY)) {
                            Region f = placeMapper.findRegionById(region.getFatherId());
                            List<Region> sons = placeMapper.findRegionByFather(f.getId());
                            for (Region s : sons) {
                                String q = f.getName() + " " + s.getName();
                                List<String> ids = hospitalPlace2Id.get(q);
                                hospitalSet.addAll(queryHospitalByIds(ids));
                            }
                        }
                    }
                    break;
                }
                case 2: { // 扩充市级别到省【或许没有必要】
                    for (Region region : regionList) {
                        if (region.getType().equals(Constant.CITY)) {
                            Region f = placeMapper.findRegionById(region.getFatherId());
                            List<Region> sons = placeMapper.findRegionByFather(f.getId());
                            for (Region s : sons) {
                                List<Region> gsons = placeMapper.findRegionByFather(s.getId());
                                for (Region gs : gsons) {
                                    String q = s.getName() + " " + gs.getName();
                                    List<String> ids = hospitalPlace2Id.get(q);
                                    hospitalSet.addAll(queryHospitalByIds(ids));
                                }
                            }
                        }
                    }
                    break;
                }
            }
            level++;
        }
        System.out.println("结束扩充医院:" + System.currentTimeMillis());
        hospitalList.addAll(hospitalSet);
        // 不知怎么的，set没法去重，手动再次去重
        Set<String> names = new HashSet<String>();
        Iterator<Hospital> iterator = hospitalList.iterator();
        while (iterator.hasNext()) {
            Hospital h = iterator.next();
            String n = h.getName();
            if (names.contains(n)) {
                iterator.remove();
            } else {
                names.add(n);
            }
        }
        // 根据疾病名称排序筛选
        hospitalList = rankService.rankHospital(hospitalList, request);
        if (hospitalList.size() > HOS_MAX_RECOMMAND_NUM) { // 多于最多推荐个数，筛选前N个
            hospitalList = hospitalList.subList(0, HOS_MAX_RECOMMAND_NUM);
        }
        System.out.println("结束筛选医院:" + System.currentTimeMillis());
        return hospitalList;
    }

    /**
     * 根据请求推荐的大学列表，包括论文信息
     *
     * @param request
     * @return
     */
    private List<Colleage> getColleages(QueryRequest request) {
        System.out.println("开始题名大学:" + System.currentTimeMillis());
        List<Colleage> colleageList = new ArrayList<Colleage>();
        Set<Colleage> colleageSet = new HashSet<Colleage>();
        List<Region> regionList = request.getRegionList(); // 行政区划集合
        List<GeoPoint> pointList = request.getPointList(); // 经纬度点集合
        List<Disease> diseaseList = request.getDiseaseList();
        // 获取行政区划下面的所有大学
        // for (Region region : regionList) {
        // if (region.getType().equals(Constant.COUNTY)) {
        // colleageSet.addAll(universityMapper.queryByRegion(region.getId()));
        // } else if (region.getType().equals(Constant.CITY)) {
        // List<Region> sons = placeMapper.findRegionByFather(region.getId());
        // for (Region r : sons) {
        // colleageSet.addAll(universityMapper.queryByRegion(r.getId()));
        // }
        // } else {
        // List<Region> sons = placeMapper.findRegionByFather(region.getId());
        // for (Region r : sons) {
        // List<Region> sonList = placeMapper.findRegionByFather(r.getId());
        // for (Region s : sonList) {
        // colleageSet.addAll(universityMapper.queryByRegion(s.getId()));
        // }
        // }
        // }
        // }
        // 目前大学父级是市级别的，提名方法如下：
        for (Region region : regionList) {
            if (region.getType().equals(Constant.COUNTY)) {
                colleageSet.addAll(universityMapper.queryByRegion(region.getFatherId()));
            } else if (region.getType().equals(Constant.CITY)) {
                colleageSet.addAll(universityMapper.queryByRegion(region.getId()));
            } else {
                List<Region> sons = placeMapper.findRegionByFather(region.getId());
                for (Region r : sons) {
                    colleageSet.addAll(universityMapper.queryByRegion(r.getId()));
                }
            }
        }
        // 获取每个经纬度节点附近的大学，取并集
        for (GeoPoint point : pointList) {
            double longitude = point.getLongitude();
            double latitude = point.getLatitude();
            // 确定选择大学的经纬度范围
            Map<String, Double> map = new HashMap<String, Double>();
            map.put("longitudeMin", longitude - geoRank);
            map.put("longitudeMax", longitude + geoRank);
            map.put("latitudeMin", latitude - geoRank);
            map.put("latitudeMax", latitude + geoRank);
            colleageSet.addAll(universityMapper.queryByRange(map));
        }
        System.out.println("结束题名大学:" + System.currentTimeMillis());
        // 初步筛选
        System.out.println("提名大学数量：" + colleageSet.size());
        // 填充大学的论文集用于排序
        Set<String> ds = new HashSet<String>();
        for (Disease d : diseaseList) {
            ds.add(d.getName());
        }
        for (Colleage colleage : colleageSet) {
            Map<String, List<Paper>> paperMap = new HashMap<String, List<Paper>>();
            for (String d : ds) {
                List<Paper> paper = paperCatchService.getPaper(colleage.getName(), d);
                paperMap.put(d, paper);
            }
            colleage.setPaperList(paperMap);
        }
        System.out.println("结束抓取论文:" + System.currentTimeMillis());
        colleageList.addAll(colleageSet);
        // 不知怎么的，set没法去重，手动再次去重
        Set<String> names = new HashSet<String>();
        Iterator<Colleage> iterator = colleageList.iterator();
        while (iterator.hasNext()) {
            Colleage c = iterator.next();
            String n = c.getName();
            if (names.contains(n)) {
                iterator.remove();
            } else {
                names.add(n);
            }
        }
        // 排序筛选
        colleageList = rankService.rankColleage(colleageList, request);
        if (colleageList.size() > COLLE_MAX_RECOMMAND_NUM) {
            colleageList = colleageList.subList(0, COLLE_MAX_RECOMMAND_NUM);
        }
        System.out.println("结束筛选大学:" + System.currentTimeMillis());
        return colleageList;
    }

    private List<Hospital> queryHospitalByIds(List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return Collections.emptyList();
        }
        Connection con = dbUtil.getConnection();
        try {
            Statement sta = con.createStatement();
            Statement sta2 = con.createStatement();
            String sql = "select * from t_hospital where id in (";
            for (String id : ids) {
                sql += id + ",";
            }
            sql = sql.substring(0, sql.length() - 1) + ")";
            // System.out.println(sql);
            ResultSet resultSet = sta.executeQuery(sql);
            List<Hospital> hospitals = new ArrayList<Hospital>();
            while (resultSet.next()) {
                Hospital hospital = new Hospital();
                hospital.setId(resultSet.getString("id"));
                hospital.setAddress(resultSet.getString("address"));
                hospital.setAlias(resultSet.getString("alias"));
                hospital.setIntroduction(resultSet.getString("introduction"));
                hospital.setLatitude(resultSet.getDouble("latitude"));
                hospital.setLongitude(resultSet.getDouble("longitude"));
                hospital.setLevel(resultSet.getString("level"));
                hospital.setName(resultSet.getString("name"));
                hospital.setPlace(resultSet.getString("place"));
                hospital.setQuality(resultSet.getString("quality"));
                hospital.setUrl(resultSet.getString("url"));
                // hospital.setExpertList(queryExpertByHospital(hospital.getId()));
                hospitals.add(hospital);
            }
            resultSet.close();
            sta.close();
            dbUtil.releaseConnection(con);  //归还连接给连接池
            return hospitals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<Hospital> queryHospitalByRange(Map<String, Double> map) {
        if (map == null || map.size() == 0) {
            return Collections.emptyList();
        }
        Connection con = dbUtil.getConnection();
        try {
            Statement sta = con.createStatement();
            Statement sta2 = con.createStatement();
            String sql = "select * from t_hospital where longitude<" + map.get("longitudeMax") + " and longitude>"
                    + map.get("longitudeMin") + " and latitude<" + map.get("latitudeMax") + " and latitude>"
                    + map.get("latitudeMin");
            // System.out.println(sql);
            ResultSet resultSet = sta.executeQuery(sql);
            List<Hospital> hospitals = new ArrayList<Hospital>();
            while (resultSet.next()) {
                Hospital hospital = new Hospital();
                hospital.setId(resultSet.getString("id"));
                hospital.setAddress(resultSet.getString("address"));
                hospital.setAlias(resultSet.getString("alias"));
                hospital.setIntroduction(resultSet.getString("introduction"));
                hospital.setLatitude(resultSet.getDouble("latitude"));
                hospital.setLongitude(resultSet.getDouble("longitude"));
                hospital.setLevel(resultSet.getString("level"));
                hospital.setName(resultSet.getString("name"));
                hospital.setPlace(resultSet.getString("place"));
                hospital.setQuality(resultSet.getString("quality"));
                hospital.setUrl(resultSet.getString("url"));
                // hospital.setExpertList(queryExpertByHospital(hospital.getId()));
                hospitals.add(hospital);
            }
            resultSet.close();
            sta.close();
            dbUtil.releaseConnection(con);  //归还连接给连接池
            return hospitals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private List<Expert> queryExpertByHospital(String id) {
        if (id == null) {
            return Collections.emptyList();
        }
        try {
            Connection con = dbUtil.getConnection();
            Statement sta2 = con.createStatement();
            String sql = "select * from t_expert where hospital_id=" + id;
            ResultSet resultSet2 = sta2.executeQuery(sql);
            List<Expert> experts = new ArrayList<Expert>();
            while (resultSet2.next()) {
                Expert expert = new Expert();
                expert.setId(resultSet2.getString("id"));
                expert.setDepartment(resultSet2.getString("department"));
                expert.setLevel(resultSet2.getString("level"));
                expert.setMajor(resultSet2.getString("major"));
                expert.setName(resultSet2.getString("name"));
                expert.setPhoto(resultSet2.getString("photo"));
                expert.setUrl(resultSet2.getString("url"));
                experts.add(expert);
            }
            resultSet2.close();
            sta2.close();
            dbUtil.releaseConnection(con);  //归还连接给连接池
            return experts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
