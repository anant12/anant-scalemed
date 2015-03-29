package com.riceucla.mobilelogger;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Configuration file for the core functionality of Lucy.
 * See the comments for each constant for configuration instructions. All fields are mandatory for the functionality of the application.
 *
 * @author Kevin Lin, Rice University
 * @since 1/5/2014
 */

/*
public class MyAsyncTask extends AsyncTask<Void, Void, Void>{
    @Override
    protected Void doInBackground(Void... arg0){

        String url = null;
        try {
            url = new String("http://ec2-54-84-183-117.compute-1.amazonaws.com/submit");

            BufferedReader bis = new BufferedReader(new InputStreamReader(url.openStream()));
        }
    }
}
*/
public class Config {
    String url = "http://ec2-52-4-118-193.compute-1.amazonaws.com/read";
    String textResult = "";

    public void startTask(){
        new myTask().execute();
    }

    public class myTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
        /*try {
            // Create a URL for the desired page
            URL url = new URL("mysite.com/thefile.txt");

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        */
            URL textUrl;
            Log.w("doInBackground", "start doinbackground");
            try {
                textUrl = new URL(url);
                Log.d("Start try", "textUrl: " + url);
                BufferedReader bufferReader
                        = new BufferedReader(new InputStreamReader(textUrl.openStream()));


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
    public void text_edit(String new_text)
    {

        String[] string = new_text.split("vfdaiw");
        if (string[2] == "True"){
            LOG_ACCELEROMETER = true;
        }
        else {
            LOG_ACCELEROMETER = false;
        }
        if (string[3] == "True"){
            LOG_APP = true;
        }
        else{
            LOG_APP = false;
        }
        if (string[4] == "True"){
            LOG_CALLS = true;
        }
        else{
            LOG_CALLS = false;
        }
        if (string[5] == "True"){
            LOG_CELLULAR = true;
        }
        else{
            LOG_CELLULAR = false;
        }
        if (string[6] == "True"){
            LOG_LOCATION = true;
        }
        else{
            LOG_LOCATION = false;
        }
        if (string[7] == "True"){
            LOG_DEVICE = true;
        }
        else{
            LOG_DEVICE = false;
        }
        if (string[8] == "True"){
            LOG_NETWORK = true;
        }
        else{
            LOG_NETWORK = false;
        }
        if (string[9] == "True"){
            LOG_SCREEN_STATUS = true;
        }
        else{
            LOG_SCREEN_STATUS = false;
        }
        if (string[10] == "True"){
            LOG_SMS = true;
        }
        else{
            LOG_SMS = false;
        }
        if (string[11] == "True"){
            LOG_STEPS = true;
        }
        else{
            LOG_STEPS = false;
        }
        if (string[12] == "True"){
            LOG_WEB = true;
        }
        else{
            LOG_WEB = false;
        }
        if (string[13] == "True"){
            LOG_WIFI = true;
        }
        else{
            LOG_WIFI = false;
        }
        Log.d("text_edit", " " +LOG_WIFI);
    }


    /*DefaultHttpClient httpclient = new DefaultHttpClient();
    HttpGet httppost = new HttpGet("http://ec2-54-152-129-219.compute-1.amazonaws.com/read");
    HttpResponse response = httpclient.execute(httppost);
    try {
        String settings_url = "http://ec2-54-152-129-219.compute-1.amazonaws.com/read";
        BufferedReader in = new BufferedReader(new InputStreamReader(settings_url.openStream()));
*/

    // Fully qualified URL of the Flask upload script on the server.
    public static final String UPLOAD_BASE_URL = "http://ec2-54-85-147-87.compute-1.amazonaws.com/upload";
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
    public static  int NOTIFICATION_HOUR = 15;
    public static  int NOTIFICATION_MINUTE = 47;
}
