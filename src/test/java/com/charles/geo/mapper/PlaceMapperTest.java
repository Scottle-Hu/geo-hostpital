package com.charles.geo.mapper;

import com.charles.geo.model.PointPlace;
import com.charles.geo.model.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class PlaceMapperTest {

    @Autowired
    private PlaceMapper placeMapper;

    @Test
    public void test01() {
        Region res = placeMapper.findRegionById("123abc");
        System.out.println(res);
        res = placeMapper.findRegionById("1000");
        System.out.println(res);
    }

    @Test
    public void test02() {
        PointPlace res = placeMapper.findPointPlaceById("12322");
        System.out.println(res);
        res = placeMapper.findPointPlaceById("1000");
        System.out.println(res);
    }

}
