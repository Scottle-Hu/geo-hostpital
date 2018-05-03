package com.charles.geo.utils;

import com.charles.geo.model.*;
import com.charles.geo.service.ner.INERService;
import com.charles.geo.service.process.pre.IPlaceDataCleanService;
import com.charles.geo.service.std.IStdService;
import com.charles.geo.service.std.impl.StdServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装查询请求对象的工厂类
 *
 * @author huqj
 * @since 1.0
 */
@Service("queryRequestFactory")
public class QueryRequestFactory {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(QueryRequestFactory.class);

    @Autowired
    private INERService nerService;

    @Autowired
    private IPlaceDataCleanService placeDataCleanService;

    /**
     * 从文本创建封装的请求
     *
     * @param text 文本
     * @return
     */
    public QueryRequest createQuery(String text, String ip, GeoPoint curPoint) {
        QueryRequest request = new QueryRequest();
        List<Region> regionList = new ArrayList<Region>();
        List<Disease> diseaseList = new ArrayList<Disease>();
        List<GeoPoint> pointList = new ArrayList<GeoPoint>();
        //做命名实体识别和标准化，将识别出的实体添加到上面创建的集合中
        nerService.nerFromText(text, pointList, regionList, diseaseList);
        //做地点数据预处理
        placeDataCleanService.clean(pointList, regionList);
        //封装推荐请求信息
        request.setDiseaseList(diseaseList);
        request.setPointList(pointList);
        request.setRegionList(regionList);
        request.setCurrentIP(ip);
        request.setCurrentPoint(curPoint);
        return request;
    }

}
