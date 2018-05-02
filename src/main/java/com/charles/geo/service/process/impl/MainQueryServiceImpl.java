package com.charles.geo.service.process.impl;

import com.charles.geo.mapper.HospitalMapper;
import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.model.*;
import com.charles.geo.service.process.IMainQueryService;
import com.charles.geo.service.rank.IRankService;
import com.charles.geo.service.rank.impl.PaperCatchServiceImpl;
import com.charles.geo.utils.Constant;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
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
    private final int HOS_MAX_RECOMMAND_NUM = 10;

    /**
     * 学校最大推荐数目
     */
    private final int COLLE_MAX_RECOMMAND_NUM = 10;

    /**
     * 按照经纬度查询的时候取附近的经纬度范围
     */
    private double geoRank = 0.036;

    @Autowired
    private IRankService rankService;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private PlaceMapper placeMapper;

    /**
     * @param request
     * @return
     */
    public InfomationResponse handler(QueryRequest request) {
        InfomationResponse response = new InfomationResponse();
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
                q = "%" + father.getName() + " " + region.getName() + "%";
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
        //获取每个经纬度节点附近的医疗机构，取并集
        for (GeoPoint point : pointList) {
            double longitude = point.getLongitude();
            double latitude = point.getLatitude();
            //计算选择医院的经纬度范围
            double longitudeMin = longitude - geoRank;
            double longitudeMax = longitude + geoRank;
            double latitudeMin = latitude - geoRank;
            double latitudeMax = latitude + geoRank;
            Map<String, Double> map = new HashMap<String, Double>();
            map.put("longitudeMin", longitudeMin);
            map.put("longitudeMax", longitudeMax);
            map.put("latitudeMin", latitudeMin);
            map.put("latitudeMax", latitudeMax);
            hospitalSet.addAll(hospitalMapper.queryByRange(map));
        }
        //根据疾病名称排序筛选
        hospitalList = rankService.rankHospital(hospitalList, request);
        return hospitalList;
    }

    /**
     * 根据请求推荐的大学列表，包括论文信息
     *
     * @param request
     * @return
     */
    private List<Colleage> getColleages(QueryRequest request) {
        List<Colleage> colleageList = new ArrayList<Colleage>();

        return colleageList;
    }
}
