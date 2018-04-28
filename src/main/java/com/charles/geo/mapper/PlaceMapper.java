package com.charles.geo.mapper;

import com.charles.geo.model.PointPlace;
import com.charles.geo.model.Region;

/**
 * 查询 t_place 表的接口
 *
 * @author huqj
 * @since 1.0
 */
public interface PlaceMapper {

    Region findRegionById(String id);

    PointPlace findPointPlaceById(String id);


}
