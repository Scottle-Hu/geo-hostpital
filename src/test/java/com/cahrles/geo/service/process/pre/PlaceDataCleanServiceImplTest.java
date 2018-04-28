package com.cahrles.geo.service.process.pre;

import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.process.pre.IPlaceDataCleanService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class PlaceDataCleanServiceImplTest {

    @Autowired
    private IPlaceDataCleanService placeDataCleanService;

    @Autowired
    private PlaceMapper placeMapper;

    @Test
    public void test01() {
        List<GeoPoint> geoPointList = new ArrayList<GeoPoint>();
        geoPointList.add(new GeoPoint(117.3421, 31.2343));
        List<Region> regionList = new ArrayList<Region>();
        Region r1 = placeMapper.findRegionById("1000");
        regionList.add(r1);
        Region r2 = placeMapper.findRegionById("1141");
        regionList.add(r2);
        Region r3 = placeMapper.findRegionById("125");
        regionList.add(r3);
        Region r4 = placeMapper.findRegionById("1027");
        regionList.add(r4);
        Region r5 = placeMapper.findRegionById("302");
        regionList.add(r5);

        System.out.println("数据预处理之前:");
        System.out.println(regionList);
        System.out.println(geoPointList);
        placeDataCleanService.clean(geoPointList, regionList);
        System.out.println("数据预处理之后:");
        System.out.println(regionList);
        System.out.println(geoPointList);
    }
}
