package com.charles.geo.utils;

import java.sql.Connection;

/**
 * @author huqj
 */
public class MyConnection {

    private Connection connection;

    /**
     * 目前是否被占用
     */
    private boolean busy;

    /**
     * 开始创建连接的时间戳，用于清理忘记归还的连接
     */
    private long startTm;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public long getStartTm() {
        return startTm;
    }

    public void setStartTm(long startTm) {
        this.startTm = startTm;
    }
}
