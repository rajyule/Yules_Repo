package com.rmadegps.rmadeservermanagement.controller;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;


public class Traccar_connection
{
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/Traccar_db?useSSL=false&amp;allowMultiQueries=true&amp;autoReconnect=true&amp;useUnicode=yes&amp;characterEncoding=UTF-8&amp;sessionVariables=sql_mode=''";
    public static final String USERNAME = "traccar";
    public static final String PASSWORD = "rV2nw72~";
    private GenericObjectPool connectionPool = null;
    public DataSource setUp() throws Exception
    {
        Class.forName(Traccar_connection.DRIVER).newInstance();

        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(100);
        ConnectionFactory cf = new DriverManagerConnectionFactory(Traccar_connection.URL, Traccar_connection.USERNAME, Traccar_connection.PASSWORD);
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }
    public GenericObjectPool getConnectionPool()

    {

        return connectionPool;
    }

}
