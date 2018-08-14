package com.charles.geo.mapper;

import com.charles.geo.model.Colleage;
import com.charles.geo.model.Hospital;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 * @since 1.0
 */
public interface UniversityMapper {

    /**
     * 根据经纬度范围查询大学
     *
     * @param map
     * @return
     */
    List<Colleage> queryByRange(Map<String, Double> map);

    /**
     * 根据行政区查询大学
     *
     * @param region
     * @return
     */
    List<Colleage> queryByRegion(String region);

    /**
     * 根据全称查询
     */
    Colleage queryByName(String name);

    /**
     * 根据简称查询
     */
    List<Colleage> queryByAliases(String alias);

    /**
     * 获取所有大学名称，用于抓取论文
     *
     * @return
     */
    List<Colleage> getAllName();
}
