package com.oceanview.config;

public final class DBConfig {
    public static final String URL =
            "jdbc:mysql://localhost:3306/ocean_view_resort?useSSL=false&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PASS = "root";
    private DBConfig() {}
}