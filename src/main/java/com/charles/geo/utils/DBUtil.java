package com.charles.geo.utils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

/**
 * @author huqj
 */
public class DBUtil {

    public static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_geo?characterEncoding=utf8"
                        , "root", "123456");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

}
