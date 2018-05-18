package com.rmadegps.rmadeservermanagement.controller;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;

public class rpt_device_data_connection
{
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://rmadeiot.com:3306/rpt_device_data?useSSL=false&allowMultiQueries=true&rewriteBatchedStatements=true&autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8";
    public static final String USERNAME = "rptdd";
    public static final String PASSWORD = "eFfk91?7";

    private GenericObjectPool connectionPool = null;

    public DataSource setUp() throws Exception
    {
        Class.forName( rpt_device_data_connection.DRIVER).newInstance();
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(100);
        connectionPool.setTestOnBorrow( true );
        connectionPool.setTestWhileIdle( true );
        ConnectionFactory cf = new DriverManagerConnectionFactory( rpt_device_data_connection.URL, rpt_device_data_connection.USERNAME, rpt_device_data_connection.PASSWORD);
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    public GenericObjectPool getConnectionPool()
    {
        return connectionPool;
    }

}
