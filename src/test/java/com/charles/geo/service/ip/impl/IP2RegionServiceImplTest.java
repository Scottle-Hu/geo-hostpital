package com.charles.geo.service.ip.impl;

import com.charles.geo.service.ip.IIP2RegionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class IP2RegionServiceImplTest {

    @Autowired
    private IIP2RegionService ip2regionService;

    @Test
    public void testIp2Region() {
        String region = ip2regionService.getCityFromIP("122.114.206.52");
        System.out.println(region);
        assertEquals("\"河南\"\"郑州\"", region);
    }


}