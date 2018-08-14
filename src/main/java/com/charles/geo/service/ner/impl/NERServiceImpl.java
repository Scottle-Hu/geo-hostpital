package com.charles.geo.service.ner.impl;

import com.charles.geo.model.Disease;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.ner.INERService;
import com.charles.geo.service.std.IStdService;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;
import java.util.*;

/**
 * @author huqj
 * @since 1.0
 */
@Service("nerService")
public class NERServiceImpl implements INERService {

    @Autowired
    private IStdService stdService;

    //自定义词典，初次加载
    private Forest forest1 = null;
    private Forest forest2 = null;
    private Forest forest3 = null;
    private Forest forest4 = null;

    @PostConstruct
    public void init() {
        System.out.println("=============加载用户自定义词典============");
        try {
            forest1 = Library.makeForest(Result.class.getResourceAsStream("/dic_disease.dic"));
            forest2 = Library.makeForest(Result.class.getResourceAsStream("/dic_university.dic"));
            forest3 = Library.makeForest(Result.class.getResourceAsStream("/dic_hospital.dic"));
            forest4 = Library.makeForest(Result.class.getResourceAsStream("/dic_geo.dic"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=============加载用户自定义词典============");
    }

    public void nerFromText(String text, List<GeoPoint> geoPoints,
                            List<Region> regionList, List<Disease> diseaseList) {
        List<String> regionNames = new ArrayList<String>();
        List<String> placeNames = new ArrayList<String>();
        List<String> peopleNames = new ArrayList<String>();
        List<String> diseaseNames = new ArrayList<String>();
        nerString(text, regionNames, placeNames, peopleNames, diseaseNames);
        System.out.println("命名实体识别结果：");
        System.out.println("行政区划实体：");
        for (String s : regionNames) {
            System.out.println(s);
        }
        System.out.println("地点实体：");
        for (String s : placeNames) {
            System.out.println(s);
        }
        System.out.println("人名实体：");
        for (String s : peopleNames) {
            System.out.println(s);
        }
        System.out.println("疾病实体：");
        for (String s : diseaseNames) {
            System.out.println(s);
        }

        //调用标准化模块将字符串转化为对象
        stdService.convert2StdName(regionNames, placeNames, regionList, geoPoints);
        System.out.println("标准化结果：");
        System.out.println("标准化行政区划名称：");
        for (Region r : regionList) {
            System.out.println(r.getName());
        }
        System.out.println("标准化经纬度点：");
        for (GeoPoint r : geoPoints) {
            System.out.println("(" + r.getLongitude() + "," + r.getLatitude() + ")");
        }
        System.out.println("标准化疾病名称：");
        diseaseList.addAll(stdService.stdDisease(diseaseNames));
        for (Disease d : diseaseList) {
            System.out.println(d.getName());
        }
    }

    //从文本中识别出对应的字符串数组
    public void nerString(String text, List<String> regionNames, List<String> placeNames, List<String> peopleNames, List<String> diseaseNames) {
        try {
            //自定义词典优先策略
            Result result = DicAnalysis.parse(text, forest1, forest2, forest3, forest4);//传入forest
            List<Term> termList = result.getTerms();
            for (Term term : termList) {
                String w = term.getName();
                String n = term.getNatureStr();
                if (n.equals("ngeo")) {  //行政区划实体，包括简称别名
                    regionNames.add(w);
                } else if (n.equals("ntu") || n.equals("nth")) {  //地点实体，包括大学、医院、简称等
                    placeNames.add(w);
                } else if (n.equals("nj")) {  //疾病实体
                    diseaseNames.add(w);
                } else if (n.equals("nr")) {  //人名
                    peopleNames.add(w);
                }
                //System.out.println(term.getName() + ":" + term.getNatureStr());
            }
            //去重
            Set<String> set = new HashSet<String>();
            Iterator<String> iterator = diseaseNames.iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
                if (set.contains(str)) {
                    iterator.remove();
                } else {
                    set.add(str);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
