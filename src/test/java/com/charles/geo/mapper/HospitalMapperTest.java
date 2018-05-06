package com.charles.geo.mapper;

import com.charles.geo.model.Hospital;
import com.charles.geo.model.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class HospitalMapperTest {

    @Autowired
    private HospitalMapper hospitalMapper;

    @Test
    public void test01() {
        List<Hospital> hospitals = hospitalMapper.queryByRegion("%宣城市 郎溪县%");
        for (Hospital h : hospitals) {
            System.out.println(h);
        }
        System.out.println();
        System.out.println("=========================================================");
        System.out.println();
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("longitudeMin", 117.32);
        map.put("longitudeMax", 117.56);
        map.put("latitudeMin", 30.32);
        map.put("latitudeMax", 31.36);
        hospitals = hospitalMapper.queryByRange(map);
        for (Hospital h : hospitals) {
            System.out.println(h);
        }
    }

    @Test
    public void test02() {
        List<Hospital> hospitals = hospitalMapper.queryByName("武汉大学中南医院");
        for (Hospital h : hospitals) {
            System.out.println(h);
        }
    }

    @Test
    public void test03() {
        List<Hospital> hospitals = hospitalMapper.queryByAlias("协和医院");
        for (Hospital h : hospitals) {
            System.out.println(h);
        }
    }

    @Test
    public void test04() {
        List<String> ids = new ArrayList<String>();
        ids.add("1");
        ids.add("3");
        ids.add("14");
        ids.add("10");
        long start = System.currentTimeMillis();
        List<Hospital> byIds = hospitalMapper.findByIds(ids);
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        System.out.println(byIds);
    }

    @Test
    public void test05() {
        List<String> places = hospitalMapper.findAllPlace();
        int index = 1;
        for (String p : places) {
            System.out.println(index++);
            System.out.println(p);
            hospitalMapper.findIdsByPlace(p);
        }
    }
}