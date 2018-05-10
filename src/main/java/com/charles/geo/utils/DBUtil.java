package com.charles.geo.utils;

import com.charles.geo.service.process.impl.MainQueryServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.PooledConnection;
import javax.ws.rs.POST;
import java.sql.*;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * 数据库连接工具，使用连接池模式管理连接
 *
 * @author huqj
 */
@Service("dbUtil")
public class DBUtil {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(DBUtil.class);

    /**
     * 使用线程安全的vector存储连接对象
     */
    private static Vector<MyConnection> connection = null;

    /**
     * 定时记录连接池状态
     */
    private final long interval = 5 * 60 * 1000;

    /**
     * 定时强制归还忘记归还的连接
     */
    private final long forceReturnTime = 20 * 60 * 1000;

    /**
     * 定时刷新数据库连接
     */
    private final long refreshInterval = 2 * 60 * 60 * 1000;

    /**
     * 初始化连接池连接数目
     */
    @Value("${init.num}")
    private int initConnectionNum;

    /**
     * 连接池最大连接数目
     */
    @Value("${max.num}")
    private int maxConnectionNum;

    /**
     * 暂时获取不到可用连接的时候等待的时间，单位毫秒
     */
    @Value("${wait.time}")
    private long waitMs;

    /**
     * 尝试重新获取的次数
     */
    @Value("${wait.num}")
    private int waitTimes;

    /**
     * 测试数据库表名称
     */
    @Value("${test.table}")
    private String testTableName;

    /**
     * 注入数据库连接属性
     */
    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    /**
     * 初始化数据库连接池并定时释放多余的数据库连接和刷新数据库
     */
    @PostConstruct
    public void watchConnection() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("加载数据库驱动失败");
        }
        initConnectionPool();
        //定时收回多余连接的定时器
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LOGGER.info("当前数据库连接池的连接数量：" + connection.size());
                releaseRestConnection();  //释放多余连接
            }
        }, interval, interval);
        //定时刷新连接池的定时器
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, refreshInterval, refreshInterval);
        //强制归还超时连接的定时器
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                forceRelease();
            }
        }, forceReturnTime, forceReturnTime);
    }

    public Connection getConnection() {
        Connection con = getFreeConnection();
        //已经达到最大连接数量并且没有空闲连接,则尝试等待然后重新获取
        if (con == null) {
            int waitNum = 0;
            while (waitNum < waitTimes && con == null) {  //等待
                waitNum++;
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                con = getFreeConnection();
            }
            return con;
        }
        return con;
    }

    /**
     * 归还连接，但注意并没有释放
     *
     * @param con
     */
    public synchronized void releaseConnection(Connection con) {
        if (con == null) {
            LOGGER.info("错误，归还空连接");
            return;
        }
        for (MyConnection mc : connection) {
            if (mc.getConnection().equals(con)) {
                mc.setBusy(false);
                break;
            }
        }
    }

    /**
     * 初始化连接池
     */
    private synchronized void initConnectionPool() {
        connection = new Vector<MyConnection>();
        for (int i = 0; i < initConnectionNum; i++) {
            addConnection(newConnection());
        }
        LOGGER.info("初始化连接池成功");
    }

    /**
     * 获取空闲连接
     */
    private synchronized Connection getFreeConnection() {
        if (connection == null) {  //当前没有初始化
            initConnectionPool();
        }
        //遍历找到空闲连接并测试是否可用
        for (MyConnection mc : connection) {
            if (!mc.isBusy() && isAvailable(mc.getConnection())) {
                mc.setBusy(true);  //设置连接非空闲
                mc.setStartTm(System.currentTimeMillis());  //设置连接借出时间
                return mc.getConnection();
            }
        }
        if (connection.size() < maxConnectionNum) {
            expandPool();
            MyConnection newCon = connection.get(connection.size() - 1);
            if (!newCon.isBusy()) {
                newCon.setBusy(true);
                newCon.setStartTm(System.currentTimeMillis());  //设置连接借出时间
                return newCon.getConnection();
            }
        }
        return null;
    }

    /**
     * 扩充连接池
     */
    private synchronized void expandPool() {
        if (connection == null) {
            connection = new Vector<MyConnection>();
        }
        if (connection.size() < maxConnectionNum) {
            addConnection(newConnection());
        }
    }

    /**
     * 新建一个连接对象
     */
    private Connection newConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            LOGGER.error("初始化连接池出错!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 添加一个连接到vector中
     */
    private void addConnection(Connection con) {
        synchronized (connection) {
            if (con != null && connection.size() < maxConnectionNum) {
                MyConnection mc = new MyConnection();
                mc.setBusy(false);
                mc.setConnection(con);
                connection.add(mc);
            }
        }
    }

    /**
     * 测试连接是否可用
     */
    private synchronized boolean isAvailable(Connection con) {
        if (con == null) {
            return false;
        }
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery("select * from " + testTableName);
            return true;
        } catch (Exception e) {
            LOGGER.info("该连接不可用:" + con);
            return false;
        } finally {
            try {
                statement.close();
                rs.close();
            } catch (SQLException e) {
                LOGGER.error("释放sta和rs出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新连接
     */
    private void refresh() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        int num = 0;
        for (MyConnection mc : connection) {
            //只更新没有占用并且不可用的连接
            if (!mc.isBusy() && !isAvailable(mc.getConnection())) {
                try {
                    mc.getConnection().close(); //注意释放之前的连接，否则数据库那边会爆
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                mc.setConnection(newConnection());
                num++;
            }
        }
        LOGGER.info("共刷新数据库连接：" + num);
    }

    /**
     * 释放多余连接
     */
    private void releaseRestConnection() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        int releaseNum = connection.size() - initConnectionNum;
        int old = releaseNum;
        if (releaseNum > 0) {
            Iterator<MyConnection> iterator = connection.iterator();
            while (iterator.hasNext() && releaseNum > 0) {
                MyConnection mc = iterator.next();
                if (!mc.isBusy()) {  //释放没有占用的连接
                    try {
                        mc.getConnection().close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    iterator.remove();
                    releaseNum--;
                }
            }
            LOGGER.info("共释放多余连接：" + (old - releaseNum));
        }
    }

    /**
     * 强制归还超时未归还的连接
     */
    private void forceRelease() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        for (MyConnection mc : connection) {
            if (mc.isBusy() && (System.currentTimeMillis() - mc.getStartTm()) > forceReturnTime) {
                mc.setBusy(false);
            }
        }
    }

    /**
     * 测试用的方法，用于获取当前连接总数和空闲连接数
     */
    public synchronized void getInfo() {
        int num = 0;
        for (MyConnection mc : connection) {
            if (!mc.isBusy()) {
                ++num;
            }
        }
        System.out.println("当前连接总数：" + connection.size() + ",可用空闲连接数:" + num);
    }

}
