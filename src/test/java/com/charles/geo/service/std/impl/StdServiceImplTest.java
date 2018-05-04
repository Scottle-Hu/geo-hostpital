package com.charles.geo.service.std.impl;

import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.process.pre.IPlaceDataCleanService;
import com.charles.geo.service.std.IStdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class StdServiceImplTest {

    @Autowired
    private IStdService stdService;

    @Autowired
    private IPlaceDataCleanService placeDataCleanService;

    @Test
    public void test01() {
        List<String> regionNames = new ArrayList<String>();
        regionNames.add("武昌");
        regionNames.add("湖北");
        regionNames.add("北京市");
        List<String> pointNames = new ArrayList<String>();
        pointNames.add("武汉大学");
        pointNames.add("九省通衢");
        pointNames.add("华科");
        pointNames.add("中南医院");
        List<Region> regionList = new ArrayList<Region>();
        List<GeoPoint> pointList = new ArrayList<GeoPoint>();
        long start = System.currentTimeMillis();
        stdService.convert2StdName(regionNames, pointNames, regionList, pointList);
        System.out.println("标准化过程耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println(regionList);
        System.out.println(pointList);
    }

    @Test
    public void test02() {
        List<String> regionNames = new ArrayList<String>();
        regionNames.add("武昌");
        regionNames.add("湖北");
        regionNames.add("北京市");
        List<String> pointNames = new ArrayList<String>();
        pointNames.add("武汉大学");
        pointNames.add("九省通衢");
        pointNames.add("华科");
        pointNames.add("中南医院");
        List<Region> regionList = new ArrayList<Region>();
        List<GeoPoint> pointList = new ArrayList<GeoPoint>();
        long start = System.currentTimeMillis();
        stdService.convert2StdName(regionNames, pointNames, regionList, pointList);
        System.out.println("标准化过程耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("预处理之前");
        for (Region region : regionList) {
            System.out.println(region);
        }
        for (GeoPoint p : pointList) {
            System.out.println(p);
        }
        placeDataCleanService.clean(pointList, regionList);
        System.out.println("预处理之后");
        for (Region region : regionList) {
            System.out.println(region);
        }
        for (GeoPoint p : pointList) {
            System.out.println(p);
        }
    }

}