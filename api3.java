package com.rmadegps.rmadeservermanagement.controller;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.mongodb.util.JSON;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
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
public class api3 {

    rmade_server_management rmade_server_management_data_connection = new rmade_server_management();
    DataSource rmade_server_management = rmade_server_management_data_connection.setUp();

    rpt_client_data_connection rpt_client_data_connection   = new rpt_client_data_connection();
    DataSource rpt_client = rpt_client_data_connection.setUp();

    rpt_myvehicle rpt_myvehicle_data_connection = new rpt_myvehicle();
    DataSource rpt_myvehicle = rpt_myvehicle_data_connection.setUp();


    public api3() throws Exception { }



    @RequestMapping(path = "/mysql_active_check_api3", method = RequestMethod.GET)
    public String mysql_active_check_api3() throws JSONException, IOException, SQLException
    {

        Connection rpt_client_con = rpt_client.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Connection rpt_myvehicle_con = rpt_myvehicle.getConnection();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Statement rpt_vehice_statement = rpt_myvehicle_con.createStatement();
        Statement rpt_client_statement = rpt_client_con.createStatement();

        Statement rmade_server_management_stmt = rmade_server_management_con.createStatement();

        String SQL_server_manage= "SELECT username FROM admin";


        String sql = "select * from testing";
        ResultSet resultSet = rpt_vehice_statement.executeQuery(sql);
        if (resultSet.next())
        {


        }
        ResultSet resultSet1 = rpt_client_statement.executeQuery(sql);
        if (resultSet1.next())
        {


        }

        ResultSet resultSet2 = rmade_server_management_stmt.executeQuery(SQL_server_manage);
        if (resultSet2.next())
        {

        }


        System.out.println("mysql_active : api3 " + dateFormat.format(date));

        JSONObject json = new JSONObject();
        json.put( "result", "success" );
        rpt_client_con.close();
        rmade_server_management_con.close();
        rpt_myvehicle_con.close();
        return String.valueOf((json));
    }


    @RequestMapping(path = "/get_device_imei_no", method = RequestMethod.GET)
    public String get_device_imei_no(@RequestParam String imei,@RequestParam String notification_id,@RequestParam String password) throws Exception
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rpt_client_con = rpt_client.getConnection();
        Connection my_vehicle_con = rpt_myvehicle.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
        Statement rpt_client_stmt1 = rpt_client_con.createStatement();
        Statement rpt_client_stmt2 = rpt_client_con.createStatement();
        Statement my_vehicle_stmt1 = my_vehicle_con.createStatement();

        String device_id="NA";
        String event_id="0";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 0); // to get previous year add -1
        Date nextYear = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String current_dt = df.format( nextYear );

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt1.executeQuery(sql1);
        if (res.next())
        {

            String sql2 = "select device_id from rpt_inventory where device_imei='" + imei + "'";
            ResultSet res2 = rpt_client_stmt1.executeQuery(sql2);
            if (res2.next())
            {
                device_id= res2.getString("device_id");

                String sql3 = "SELECT id FROM events WHERE deviceid='"+device_id+"' ORDER BY id DESC LIMIT 1";
                ResultSet res3 = my_vehicle_stmt1.executeQuery(sql3);
                if (res3.next())
                {
                    event_id = res3.getString("id");
                }

                    String sql4="update device_config set device_id='"+device_id+"', event_id='"+event_id+"',command_send='NA',command_result='NA',notification_id='"+notification_id+"',device_imei_no='"+imei+"',status=1,active_dt='"+current_dt+"'";
                    rpt_client_stmt2.executeUpdate( sql4 );

                    JSONObject jsonlast = new JSONObject();
                    jsonlast.put( "result", "success" );
                    rmade_server_management_con.close();
                    rpt_client_con.close();
                    return String.valueOf( jsonlast );



            }
            else
            {
                JSONObject jsonlast = new JSONObject();
                jsonlast.put( "result", "device not exist" );
                rmade_server_management_con.close();
                rpt_client_con.close();
                return String.valueOf( jsonlast );
            }


        }
        else
        {
            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", "failure" );
            rmade_server_management_con.close();
            rpt_client_con.close();
            return String.valueOf( jsonlast );
        }


    }

    @RequestMapping(path = "/device_location_data", method = RequestMethod.GET)
    public String device_location_data(@RequestParam String imei,@RequestParam String password) throws Exception, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection my_vehicle_con = rpt_myvehicle.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
        Statement my_vehicle_stmt1 = my_vehicle_con.createStatement();

        String device_id="NA";
        String device_type="NA";
        String server_time="NA";
        String device_time="NA";
        String valid="NA";
        String latitude="NA";
        String longitude="NA";
        String altitude="NA";
        String speed="NA";
        String address="NA";
        String ignition="NA";
        String battery="NA";
        String attributes="NA";
        String imei_no="NA";

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat( "dd-MM-yyyy hh:mm a" );
        DateFormat dp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_dt = df.format( date );


        JSONArray jsonArray = new JSONArray();

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt1.executeQuery(sql1);
        if (res.next())
        {
            String sql2 = "select * from rpt_device_data_single where imei_no='" + imei + "' AND attribute_1 = 'device_online'";
            ResultSet res2 = my_vehicle_stmt1.executeQuery(sql2);
            if (res2.next())
            {
                device_id= res2.getString("device_id");
                device_type= res2.getString("device_type");
                server_time= res2.getString("server_time");
                device_time= res2.getString("device_time");
                valid= res2.getString("valid");
                latitude= res2.getString("latitude");
                longitude= res2.getString("longitude");
                altitude= res2.getString("altitude");
                speed= res2.getString("speed");
                address= res2.getString("address");
                ignition= res2.getString("ign_ac");
                battery= res2.getString("battery");
                attributes= res2.getString("others1");
                imei_no= res2.getString("imei_no");


                java.util.Date server_time_df = dp.parse(server_time);
                java.util.Date device_time_df = dp.parse(device_time);

                server_time= df.format( server_time_df );
                device_time= df.format( device_time_df );

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("device_id",device_id);
                jsonObject.put("device_type",device_type);
                jsonObject.put("server_time",server_time);
                jsonObject.put("device_time",device_time);
                jsonObject.put("current_time",current_dt);
                jsonObject.put("valid",valid);
                jsonObject.put("latitude",latitude);
                jsonObject.put("longitude",longitude);
                jsonObject.put("altitude",altitude);
                jsonObject.put("speed",speed);
                jsonObject.put("address",address);
                jsonObject.put("ignition",ignition);
                jsonObject.put("battery",battery);
                jsonObject.put("attributes",attributes);
                jsonObject.put("device_imei_no",imei_no);

                jsonArray.put(jsonObject);

                JSONObject result = new JSONObject();
                result.put( "result", jsonArray );
                rmade_server_management_con.close();
                my_vehicle_con.close();
                return String.valueOf( result );

            }
            else
            {
                JSONObject jsonlast = new JSONObject();
                jsonlast.put( "result", "device_offline" );
                rmade_server_management_con.close();
                my_vehicle_con.close();
                return String.valueOf( jsonlast );
            }
        }
        else
        {
            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", "failure" );
            rmade_server_management_con.close();
            my_vehicle_con.close();
            return String.valueOf( jsonlast );
        }



    }

    @RequestMapping(path = "/device_api_config_cmd", method = RequestMethod.GET)
    public String device_api_config_cmd(@RequestParam String imei,@RequestParam String password) throws Exception, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rpt_client_con = rpt_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
        Statement rpt_client_stmt1 = rpt_client_con.createStatement();

        String command="NA";
        String command_result="NA";




        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt1.executeQuery(sql1);
        if (res.next())
        {
            String sql2 = "select rpt_device_cmd.command,rpt_device_cmd.command_result from rpt_inventory,rpt_device_cmd where rpt_inventory.device_type=rpt_device_cmd.device_type AND rpt_inventory.device_imei='" + imei + "'";
            ResultSet res2 = rpt_client_stmt1.executeQuery(sql2);
            while (res2.next())
            {

                JSONObject jsonObject1 = new JSONObject();

                command= res2.getString("command");
                command_result= res2.getString("command_result");

                jsonObject1.put("command_send",command);
                jsonObject1.put("command_result",command_result);
                jsonArray1.put(jsonObject1);

            }

            JSONObject jsonObject2 = new JSONObject();


            jsonObject2.put("command",jsonArray1);

            jsonArray2.put(jsonObject2);


            JSONObject result = new JSONObject();
            result.put( "result", jsonArray2 );
            rmade_server_management_con.close();
            rpt_client_con.close();
            return String.valueOf( result );

        }
        JSONObject jsonlast = new JSONObject();
        jsonlast.put( "result", "failure" );
        rmade_server_management_con.close();
        rpt_client_con.close();
        return String.valueOf( jsonlast );
    }

    @RequestMapping(path = "/device_api_config", method = RequestMethod.GET)
    public String device_api_config(@RequestParam String imei,@RequestParam String command_send,@RequestParam String command_result,@RequestParam String password) throws Exception, IOException
    {
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rpt_client_con = rpt_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
        Statement rpt_client_stmt1 = rpt_client_con.createStatement();
        Statement rpt_client_stmt2 = rpt_client_con.createStatement();

        String device_id = "NA";

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt1.executeQuery(sql1);
        if (res.next())
        {
            String sql2 = "select device_id from rpt_inventory where device_imei='" + imei + "'";
            ResultSet res2 = rpt_client_stmt1.executeQuery(sql2);
            if (res2.next())
            {
                device_id= res2.getString("device_id");

                try {
                    JSONObject data = new JSONObject();
                    data.put("data", command_send);

                    JSONObject input = new JSONObject()
                            .put("deviceId", device_id)
                            .put("type", "custom")
                            .put("attributes", data);


                    URL url = new URL("http://rmadeiot.com:444/api/commands/send");
                    java.net.Authenticator.setDefault(new java.net.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("admin", "trcRPT#@20".toCharArray());
                        }
                    });
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(String.valueOf(input).getBytes());
                    os.flush();
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    String output = br.readLine();
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                String sql4="update device_config set command_send='"+command_send+"',command_result='"+command_result+"' WHERE device_imei_no='"+imei+"'";
                rpt_client_stmt2.executeUpdate( sql4 );

                JSONObject jsonlast = new JSONObject();
                jsonlast.put("result", "success");
                rmade_server_management_con.close();
                rpt_client_con.close();
                return String.valueOf(jsonlast);

            }
        }
        JSONObject jsonlast = new JSONObject();
        jsonlast.put( "result", "failure" );
        rmade_server_management_con.close();
        rpt_client_con.close();
        return String.valueOf( jsonlast );

    }
}
