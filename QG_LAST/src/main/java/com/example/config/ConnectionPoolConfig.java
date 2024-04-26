package com.example.config;

public class ConnectionPoolConfig {
    public String url = "jdbc:mysql://localhost:3306/mydb";
    public String user = "root";
    public String password = "771015";
    public int minConnections=5;
    public int maxConnections=20;
    public int maxIdleTime=5000; // 连接的最大空闲时间,单位毫秒

}
