package com.rmadegps.rmadeservermanagement.controller;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;


public class rpt_myvehicle
{
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://rmadeiot.com:3306/rpt_myvehicle?useSSL=false&amp;allowMultiQueries=true&amp;autoReconnect=true&amp;useUnicode=yes&amp;characterEncoding=UTF-8&amp;sessionVariables=sql_mode=''";
    public static final String USERNAME = "rpt_user";
    public static final String PASSWORD = "igH7t*27";
    private GenericObjectPool connectionPool = null;
    public DataSource setUp() throws Exception
    {
        Class.forName( rpt_myvehicle.DRIVER).newInstance();
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(100);
        connectionPool.setTestOnBorrow( true );
        connectionPool.setTestWhileIdle( true );
        ConnectionFactory cf = new DriverManagerConnectionFactory( rpt_myvehicle.URL, rpt_myvehicle.USERNAME, rpt_myvehicle.PASSWORD);
        PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }
    public GenericObjectPool getConnectionPool()
    {
        return connectionPool;
    }

}
