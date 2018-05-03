package com.charles.geo.mapper;

import com.charles.geo.model.Disease;

import java.util.List;

/**
 * @author huqj
 * @since 1.0
 */
public interface DiseaseMapper {

    /**
     * 通过疾病名称查找 疾病-科室 的关系列表
     *
     * @param name
     * @return
     */
    List<Disease> findByName(String name);

    /**
     * 通过科室名称查询 疾病-科室 列表
     *
     * @param depart
     * @return
     */
    List<Disease> findByDepart(String depart);
}
