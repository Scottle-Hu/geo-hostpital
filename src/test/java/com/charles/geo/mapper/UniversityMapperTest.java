package com.charles.geo.mapper;

import com.charles.geo.model.Colleage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class UniversityMapperTest {

    @Autowired
    private UniversityMapper universityMapper;

    @Test
    public void test01() {
        List<Colleage> colleageList = universityMapper.queryByRegion("100");
        for (Colleage s : colleageList) {
            System.out.println(s);
        }
        System.out.println("=============================================");
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("longitudeMin", 114.36);
        map.put("longitudeMax", 114.40);
        map.put("latitudeMin", 30.50);
        map.put("latitudeMax", 30.55);
        colleageList = universityMapper.queryByRange(map);
        for (Colleage s : colleageList) {
            System.out.println(s);
        }
    }

}