package com.charles.geo.service.process.impl;

import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.model.Region;
import com.charles.geo.service.process.IMainQueryService;
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

    @Test
    public void test01() {
        QueryRequest request = new QueryRequest();
        List<Region> regionList = new ArrayList<Region>();
        Region r1 = placeMapper.findRegionById("100");
    }

}