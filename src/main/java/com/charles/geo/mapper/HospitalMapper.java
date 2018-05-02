package com.charles.geo.mapper;

import com.charles.geo.model.Hospital;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 * @since 1.0
 */
public interface HospitalMapper {

    /**
     * 根据经纬度范围查询医院
     *
     * @param map
     * @return
     */
    List<Hospital> queryByRange(Map<String, Double> map);

    /**
     * 根据行政区查询医院
     *
     * @param region
     * @return
     */
    List<Hospital> queryByRegion(String region);

}
