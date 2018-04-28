package com.charles.geo.service.process.pre;

import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;

import java.util.List;

/**
 * 对地点数据进行清洗、排除异常数据
 *
 * @author huqj
 * @since 1.0
 */
public interface IPlaceDataCleanService {

    /**
     * @param geoPointList 经纬度列表
     * @param regionList   行政区划列表
     */
    void clean(List<GeoPoint> geoPointList, List<Region> regionList);
    
}
