package com.charles.geo.utils;

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
public class DBUtilTest {

    @Autowired
    private DBUtil dbUtil;

    @Test
    public void test01() {
        dbUtil.getConnection();
        dbUtil.getConnection();
        dbUtil.close();
        dbUtil.close();
    }
}