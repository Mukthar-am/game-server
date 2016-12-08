package com.adof.gameserver.tas;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

/**
 * Created by 15692 on 02/08/16.
 */
public class GZipClient {


    public static void main(String[] args) throws Exception {
        String url = "http://localhost:8080/springwebeg/track/gzip1";
        String gz = "/Users/15692/Data/git/myntra/qa/touchload/src/matouchload/configs/BatchPayload.json.gz";
        String txtfile = "/Users/15692/Data/git/myntra/qa/touchload/src/matouchload/configs/file.txt";
        File gzfile = new File(gz);
        File textfile = new File(txtfile);

//        HttpClient httpclient = new DefaultHttpClient();
//        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//
//        HttpPost httppost = new HttpPost("http://localhost:8080/springwebeg/track/gzip");

//
//        MultipartEntity mpEntity = new MultipartEntity();
//        ContentBody cbFile = new FileBody(file, "multipart/form-data");
//        mpEntity.addPart("file", cbFile);
//
//
//        httppost.setEntity(mpEntity);
//        System.out.println("executing request " + httppost.getRequestLine());
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity resEntity = response.getEntity();
//
//        System.out.println(response.getStatusLine());
//        if (resEntity != null) {
//            System.out.println(EntityUtils.toString(resEntity));
//        }
//        if (resEntity != null) {
//            resEntity.consumeContent();
//        }
//
//        httpclient.getConnectionManager().shutdown();

        HttpClient http = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        //post.addHeader(filepath, file.getAbsolutePath());
        post.addHeader("file", gzfile.getAbsolutePath());
        post.addHeader("Content-Encoding", "gzip");
        MultipartEntity multipart = new MultipartEntity();
        ContentBody fileContent = new FileBody(gzfile); //For tar.gz: "application/x-gzip"
        multipart.addPart("file", fileContent);


        post.setEntity(multipart);
        HttpResponse response = null;

        try {
            response = http.execute(post);
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(response.getStatusLine().toString());
        } catch (Exception ex){
            ex.printStackTrace();
        }


    }
}
