package com.charles.geo.mapper;

import com.charles.geo.model.Expert;

import java.util.List;

/**
 * @author huqj
 * @since 1.0
 */
public interface ExpertMapper {

    List<Expert> findByHospitalId(String id);
}
