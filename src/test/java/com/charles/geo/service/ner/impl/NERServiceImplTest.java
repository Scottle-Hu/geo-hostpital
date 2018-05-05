package com.charles.geo.service.ner.impl;

import com.charles.geo.model.Disease;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.ner.INERService;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
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
public class NERServiceImplTest {

    @Autowired
    private INERService nerService;

    @Test
    public void test01() {
        Forest forest1 = null;
        Forest forest2 = null;
        Forest forest3 = null;
        Forest forest4 = null;
        try {
            forest1 = Library.makeForest(Result.class.getResourceAsStream("/dic_disease.dic"));
            forest2 = Library.makeForest(Result.class.getResourceAsStream("/dic_university.dic"));
            forest3 = Library.makeForest(Result.class.getResourceAsStream("/dic_hospital.dic"));
            forest4 = Library.makeForest(Result.class.getResourceAsStream("/dic_geo.dic"));
            String str = "东方芝加哥,WHU,武大，武汉大学中南医院，中性白(粒)细胞这是一段，测试自定义词典";
            Result result = DicAnalysis.parse(str, forest1, forest2, forest3, forest4);//传入forest
            List<Term> termList = result.getTerms();
            for (Term term : termList) {
                System.out.println(term.getName() + ":" + term.getNatureStr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02() {
        String text = "昨天晚上十二点，在湖北省武汉市武汉大学梅园四舍，张学思突然癫痫发作，吃了一点药后感觉好多了，" +
                "最后才缓和下来，李三珍一直陪在他身边，后来还带他去了协和医院，协和医院就在华科的旁边，华科医学院对于" +
                "肺癌的研究很拔尖";
        List<String> regionNames = new ArrayList<String>();
        List<String> placeNames = new ArrayList<String>();
        List<String> peopleNames = new ArrayList<String>();
        List<String> diseaseNames = new ArrayList<String>();
        long start = System.currentTimeMillis();
        nerService.nerString(text, regionNames, placeNames, peopleNames, diseaseNames);
        System.out.println("命名实体识别耗时：" + (System.currentTimeMillis() - start) / 1000 + "秒");
        System.out.println("行政区划实体：" + regionNames);
        System.out.println("地点实体：" + placeNames);
        System.out.println("人名实体：" + peopleNames);
        System.out.println("疾病实体：" + diseaseNames);
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
        System.out.println("行政区划对象:");
        for (Region region : regionList) {
            System.out.println(region);
        }
        System.out.println("经纬度对象：");
        for (GeoPoint p : pointList) {
            System.out.println(p);
        }
        System.out.println("疾病对象：");
        for (Disease d : diseaseList) {
            System.out.println(d);
        }
    }

}