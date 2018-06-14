package com.charles.geo.service.ner.impl;

import com.charles.geo.model.Disease;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.ner.INERService;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
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
//            forest3 = Library.makeForest(Result.class.getResourceAsStream("/dic_hospital.dic"));
            forest4 = Library.makeForest(Result.class.getResourceAsStream("/dic_geo.dic"));
            String str = "东方芝加哥,WHU,武大，武汉大学中南医院，中性白(粒)细胞这是一段，测试自定义词典";
            Result result = DicAnalysis.parse(str, forest1, forest2, forest3, forest4);//传入forest
            List<Term> termList = result.getTerms();
            for (Term term : termList) {
                System.out.println(term.getName() + ":" + term.getNatureStr());
            }
            result = ToAnalysis.parse("江城海南省海口市和平北路47号");
            termList = result.getTerms();
            for (Term term : termList) {
                System.out.println(term.getName() + ":" + term.getNatureStr());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02() {
        String text = "没想到四月的江城还能这么冷，刚来就感冒了";
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
        String text = "想转院到一个大医院治疗，武汉市第二医院怎么样？";
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