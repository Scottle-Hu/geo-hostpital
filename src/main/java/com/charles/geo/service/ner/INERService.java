package com.charles.geo.service.ner;

import com.charles.geo.model.Disease;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.util.List;

/**
 * 命名实体识别接口
 *
 * @author Charles
 * @since 1.0
 */
public interface INERService {

    /**
     * 命名实体识别方法
     *
     * @param geoPoints
     * @param regionList
     */
    void nerFromText(String text, List<GeoPoint> geoPoints, List<Region> regionList, List<Disease> diseaseList);

}