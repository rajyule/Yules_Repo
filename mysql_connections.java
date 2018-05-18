package com.rmadegps.rmadeservermanagement.controller;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/restapi/rsm/app")
public class mysql_connections
{
    rmade_server_management rmade_server_management_data_connection = new rmade_server_management();
    DataSource rmade_server_management = rmade_server_management_data_connection.setUp();
/*
    public Connection rmade_server_management_con = rmade_server_management.getConnection();

    Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
    Statement rmade_server_management_stmt2 = rmade_server_management_con.createStatement();*/

    public mysql_connections() throws Exception { }



    @RequestMapping(path = "/mysql_active_check_api4", method = RequestMethod.GET)
    public String mysql_active_check_api4() throws JSONException, IOException, SQLException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Statement rmade_server_management_stmt = rmade_server_management_con.createStatement();


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String sql = "SELECT username FROM admin";
        ResultSet resultSet = rmade_server_management_stmt.executeQuery(sql);
        if (resultSet.next())
        {


        }

        System.out.println("mysql_active : api4 " + dateFormat.format(date));

        JSONObject json = new JSONObject();
        json.put( "result", "success" );
        rmade_server_management_con.close();
        return String.valueOf( json );
    }


    @RequestMapping(path = "current_mysql_connection", method = RequestMethod.GET)
    public String current_mysql_connection(@RequestParam String password) throws SQLException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt2 = rmade_server_management_con.createStatement();

        JSONArray array = new JSONArray();

        String sql5 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt1.executeQuery( sql5 );
        if (res.next())
        {
            String sql = "SHOW STATUS WHERE Variable_name ='Threads_connected'";
            //show status like '%onn%';
            // show status like 'Conn%';

            ResultSet res1 = rmade_server_management_stmt2.executeQuery( sql );
            if (res1.next())
            {
                String Variable_name = res1.getString( "Variable_name" );
                String Value = res1.getString( "Value" );

                JSONObject json = new JSONObject();
                json.put( "Variable_name", Variable_name );
                json.put( "Value", Value );
                array.put( json );

                JSONObject jsonlast = new JSONObject();
                jsonlast.put( "result", array );
                rmade_server_management_con.close();
                return String.valueOf( jsonlast );
            }
            else
            {
                JSONObject jsonlast = new JSONObject();
                jsonlast.put( "result", "failure" );
                rmade_server_management_con.close();
                return String.valueOf( jsonlast );
            }


        }
        else
        {
            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", "failure" );
            rmade_server_management_con.close();
            return String.valueOf( jsonlast );
        }
    }

 @RequestMapping(path = "current_server_uptime", method = RequestMethod.GET)
  public String current_server_uptime(@RequestParam String password) throws SQLException
   {
            Connection rmade_server_management_con = rmade_server_management.getConnection();

            Statement rmade_server_management_stmt2 = rmade_server_management_con.createStatement();

            String sql6 = "select * from admin where password='" + password + "'";
            ResultSet res = rmade_server_management_stmt2.executeQuery( sql6 );
            if (res.next())
            {
                StringBuilder output = new StringBuilder();
                String s = null;
                // String[] array= new String[1000];
                try {

                    Process p = Runtime.getRuntime().exec( "uptime" );

                    BufferedReader stdInput = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                    BufferedReader stdError = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );

                    // read the output from the command
                    while ((s = stdInput.readLine()) != null) {
                        output.append( s );
                    }

                    // read any errors from the attempted command
                    while ((s = stdError.readLine()) != null) {
                    }

                } catch (IOException e) {
                    System.out.println( "Exception Error: " );
                    e.printStackTrace();

                }
                JSONObject json = new JSONObject();
                json.put( "result", output );
                rmade_server_management_con.close();
                return String.valueOf( json );
            }
            else
            {
                JSONObject json = new JSONObject();
                json.put( "result", "failure" );
                rmade_server_management_con.close();
                return String.valueOf( json );
            }

        }

    }
