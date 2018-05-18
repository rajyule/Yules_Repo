package com.rmadegps.rmadeservermanagement.controller;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import javax.sql.DataSource;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class active_listeners
{
    rpt_client_data_connection rpt_client_data_connection   = new rpt_client_data_connection();
    DataSource rpt_client = rpt_client_data_connection.setUp();

    rpt_myvehicle rpt_myvehicle_data_connection = new rpt_myvehicle();
    DataSource rpt_myvehicle = rpt_myvehicle_data_connection.setUp();

    Connection rpt_client_con = rpt_client.getConnection();
    Connection my_vehicle_con = rpt_myvehicle.getConnection();

    Traccar_connection Traccar_connection = new Traccar_connection();
    DataSource Traccar = Traccar_connection.setUp();

    rvms_client_data_connection rvms_client_data_connection=new rvms_client_data_connection();
    DataSource rvms_client=rvms_client_data_connection.setUp();


    Connection rvms_client_con = rvms_client.getConnection();
    Connection traccar_con = Traccar.getConnection();



    Statement rpt_client_stmt1 = rpt_client_con.createStatement();
    Statement rpt_client_stmt2 = rpt_client_con.createStatement();
    Statement rpt_client_stmt3 = rpt_client_con.createStatement();
    Statement rpt_client_stmt4 = rpt_client_con.createStatement();
    Statement rpt_client_stmt5 = rpt_client_con.createStatement();
    Statement rpt_client_stmt6 = rpt_client_con.createStatement();
    Statement rpt_client_statement = rpt_client_con.createStatement();

    Statement my_vehicle_stmt1 = my_vehicle_con.createStatement();
    Statement my_vehicle_stmt2 = my_vehicle_con.createStatement();
    Statement rpt_vehice_statement = my_vehicle_con.createStatement();


    Statement rvms_client_stmt1=rvms_client_con.createStatement();
    Statement rvms_client_stmt2=rvms_client_con.createStatement();
    Statement rvms_client_stmt3=rvms_client_con.createStatement();
    Statement rvms_client_stmt4=rvms_client_con.createStatement();
    Statement rvms_client_stmt5=rvms_client_con.createStatement();
    Statement rvms_client_stmt6=rvms_client_con.createStatement();

    Statement traccar_stmt1=traccar_con.createStatement();
    Statement traccar_stmt2=traccar_con.createStatement();
    Statement traccar_stmt3=traccar_con.createStatement();


    public active_listeners() throws Exception { }


    public void ido_command_result_rvms()
    {

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {

                String device_id = "NA";
                String event_id = "NA";
                String command_result = "NA";
                String notification_id = "NA";
                String attributes = "NA";
                String result = "NA";
                String active_dt = "NA";
                String alert_message = "NA";
                String new_event_id = "NA";
                String command_name = null;
                String id = null;
                try
                {

                    String sql1 = "select * from device_config_ido WHERE status=1";
                    ResultSet res = rvms_client_stmt1.executeQuery(sql1);
                    while (res.next())
                    {


                        device_id = res.getString("device_id");
                        event_id = res.getString("event_id");
                        command_result = res.getString("command_result");
                        notification_id = res.getString("notification_id");
                        active_dt = res.getString("active_dt");
                        command_name = res.getString("command_name");
                        id = res.getString("id");

                        String sql2 = "SELECT attributes,id FROM events WHERE deviceid = '" + device_id + "'AND id >" + event_id;
                        ResultSet res2 = traccar_stmt1.executeQuery(sql2);
                        if (res2.next()) {
                            attributes = res2.getString("attributes");
                            new_event_id = res2.getString("id");

                            try {
                                JSONObject cmdrst = new JSONObject(attributes);
                                result = cmdrst.getString("result");

                                if (result.contains(command_result))
                                {
                                    alert_message = "success";

                                }
                                else if(result.contains("Already"))
                                {
                                    alert_message = "already";
                                }
                                else
                                {
                                    alert_message = "failure";
                                }

                                String web_url="https://www.rmadeindia.com/restapi/ido/ido_gcm_push.php?type="+command_name+"&gcm_id="+notification_id+"&status="+alert_message;

                                try
                                {
                                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                            return null;
                                        }

                                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                        }

                                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                        }
                                    }
                                    };

                                    // Install the all-trusting trust manager
                                    SSLContext sc = SSLContext.getInstance("SSL");
                                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                                    // Create all-trusting host name verifier
                                    HostnameVerifier allHostsValid = new HostnameVerifier() {
                                        public boolean verify(String hostname, SSLSession session) {
                                            return true;
                                        }
                                    };

                                    // Install the all-trusting host verifier
                                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                                    URL url = new URL(web_url);
                                    URLConnection con = url.openConnection();
                                    Reader reader = new InputStreamReader(con.getInputStream());
                                    while (true) {
                                        int ch = reader.read();
                                        if (ch == -1) {
                                            break;
                                        }
                                    }

                                    String sql5="DELETE FROM device_config_ido WHERE `id`="+id;
                                    rvms_client_stmt2.executeUpdate( sql5 );

                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                            }
                            catch (JSONException ex)
                            {

                            }
                        }


                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, 0); // to get previous year add -1
                        Date nextYear = cal.getTime();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d1 = format.parse(active_dt);

                        int process_calc = Math.abs((int) ((d1.getTime() - nextYear.getTime()) / 1000));




                        if(process_calc >= 300)
                        {
                            String sql4="DELETE FROM device_config_ido WHERE `id`="+id;
                            rvms_client_stmt3.executeUpdate( sql4 );

                        }


                    }

                }
                catch (Exception e)
                {
                    System.out.println( "SQL Exception");
                    e.printStackTrace();
                }



            }
        }, 0, 15, TimeUnit.SECONDS);


    }

    public void ido_command_result_rpt()
    {

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {

                String device_id = "NA";
                String event_id = "NA";
                String command_result = "NA";
                String notification_id = "NA";
                String attributes = "NA";
                String result = "NA";
                String active_dt = "NA";
                String alert_message = "NA";
                String new_event_id = "NA";
                String command_name = null;
                String id = null;
                try
                {

                    String sql1 = "select * from device_config_ido WHERE status=1";
                    ResultSet res = rpt_client_stmt4.executeQuery(sql1);
                    while (res.next())
                    {


                        device_id = res.getString("device_id");
                        event_id = res.getString("event_id");
                        command_result = res.getString("command_result");
                        notification_id = res.getString("notification_id");
                        active_dt = res.getString("active_dt");
                        command_name = res.getString("command_name");
                        id = res.getString("id");

                        String sql2 = "SELECT attributes,id FROM events WHERE deviceid = '" + device_id + "'AND id >" + event_id;
                        ResultSet res2 = my_vehicle_stmt2.executeQuery(sql2);
                        if (res2.next()) {
                            attributes = res2.getString("attributes");
                            new_event_id = res2.getString("id");

                            try {
                                JSONObject cmdrst = new JSONObject(attributes);
                                result = cmdrst.getString("result");

                                if (result.contains(command_result))
                                {
                                    alert_message = "success";

                                }
                                else if(result.contains("Already"))
                                {
                                    alert_message = "already";
                                }
                                else
                                {
                                    alert_message = "failure";
                                }

                                String web_url="https://www.rmadeindia.com/restapi/ido/ido_gcm_push.php?type="+command_name+"&gcm_id="+notification_id+"&status="+alert_message;

                                try
                                {
                                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                            return null;
                                        }

                                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                        }

                                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                        }
                                    }
                                    };

                                    // Install the all-trusting trust manager
                                    SSLContext sc = SSLContext.getInstance("SSL");
                                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                                    // Create all-trusting host name verifier
                                    HostnameVerifier allHostsValid = new HostnameVerifier() {
                                        public boolean verify(String hostname, SSLSession session) {
                                            return true;
                                        }
                                    };

                                    // Install the all-trusting host verifier
                                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                                    URL url = new URL(web_url);
                                    URLConnection con = url.openConnection();
                                    Reader reader = new InputStreamReader(con.getInputStream());
                                    while (true) {
                                        int ch = reader.read();
                                        if (ch == -1) {
                                            break;
                                        }
                                    }

                                    String sql5="DELETE FROM device_config_ido WHERE `id`="+id;
                                    rpt_client_stmt5.executeUpdate( sql5 );

                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                            }
                            catch (JSONException ex)
                            {

                            }
                        }


                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, 0); // to get previous year add -1
                        Date nextYear = cal.getTime();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d1 = format.parse(active_dt);

                        int process_calc = Math.abs((int) ((d1.getTime() - nextYear.getTime()) / 1000));




                        if(process_calc >= 300)
                        {
                            String sql4="DELETE FROM device_config_ido WHERE `id`="+id;
                            rpt_client_stmt6.executeUpdate( sql4 );

                        }


                    }

                }
                catch (Exception e)
                {
                    System.out.println( "SQL Exception");
                    e.printStackTrace();
                }



            }
        }, 0, 15, TimeUnit.SECONDS);


    }

    public void device_command_result_rpt()
    {

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {

                String device_id = "NA";
                String device_imei_no = "NA";
                String event_id = "NA";
                String command_send = "NA";
                String command_result = "NA";
                String notification_id = "NA";
                String attributes = "NA";
                String result = "NA";
                String active_dt = "NA";
                String alert_message = "NA";
                String new_event_id = "NA";
                String jsonResponse = null;
                try
                {

                    String sql1 = "select * from device_config WHERE status=1";
                    ResultSet res = rpt_client_stmt1.executeQuery(sql1);
                    if (res.next()) {


                        device_id = res.getString("device_id");
                        event_id = res.getString("event_id");
                        command_send = res.getString("command_send");
                        command_result = res.getString("command_result");
                        notification_id = res.getString("notification_id");
                        active_dt = res.getString("active_dt");

                        String sql2 = "SELECT attributes,id FROM events WHERE deviceid = '" + device_id + "'AND id >" + event_id;
                        ResultSet res2 = my_vehicle_stmt1.executeQuery(sql2);
                        if (res2.next()) {
                            attributes = res2.getString("attributes");
                            new_event_id = res2.getString("id");

                            try {
                                JSONObject cmdrst = new JSONObject(attributes);
                                result = cmdrst.getString("result");

                                if (result.contains(command_result))
                                {
                                    alert_message = "success";

                                }
                                else
                                {
                                    alert_message = "failure";
                                }


                                try {


                                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                    con.setUseCaches(false);
                                    con.setDoOutput(true);
                                    con.setDoInput(true);

                                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                    con.setRequestProperty("Authorization", "Basic Y2ZmNTkyMGItZTJlZS00OTNhLWJlNTItZmZkNzNkMDg4NThh");
                                    con.setRequestMethod("POST");


                                    String strJsonBody = "{"
                                            + "\"app_id\": \"21aa11cf-0d93-423e-9e8a-e6b3f8e1c635\","
                                            + "\"include_player_ids\": [\"" + notification_id + "\"],"
                                            + "\"data\": {\"result\": \"" + alert_message + "\"},"
                                            + "\"contents\": {\"en\": \"" + alert_message + "\"},"
                                            + "\"headings\": {\"en\": \"" + "Command Send: " + command_send + "  --  Command Result: " + command_result + "\"},"

                                            + "\"priority\": \"high\""
                                            + "}";


                                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                                    con.setFixedLengthStreamingMode(sendBytes.length);

                                    OutputStream outputStream = con.getOutputStream();
                                    outputStream.write(sendBytes);

                                    int httpResponse = con.getResponseCode();

                                    if (httpResponse >= HttpURLConnection.HTTP_OK
                                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                        scanner.close();
                                    } else {
                                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                        scanner.close();
                                    }

                                    String sql5="update device_config set event_id='"+new_event_id+"'";
                                    rpt_client_stmt2.executeUpdate( sql5 );

                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                            }
                            catch (JSONException ex)
                            {

                            }
                        }


                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, 0); // to get previous year add -1
                        Date nextYear = cal.getTime();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d1 = format.parse(active_dt);

                        int process_calc = Math.abs((int) ((d1.getTime() - nextYear.getTime()) / 1000));




                        if(process_calc >= 86400)
                        {
                            String sql4="update device_config set status=0,device_id=0";
                            rpt_client_stmt3.executeUpdate( sql4 );

                        }


                    }

                }
                catch (Exception e)
                {
                    System.out.println( "SQL Exception");
                    e.printStackTrace();
                }



            }
        }, 0, 30, TimeUnit.SECONDS);


    }

    public void device_command_result_rvms()
    {

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {

                String device_id = "NA";
                String device_imei_no = "NA";
                String event_id = "NA";
                String command_send = "NA";
                String command_result = "NA";
                String notification_id = "NA";
                String attributes = "NA";
                String result = "NA";
                String active_dt = "NA";
                String alert_message = "NA";
                String new_event_id = "NA";
                String jsonResponse = null;
                try
                {

                    String sql1 = "select * from device_config WHERE status=1";
                    ResultSet res = rvms_client_stmt4.executeQuery(sql1);
                    if (res.next()) {


                        device_id = res.getString("device_id");
                        event_id = res.getString("event_id");
                        command_send = res.getString("command_send");
                        command_result = res.getString("command_result");
                        notification_id = res.getString("notification_id");
                        active_dt = res.getString("active_dt");

                        String sql2 = "SELECT attributes,id FROM events WHERE deviceid = '" + device_id + "'AND id >" + event_id;
                        ResultSet res2 = traccar_stmt2.executeQuery(sql2);
                        if (res2.next()) {
                            attributes = res2.getString("attributes");
                            new_event_id = res2.getString("id");

                            try {
                                JSONObject cmdrst = new JSONObject(attributes);
                                result = cmdrst.getString("result");

                                if (result.contains(command_result))
                                {
                                    alert_message = "success";

                                }
                                else
                                {
                                    alert_message = "failure";
                                }


                                try {


                                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                                    con.setUseCaches(false);
                                    con.setDoOutput(true);
                                    con.setDoInput(true);

                                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                    con.setRequestProperty("Authorization", "Basic Y2ZmNTkyMGItZTJlZS00OTNhLWJlNTItZmZkNzNkMDg4NThh");
                                    con.setRequestMethod("POST");


                                    String strJsonBody = "{"
                                            + "\"app_id\": \"21aa11cf-0d93-423e-9e8a-e6b3f8e1c635\","
                                            + "\"include_player_ids\": [\"" + notification_id + "\"],"
                                            + "\"data\": {\"result\": \"" + alert_message + "\"},"
                                            + "\"contents\": {\"en\": \"" + alert_message + "\"},"
                                            + "\"headings\": {\"en\": \"" + "Command Send: " + command_send + "  --  Command Result: " + command_result + "\"},"

                                            + "\"priority\": \"high\""
                                            + "}";


                                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                                    con.setFixedLengthStreamingMode(sendBytes.length);

                                    OutputStream outputStream = con.getOutputStream();
                                    outputStream.write(sendBytes);

                                    int httpResponse = con.getResponseCode();

                                    if (httpResponse >= HttpURLConnection.HTTP_OK
                                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                        scanner.close();
                                    } else {
                                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                                        scanner.close();
                                    }

                                    String sql5="update device_config set event_id='"+new_event_id+"'";
                                    rvms_client_stmt5.executeUpdate( sql5 );

                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                            }
                            catch (JSONException ex)
                            {

                            }
                        }


                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, 0); // to get previous year add -1
                        Date nextYear = cal.getTime();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        Date d1 = format.parse(active_dt);

                        int process_calc = Math.abs((int) ((d1.getTime() - nextYear.getTime()) / 1000));




                        if(process_calc >= 86400)
                        {
                            String sql4="update device_config set status=0,device_id=0";
                            rpt_client_stmt3.executeUpdate( sql4 );

                        }


                    }

                }
                catch (Exception e)
                {
                    System.out.println( "SQL Exception");
                    e.printStackTrace();
                }



            }
        }, 0, 30, TimeUnit.SECONDS);


    }

    public void mysql_active() throws SQLException
    {
        String sql = "select * from testing";
        ResultSet res1 = rpt_vehice_statement.executeQuery(sql);
        if (res1.next())
        {

        }
        ResultSet res2 = rpt_client_statement.executeQuery(sql);
        if (res2.next())
        {

        }

        ResultSet res3 = rvms_client_stmt6.executeQuery(sql);
        if (res3.next())
        {

        }
        ResultSet res4 = traccar_stmt3.executeQuery(sql);
        if (res4.next())
        {

        }


    }


}


