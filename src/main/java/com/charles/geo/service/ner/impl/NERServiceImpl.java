package com.charles.geo.service.ner.impl;

import com.charles.geo.model.Disease;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.ner.INERService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huqj
 * @since 1.0
 */
@Service("nerService")
public class NERServiceImpl implements INERService {

    public void nerFromText(String text, List<GeoPoint> geoPoints,
                            List<Region> regionList, List<Disease> diseaseList) {

    }

}
