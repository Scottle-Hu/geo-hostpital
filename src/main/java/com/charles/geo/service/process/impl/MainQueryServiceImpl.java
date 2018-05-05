package com.charles.geo.service.process.impl;

import com.charles.geo.mapper.HospitalMapper;
import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.mapper.UniversityMapper;
import com.charles.geo.model.*;
import com.charles.geo.service.process.IMainQueryService;
import com.charles.geo.service.rank.IPaperCatchService;
import com.charles.geo.service.rank.IRankService;
import com.charles.geo.utils.Constant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final int HOS_MAX_RECOMMAND_NUM = 8;

    /**
     * 医疗机构最小推荐数目
     */
    private final int HOS_MIN_RECOMMAND_NUM = 1;

    /**
     * 学校最大推荐数目
     */
    private final int COLLE_MAX_RECOMMAND_NUM = 4;

    /**
     * 学校最小推荐数目
     */
    private final int COLLE_MIN_RECOMMAND_NUM = 1;

    /**
     * 按照经纬度查询的时候取附近的经纬度范围
     */
    private double geoRank = 0.036;  //4KM

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

    /**
     * @param request
     * @return
     */
    public InformationResponse handler(QueryRequest request) {
        InformationResponse response = new InformationResponse();
        //获取推荐的医疗机构
        List<Hospital> hospitals = getHospitals(request);
        //获取推荐的大学及研究信息
        List<Colleage> colleages = getColleages(request);
        //封装信息
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
        List<Region> regionList = request.getRegionList();  //行政区划集合
        List<GeoPoint> pointList = request.getPointList();  //经纬度点集合
        //获取每个行政区下面的医疗机构，取并集
        for (Region region : regionList) {
            String q = null;
            //构造查询条件
            if (region.getType().equals(Constant.COUNTY)) {  //区县级别
                Region father = placeMapper.findRegionById(region.getFatherId());
                q = father.getName() + " " + region.getName();
                hospitalSet.addAll(hospitalMapper.queryByRegion(q));
            } else if (region.getType().equals(Constant.CITY)) {  //市级别
                q = region.getName() + "%"; //模糊匹配
                hospitalSet.addAll(hospitalMapper.queryByRegion(q));
            } else { //省级别,找到所有的市然后查询
                List<Region> sons = placeMapper.findRegionByFather(region.getId());
                for (Region s : sons) {
                    q = s.getName() + "%";
                    hospitalSet.addAll(hospitalMapper.queryByRegion(q));
                }
            }
        }
        System.out.println("结束行政区题名医院:" + System.currentTimeMillis());
        //获取每个经纬度节点附近的医疗机构，取并集
        for (GeoPoint point : pointList) {
            double longitude = point.getLongitude();
            double latitude = point.getLatitude();
            //计算选择医院的经纬度范围
            Map<String, Double> map = new HashMap<String, Double>();
            map.put("longitudeMin", longitude - geoRank);
            map.put("longitudeMax", longitude + geoRank);
            map.put("latitudeMin", latitude - geoRank);
            map.put("latitudeMax", latitude + geoRank);
            hospitalSet.addAll(hospitalMapper.queryByRange(map));
        }
        int level = 1;
        System.out.println("结束经纬度题名医院:" + System.currentTimeMillis());
        //如果医院候选列表过少，将行政区划向上扩张
        while (hospitalSet.size() < HOS_MIN_RECOMMAND_NUM) {
            if (level > 2) {
                LOGGER.error("所提名的推荐医院候选列表元素个数太少,可能是数据原因。");
                break;
            }
            switch (level) {
                case 1: {  //扩充区县级别到市
                    for (Region region : regionList) {
                        if (region.getType().equals(Constant.COUNTY)) {
                            Region f = placeMapper.findRegionById(region.getFatherId());
                            String q = f.getName() + "%";
                            hospitalSet.addAll(hospitalMapper.queryByRegion(q));
                        }
                    }
                    break;
                }
                case 2: {  //扩充市级别到省【或许没有必要】
                    for (Region region : regionList) {
                        if (region.getType().equals(Constant.CITY)) {
                            Region f = placeMapper.findRegionById(region.getFatherId());
                            List<Region> sons = placeMapper.findRegionByFather(f.getId());
                            for (Region s : sons) {
                                String q = s.getName() + "%";
                                hospitalSet.addAll(hospitalMapper.queryByRegion(q));
                            }
                        }
                    }
                    break;
                }
            }
            level++;
        }
        System.out.println("结束扩充医院:" + System.currentTimeMillis());
        //根据疾病名称排序筛选
        hospitalList.addAll(hospitalSet);
        hospitalList = rankService.rankHospital(hospitalList, request);
        if (hospitalList.size() > HOS_MAX_RECOMMAND_NUM) {  //多于最多推荐个数，筛选前N个
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
        List<Region> regionList = request.getRegionList();  //行政区划集合
        List<GeoPoint> pointList = request.getPointList();  //经纬度点集合
        List<Disease> diseaseList = request.getDiseaseList();
        //获取行政区划下面的所有大学
//        for (Region region : regionList) {
//            if (region.getType().equals(Constant.COUNTY)) {
//                colleageSet.addAll(universityMapper.queryByRegion(region.getId()));
//            } else if (region.getType().equals(Constant.CITY)) {
//                List<Region> sons = placeMapper.findRegionByFather(region.getId());
//                for (Region r : sons) {
//                    colleageSet.addAll(universityMapper.queryByRegion(r.getId()));
//                }
//            } else {
//                List<Region> sons = placeMapper.findRegionByFather(region.getId());
//                for (Region r : sons) {
//                    List<Region> sonList = placeMapper.findRegionByFather(r.getId());
//                    for (Region s : sonList) {
//                        colleageSet.addAll(universityMapper.queryByRegion(s.getId()));
//                    }
//                }
//            }
//        }
        //目前大学父级是市级别的，提名方法如下：
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
        //获取每个经纬度节点附近的大学，取并集
        for (GeoPoint point : pointList) {
            double longitude = point.getLongitude();
            double latitude = point.getLatitude();
            //确定选择大学的经纬度范围
            Map<String, Double> map = new HashMap<String, Double>();
            map.put("longitudeMin", longitude - geoRank);
            map.put("longitudeMax", longitude + geoRank);
            map.put("latitudeMin", latitude - geoRank);
            map.put("latitudeMax", latitude + geoRank);
            colleageSet.addAll(universityMapper.queryByRange(map));
        }
        System.out.println("结束题名大学:" + System.currentTimeMillis());
        //初步筛选

        //填充大学的论文集用于排序
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
        //排序筛选
        colleageList.addAll(colleageSet);
        colleageList = rankService.rankColleage(colleageList, request);
        if (colleageList.size() > COLLE_MAX_RECOMMAND_NUM) {
            colleageList = colleageList.subList(0, COLLE_MAX_RECOMMAND_NUM);
        }
        System.out.println("结束筛选大学:" + System.currentTimeMillis());
        return colleageList;
    }
}
