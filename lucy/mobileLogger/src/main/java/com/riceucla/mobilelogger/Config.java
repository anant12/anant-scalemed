package com.riceucla.mobilelogger;

import android.os.AsyncTask;

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


    public class MyTask extends AsyncTask<Void, Void, Void>{

        String textResult;
        String url = new String("http://ec2-54-84-183-117.compute-1.amazonaws.com/read");

        @Override
        protected Void doInBackground(Void... params) {

            URL textUrl;

            try {
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
            /*
            try {
                textUrl = new URL(url);

                BufferedReader bufferReader
                        = new BufferedReader(new InputStreamReader(textUrl.openStream()));


                String StringBuffer;
                String stringText = "";
                while ((StringBuffer = bufferReader.readLine()) != null) {
                    stringText += StringBuffer;
                }
                bufferReader.close();

                textResult = stringText;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            }
            */
            return null;

        }


    /*DefaultHttpClient httpclient = new DefaultHttpClient();
    HttpGet httppost = new HttpGet("http://ec2-54-152-129-219.compute-1.amazonaws.com/read");
    HttpResponse response = httpclient.execute(httppost);
    try {
        String settings_url = "http://ec2-54-152-129-219.compute-1.amazonaws.com/read";
        BufferedReader in = new BufferedReader(new InputStreamReader(settings_url.openStream()));
*/
    }
    // Fully qualified URL of the Flask upload script on the server.
    public static final String UPLOAD_BASE_URL = "http://ec2-54-85-147-87.compute-1.amazonaws.com/upload";
    // Interval between upload attempts, in seconds
    public static final int UPLOAD_INTERVAL = 1000;

    // Components to be logged. Set to true to log the component; false otherwise.
    public static final boolean LOG_CALLS = true;
    public static final boolean LOG_SMS = true;
    public static final boolean LOG_WEB = true;
    public static final boolean LOG_LOCATION = true;
    public static final boolean LOG_APP = true;
    public static final boolean LOG_WIFI = true;
    public static final boolean LOG_CELLULAR = true;
    public static final boolean LOG_ACCELEROMETER = true;
    public static final boolean LOG_DEVICE = true;
    public static final boolean LOG_NETWORK = true;
    public static final boolean LOG_SCREEN_STATUS = true;
    public static final boolean LOG_STEPS = true;
}
