package com.charles.geo.service.std.impl;

import com.charles.geo.model.Disease;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Hospital;
import com.charles.geo.model.Region;
import com.charles.geo.service.rank.impl.PaperCatchServiceImpl;
import com.charles.geo.service.std.IStdService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huqj
 * @sin 1.0
 */
@Service("stdService")
public class StdServiceImpl implements IStdService {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(StdServiceImpl.class);

    public GeoPoint convertPOI2Point(String place) {
        return null;
    }

    public List<Disease> convertMedicine2Disease(String medicine) {
        return null;
    }

    public void convert2StdName(List<String> regionNames, List<String> pointNames, List<Region> regionList, List<GeoPoint> pointList) {

    }

    public List<Disease> stdDisease(List<String> diseaseNames) {
        return null;
    }

    public List<Hospital> convertPeople2Hospital(String name) {
        return null;
    }
}
