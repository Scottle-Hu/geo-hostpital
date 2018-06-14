package com.charles.geo.service.rank.impl;

import com.charles.geo.model.Disease;
import com.charles.geo.model.Expert;
import com.charles.geo.model.Hospital;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.service.rank.IRankService;
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
public class RankServiceImplTest {

    @Autowired
    private IRankService rankService;

    @Test
    public void test01() {
        QueryRequest request = new QueryRequest();
        List<Disease> diseaseList = new ArrayList<Disease>();
        Disease d = new Disease();
        d.setName("脑肿瘤");
        diseaseList.add(d);
        request.setDiseaseList(diseaseList);
        List<Hospital> hospitals = new ArrayList<Hospital>();

        Hospital h1 = new Hospital();
        h1.setName("湖北省肿瘤医院");
        h1.setLevel("三级甲等");
        List<Expert> experts = new ArrayList<Expert>();
        Expert p1 = new Expert();
        p1.setDepartment("肿瘤内科");
        experts.add(p1);
        Expert p2 = new Expert();
        p1.setDepartment("肿瘤内科");
        experts.add(p2);
        Expert p3 = new Expert();
        p1.setDepartment("肿瘤内科");
        experts.add(p3);
        h1.setExpertList(experts);
        hospitals.add(h1);

        Hospital h2 = new Hospital();
        h2.setName("武汉脑科医院");
        h2.setLevel("三级乙等");
        List<Expert> experts2 = new ArrayList<Expert>();
        h2.setExpertList(experts2);
        hospitals.add(h2);

        Hospital h3 = new Hospital();
        h3.setName("武汉协和肿瘤医院");
        h3.setLevel("未知");
        List<Expert> experts3 = new ArrayList<Expert>();
        Expert p21 = new Expert();
        p21.setDepartment("肿瘤科");
        experts.add(p21);
        h3.setExpertList(experts3);
        hospitals.add(h3);

        Hospital h4 = new Hospital();
        h4.setName("湖北省人民医院");
        h4.setLevel("三级甲等");
        List<Expert> experts4 = new ArrayList<Expert>();
        Expert p41 = new Expert();
        p41.setDepartment("肿瘤科");
        experts4.add(p41);
        Expert p42 = new Expert();
        p42.setDepartment("肿瘤科");
        experts4.add(p42);
        Expert p43 = new Expert();
        p43.setDepartment("肿瘤科");
        experts4.add(p43);
        Expert p44 = new Expert();
        p44.setDepartment("肿瘤科");
        experts4.add(p44);
        Expert p45 = new Expert();
        p45.setDepartment("神经外科");
        experts4.add(p45);
        Expert p46 = new Expert();
        p46.setDepartment("神经外科");
        experts4.add(p46);
        Expert p47 = new Expert();
        p47.setDepartment("神经外科");
        experts4.add(p47);
        Expert p48 = new Expert();
        p48.setDepartment("神经外科");
        experts4.add(p48);
        Expert p49 = new Expert();
        p49.setDepartment("神经外科");
        experts4.add(p49);
        h4.setExpertList(experts4);
        hospitals.add(h4);

        Hospital h5 = new Hospital();
        h5.setName("湖北省中山医院");
        h5.setLevel("三级甲等");
        List<Expert> experts5 = new ArrayList<Expert>();
        Expert p51 = new Expert();
        p51.setDepartment("神经外科");
        experts5.add(p51);
        Expert p52 = new Expert();
        p52.setDepartment("肿瘤内科");
        experts5.add(p52);
        Expert p53 = new Expert();
        p53.setDepartment("肿瘤内科");
        experts5.add(p53);
        h5.setExpertList(experts5);
        hospitals.add(h5);

        Hospital h6 = new Hospital();
        h6.setName("武汉总医院");
        h6.setLevel("三级甲等");
        List<Expert> experts6 = new ArrayList<Expert>();
        Expert p61 = new Expert();
        p61.setDepartment("神经外科");
        experts6.add(p61);
        Expert p62 = new Expert();
        p52.setDepartment("肿瘤科");
        experts6.add(p62);
        Expert p63 = new Expert();
        p63.setDepartment("肿瘤科");
        experts6.add(p63);
        Expert p64 = new Expert();
        p64.setDepartment("神经外科");
        experts6.add(p64);
        h6.setExpertList(experts6);
        hospitals.add(h6);

        Hospital h7 = new Hospital();
        h7.setName("武汉市武昌区水果湖医院");
        h7.setLevel("二级乙等");
        List<Expert> experts7 = new ArrayList<Expert>();
        h7.setExpertList(experts7);
        hospitals.add(h7);

        Hospital h8 = new Hospital();
        h8.setName("武汉大学中南医院");
        h8.setLevel("三级甲等");
        List<Expert> experts8 = new ArrayList<Expert>();
        Expert p81 = new Expert();
        p81.setDepartment("神经外科");
        experts8.add(p81);
        Expert p82 = new Expert();
        p82.setDepartment("肿瘤科");
        experts8.add(p82);
        Expert p83 = new Expert();
        p83.setDepartment("肿瘤科");
        experts8.add(p83);
        Expert p84 = new Expert();
        p84.setDepartment("神经外科");
        experts8.add(p84);
        Expert p85 = new Expert();
        p85.setDepartment("神经外科");
        experts8.add(p85);
        Expert p86 = new Expert();
        p86.setDepartment("肿瘤科");
        experts8.add(p86);
        Expert p87 = new Expert();
        p87.setDepartment("肿瘤科");
        experts8.add(p87);
        Expert p88 = new Expert();
        p88.setDepartment("神经外科");
        experts8.add(p88);
        Expert p89 = new Expert();
        p89.setDepartment("肿瘤科");
        experts8.add(p89);
        Expert p810 = new Expert();
        p810.setDepartment("神经外科");
        experts8.add(p810);
        h8.setExpertList(experts8);
        hospitals.add(h8);

        rankService.rankHospital(hospitals, request);
        hospitals = hospitals.subList(0, 5);
        for (Hospital h : hospitals) {
            System.out.println(h);
        }
    }

}