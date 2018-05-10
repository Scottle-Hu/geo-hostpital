package com.charles.geo.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
    }

    //测试正常使用数据库连接池
    @Test
    public void test02() {
        Connection con1 = dbUtil.getConnection();
        Connection con2 = dbUtil.getConnection();
        dbUtil.releaseConnection(con1);
        dbUtil.releaseConnection(con2);
    }

    //测试超过初始连接数使用连接池
    @Test
    public void test03() {
        Connection con1 = dbUtil.getConnection();
        Connection con2 = dbUtil.getConnection();
        Connection con3 = dbUtil.getConnection();
        Connection con4 = dbUtil.getConnection();
        dbUtil.releaseConnection(con1);
        dbUtil.releaseConnection(con2);
        dbUtil.releaseConnection(con3);
        dbUtil.releaseConnection(con4);
        dbUtil.releaseConnection(null);  //测试容错
    }

    //测试超过最大连接数量使用连接池
    @Test
    public void test04() {
        Connection con1 = dbUtil.getConnection();
        System.out.println(con1);
        Connection con2 = dbUtil.getConnection();
        System.out.println(con2);
        Connection con3 = dbUtil.getConnection();
        System.out.println(con3);
        Connection con4 = dbUtil.getConnection();
        System.out.println(con4);
        Connection con5 = dbUtil.getConnection();
        System.out.println(con5);
        Connection con6 = dbUtil.getConnection();
        System.out.println(con6);
        Connection con7 = dbUtil.getConnection();
        System.out.println(con7);
        Connection con8 = dbUtil.getConnection();
        System.out.println(con8);
        Connection con9 = dbUtil.getConnection();  //如果一直等待释放空闲连接会产生死锁
        System.out.println(con9);
        dbUtil.releaseConnection(con1);
        dbUtil.releaseConnection(con2);
        dbUtil.releaseConnection(con3);
        dbUtil.releaseConnection(con4);
        dbUtil.releaseConnection(con5);
        dbUtil.releaseConnection(con6);
        dbUtil.releaseConnection(con7);
        dbUtil.releaseConnection(con8);
        dbUtil.releaseConnection(con9);
    }

    //测试多线程的情况下使用连接池
    @Test
    public void test05() {
        MyThread t[] = new MyThread[9];
        t[0] = new MyThread(dbUtil);
        t[1] = new MyThread(dbUtil);
        t[2] = new MyThread(dbUtil);
        t[3] = new MyThread(dbUtil);
        t[4] = new MyThread(dbUtil);
        t[5] = new MyThread(dbUtil);
        t[6] = new MyThread(dbUtil);
        t[7] = new MyThread(dbUtil);
        t[8] = new MyThread(dbUtil);
        for (MyThread mt : t) {
            mt.start();
        }
        dbUtil.getInfo();
        //等待打印结果
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class MyThread extends Thread {

    private DBUtil dbUtil;

    MyThread(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    @Override
    public void run() {
        //模拟线程使用数据库连接一段时间然后归还
        try {
            Thread.sleep((long) (4000 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Connection con = dbUtil.getConnection();
        System.out.println(this.getName() + ":获取到连接" + con);
        dbUtil.getInfo();
        Statement st = null;
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep((long) (4000 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dbUtil.releaseConnection(con);
        System.out.println(this.getName() + ":归还连接" + con);
        dbUtil.getInfo();
    }
}