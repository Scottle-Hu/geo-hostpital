package com.charles.geo.mapper;

import com.charles.geo.model.PointPlace;
import com.charles.geo.model.Region;

import java.util.List;

/**
 * 查询 t_place 表的接口
 *
 * @author huqj
 * @since 1.0
 */
public interface PlaceMapper {

    Region findRegionById(String id);

    //根据father_id查询子级别的行政区
    List<Region> findRegionByFather(String fatherId);

    PointPlace findPointPlaceById(String id);

}
