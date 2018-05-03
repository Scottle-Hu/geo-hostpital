package com.charles.geo.mapper;

import com.charles.geo.model.PointPlace;
import com.charles.geo.model.Region;
import com.sun.org.apache.regexp.internal.RE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        res = placeMapper.findRegionById("1");
        System.out.println(res);
    }

    @Test
    public void test02() {
        PointPlace res = placeMapper.findPointPlaceById("12322");
        System.out.println(res);
        res = placeMapper.findPointPlaceById("1000");
        System.out.println(res);
    }

   /* @Test
    public void test03() {
        List<Region> regionList = placeMapper.findAllRegion();
        int id = 6280;
        for (Region region : regionList) {
            String name = region.getName();
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", id + "");
            map.put("alias", name);
            map.put("type", "3");
            map.put("place_id", region.getId());
            placeMapper.insertAlias(map);
            id++;
            System.out.println(id - 6280);
        }
    }*/

    @Test
    public void test04() {
        System.out.println(placeMapper.findPlaceIdByAlias("北京"));
    }

}
