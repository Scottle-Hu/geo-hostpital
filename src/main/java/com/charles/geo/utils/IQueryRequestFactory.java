package com.charles.geo.utils;

import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.QueryRequest;

/**
 * @author huqj
 * @since 1.0
 */
public interface IQueryRequestFactory {

    QueryRequest createQuery(String text, String ip, GeoPoint curPoint);
}
