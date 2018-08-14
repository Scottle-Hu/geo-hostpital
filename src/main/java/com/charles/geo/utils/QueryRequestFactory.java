package com.charles.geo.utils;

import com.charles.geo.model.*;
import com.charles.geo.service.ip.IIP2RegionService;
import com.charles.geo.service.ner.INERService;
import com.charles.geo.service.process.pre.IPlaceDataCleanService;
import com.charles.geo.service.std.IStdService;
import com.charles.geo.service.std.impl.StdServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装查询请求对象的工厂类
 *
 * @author huqj
 * @since 1.0
 */
@Service("queryRequestFactory")
public class QueryRequestFactory implements IQueryRequestFactory {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(QueryRequestFactory.class);

    @Autowired
    private INERService nerService;

    @Autowired
    private IPlaceDataCleanService placeDataCleanService;

    @Autowired
    private IIP2RegionService iip2regionService;

    /**
     * 从文本创建封装的请求
     *
     * @param text 文本
     * @return
     */
    public QueryRequest createQuery(String text, String ip, GeoPoint curPoint, String uid) {
        String regionFromIP = iip2regionService.getCityFromIP(ip);
        System.out.println("根据ip获取的地点：" + regionFromIP);
        if (!StringUtils.isEmpty(regionFromIP))
            text = text + "," + regionFromIP;
        QueryRequest request = new QueryRequest();
        List<Region> regionList = new ArrayList<Region>();
        List<Disease> diseaseList = new ArrayList<Disease>();
        List<GeoPoint> pointList = new ArrayList<GeoPoint>();
        //做命名实体识别和标准化，将识别出的实体添加到上面创建的集合中
        nerService.nerFromText(text, pointList, regionList, diseaseList);
        //做地点数据预处理
        placeDataCleanService.clean(pointList, regionList);
        System.out.println("对地点数据预处理之后：");
        System.out.println("行政区划：");
        for (Region r : regionList) {
            System.out.println(r.getName());
        }
        System.out.println("经纬度点：");
        for (GeoPoint r : pointList) {
            System.out.println("(" + r.getLongitude() + "," + r.getLatitude() + ")");
        }
        //封装推荐请求信息
        request.setDiseaseList(diseaseList);
        if (curPoint != null)
            pointList.add(curPoint);
        request.setPointList(pointList);
        request.setRegionList(regionList);
        request.setUid(uid);
        return request;
    }

}
