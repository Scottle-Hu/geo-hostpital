package com.charles.geo.utils;

import com.charles.geo.service.process.impl.MainQueryServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author huqj
 */
@Service("dbUtil")
public class DBUtil {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(DBUtil.class);

    private static Connection connection = null;

    /**
     * 当前数据库连接的个数，当关闭的时候检测是否为1
     */
    private static int connectNum = 0;

    /**
     * 定时记录数据库连接数
     */
    private final long interval = 5 * 60 * 1000;

    //注入数据库连接属性
    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    /**
     * 定时检测数据库连接数目情况
     */
    @PostConstruct
    public void watchConnection() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LOGGER.info("当前数据库连接数目：" + connectNum);
            }
        }, interval, interval);
    }

    public Connection getConnection() {
        ++connectNum;
        LOGGER.info("当前连接数目:" + connectNum);
        if (connection != null) {
            return connection;
        }
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        --connectNum;  //获取失败
        return null;
    }

    public void close() {
        if (connectNum <= 0) {  //非法调用
            return;
        }
        connectNum--;
        LOGGER.info("剩余连接数目：" + connectNum);
        if (connection != null && connectNum == 0) {  //当还有其他程序在使用连接的时候不能关闭
            try {
                connection.close();
                connection = null;
                LOGGER.info("数据库连接已关闭...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
