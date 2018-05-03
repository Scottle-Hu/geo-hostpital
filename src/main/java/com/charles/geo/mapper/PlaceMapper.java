package com.charles.geo.mapper;

import com.charles.geo.model.PointPlace;
import com.charles.geo.model.Region;

import java.util.List;
import java.util.Map;

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

    //查询所有行政区划
    List<Region> findAllRegion();

    int insertAlias(Map<String, String> map);

    //通过别名查询标准地点的id，结果可能有多个
    List<String> findPlaceIdByAlias(String alias);

    PointPlace findPointPlaceById(String id);

}
