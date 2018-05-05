package com.charles.geo.service.process.impl;

import com.charles.geo.mapper.DiseaseMapper;
import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.model.*;
import com.charles.geo.service.ner.INERService;
import com.charles.geo.service.process.IMainQueryService;
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
public class MainQueryServiceImplTest {

    @Autowired
    private IMainQueryService mainQueryService;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private IStdService stdService;

    @Autowired
    private IPlaceDataCleanService placeDataCleanService;

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Autowired
    private INERService nerService;

    @Test
    public void test01() {
        QueryRequest request = new QueryRequest();
        List<Region> regionList = new ArrayList<Region>();
        Region r1 = placeMapper.findRegionById("100");
    }

    @Test
    public void testMain() {
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
        start = System.currentTimeMillis();
        placeDataCleanService.clean(pointList, regionList);
        System.out.println("预处理过程耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("预处理之后");
        for (Region region : regionList) {
            System.out.println(region);
        }
        for (GeoPoint p : pointList) {
            System.out.println(p);
        }
        //推荐算法
        QueryRequest request = new QueryRequest();
        request.setPointList(pointList);
        request.setRegionList(regionList);
        List<Disease> diseaseList = diseaseMapper.findByName("肺癌");
        diseaseList.addAll(diseaseMapper.findByName("白血病"));
        request.setDiseaseList(diseaseList);
        start = System.currentTimeMillis();
        InformationResponse res = mainQueryService.handler(request);
        System.out.println("推荐算法过程耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("推荐医院:");
        List<Hospital> hospitalList = res.getHospitalList();
        for (Hospital h : hospitalList) {
            System.out.println(h);
        }
        System.out.println("推荐大学");
        List<Colleage> colleageList = res.getColleageList();
        for (Colleage c : colleageList) {
            System.out.println(c);
        }
    }

    @Test
    public void test03() {
        String text = "昨天晚上十二点，在湖北省武汉市武汉大学梅园四舍，张学思突然癫痫发作，吃了一点药后感觉好多了，" +
                "最后才缓和下来，李三珍一直陪在他身边，后来还带他去了协和医院，协和医院就在华科的旁边，华科医学院对于" +
                "肺癌的研究很拔尖";
        List<Region> regionList = new ArrayList<Region>();
        List<GeoPoint> pointList = new ArrayList<GeoPoint>();
        List<Disease> diseaseList = new ArrayList<Disease>();
        long start = System.currentTimeMillis();
        nerService.nerFromText(text, pointList, regionList, diseaseList);
        System.out.println("命名实体识别并标准化耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("疾病对象：");
        for (Disease d : diseaseList) {
            System.out.println(d);
        }
        System.out.println("预处理之前");
        for (Region region : regionList) {
            System.out.println(region);
        }
        for (GeoPoint p : pointList) {
            System.out.println(p);
        }
        start = System.currentTimeMillis();
        placeDataCleanService.clean(pointList, regionList);
        System.out.println("预处理过程耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("预处理之后");
        for (Region region : regionList) {
            System.out.println(region);
        }
        for (GeoPoint p : pointList) {
            System.out.println(p);
        }
        //推荐算法
        QueryRequest request = new QueryRequest();
        request.setPointList(pointList);
        request.setRegionList(regionList);
        request.setDiseaseList(diseaseList);
        start = System.currentTimeMillis();
        InformationResponse res = mainQueryService.handler(request);
        System.out.println("推荐算法过程耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("推荐医院:");
        List<Hospital> hospitalList = res.getHospitalList();
        for (Hospital h : hospitalList) {
            System.out.println(h);
        }
        System.out.println("推荐大学");
        List<Colleage> colleageList = res.getColleageList();
        for (Colleage c : colleageList) {
            System.out.println(c);
        }

    }

}