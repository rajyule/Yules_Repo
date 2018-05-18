package com.rmadegps.rmadeservermanagement.controller;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("restapi/rsm/app")
public class api2
{

    rmade_server_management rmade_server_management_data_connection = new rmade_server_management();
    DataSource rmade_server_management = rmade_server_management_data_connection.setUp();



    public api2() throws Exception { }


    @RequestMapping(path = "/mysql_active_check_api2", method = RequestMethod.GET)
    public String mysql_active_check_api2() throws JSONException, IOException, SQLException
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

        System.out.println("mysql_active : api2 " + dateFormat.format(date));

        JSONObject json = new JSONObject();
        json.put( "result", "success" );
        rmade_server_management_con.close();
        return String.valueOf( (json) );
    }


    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login(@RequestParam String username, @RequestParam String password) throws Exception, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        String sql1 = "select username,password from admin where username='" + username + "' and password='" + password + "'";
        ResultSet res = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res.next())
        {

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", "success" );
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


    @RequestMapping(path = "/dashboard", method = RequestMethod.GET)
    public String dashboard(@RequestParam String password) throws Exception, IOException
    {

        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt2 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt3 = rmade_server_management_con.createStatement();

        JSONArray result_array = new JSONArray();
        JSONArray bigdata_services_array = new JSONArray();
        JSONArray java_service_array = new JSONArray();

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt2.executeQuery( sql1 );
        if (res.next())
        {
            String sql2 = "select * from rpt_server_jars";
            ResultSet res1 = rmade_server_management_stmt3.executeQuery( sql2 );
            while (res1.next())
            {


                String pid_name = res1.getString( "pid_name" );
                String pid_type = res1.getString( "pid_type" );
                String pid_nohup_name = res1.getString( "pid_nohup_name" );
                String pid_id = res1.getString( "pid_id" );
                String pid_status = res1.getString( "pid_status" );
                String image_path = res1.getString( "image_path" );
                String nohup_out_status = res1.getString( "nohup_out_status" );
                String nohup_out_path = res1.getString( "nohup_out_path" );

                if (pid_type.equals( "bigdata_service" )) {
                    JSONObject json_bigdata = new JSONObject();
                    json_bigdata.put( "pid_name", pid_name );
                    json_bigdata.put( "pid_nohup_name", pid_nohup_name );
                    json_bigdata.put( "pid_id", pid_id );
                    json_bigdata.put( "pid_status", pid_status );
                    json_bigdata.put( "image_path", image_path );
                    json_bigdata.put( "nohup_out_status", nohup_out_status );
                    json_bigdata.put( "nohup_out_path", nohup_out_path );

                    bigdata_services_array.put( json_bigdata );

                } else if (pid_type.equals( "java_service" )) {
                    JSONObject json_java = new JSONObject();
                    json_java.put( "pid_name", pid_name );
                    json_java.put( "pid_nohup_name", pid_nohup_name );
                    json_java.put( "pid_id", pid_id );
                    json_java.put( "pid_status", pid_status );
                    json_java.put( "image_path", image_path );
                    json_java.put( "nohup_out_status", nohup_out_status );
                    json_java.put( "nohup_out_path", nohup_out_path );

                    java_service_array.put( json_java );

                }


            }

            JSONObject json_result = new JSONObject();
            json_result.put( "bigdata_service", bigdata_services_array );
            json_result.put( "java_service", java_service_array );

            result_array.put( json_result );

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", result_array );
            rmade_server_management_con.close();
            /*rmade_server_management_stmt2.close();
            rmade_server_management_stmt3.close();*/
            return String.valueOf( jsonlast );
        }
        JSONObject jsonlast = new JSONObject();
        jsonlast.put( "result", "failure" );
        rmade_server_management_con.close();
        /*rmade_server_management_stmt2.close();
        rmade_server_management_stmt3.close();*/
        return String.valueOf( jsonlast );
    }


    @RequestMapping(path = "/dashboard_model_window", method = RequestMethod.GET)
    public String dashboard_model_window(@RequestParam String pid_number, @RequestParam String password) throws SQLException, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt4 = rmade_server_management_con.createStatement();


        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt4.executeQuery( sql2 );

        StringBuilder output = new StringBuilder();
        if (res.next()) {
            String s = null;

            // String[] array= new String[1000];

            try {

                Process p = Runtime.getRuntime().exec( "top -n 1 -b -p" + pid_number );

                BufferedReader stdInput = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                BufferedReader stdError = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );

                // read the output from the command
                while ((s = stdInput.readLine()) != null) {
                    output.append( s + "</br>" );
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
           // rmade_server_management_stmt4.close();
            rmade_server_management_con.close();
            return String.valueOf( json );


        } else {


            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
           // rmade_server_management_stmt4.close();
            rmade_server_management_con.close();
            return String.valueOf( json );
        }
    }



    @RequestMapping(path = "/dashboard_top", method = RequestMethod.GET)
    public String dashboard_top(@RequestParam String password) throws SQLException, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt4 = rmade_server_management_con.createStatement();


        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt4.executeQuery( sql2 );

        StringBuilder output = new StringBuilder();
        if (res.next()) {
            String s = null;

            // String[] array= new String[1000];

            try {

                Process p = Runtime.getRuntime().exec( "top -n 1 -b");

                BufferedReader stdInput = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                BufferedReader stdError = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );

                // read the output from the command

                int i=1;

                while ((s = stdInput.readLine()) != null)
                {
                   if(i >=7 && i<=17)
                   {
                       output.append( s + "</br></br>" );
                   }
                   i++;
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
            // rmade_server_management_stmt4.close();
            rmade_server_management_con.close();
            return String.valueOf( json );


        }
        else
        {


            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            // rmade_server_management_stmt4.close();
            rmade_server_management_con.close();
            return String.valueOf( json );
        }
    }



    @RequestMapping(path = "/hadoop_service_start", method = RequestMethod.GET)
    public String hadoop_service_start(@RequestParam String password) throws SQLException, IOException, InterruptedException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt5 = rmade_server_management_con.createStatement();

        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt5.executeQuery( sql2 );
        if (res.next())
        {
           /* String cmd1 = "hadoop-daemon.sh start namenode";
            String cmd2 = "hadoop-daemon.sh start datanode";
            String cmd3 = "yarn-daemon.sh start resourcemanager";
            String cmd4 = "yarn-daemon.sh start nodemanager";
            String cmd5 = "mr-jobhistory-daemon.sh start historyserver";
            Runtime run = Runtime.getRuntime();
            Process pr1 = run.exec(cmd1);
            pr1.waitFor();
            Process pr2 = run.exec(cmd2);
            pr2.waitFor();
            Process pr3 = run.exec(cmd3);
            pr3.waitFor();
            Process pr4 = run.exec(cmd4);
            pr4.waitFor();
            Process pr5 = run.exec(cmd5);
            pr5.waitFor();*/
            JSONObject json = new JSONObject();
            json.put( "result", "success" );
           // rmade_server_management_stmt5.close();
            rmade_server_management_con.close();
            return String.valueOf( json );
        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            //rmade_server_management_stmt5.close();
            rmade_server_management_con.close();
            return String.valueOf( json );
        }
    }



    @RequestMapping(path = "/add", method = RequestMethod.GET)
    public String add(@RequestParam String pid_name, @RequestParam String pid_type, @RequestParam String pid_nohup_name, @RequestParam String pid_id, @RequestParam int pid_status, @RequestParam String image_path, @RequestParam int nohup_out_status, @RequestParam String nohup_out_path, @RequestParam String password) throws SQLException, IOException, InterruptedException
    {

        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt6 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt7 = rmade_server_management_con.createStatement();


        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt6.executeQuery( sql2 );
        if (res.next())
        {
            String sql3 = "insert into rpt_server_jars(pid_name,pid_type,pid_nohup_name,pid_id,pid_status,image_path,nohup_out_status,nohup_out_path)values('" + pid_name + "','" + pid_type + "','" + pid_nohup_name + "','" + pid_id + "'," + pid_status + ",'" + image_path + "'," + nohup_out_status + ",'" + nohup_out_path + "')";
            rmade_server_management_stmt7.executeUpdate( sql3 );

            JSONObject json = new JSONObject();
            json.put( "result", "success" );
           /* rmade_server_management_stmt6.close();
            rmade_server_management_stmt7.close();*/
            rmade_server_management_con.close();
            return String.valueOf( (json) );

        } else {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            /*rmade_server_management_stmt6.close();
            rmade_server_management_stmt7.close();*/
            rmade_server_management_con.close();
            return String.valueOf( (json) );
        }


    }


    @RequestMapping(path = "/edit", method = RequestMethod.GET)
    public String edit(@RequestParam int id, @RequestParam String pid_name, @RequestParam String pid_type, @RequestParam String pid_nohup_name, @RequestParam String pid_id, @RequestParam int pid_status, @RequestParam int nohup_out_status, @RequestParam String nohup_out_path, @RequestParam String password) throws SQLException, IOException, InterruptedException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt8 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt9 = rmade_server_management_con.createStatement();

        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt8.executeQuery( sql2 );
        if (res.next())
        {
            String sql4 = "update rpt_server_jars set pid_name='" + pid_name + "',pid_type='" + pid_type + "',pid_nohup_name='" + pid_nohup_name + "',pid_id='" + pid_id + "',pid_status=" + pid_status + ",nohup_out_status=" + nohup_out_status + ",nohup_out_path='" + nohup_out_path + "' where id=" + id + "";
            rmade_server_management_stmt9.executeUpdate( sql4 );

            JSONObject json = new JSONObject();
            json.put( "result", "success" );
           /* rmade_server_management_stmt8.close();
            rmade_server_management_stmt9.close();*/
            rmade_server_management_con.close();
            return String.valueOf( (json) );

        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
          /*  rmade_server_management_stmt8.close();
            rmade_server_management_stmt9.close();*/
            rmade_server_management_con.close();
            return String.valueOf( (json) );
        }

    }


    @RequestMapping(path = "/edit_list_view", method = RequestMethod.GET)
    public String edit_list_view(@RequestParam String password) throws SQLException, IOException, InterruptedException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt10 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt11 = rmade_server_management_con.createStatement();

        JSONArray array = new JSONArray();
        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt10.executeQuery( sql2 );
        if (res.next())
        {
            String sql5 = "select * from rpt_server_jars";
            ResultSet res1 = rmade_server_management_stmt11.executeQuery( sql5 );
            while (res1.next())
            {
                String id = res1.getString( "id" );
                String pid_name = res1.getString( "pid_name" );
                String pid_type = res1.getString( "pid_type" );
                String pid_nohup_name = res1.getString( "pid_nohup_name" );
                String pid_id = res1.getString( "pid_id" );
                String pid_status = res1.getString( "pid_status" );
                String image_path = res1.getString( "image_path" );
                String nohup_out_status = res1.getString( "nohup_out_status" );
                String nohup_out_path = res1.getString( "nohup_out_path" );
                JSONObject json = new JSONObject();
                json.put( "id", id );
                json.put( "pid_name", pid_name );
                json.put( "pid_type", pid_type );
                json.put( "pid_nohup_name", pid_nohup_name );
                json.put( "pid_id", pid_id );
                json.put( "pid_status", pid_status );
                json.put( "image_path", image_path );
                json.put( "nohup_out_status", nohup_out_status );
                json.put( "nohup_out_path", nohup_out_path );
                array.put( json );

            }

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", array );
            /*rmade_server_management_stmt10.close();
            rmade_server_management_stmt11.close();*/
            rmade_server_management_con.close();
            return String.valueOf( jsonlast );
        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
           /* rmade_server_management_stmt10.close();
            rmade_server_management_stmt11.close();*/
            rmade_server_management_con.close();
            return String.valueOf( json );
        }

    }


    @RequestMapping(path = "/edit_view", method = RequestMethod.GET)
    public String edit_view(@RequestParam int id, @RequestParam String password) throws SQLException, IOException, InterruptedException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt12= rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt13 = rmade_server_management_con.createStatement();

        JSONArray array = new JSONArray();


        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt12.executeQuery( sql2 );
        if (res.next())
        {
            String sql5 = "select * from rpt_server_jars where id=" + id + "";
            ResultSet res1 = rmade_server_management_stmt13.executeQuery( sql5 );
            if (res1.next())
            {
                String id1 = res1.getString( "id" );
                String pid_name = res1.getString( "pid_name" );
                String pid_type = res1.getString( "pid_type" );
                String pid_nohup_name = res1.getString( "pid_nohup_name" );
                String pid_id = res1.getString( "pid_id" );
                String pid_status = res1.getString( "pid_status" );
                String image_path = res1.getString( "image_path" );
                String nohup_out_status = res1.getString( "nohup_out_status" );
                String nohup_out_path = res1.getString( "nohup_out_path" );
                JSONObject json = new JSONObject();
                json.put( "id", id1 );
                json.put( "pid_name", pid_name );
                json.put( "pid_type", pid_type );
                json.put( "pid_nohup_name", pid_nohup_name );
                json.put( "pid_id", pid_id );
                json.put( "pid_status", pid_status );
                json.put( "image_path", image_path );
                json.put( "nohup_out_status", nohup_out_status );
                json.put( "nohup_out_path", nohup_out_path );
                array.put( json );

            }

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", array );
           /* rmade_server_management_stmt12.close();
            rmade_server_management_stmt13.close();*/
            rmade_server_management_con.close();
            return String.valueOf( jsonlast );
        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
         /*   rmade_server_management_stmt12.close();
            rmade_server_management_stmt13.close();*/
            rmade_server_management_con.close();
            return String.valueOf( json );
        }

    }


    @RequestMapping(value = "/nohup_out", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<InputStreamResource> getImage1(@RequestParam String pid_number, @RequestParam String password) throws IOException, SQLException
    {

        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt14 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt15 = rmade_server_management_con.createStatement();

        String nohup_out_path = "/home/rmade_server/nohup_out/no_result.out";
        String filePath = "NA";
        //  ClassPathResource imgFile = new ClassPathResource("root/nohup_out/rpttxrx.out");
        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt14.executeQuery( sql2 );
        if (res.next())
        {
            String sql = "select nohup_out_path from rpt_server_jars where pid_id='" + pid_number + "'";
            ResultSet res1 = rmade_server_management_stmt15.executeQuery( sql );
            if (res1.next())
            {
                nohup_out_path = res1.getString( "nohup_out_path" );

            }
        }
        filePath = nohup_out_path;
        File downloadFile = new File( filePath );
        FileInputStream inStream = new FileInputStream( downloadFile );
      /*  rmade_server_management_stmt14.close();
        rmade_server_management_stmt15.close();*/
        rmade_server_management_con.close();
        return ResponseEntity
                .ok()
                .contentType( MediaType.valueOf( MediaType.TEXT_PLAIN_VALUE ) )
                .body( new InputStreamResource( inStream ) );
    }


    @RequestMapping(path = "/delete", method = RequestMethod.GET)
    public String delete(@RequestParam int id) throws SQLException, IOException, InterruptedException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt16 = rmade_server_management_con.createStatement();

        String sql7 = "delete from rpt_server_jars where id=" + id + "";
        rmade_server_management_stmt16.executeUpdate( sql7 );
        JSONObject json = new JSONObject();
        json.put( "result", "success" );
       /* rmade_server_management_stmt16.close();*/
        rmade_server_management_con.close();
        return String.valueOf( json );
    }



    @RequestMapping(value = "/nohup_image", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<InputStreamResource> getImage(@RequestParam String pid_number, @RequestParam String password) throws IOException, SQLException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt17 = rmade_server_management_con.createStatement();
        Statement rmade_server_management_stmt18 = rmade_server_management_con.createStatement();

        String image_path = "/home/rmade_server/nohup_out/images/no_image.png";
        String filePath = "NA";
        //  ClassPathResource imgFile = new ClassPathResource("root/nohup_out/rpttxrx.out");

        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt17.executeQuery( sql2 );
        if(res.next())
        {
            String sql = "select image_path from rpt_server_jars where pid_id='" + pid_number + "'";
            ResultSet res1 = rmade_server_management_stmt18.executeQuery( sql );
            if(res1.next())
            {
                image_path = res1.getString( "image_path" );
                if(image_path.equals(""))
                {
                    image_path = "/home/rmade_server/nohup_out/images/no_image.png";
                }
               //

            }

        }
        filePath =image_path;
        File downloadFile = new File( filePath );
        FileInputStream inStream = new FileInputStream( downloadFile );

       /* rmade_server_management_stmt17.close();
        rmade_server_management_stmt18.close();*/

        rmade_server_management_con.close();

        return ResponseEntity
                .ok()
                .contentType( MediaType.valueOf( MediaType.TEXT_PLAIN_VALUE ) )
                .body( new InputStreamResource( inStream ) );

    }


    @RequestMapping(path = "/kill_pid", method = RequestMethod.GET)
    public String kill_pid(@RequestParam String pid_number, @RequestParam String password) throws SQLException, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt19 = rmade_server_management_con.createStatement();

        String sql2 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt19.executeQuery( sql2 );

        StringBuilder output = new StringBuilder();
        if (res.next()) {
            String s = null;

            // String[] array= new String[1000];

            try {

                Process p = Runtime.getRuntime().exec( "kill -9 " + pid_number );

                BufferedReader stdInput = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                BufferedReader stdError = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );

                // read the output from the command
                while ((s = stdInput.readLine()) != null) {
                    output.append( s );
                }

                // read any errors from the attempted command
                while ((s = stdError.readLine()) != null) {

                }


            }
            catch (IOException e)
            {
                System.out.println( "Exception Error: " );
                e.printStackTrace();

            }


            JSONObject json = new JSONObject();
            json.put( "result", "success" );
         /*   rmade_server_management_stmt19.close();*/
            rmade_server_management_con.close();
            return String.valueOf( json );


        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
          /*  rmade_server_management_stmt19.close();*/
            rmade_server_management_con.close();
            return String.valueOf( json );
        }
    }


    @RequestMapping(path = "/load_usage", method = RequestMethod.GET)
    public String load_usage() throws SQLException, IOException
    {
        try
        {

            Connection rmade_server_management_con = rmade_server_management.getConnection();

            Statement rmade_server_management_stmt19 = rmade_server_management_con.createStatement();

            String load_value="NA";
            String load_time="NA";

            JSONArray array=null;
            JSONArray array1=new JSONArray( );

            SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd 00:00:00" );
            String current_dt = df.format( Calendar.getInstance().getTime() );


            SimpleDateFormat df1 = new SimpleDateFormat( "yyyy-MM-dd 23:59:59" );
            String current_dt1 = df1.format( Calendar.getInstance().getTime() );



            ResultSet res=rmade_server_management_stmt19.executeQuery( "select load_value,load_time from load_average WHERE date_time BETWEEN '"+current_dt+"' AND '"+current_dt1+"'" );
            while (res.next())
            {
                load_value=res.getString( "load_value" );
                load_time=res.getString( "load_time" );
                array=new JSONArray(  );
                array.put( new BigInteger( load_time ));
                array.put( Double.valueOf( load_value ) );
                array1.put( array );
            }

            rmade_server_management_con.close();
            return String.valueOf( array1 );

        }
        catch (Exception ex)
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            return String.valueOf( json );
        }


    }


}




