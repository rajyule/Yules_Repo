package com.rmadegps.rmadeservermanagement.controller;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;

public class rmade_server_management {

    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://rmadeiot.com:3306/rmade_server_manage?useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
    public static final String USERNAME = "rmade_server";
    public static final String PASSWORD = "Tm1b#f98";
    private GenericObjectPool connectionPool = null;
    public DataSource setUp() throws Exception
    {
        Class.forName( rmade_server_management.DRIVER).newInstance();
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(100);
        connectionPool.setTestOnBorrow( true );
        connectionPool.setTestWhileIdle( true );
        ConnectionFactory cf = new DriverManagerConnectionFactory( rmade_server_management.URL, rmade_server_management.USERNAME, rmade_server_management.PASSWORD);
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }
    public GenericObjectPool getConnectionPool()
    {
        return connectionPool;
    }



}
