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

    /**
     * 根据专家名称连接查询，获取医院集合
     *
     * @param expert
     * @return
     */
    List<Hospital> queryByExpert(String expert);

    /**
     * 根据医院名称查询
     *
     * @param name
     * @return
     */
    List<Hospital> queryByName(String name);

    /**
     * 根据医院别名查询
     *
     * @param name
     * @return
     */
    List<Hospital> queryByAlias(String name);

}
