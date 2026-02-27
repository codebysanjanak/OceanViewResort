package com.oceanview.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataSourceProvider {
    private static final DataSourceProvider INSTANCE = new DataSourceProvider();

    private DataSourceProvider() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    public static DataSourceProvider getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASS);
    }
}