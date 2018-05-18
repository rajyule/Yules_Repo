package com.rmadegps.rmadeservermanagement.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("restapi/rsm/app")
public class rvms_device {



    rvms_myvehicle rvms_myvehicle_data_connection = new rvms_myvehicle();
    DataSource rvms_myvehicle = rvms_myvehicle_data_connection.setUp();
    
     Traccar_connection Traccar_connection = new Traccar_connection();
     DataSource Traccar = Traccar_connection.setUp();

     rvms_client_data_connection rvms_client_data_connection=new rvms_client_data_connection();
     DataSource rvms_client=rvms_client_data_connection.setUp();

     rvms_device_data_connection rvms_device_data_connection =new rvms_device_data_connection();
     DataSource rvms_device= rvms_device_data_connection.setUp();


    rmade_server_management rmade_server_management_data_connection = new rmade_server_management();
    DataSource rmade_server_management = rmade_server_management_data_connection.setUp();




    MongoClient mongo = new MongoClient( "localhost", 27017 );
    MongoDatabase database = mongo.getDatabase( "rmade_mongo_db" );

    public rvms_device() throws Exception { }



    @RequestMapping(path = "/mysql_active_check_api6", method = RequestMethod.GET)
    public String mysql_active_check_api6() throws JSONException, IOException, SQLException
    {

        Connection rvms_client_con = rvms_client.getConnection();
        Connection rvms_device_con = rvms_device.getConnection();
        Connection Traccar_con = Traccar.getConnection();

        Connection rmade_server_management_con = rmade_server_management.getConnection();


        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Statement Traccar_statement = Traccar_con.createStatement();
        Statement rvms_device_statement = rvms_device_con.createStatement();
        Statement rvms_client_statement = rvms_client_con.createStatement();
        Statement rmade_server_management_stmt = rmade_server_management_con.createStatement();

        String SQL_server_manage= "SELECT username FROM admin";

        String sql = "select * from testing";
        ResultSet resultSet = Traccar_statement.executeQuery(sql);
        if (resultSet.next())
        {

        }
        ResultSet resultSet1 = rvms_device_statement.executeQuery(sql);
        if (resultSet1.next())
        {

        }
        ResultSet resultSet2 = rvms_client_statement.executeQuery(sql);
        if (resultSet2.next())
        {

        }

        ResultSet resultSet3 = rmade_server_management_stmt.executeQuery(SQL_server_manage);
        if (resultSet3.next())
        {

        }

        System.out.println("mysql_active : api5 " + dateFormat.format(date));

        JSONObject json = new JSONObject();
        json.put( "result", "success" );
        rvms_client_con.close();
        rvms_device_con.close();
        Traccar_con.close();
        return String.valueOf((json));
    }


    @RequestMapping(path = "/rvms_device_add", method = RequestMethod.GET)
    public String rvms_device_add(@RequestParam String device_official_name, @RequestParam String name, @RequestParam String device_protocol_type, @RequestParam String imei_no, @RequestParam String rvms_status, @RequestParam String password) throws SQLException, IOException
    {
        int disabled;
        int rvms_status1;
        int device_id;

        Connection Traccar_con = Traccar.getConnection();
        Connection rvms_device_con = rvms_device.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement Traccar_stmt1 = Traccar_con.createStatement();

        Statement Traccar_stmt2 = Traccar_con.createStatement();
        Statement Traccar_stmt3 = Traccar_con.createStatement();
        Statement Traccar_stmt4 = Traccar_con.createStatement();

        Statement rvms_device_stmt1 = rvms_device_con.createStatement();


        String sql5 = "select * from admin where password='" + password + "'";


        ResultSet res = rmade_server_management_stmt1.executeQuery( sql5 );
        if (res.next())
        {

            if(rvms_status.equals("enable"))
            {
                disabled=0;
                rvms_status1=1;
            }
            else
            {
                disabled=1;
                rvms_status1=0;
            }


            /**************** Adding Device In Traccar through API ******************/


            JSONObject input = new JSONObject()
                    .put("name",name)
                    .put("uniqueId",imei_no);

            URL url = new URL("http://rmadeiot.com:333/api/devices");
            java.net.Authenticator.setDefault(new java.net.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication("admin", "aaXd66*4".toCharArray());
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


            /**************** END******************/



            String sql = "select id from devices where uniqueid='" + imei_no + "'";

            ResultSet res1 = Traccar_stmt1.executeQuery( sql );
            if (res1.next())
            {
                device_id = res1.getInt( "id" );

                //MySQL insert and update


                String sql_update_devices = "update devices set disabled=" + disabled + " where id='" + device_id + "'";
                Traccar_stmt2.executeUpdate( sql_update_devices );



                String sql1 = "INSERT INTO `rvms_device_data_process` (`device_id`, `imei_no`, `server_time`, `device_time`, `valid`, `latitude`, `longitude`, `altitude`, `speed`, `course`, `address`, `ign_ac`, `battery`,`distance`, `run`, `idle`, `stop`, `dist_multi_factor`, `speed_multi_factor`, `device_type`, `type`, `status`, `others1`, `others2`) VALUES ( " + device_id + ",  '" + imei_no + "' , '2018-01-18 15:06:49','2018-01-17 17:05:43', '1', '11.2183', '78.1614', '0', '0', '228', 'NA', 'false', 'false', '0', '0', '0', '6', '1.5', '1.5','" + device_protocol_type + "', 'dh', '"+rvms_status1+"','{\"sat\":14,\"ignition\":false,\"distance\":8.32,\"totalDistance\":9076943.76,\"motion\":true}', '{\"radioType\":\"gsm\",\"considerIp\":false,\"cellTowers\":[{\"cellId\":10115,\"locationAreaCode\":42104,\"mobileCountryCode\":404,\"mobileNetworkCode\":43}]}')";
                Traccar_stmt3.executeUpdate( sql1 );

                String sql2 = "INSERT INTO `rvms_device_data_single` (`device_id`, `imei_no`, `device_type`, `server_time`, `device_time`, `valid`, `latitude`, `longitude`, `altitude`, `speed`, `course`, `address`,`ign_ac`, `ac_value`, `battery`, `distance`, `odometer`, `run`, `idle`, `stop`, `status`, `others1`, `others2`, `attribute_1`, `attribute_2`) VALUES (" + device_id + ", '" + imei_no + "' ,'" + device_protocol_type + "','2018-01-18 16:06:56', '2018-01-17 17:05:43', '1', '11.2183', '78.1614', '0', '1', '228', 'NA', 'false', 'NA', 'false', '0', '27679.742', '0', '0', '0','"+rvms_status1+"','{\"sat\":14,\"ignition\":false,\"distance\":8.32,\"totalDistance\":9076943.76,\"motion\":true}', '{\"radioType\":\"gsm\",\"considerIp\":false,\"cellTowers\":[{\"cellId\":10115,\"locationAreaCode\":42104,\"mobileCountryCode\":404,\"mobileNetworkCode\":43}]}', 'device_offline', 'NA')";
                Traccar_stmt4.executeUpdate( sql2 );

                String sql3 = "CREATE TABLE rvts" + device_id + "(\n" +
                        "  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                        "  `device_id` int(11) NOT NULL DEFAULT '0',\n" +
                        "  `server_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                        "  `device_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,\n" +
                        "  `valid` varchar(11) DEFAULT 'NA',\n" +
                        "  `latitude` float DEFAULT '0',\n" +
                        "  `longitude` float NOT NULL DEFAULT '0',\n" +
                        "  `altitude` float NOT NULL DEFAULT '0',\n" +
                        "  `speed` float NOT NULL DEFAULT '0',\n" +
                        "  `course` float NOT NULL DEFAULT '0',\n" +
                        "  `address` varchar(512) DEFAULT 'NA',\n" +
                        "  `ign_ac` varchar(512) DEFAULT 'NA',\n" +
                        "  `battery` varchar(512) DEFAULT 'NA',\n" +
                        "  `distance` float NOT NULL DEFAULT '0',\n" +
                        "  `run` float NOT NULL DEFAULT '0',\n" +
                        "  `idle` float NOT NULL DEFAULT '0',\n" +
                        "  `stop` float NOT NULL DEFAULT '0',\n" +
                        "  `device_type` varchar(20) DEFAULT 'NA',\n" +
                        "  `type` varchar(10) DEFAULT 'NA',\n" +
                        "  `others1` varchar(4000) DEFAULT 'NA',\n" +
                        "  `others2` varchar(4000) DEFAULT 'NA',\n" +
                        "  `processed_status` int(11) DEFAULT '0',\n" +
                        "   INDEX `device_time` (`device_time`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n";
                rvms_device_stmt1.executeUpdate( sql3 );


                Date date = new Date();
                SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                String current_time = df.format( date.getTime() );



                // Mongo Insert

                for (int i = 1; i <= 3; i++)
                {

                    MongoCollection<Document> collection = database.getCollection( "rvms_device_data_single" + i );

                    Document document = new Document( "title", "MongoDB" )
                            .append( "device_id", String.valueOf( device_id ) )
                            .append( "server_time", current_time )
                            .append( "device_time", current_time )
                            .append( "valid", "0" )
                            .append( "longitude", "80.2707" )
                            .append( "latitude", "13.0827" )
                            .append( "altitude", "NA" )
                            .append( "speed", "0" )
                            .append( "course", "NA" )
                            .append( "address", "NA" )
                            .append( "ign_ac", "false" )
                            .append( "battery", "false" )
                            .append( "distance", "0" )
                            .append( "run", "0" )
                            .append( "idle", "0" )
                            .append( "stop", "0" )
                            .append( "status", rvms_status1 )
                            .append( "others1", "NA" )
                            .append( "others2", "NA" )
                            .append( "odometer", "NA" )
                            .append( "attribute_1", "NA" )
                            .append( "attribute_2", "NA" )
                            .append( "gps_signal", "OFF" )
                            .append( "device_official_name", device_official_name )
                            .append( "imei_no", imei_no )
                            .append( "immobilizer", "NA" )
                            .append( "parking_alert", "OFF" )
                            .append( "ac_value", "false" );
                    collection.insertOne( document );

                }
            }

            JSONObject json = new JSONObject();
            json.put( "result", "success" );


            Traccar_con.close();
            rvms_client__con.close();
            rmade_server_management_con.close();
            rvms_device_con.close();

            return String.valueOf( json );
        }
        else
        {

            JSONObject json = new JSONObject();
            json.put( "result", "failure" );

            Traccar_con.close();
            rvms_client__con.close();
            rmade_server_management_con.close();
            rvms_device_con.close();

            return String.valueOf( json );
        }
    }

    @RequestMapping(path = "/rvms_device_delete", method = RequestMethod.GET)
    public String rvms_device_delete(@RequestParam String imei_no, @RequestParam String password) throws SQLException, IOException
    {
        Connection rvms__myvehicle_con = rvms_myvehicle.getConnection();
        Connection rvms_device_con = rvms_device.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();


        Statement rmade_server_management_stmt2 = rmade_server_management_con.createStatement();
        Statement rvms_client_stmt5 = rvms_client__con.createStatement();

        Statement rvms__myvehicle_stmt4 = rvms__myvehicle_con.createStatement();
        Statement rvms__myvehicle_stmt5 = rvms__myvehicle_con.createStatement();
        Statement rvms__myvehicle_stmt6 = rvms__myvehicle_con.createStatement();

        Statement rvms_device_stmt2 = rvms_device_con.createStatement();

        String device_id="NA";

        String sql = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt2.executeQuery( sql );
        if (res.next()) {


            String sql_imei = "SELECT id FROM devices WHERE uniqueid = '"+imei_no+"'";
            ResultSet res_imei = rvms__myvehicle_stmt4.executeQuery(sql_imei);
            if (res_imei.next())
            {

                device_id=res_imei.getString("id");




                // MySQL Delete

                /**************** Adding Device In Traccar through API ******************/

                URL url = new URL("http://rmadeiot.com:444/api/devices/" + device_id);
                java.net.Authenticator.setDefault(new java.net.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("admin", "trcRVMS#@20".toCharArray());
                    }
                });
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStream os = conn.getOutputStream();
                os.flush();
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output = br.readLine();
                conn.disconnect();

                /**************** END******************/

                String sql6 = "delete from rvms_inventory where device_id='" + device_id + "'";
                rvms_client_stmt5.executeUpdate(sql6);

                String sql2 = "delete from rvms_device_data_process where device_id=" + device_id + "";
                rvms__myvehicle_stmt5.executeUpdate(sql2);

                String sql3 = "delete from rvms_device_data_single where device_id=" + device_id + "";
                rvms__myvehicle_stmt6.executeUpdate(sql3);

                String sql4 = "DROP TABLE rvmsd" + device_id + "";
                rvms_device_stmt2.executeUpdate(sql4);



                for (int i = 1; i <= 3; i++) {

                    MongoCollection<Document> collection = database.getCollection("rvms_device_data_single" + i);

                    collection.deleteOne(Filters.eq("device_id", device_id));

                }


                JSONObject json = new JSONObject();
                json.put("result", "success");
                rvms__myvehicle_con.close();
                rvms_device_con.close();
                rmade_server_management_con.close();
                rvms_client__con.close();

                return String.valueOf(json);
            }
        }


        JSONObject json = new JSONObject();
        json.put( "result", "failure" );
        rvms__myvehicle_con.close();
        rvms_device_con.close();
        rmade_server_management_con.close();
        rvms_client__con.close();
        return String.valueOf( json );



    }

    @RequestMapping(path = "/rvms_device_edit", method = RequestMethod.GET)
    public String rvms_device_edit(@RequestParam String device_iid,@RequestParam String device_mobile,@RequestParam String sim_type,@RequestParam String voice_monitor_status,@RequestParam String name, @RequestParam String imei_no, @RequestParam String rvms_status, @RequestParam String speed_multi_factor, @RequestParam String device_id,@RequestParam String device_type,@RequestParam String device_protocol_type, @RequestParam String password) throws SQLException, UnknownHostException
    {

        int disabled;
        int rvms_status1;

        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt3 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt2 = rvms_client__con.createStatement();

        Statement rvms_myvehicle_stmt7 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt8 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt9 = rvms_myvehicle_con.createStatement();

        String sql5 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt3.executeQuery( sql5 );
        if (res.next())
        {

            if(rvms_status.equals("enable"))
            {
                disabled=0;
                rvms_status1=1;
            }
            else
            {
                disabled=1;
                rvms_status1=0;
            }

            String sql6="update rvms_inventory set device_type='"+device_type+"', device_id='"+device_id+"',device_iid='"+device_iid+"',device_mobile='"+device_mobile+"',device_imei='"+imei_no+"',sim_type='"+sim_type+"',voice_monitor_status='"+voice_monitor_status+"',status='"+rvms_status1+"' where device_id='"+ device_id +"'";
            rvms_client_stmt2.executeUpdate( sql6 );

            String sql = "update devices set name='" + name + "',uniqueid='" + imei_no + "',disabled=" + disabled + ",speed_multi_factor='" + speed_multi_factor + "' where id='" + device_id + "'";
            rvms_myvehicle_stmt7.executeUpdate( sql );

            //MySQL Update

            String sql1 = "update `rvms_device_data_process` set imei_no='" + imei_no + "',device_type='"+device_protocol_type+"',status='"+rvms_status1+"' where device_id='" + device_id + "'";
            rvms_myvehicle_stmt8.executeUpdate( sql1 );

            String sql2 = "update `rvms_device_data_single` set imei_no='" + imei_no + "',device_type='"+device_protocol_type+"',status='"+rvms_status1+"' where device_id='" + device_id + "'";
            rvms_myvehicle_stmt9.executeUpdate( sql2 );



            // Mongo Update

            for (int i = 1; i <= 3; i++)
            {

                MongoCollection<Document> collection = database.getCollection( "rvms_device_data_single" + i );
                collection.updateOne( Filters.eq( "device_id", device_id ), Updates.set( "imei_no", imei_no ) );
                collection.updateOne( Filters.eq( "device_id", device_id ), Updates.set( "device_type", device_type ) );
                collection.updateOne( Filters.eq( "device_id", device_id ), Updates.set( "status", rvms_status1 ) );
            }

            JSONObject json = new JSONObject();
            json.put( "result", "success" );

            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );
        }
        else
        {

            JSONObject json = new JSONObject();
            json.put( "result", "failure" );


            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );

        }

    }

    @RequestMapping(path = "/rvms_device_allocation", method = RequestMethod.GET)
    public String rvms_device_allocation(@RequestParam String device_iid,@RequestParam String device_mobile,@RequestParam String sim_type,@RequestParam String status,@RequestParam String imei_no,@RequestParam String password) throws SQLException, UnknownHostException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt1 = rvms_client__con.createStatement();

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {

            String sql2="update rvms_inventory set device_iid = '"+device_iid+"',device_mobile='"+device_mobile+"',sim_type='"+sim_type+"',status='"+status+"' where device_imei='"+ imei_no +"'";
            rvms_client_stmt1.executeUpdate( sql2 );

            JSONObject json = new JSONObject();
            json.put( "result", "success" );
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );

        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );
        }


    }

    @RequestMapping(path = "/rvms_device_allocation_update_iid", method = RequestMethod.GET)
    public String rvms_device_allocation_update_iid(@RequestParam String old_device_iid,@RequestParam String new_device_iid,@RequestParam String password) throws SQLException, UnknownHostException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt1 = rvms_client__con.createStatement();
        Statement rvms_client_stmt2 = rvms_client__con.createStatement();
        Statement rvms_client_stmt3 = rvms_client__con.createStatement();
        Statement rvms_client_stmt4 = rvms_client__con.createStatement();


        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {

            String sql2 = "select id from rvms_inventory where device_iid='" + old_device_iid + "'";
            ResultSet res2 = rvms_client_stmt1.executeQuery( sql2 );
            if (res2.next())
            {
                String old_device_iid_id=res2.getString("id");

                String sql3 = "select id from rvms_inventory where device_iid='" + new_device_iid + "'";
                ResultSet res3 = rvms_client_stmt2.executeQuery( sql3 );
                if (res3.next())
                {
                    String new_device_iid_id=res3.getString("id");

                    String sql4="update rvms_inventory set device_iid = '"+new_device_iid+"' where id='"+ old_device_iid_id +"'";
                    rvms_client_stmt3.executeUpdate( sql4 );

                    String sql5="update rvms_inventory set device_iid = '"+old_device_iid+"' where id='"+ new_device_iid_id +"'";
                    rvms_client_stmt4.executeUpdate( sql5 );

                    JSONObject json = new JSONObject();
                    json.put( "result", "success" );
                    rvms_myvehicle_con.close();
                    rmade_server_management_con.close();
                    rvms_client__con.close();
                    return String.valueOf( json );
                }

            }

        }

        JSONObject json = new JSONObject();
        json.put( "result", "failure" );
        rvms_myvehicle_con.close();
        rmade_server_management_con.close();
        rvms_client__con.close();
        return String.valueOf( json );



    }

    @RequestMapping(path = "/rvms_device_allocation_update_status", method = RequestMethod.GET)
    public String rvms_device_allocation_update_status(@RequestParam String status,@RequestParam String imei_no,@RequestParam String password) throws SQLException, UnknownHostException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt1 = rvms_client__con.createStatement();

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {
            String  sql4="NA";
            if(status.equals("1"))
            {
                sql4="update rvms_inventory set status ='"+status+"', device_used_status=0 where device_imei='"+ imei_no +"'";
            }
            else
            {
                sql4="update rvms_inventory set status = 0 where device_imei='"+ imei_no +"'";
            }

            rvms_client_stmt1.executeUpdate( sql4 );

            JSONObject json = new JSONObject();
            json.put( "result", "success" );
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );


        }

        JSONObject json = new JSONObject();
        json.put( "result", "failure" );
        rvms_myvehicle_con.close();
        rmade_server_management_con.close();
        rvms_client__con.close();
        return String.valueOf( json );



    }

    @RequestMapping(path = "/rvms_device_allocation_update_sim", method = RequestMethod.GET)
    public String rvms_device_allocation_update_sim(@RequestParam String old_device_mob,@RequestParam String new_device_mob,@RequestParam String password) throws SQLException, UnknownHostException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt1 = rvms_client__con.createStatement();
        Statement rvms_client_stmt2 = rvms_client__con.createStatement();
        Statement rvms_client_stmt3 = rvms_client__con.createStatement();
        Statement rvms_client_stmt4 = rvms_client__con.createStatement();

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {

            String sql2 = "select id from rvms_inventory where device_mobile='" + old_device_mob + "'";
            ResultSet res2 = rvms_client_stmt1.executeQuery( sql2 );
            if (res2.next())
            {
                String old_device_mob_id=res2.getString("id");

                String sql3 = "select id from rvms_inventory where device_mobile='" + new_device_mob + "'";
                ResultSet res3 = rvms_client_stmt2.executeQuery( sql3 );
                if (res3.next())
                {
                    String new_device_mob_id=res3.getString("id");

                    String sql4="update rvms_inventory set device_mobile = '"+new_device_mob+"' where id='"+ old_device_mob_id +"'";
                    rvms_client_stmt3.executeUpdate( sql4 );

                    String sql5="update rvms_inventory set device_mobile = '"+old_device_mob+"' where id='"+ new_device_mob_id +"'";
                    rvms_client_stmt4.executeUpdate( sql5 );

                    JSONObject json = new JSONObject();
                    json.put( "result", "success" );
                    rvms_myvehicle_con.close();
                    rmade_server_management_con.close();
                    rvms_client__con.close();
                    return String.valueOf( json );
                }

            }

        }

        JSONObject json = new JSONObject();
        json.put( "result", "failure" );
        rvms_myvehicle_con.close();
        rmade_server_management_con.close();
        rvms_client__con.close();
        return String.valueOf( json );



    }

    @RequestMapping(path = "/rvms_device_time_update", method = RequestMethod.GET)
    public String rvms_device_time_update(@RequestParam String imei_no, @RequestParam String password) throws JSONException, ParseException, SQLException,IOException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement rvms_myvehicle_stmt1 = rvms_myvehicle_con.createStatement();

        String device_time="NA";


        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {
            Date date=new Date();
            SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            device_time = df.format( date );

            String sql_update ="UPDATE rvms_device_data_single SET device_time='"+device_time+"' WHERE imei_no='"+imei_no+"'";
            rvms_myvehicle_stmt1.executeUpdate( sql_update );

            JSONObject json = new JSONObject();
            json.put( "result", "success" );
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            return String.valueOf( json );

        }
        else
        {
            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            return String.valueOf( json );

        }

    }

    @RequestMapping(path = "/rvms_device_diagnostics_custom", method = RequestMethod.GET)
    public String rvms_device_diagnostics_custom(@RequestParam String type, @RequestParam String value, @RequestParam String password) throws JSONException, ParseException, SQLException,IOException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client_con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();
        Statement rvms_myvehicle_stmt3 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt4 = rvms_myvehicle_con.createStatement();

        Statement rvms_client_stmt1 = rvms_client_con.createStatement();
        Statement rvms_client_stmt3 = rvms_client_con.createStatement();
        Statement rvms_client_stmt4 = rvms_client_con.createStatement();

        BasicDBObject query = new BasicDBObject();

        String vehicle_current_status = "NA";

        JSONObject vehicle_list_json = new JSONObject();
        JSONArray array_vehicle = new JSONArray();

        String device_id="NA";
        String device_model_name="NA";
        String device_iid="NA";
        String device_mobile="NA";
        String sim_type="NA";
        String device_imei="NA";
        String status="NA";
        String client_id="NA";
        String client_name="NA";
        String client_mobile="NA";
        String vehicle_no="NA";
        String sql2="NA";


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 0); // to get previous year add -1
        Date Year = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String current_dt = df.format( Year );


        if(type.equals("imei_no"))
        {
            sql2 = "select device_id,device_model_name,device_iid,device_mobile,sim_type,device_imei from rvms_inventory where device_imei='" + value + "'";

        }
        else if(type.equals("iid"))
        {
            sql2 = "select device_id,device_model_name,device_iid,device_mobile,sim_type,device_imei from rvms_inventory where device_iid='" + value + "'";

        }
        else if(type.equals("vehicle_no"))
        {
            sql2 = "select rvms_inventory.device_id,rvms_inventory.device_model_name,rvms_inventory.device_iid,rvms_inventory.device_mobile,rvms_inventory.sim_type,rvms_inventory.device_imei from rvms_inventory,client_device where rvms_inventory.device_id=client_device.device_id AND client_device.vehicle_no='" + value + "'";

        }
        else if(type.equals("mobile"))
        {
            sql2 = "select rvms_inventory.device_id,rvms_inventory.device_model_name,rvms_inventory.device_iid,rvms_inventory.device_mobile,rvms_inventory.sim_type,rvms_inventory.device_imei from rvms_inventory,client_device where rvms_inventory.device_id=client_device.device_id AND client_device.device_mobile='" + value + "'";

        }

        ResultSet res2 = rvms_client_stmt1.executeQuery(sql2);
        if (res2.next())
        {
            device_id=res2.getString("device_id");
            device_model_name=res2.getString("device_model_name");
            device_iid=res2.getString("device_iid");
            device_mobile=res2.getString("device_mobile");
            sim_type=res2.getString("sim_type");
            device_imei=res2.getString("device_imei");
        }


        String sql_client_device = "SELECT client_id,vehicle_no FROM client_device WHERE device_id='"+device_id+"'";
        ResultSet res_cd = rvms_client_stmt3.executeQuery(sql_client_device);
        if (res_cd.next())
        {
            client_id=res_cd.getString("client_id");
            vehicle_no=res_cd.getString("vehicle_no");

            String sql_client_data = "SELECT client_name,client_mobile FROM client_data WHERE client_id='"+client_id+"'";
            ResultSet res_c_d = rvms_client_stmt4.executeQuery(sql_client_data);
            if (res_c_d.next())
            {
                client_name=res_c_d.getString("client_name");
                client_mobile=res_c_d.getString("client_mobile");

            }

        }



        String sql_status="SELECT attribute_1 FROM rvms_device_data_single WHERE device_id='"+device_id+"'";
        ResultSet res_status = rvms_myvehicle_stmt3.executeQuery(sql_status);
        if (res_status.next())
        {
            status=res_status.getString("attribute_1");
        }

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {
            query.put("device_id", device_id);
            FindIterable<Document> mydatabaserecord2 = database.getCollection("rvms_device_data_single1").find(query);
            MongoCursor<Document> iterator2 = mydatabaserecord2.iterator();
            if (iterator2.hasNext())
            {
                Document doc2 = iterator2.next();
                String gps_signal = "NA";
                String device_time = doc2.getString("device_time");
                String run = doc2.getString("run");
                String idle = doc2.getString("idle");
                String stop = doc2.getString("stop");
                String odometer = doc2.getString("odometer");
                String address = doc2.getString("address");
                String battery = doc2.getString("battery");
                String speed = doc2.getString("speed");
                String latitude = doc2.getString("latitude");
                String longitude = doc2.getString("longitude");
                String Ignition = doc2.getString("ign_ac");
                String valid = doc2.getString("valid");
                float distance = Float.parseFloat(doc2.getString("distance"));
                String parking_alert = doc2.getString("parking_alert");


                String status_message="NA";

                String sql_position = "SELECT valid,servertime from positions where deviceid='"+device_id+"' ORDER BY servertime DESC LIMIT 1";
                ResultSet res_position = rvms_myvehicle_stmt4.executeQuery( sql_position );
                if (res_position.next())
                {

                    valid=res_position.getString("valid");
                    String servertime=res_position.getString("servertime");
                    if(valid.equals("1"))
                    {
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String current_date = format.format(date);
                        Date d1 = format.parse(current_date);
                        Date d2 = format.parse(servertime);
                        int process_calc = Math.abs((int) ((d2.getTime() - d1.getTime()) / 1000));

                        if (process_calc >= 300)
                        {
                            status_message="Offline waiting for network connection";
                        }
                        else
                        {
                            status_message="Online and working good";

                        }

                    }
                    else
                    {
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String current_date = format.format(date);
                        Date d1 = format.parse(current_date);
                        Date d2 = format.parse(servertime);
                        int process_calc = Math.abs((int) ((d2.getTime() - d1.getTime()) / 1000));

                        if (process_calc >= 300)
                        {
                            status_message="Offline and not connected to GPS";

                        }
                        else
                        {
                            status_message="Online but not connected to GPS. Ensure device is in open sky";

                        }
                    }

                }
                else
                {
                    status_message="Offline waiting for network connection";
                }


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date device_t = dateFormat.parse(device_time);
                java.util.Date utilDate1 = new java.util.Date(device_t.getTime());
                DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);
                String updated_device_time = dateFormat1.format(utilDate1);
                String updated_current_time = dateFormat1.format(Year);

                if (battery.equals("true")) {
                    battery = "Device Connected";
                } else {
                    battery = "Device Disconnected";
                }


                if (Ignition.equals("true") && Float.parseFloat(speed) >= 2) {
                    Ignition = "ON";
                    vehicle_current_status = "running";
                } else if (Ignition.equals("true") && Float.parseFloat(speed) < 2) {
                    Ignition = "ON";
                    vehicle_current_status = "idle";
                } else if (Ignition.equals("false")) {
                    Ignition = "OFF";
                    vehicle_current_status = "stop";
                }


                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String current_date = format.format(date);
                Date d1 = format.parse(current_date);
                Date d2 = format.parse(device_time);
                int process_calc = Math.abs((int) ((d2.getTime() - d1.getTime()) / 1000));

                if (process_calc >= 3600) {
                    gps_signal = "OFF";
                } else {
                    gps_signal = "ON";
                }


                if (distance <= 1) {
                    vehicle_list_json.put("total_distance_24hrs", distance + " Km");

                } else {
                    vehicle_list_json.put("total_distance_24hrs", distance + " Kms");

                }


                vehicle_list_json.put("address", address.replace("?", ""));
                vehicle_list_json.put("device_dt", updated_device_time);
                vehicle_list_json.put("current_dt", updated_current_time);
                vehicle_list_json.put("battery", battery);
                vehicle_list_json.put("odometer", odometer + " Kms");
                vehicle_list_json.put("run", second_to_hours(Float.parseFloat(run)));
                vehicle_list_json.put("idle", second_to_hours(Float.parseFloat(idle)));
                vehicle_list_json.put("stop", second_to_hours(Float.parseFloat(stop)));
                vehicle_list_json.put("speed", speed);
                vehicle_list_json.put("latitude", latitude);
                vehicle_list_json.put("longitude", longitude);
                vehicle_list_json.put("ign", Ignition);
                vehicle_list_json.put("gps_signal", gps_signal);
                vehicle_list_json.put("vehicle_current_status", vehicle_current_status);
                vehicle_list_json.put("parking_alert", parking_alert);
                vehicle_list_json.put("device_model_name", device_model_name);
                vehicle_list_json.put("valid", valid);
                vehicle_list_json.put("client_id", client_id);
                vehicle_list_json.put("client_name", client_name);
                vehicle_list_json.put("client_mobile", client_mobile);
                vehicle_list_json.put("vehicle_no", vehicle_no);
                vehicle_list_json.put("device_iid", device_iid);
                vehicle_list_json.put("device_mobile", device_mobile);
                vehicle_list_json.put("sim_type", sim_type);
                vehicle_list_json.put("device_imei", device_imei);
                vehicle_list_json.put("status_message", status_message);

                array_vehicle.put(vehicle_list_json);

                JSONObject jsonlast = new JSONObject();
                jsonlast.put("result", array_vehicle);
                rvms_client_con.close();
                rmade_server_management_con.close();
                rvms_myvehicle_con.close();
                return String.valueOf(jsonlast);

            }

        }


        JSONObject jsonlast = new JSONObject();
        jsonlast.put("result", "failure");
        rvms_client_con.close();
        rmade_server_management_con.close();
        rvms_myvehicle_con.close();
        return String.valueOf(jsonlast);

    }

    @RequestMapping(path = "/rvms_device_diagnostics", method = RequestMethod.GET)
    public String rvms_device_diagnostics(@RequestParam String type, @RequestParam String imei_no, @RequestParam String command_send, @RequestParam String command_result,@RequestParam String command_name,@RequestParam String gcm_id,@RequestParam String password) throws JSONException, ParseException, SQLException,IOException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client_con = rvms_client.getConnection();

        Statement rmade_server_management_stmt1 = rmade_server_management_con.createStatement();

        Statement rvms_myvehicle_stmt1 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt2 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt3 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt4 = rvms_myvehicle_con.createStatement();

        Statement rvms_client_stmt1 = rvms_client_con.createStatement();
        Statement rvms_client_stmt2 = rvms_client_con.createStatement();
        Statement rvms_client_stmt3 = rvms_client_con.createStatement();
        Statement rvms_client_stmt4 = rvms_client_con.createStatement();

        BasicDBObject query = new BasicDBObject();

        String vehicle_current_status = "NA";

        JSONObject vehicle_list_json = new JSONObject();
        JSONArray array_vehicle = new JSONArray();

        String device_id="NA";
        String device_model_name="NA";
        String device_iid="NA";
        String device_mobile="NA";
        String sim_type="NA";
        String status="NA";
        String client_id="NA";
        String client_name="NA";
        String client_mobile="NA";
        String vehicle_no="NA";


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 0); // to get previous year add -1
        Date Year = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String current_dt = df.format( Year );


        String sql2 = "select device_id,device_model_name,device_iid,device_mobile,sim_type from rvms_inventory where device_imei='" + imei_no + "'";
        ResultSet res2 = rvms_client_stmt1.executeQuery(sql2);
        if (res2.next())
        {
            device_id=res2.getString("device_id");
            device_model_name=res2.getString("device_model_name");
            device_iid=res2.getString("device_iid");
            device_mobile=res2.getString("device_mobile");
            sim_type=res2.getString("sim_type");
        }


        String sql_client_device = "SELECT client_id,vehicle_no FROM client_device WHERE device_id='"+device_id+"'";
        ResultSet res_cd = rvms_client_stmt3.executeQuery(sql_client_device);
        if (res_cd.next())
        {
            client_id=res_cd.getString("client_id");
            vehicle_no=res_cd.getString("vehicle_no");

            String sql_client_data = "SELECT client_name,client_mobile FROM client_data WHERE client_id='"+client_id+"'";
            ResultSet res_c_d = rvms_client_stmt4.executeQuery(sql_client_data);
            if (res_c_d.next())
            {
                client_name=res_c_d.getString("client_name");
                client_mobile=res_c_d.getString("client_mobile");

            }

        }



        String sql_status="SELECT attribute_1 FROM rvms_device_data_single WHERE device_id='"+device_id+"'";
        ResultSet res_status = rvms_myvehicle_stmt3.executeQuery(sql_status);
        if (res_status.next())
        {
            status=res_status.getString("attribute_1");
        }

        String sql1 = "select * from admin where password='" + password + "'";
        ResultSet res1 = rmade_server_management_stmt1.executeQuery( sql1 );
        if (res1.next())
        {

            if(type.equals("location"))
            {

                query.put("device_id", device_id);

                FindIterable<Document> mydatabaserecord2 = database.getCollection("rvms_device_data_single1").find(query);
                MongoCursor<Document> iterator2 = mydatabaserecord2.iterator();
                if (iterator2.hasNext())
                {
                    Document doc2 = iterator2.next();
                    String gps_signal = "NA";
                    String device_time = doc2.getString("device_time");
                    String run = doc2.getString("run");
                    String idle = doc2.getString("idle");
                    String stop = doc2.getString("stop");
                    String odometer = doc2.getString("odometer");
                    String address = doc2.getString("address");
                    String battery = doc2.getString("battery");
                    String speed = doc2.getString("speed");
                    String latitude = doc2.getString("latitude");
                    String longitude = doc2.getString("longitude");
                    String Ignition = doc2.getString("ign_ac");
                    String valid = doc2.getString("valid");
                    float distance = Float.parseFloat(doc2.getString("distance"));
                    String parking_alert = doc2.getString("parking_alert");

                    String status_message="NA";

                    String sql_position = "SELECT valid,servertime from positions where deviceid='"+device_id+"' ORDER BY servertime DESC LIMIT 1";
                    ResultSet res_position = rvms_myvehicle_stmt4.executeQuery( sql_position );
                    if (res_position.next())
                    {

                        valid=res_position.getString("valid");
                        String servertime=res_position.getString("servertime");
                        if(valid.equals("1"))
                        {
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String current_date = format.format(date);
                            Date d1 = format.parse(current_date);
                            Date d2 = format.parse(servertime);
                            int process_calc = Math.abs((int) ((d2.getTime() - d1.getTime()) / 1000));

                            if (process_calc >= 300)
                            {
                                status_message="Offline waiting for network connection";
                            }
                            else
                            {
                                status_message="Online and working good";

                            }

                        }
                        else
                        {
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String current_date = format.format(date);
                            Date d1 = format.parse(current_date);
                            Date d2 = format.parse(servertime);
                            int process_calc = Math.abs((int) ((d2.getTime() - d1.getTime()) / 1000));

                            if (process_calc >= 300)
                            {
                                status_message="Offline and not connected to GPS";

                            }
                            else
                            {
                                status_message="Online but not connected to GPS. Ensure device is in open sky";

                            }
                        }

                    }
                    else
                    {
                        status_message="Offline waiting for network connection";
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date device_t = dateFormat.parse(device_time);
                    java.util.Date utilDate1 = new java.util.Date(device_t.getTime());
                    DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);
                    String updated_device_time = dateFormat1.format(utilDate1);
                    String updated_current_time = dateFormat1.format(Year);

                    if (battery.equals("true")) {
                        battery = "Device Connected";
                    } else {
                        battery = "Device Disconnected";
                    }


                    if (Ignition.equals("true") && Float.parseFloat(speed) >= 2) {
                        Ignition = "ON";
                        vehicle_current_status = "running";
                    } else if (Ignition.equals("true") && Float.parseFloat(speed) < 2) {
                        Ignition = "ON";
                        vehicle_current_status = "idle";
                    } else if (Ignition.equals("false")) {
                        Ignition = "OFF";
                        vehicle_current_status = "stop";
                    }


                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String current_date = format.format(date);
                    Date d1 = format.parse(current_date);
                    Date d2 = format.parse(device_time);
                    int process_calc = Math.abs((int) ((d2.getTime() - d1.getTime()) / 1000));

                    if (process_calc >= 3600) {
                        gps_signal = "OFF";
                    } else {
                        gps_signal = "ON";
                    }


                    if (distance <= 1) {
                        vehicle_list_json.put("total_distance_24hrs", distance + " Km");

                    } else {
                        vehicle_list_json.put("total_distance_24hrs", distance + " Kms");

                    }

                    vehicle_list_json.put("address", address.replace("?", ""));
                    vehicle_list_json.put("device_dt", updated_device_time);
                    vehicle_list_json.put("current_dt", updated_current_time);
                    vehicle_list_json.put("battery", battery);
                    vehicle_list_json.put("odometer", odometer + " Kms");
                    vehicle_list_json.put("run", second_to_hours(Float.parseFloat(run)));
                    vehicle_list_json.put("idle", second_to_hours(Float.parseFloat(idle)));
                    vehicle_list_json.put("stop", second_to_hours(Float.parseFloat(stop)));
                    vehicle_list_json.put("speed", speed);
                    vehicle_list_json.put("latitude", latitude);
                    vehicle_list_json.put("longitude", longitude);
                    vehicle_list_json.put("ign", Ignition);
                    vehicle_list_json.put("gps_signal", gps_signal);
                    vehicle_list_json.put("vehicle_current_status", vehicle_current_status);
                    vehicle_list_json.put("parking_alert", parking_alert);
                    vehicle_list_json.put("device_model_name", device_model_name);
                    vehicle_list_json.put("valid", valid);
                    vehicle_list_json.put("client_id", client_id);
                    vehicle_list_json.put("client_name", client_name);
                    vehicle_list_json.put("client_mobile", client_mobile);
                    vehicle_list_json.put("vehicle_no", vehicle_no);
                    vehicle_list_json.put("device_iid", device_iid);
                    vehicle_list_json.put("device_mobile", device_mobile);
                    vehicle_list_json.put("sim_type", sim_type);
                    vehicle_list_json.put("status_message", status_message);

                    array_vehicle.put(vehicle_list_json);

                    JSONObject jsonlast = new JSONObject();
                    jsonlast.put("result", array_vehicle);
                    rvms_client_con.close();
                    rmade_server_management_con.close();
                    rvms_myvehicle_con.close();
                    return String.valueOf(jsonlast);

                }

            }
            else if(type.equals("cmd"))
            {
                if (status.equals("device_online"))
                {
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
                                return new PasswordAuthentication("admin", "trcrvms#@20".toCharArray());
                            }
                        });
                        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        OutputStream os = conn.getOutputStream();
                        os.write(String.valueOf(input).getBytes());
                        os.flush();
                        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                        String output = br.readLine();
                        conn.disconnect();
                    }
                    catch (MalformedURLException e)
                    {
                        e.printStackTrace();
                    }


                    String sql3 = "SELECT id FROM events WHERE deviceid='" + device_id + "' ORDER BY id DESC LIMIT 1";
                    ResultSet res3 = rvms_myvehicle_stmt1.executeQuery(sql3);
                    if (res3.next()) {
                        String event_id = res3.getString("id");

                        String sql4 = "insert into device_config_ido (device_id,device_imei_no,event_id,command_name,command_send,command_result,notification_id,status,active_dt) VALUE ('" + device_id + "','" + imei_no + "','" + event_id + "','" + command_name + "','" + command_send + "','" + command_result + "','" + gcm_id + "','1','" + current_dt + "')";
                        rvms_client_stmt2.executeUpdate(sql4);


                    } else {
                        String sql4 = "SELECT id FROM events ORDER BY id DESC LIMIT 1";
                        ResultSet res4 = rvms_myvehicle_stmt2.executeQuery(sql4);
                        if (res4.next()) {
                            String event_id = res4.getString("id");

                            String sql5 = "insert into device_config_ido (device_id,device_imei_no,event_id,command_name,command_send,command_result,notification_id,status,active_dt) VALUE ('" + device_id + "','" + imei_no + "','" + event_id + "','" + command_name + "','" + command_send + "','" + command_result + "','" + gcm_id + "','1','" + current_dt + "')";
                            rvms_client_stmt2.executeUpdate(sql5);


                        }

                    }

                    JSONObject jsonlast = new JSONObject();
                    jsonlast.put("result", "success");
                    rvms_client_con.close();
                    rmade_server_management_con.close();
                    rvms_myvehicle_con.close();
                    return String.valueOf(jsonlast);

                }
                else
                {
                    JSONObject jsonlast = new JSONObject();
                    jsonlast.put("result", "offline");
                    rvms_client_con.close();
                    rmade_server_management_con.close();
                    rvms_myvehicle_con.close();
                    return String.valueOf(jsonlast);
                }

            }


        }


        JSONObject jsonlast = new JSONObject();
        jsonlast.put("result", "failure");
        rvms_client_con.close();
        rmade_server_management_con.close();
        rvms_myvehicle_con.close();
        return String.valueOf(jsonlast);

    }








/*    @RequestMapping(path = "/rvms_device_delete", method = RequestMethod.GET)
    public String rvms_device_delete(@RequestParam String device_id, @RequestParam String password) throws SQLException, IOException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rvms_device_con = rvms_device.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();


        Statement rmade_server_management_stmt2 = rmade_server_management_con.createStatement();
        Statement rvms_client_stmt5 = rvms_client__con.createStatement();

        Statement rvms_myvehicle_stmt4 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt5 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt6 = rvms_myvehicle_con.createStatement();

        Statement rvms_device_stmt2 = rvms_device_con.createStatement();


        Connection cql_con = cql.getConnection();
        Statement cql_stmt2 = cql_con.createStatement();
        String sql = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt2.executeQuery( sql );
        if (res.next())
        {
            // MySQL Delete

            *//**************** Adding Device In Traccar through API ******************//*

            URL url = new URL("http://rmadeiot.com:444/api/devices/"+device_id);
            java.net.Authenticator.setDefault(new java.net.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication("admin", "trcrvms#@20".toCharArray());
                }
            });
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = br.readLine();
            conn.disconnect();

            *//**************** END******************//*

            String sql6="delete from rvms_inventory where device_id='"+device_id+"'";
            rvms_client_stmt5.executeUpdate( sql6 );

            String sql2 = "delete from rvms_device_data_process where device_id=" + device_id + "";
            rvms_myvehicle_stmt5.executeUpdate( sql2 );

            String sql3 = "delete from rvms_device_data_single where device_id=" + device_id + "";
            rvms_myvehicle_stmt6.executeUpdate( sql3 );

            String sql4 = "DROP TABLE rvmsd"+device_id+"";
            rvms_device_stmt2.executeUpdate( sql4 );

            // CQL Delete

            String sql5 = "delete from rvms_device_data_single where device_id='" + device_id + "'";
            cql_stmt2.executeUpdate( sql5 );


            for (int i = 1; i <= 3; i++)
            {

                MongoCollection<Document> collection = database.getCollection( "rvms_device_data_single" + i );

                collection.deleteOne( Filters.eq( "device_id", device_id ) );

            }


            JSONObject json = new JSONObject();
            json.put( "result", "success" );
            cql_stmt2.close();
            cql_con.close();
            *//*rmade_server_management_stmt2.close();
            rvms_client_stmt5.close();
            rvms_myvehicle_stmt4.close();
            rvms_myvehicle_stmt5.close();
            rvms_myvehicle_stmt6.close();
            rvms_device_stmt2.close();*//*
            rvms_myvehicle_con.close();
            rvms_device_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();

            return String.valueOf( json );
        }
        else
        {

            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            cql_stmt2.close();
            cql_con.close();
           *//* rmade_server_management_stmt2.close();
            rvms_client_stmt5.close();
            rvms_myvehicle_stmt4.close();
            rvms_myvehicle_stmt5.close();
            rvms_myvehicle_stmt6.close();
            rvms_device_stmt2.close();*//*
            rvms_myvehicle_con.close();
            rvms_device_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );
        }
    }


    @RequestMapping(path = "/rvms_device_edit", method = RequestMethod.GET)
    public String rvms_device_edit(@RequestParam String device_iid,@RequestParam String device_mobile,@RequestParam String sim_type,@RequestParam String voice_monitor_status,@RequestParam String name, @RequestParam String imei_no, @RequestParam String rvms_status, @RequestParam String speed_multi_factor, @RequestParam String device_id,@RequestParam String device_type,@RequestParam String device_protocol_type, @RequestParam String password) throws SQLException, UnknownHostException
    {

        int disabled;
        int rvms_status1;

        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt3 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt2 = rvms_client__con.createStatement();

        Statement rvms_myvehicle_stmt7 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt8 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt9 = rvms_myvehicle_con.createStatement();

        Connection cql_con = cql.getConnection();
        Statement cql_stmt3 = cql_con.createStatement();
        String sql5 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt3.executeQuery( sql5 );
        if (res.next())
        {

            if(rvms_status.equals("enable"))
            {
                disabled=0;
                rvms_status1=1;
            }
            else
            {
                disabled=1;
                rvms_status1=0;
            }

            String sql6="update rvms_inventory set device_type='"+device_type+"', device_id='"+device_id+"',device_iid='"+device_iid+"',device_mobile='"+device_mobile+"',device_imei='"+imei_no+"',sim_type='"+sim_type+"',voice_monitor_status='"+voice_monitor_status+"',status='"+rvms_status1+"' where device_id='"+ device_id +"'";
            rvms_client_stmt2.executeUpdate( sql6 );

            String sql = "update devices set name='" + name + "',uniqueid='" + imei_no + "',disabled=" + disabled + ",speed_multi_factor='" + speed_multi_factor + "' where id='" + device_id + "'";
            rvms_myvehicle_stmt7.executeUpdate( sql );

            //MySQL Update

            String sql1 = "update `rvms_device_data_process` set imei_no='" + imei_no + "',device_type='"+device_protocol_type+"',status='"+rvms_status1+"' where device_id='" + device_id + "'";
            rvms_myvehicle_stmt8.executeUpdate( sql1 );

            String sql2 = "update `rvms_device_data_single` set imei_no='" + imei_no + "',device_type='"+device_protocol_type+"',status='"+rvms_status1+"' where device_id='" + device_id + "'";
            rvms_myvehicle_stmt9.executeUpdate( sql2 );


            //Cassandra Update

            String sql4 = "update rvms_device_data_single set imei_no='" + imei_no + "',device_type='"+device_type+"',status='"+rvms_status1+"' where device_id='" + device_id + "'";
            cql_stmt3.executeUpdate( sql4 );


            // Mongo Update

            for (int i = 1; i <= 3; i++)
            {

                MongoCollection<Document> collection = database.getCollection( "rvms_device_data_single" + i );
                collection.updateOne( Filters.eq( "device_id", device_id ), Updates.set( "imei_no", imei_no ) );
                collection.updateOne( Filters.eq( "device_id", device_id ), Updates.set( "device_type", device_type ) );
                collection.updateOne( Filters.eq( "device_id", device_id ), Updates.set( "status", rvms_status1 ) );
            }

            JSONObject json = new JSONObject();
            json.put( "result", "success" );
            cql_stmt3.close();
            cql_con.close();
          *//*  rmade_server_management_stmt3.close();
            rvms_client_stmt2.close();
            rvms_myvehicle_stmt7.close();
            rvms_myvehicle_stmt8.close();
            rvms_myvehicle_stmt9.close();*//*
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );
        }
        else
        {

            JSONObject json = new JSONObject();
            json.put( "result", "failure" );
            cql_stmt3.close();
            cql_con.close();
            *//*rmade_server_management_stmt3.close();
            rvms_client_stmt2.close();
            rvms_myvehicle_stmt7.close();
            rvms_myvehicle_stmt8.close();
            rvms_myvehicle_stmt9.close();*//*

            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( json );

        }

    }

    @RequestMapping(path = "/rvms_device_edit_list_view", method = RequestMethod.GET)
    public String rvms_device_edit_list_view(@RequestParam String password) throws SQLException, UnknownHostException
    {

        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();

        Statement rmade_server_management_stmt4 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt3 = rvms_client__con.createStatement();

        Statement rvms_myvehicle_stmt10 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt11 = rvms_myvehicle_con.createStatement();

        String sql5 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt4.executeQuery( sql5 );
        if (res.next())
        {
            String name = "NA";
            String id = "NA";
            String uniqueid = "NA";
            String disabled = "NA";
            String speed_multi_factor="NA";
            String device_iid="NA";
            String device_mobile="NA";
            String device_imei="NA";
            String sim_type="NA";
            String voice_monitor_status="NA";

            int device_id=0;
            String device_type="NA";

            JSONArray array = new JSONArray();

            String sql = "select * from rvms_inventory";
            ResultSet res1 = rvms_client_stmt3.executeQuery( sql );
            while (res1.next())
            {
                JSONObject json1 = new JSONObject();

                device_id = res1.getInt( "device_id" );
                device_type = res1.getString( "device_type" );
                device_iid = res1.getString( "device_iid" );
                device_mobile = res1.getString( "device_mobile" );
                device_imei = res1.getString( "device_imei" );
                sim_type = res1.getString( "sim_type" );
                voice_monitor_status = res1.getString( "voice_monitor_status" );


                String sql6 = "select * from devices where id="+device_id+"";
                ResultSet res2 = rvms_myvehicle_stmt10.executeQuery( sql6 );
                if(res2.next())
                {
                    name = res2.getString( "name" );
                    uniqueid = res2.getString( "uniqueid" );
                    disabled = res2.getString( "disabled" );
                    speed_multi_factor = res2.getString( "speed_multi_factor" );

                    json1.put( "device_id", device_id );
                    json1.put( "device_type", device_type );
                    json1.put( "device_iid", device_iid );
                    json1.put( "device_mobile", device_mobile );
                    json1.put( "device_imei", device_imei );
                    json1.put( "sim_type", sim_type );
                    json1.put( "voice_monitor_status", voice_monitor_status );

                    json1.put( "name", name );
                    json1.put( "uniqueid", uniqueid );
                    json1.put( "rvms_status", disabled );
                    json1.put( "speed_multi_factor", speed_multi_factor );


                    String sql7 = "select device_type from rvms_device_data_single where device_id="+device_id+"";
                    ResultSet res3 = rvms_myvehicle_stmt11.executeQuery( sql7 );
                    if(res3.next())
                    {
                        String device_protocol_type = res3.getString( "device_type" );
                        json1.put( "device_protocol_type", device_protocol_type );
                    }

                    array.put( json1 );

                }

            }

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", array );
          *//*  rmade_server_management_stmt4.close();
            rvms_client_stmt3.close();
            rvms_myvehicle_stmt10.close();
            rvms_myvehicle_stmt11.close();*//*
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();

            return String.valueOf( jsonlast );
        }

        else
        {

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", "failure" );
               *//* rmade_server_management_stmt4.close();
                rvms_client_stmt3.close();
                rvms_myvehicle_stmt10.close();
                rvms_myvehicle_stmt11.close();*//*
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( jsonlast );

        }

    }


    @RequestMapping(path = "/rvms_device_edit_view", method = RequestMethod.GET)
    public String rvms_device_edit_view(@RequestParam String device_id ,@RequestParam String password) throws SQLException, UnknownHostException
    {
        Connection rvms_myvehicle_con = rvms_myvehicle.getConnection();
        Connection rmade_server_management_con = rmade_server_management.getConnection();
        Connection rvms_client__con = rvms_client.getConnection();


        Statement rmade_server_management_stmt5 = rmade_server_management_con.createStatement();

        Statement rvms_client_stmt4 = rvms_client__con.createStatement();

        Statement rvms_myvehicle_stmt12 = rvms_myvehicle_con.createStatement();
        Statement rvms_myvehicle_stmt11 = rvms_myvehicle_con.createStatement();

        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();

        String sql5 = "select * from admin where password='" + password + "'";
        ResultSet res = rmade_server_management_stmt5.executeQuery( sql5 );
        if (res.next())
        {

            String sql6="select * from rvms_inventory where device_id='"+device_id+"'";
            ResultSet res1=rvms_client_stmt4.executeQuery(sql6);
            if(res1.next())
            {
                int device_id1=res1.getInt( "device_id" );
                String device_type=res1.getString( "device_type" );
                String device_iid = res1.getString( "device_iid" );
                String device_mobile = res1.getString( "device_mobile" );
                String device_imei = res1.getString( "device_imei" );
                String sim_type = res1.getString( "sim_type" );
                String voice_monitor_status = res1.getString( "voice_monitor_status" );

                String sql = "select * from devices where id='"+device_id+"'";
                ResultSet res2 = rvms_myvehicle_stmt12.executeQuery( sql );
                if(res2.next())
                {
                    String name = res2.getString( "name" );
                    String id = res2.getString( "id" );
                    String uniqueid = res2.getString( "uniqueid" );
                    String disabled = res2.getString( "disabled" );
                    String speed_multi_factor = res2.getString( "speed_multi_factor" );

                    json.put("device_id",device_id1);
                    json.put( "device_type", device_type );
                    json.put( "device_iid", device_iid );
                    json.put( "device_mobile", device_mobile );
                    json.put( "device_imei", device_imei );
                    json.put( "sim_type", sim_type );
                    json.put( "voice_monitor_status", voice_monitor_status );

                    json.put( "name", name );
                    json.put( "id", id );
                    json.put( "uniqueid", uniqueid );
                    json.put( "disabled", disabled );
                    json.put( "speed_multi_factor", speed_multi_factor );


                    String sql7 = "select device_type from rvms_device_data_single where device_id="+device_id+"";
                    ResultSet res3 = rvms_myvehicle_stmt11.executeQuery( sql7 );
                    if(res3.next())
                    {
                        String device_protocol_type = res3.getString( "device_type" );
                        json.put( "device_protocol_type", device_protocol_type );
                    }

                    array.put( json );

                }


            }

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", array );
            *//*rmade_server_management_stmt5.close();
            rvms_client_stmt4.close();
            rvms_myvehicle_stmt12.close();
            rvms_myvehicle_stmt11.close();*//*
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();

            return String.valueOf( jsonlast );
        }

        else
        {

            JSONObject jsonlast = new JSONObject();
            jsonlast.put( "result", "failure" );
          *//*  rmade_server_management_stmt5.close();
            rvms_client_stmt4.close();
            rvms_myvehicle_stmt12.close();
            rvms_myvehicle_stmt11.close();*//*
            rvms_myvehicle_con.close();
            rmade_server_management_con.close();
            rvms_client__con.close();
            return String.valueOf( jsonlast );

        }

    }*/
    public String second_to_hours(float second_input)
    {
        long secondsIn = (long) second_input;
        long dayCount = TimeUnit.SECONDS.toDays(secondsIn);
        long secondsCount = secondsIn - TimeUnit.DAYS.toSeconds(dayCount);
        long hourCount = TimeUnit.SECONDS.toHours(secondsCount);
        secondsCount -= TimeUnit.HOURS.toSeconds(hourCount);
        long minutesCount = TimeUnit.SECONDS.toMinutes(secondsCount);
        secondsCount -= TimeUnit.MINUTES.toSeconds(minutesCount);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.format("%02d:%02d:%02d %s", hourCount, minutesCount, secondsCount, (hourCount == 1) ? "hour" : "hours"));
        return String.valueOf( sb2 );
    }



}







