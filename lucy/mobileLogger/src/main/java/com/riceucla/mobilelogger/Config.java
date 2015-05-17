package com.riceucla.mobilelogger;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Configuration file for the core functionality of Lucy.
 * See the comments for each constant for configuration instructions. All fields are mandatory for the functionality of the application.
 *
 * @author Kevin Lin, Anant Tibrewal
 * @since 1/5/2014
 */

public class Config {
    String url = "http://ec2-52-5-43-17.compute-1.amazonaws.com/read";
    String textResult = "";

    public void startTask(){
        new myTask().execute();
    }
    // gets configuration setting data from server
    public class myTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {

            // username and password for server credential authentication
            String credentials = "j2bnJmjVNP2M" + ":" + "SEHdtpCD23Bamk2d";
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            DefaultHttpClient client = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Basic "+ credBase64);
            Log.w("doInBackground", "start doinbackground");
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();
                Log.d("Start try", "textUrl: " + url);
                BufferedReader bufferReader
                        = new BufferedReader(new InputStreamReader(content));

                String StringBuffer;
                String stringText = "";
                while ((StringBuffer = bufferReader.readLine()) != null) {
                    stringText += StringBuffer;
                }
                bufferReader.close();

                textResult = stringText;
                text_edit(textResult);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            }
            return null;
        }
    }

    // parses data from server and gets needed data
    public void text_edit(String new_text)
    {

        String[] string = new_text.split("vfdaiw");
        String t = "True";
        if (string[2].toLowerCase().contains(t.toLowerCase())){
            LOG_ACCELEROMETER = true;
        }
        else {
            LOG_ACCELEROMETER = false;
        }
        if (string[3].toLowerCase().contains(t.toLowerCase())){
            LOG_APP = true;
        }
        else{
            LOG_APP = false;
        }
        if (string[4].toLowerCase().contains(t.toLowerCase())){
            LOG_CALLS = true;
        }
        else{
            LOG_CALLS = false;
        }
        if (string[5].toLowerCase().contains(t.toLowerCase())){
            LOG_CELLULAR = true;
        }
        else{
            LOG_CELLULAR = false;
        }
        if (string[6].toLowerCase().contains(t.toLowerCase())){
            LOG_LOCATION = true;
        }
        else{
            LOG_LOCATION = false;
        }
        if (string[7].toLowerCase().contains(t.toLowerCase())){
            LOG_DEVICE = true;
        }
        else{
            LOG_DEVICE = false;
        }
        if (string[8].toLowerCase().contains(t.toLowerCase())){
            LOG_NETWORK = true;
        }
        else{
            LOG_NETWORK = false;
        }
        if (string[9].toLowerCase().contains(t.toLowerCase())){
            LOG_SCREEN_STATUS = true;
        }
        else{
            LOG_SCREEN_STATUS = false;
        }
        if (string[10].toLowerCase().contains(t.toLowerCase())){
            LOG_SMS = true;
        }
        else{
            LOG_SMS = false;
        }
        if (string[11].toLowerCase().contains(t.toLowerCase())){
            LOG_STEPS = true;
        }
        else{
            LOG_STEPS = false;
        }
        if (string[12].toLowerCase().contains(t.toLowerCase())){
            LOG_WEB = true;
        }
        else{
            LOG_WEB = false;
        }
        if (string[13].toLowerCase().contains(t.toLowerCase())){
            LOG_WIFI = true;
        }
        else{
            LOG_WIFI = false;
        }
        Log.d("text_edit", " " +string[2] + string[3] + LOG_ACCELEROMETER + LOG_APP);
    }

    // Fully qualified URL of the Flask upload script on the server.
    public static final String UPLOAD_BASE_URL = "http://ec2-52-5-43-17.compute-1.amazonaws.com/upload";
    // Interval between upload attempts, in seconds
    public static final int UPLOAD_INTERVAL = 1000;

    // Components to be logged. Set to true to log the component; false otherwise.
    public static  boolean LOG_CALLS = true;
    public static  boolean LOG_SMS = true;
    public static  boolean LOG_WEB = true;
    public static  boolean LOG_LOCATION = true;
    public static  boolean LOG_APP = true;
    public static  boolean LOG_WIFI = true;
    public static  boolean LOG_CELLULAR = true;
    public static  boolean LOG_ACCELEROMETER = true;
    public static  boolean LOG_DEVICE = true;
    public static  boolean LOG_NETWORK = true;
    public static  boolean LOG_SCREEN_STATUS = true;
    public static  boolean LOG_STEPS = true;
    public static  int NOTIFICATION_HOUR = 13;
    public static  int NOTIFICATION_MINUTE = 02;
}
