package com;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class ValueAnnotationTest {

    @Value("${jdbc.driver}")
    private String driver;

    @Value("${init.num}")
    private int initNum;

    @Test
    public void testValue() {
        System.out.println(driver);
        System.out.println(initNum);
    }

}
