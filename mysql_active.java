package com.rmadegps.rmadeservermanagement.controller;


import javax.net.ssl.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;

public class mysql_active
{
    public void mysql_active() throws Exception
    {
        String url_array[] = new String[5];


        url_array[0] = "https://rmadeiot.com:6005/restapi/rsm/app/mysql_active_check_api2";
        url_array[1] = "https://rmadeiot.com:6005/restapi/rsm/app/mysql_active_check_api3";
        url_array[2] = "https://rmadeiot.com:6005/restapi/rsm/app/mysql_active_check_api4";
        url_array[3] = "https://rmadeiot.com:6005/restapi/rsm/app/mysql_active_check_api5";
        url_array[4] = "https://rmadeiot.com:6005/restapi/rsm/app/mysql_active_check_api6";

        for(int i=0;i<url_array.length;i++)
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

            URL url = new URL(url_array[i]);
            URLConnection con = url.openConnection();
            Reader reader = new InputStreamReader(con.getInputStream());
            while (true) {
                int ch = reader.read();
                if (ch == -1) {
                    break;
                }
            }
        }

    }


}
