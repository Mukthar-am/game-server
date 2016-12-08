package com.adof.gameserver;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by 15692 on 22/07/16.
 */
public class JsonClient {

    public static void main(String[] args) {
//        JsonClient.getJson();

        JsonClient.postman();
    }

    public static void postman() {
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost getRequest = new HttpPost("http://localhost:8080/springwebeg/track/events");
            getRequest.addHeader("accept", "application/json");

            StringEntity params =new StringEntity("{\"description\":\"Muks is here\",\"name\":\"mukthar\",\"age\":\"20\", \"city\":\"Ballari\"} ");
            getRequest.setEntity(params);

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("======" + response.getEntity().getContent().toString());
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            httpClient.getConnectionManager().shutdown();

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public static void postJson() {
        HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpPost request = new HttpPost("http://localhost:8080/springwebeg/track/postPerson");

            request.addHeader("content-type", "application/json");
            request.addHeader("Accept","application/json");

            StringEntity params =new StringEntity("details={\"name\":\"myname\",\"age\":\"20\"} ");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            System.out.println("Response: = " + response.getStatusLine());
            System.out.println("Response: = " + response.getEntity());

            // handle response here...
        }catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
//        JSONObject json = new JSONObject();
//        json.put("someKey", "someValue");
//
//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//
//        try {
//            HttpPost request = new HttpPost("http://localhost:8080/springwebeg/track/postPerson");
//            StringEntity params = new StringEntity(json.toString());
//            request.addHeader("content-type", "application/json");
//            request.setEntity(params);
//            CloseableHttpResponse response = httpClient.execute(request);
//
//            System.out.println(response.toString());
//
//        } catch (Exception ex) {
//            // handle exception here
//        } finally {
//            try {
//                httpClient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }


    public static void getJson() {
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet("http://localhost:8080/springwebeg/track/getPerson");
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            httpClient.getConnectionManager().shutdown();

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
