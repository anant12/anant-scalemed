package edu.rice.moodreminder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Configuration file for the core functionality of Mood Reminder. We assume this application works within the framework of Lucy.
 * See the comments for each constant for configuration instructions. All fields are mandatory for the functionality of the application.
 *
 * @author Kevin Lin, Rice University
 * @since 12/12/2014
 */
public class Config {

    String textResult = "";
    String url = "http://ec2-52-5-43-17.compute-1.amazonaws.com/readcopy";

    public void startTask(){
        new myTask().execute();
    }
    public class myTask extends AsyncTask<Void, Void, Void> {
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
            String credentials = "j2bnJmjVNP2M" + ":" + "SEHdtpCD23Bamk2d";
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            DefaultHttpClient client = new DefaultHttpClient();


            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Basic "+ credBase64);

            URL textUrl;
            Log.w("doInBackground", "start doinbackground");
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                textUrl = new URL(url);
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
                Log.w("textResult", ""+textResult);
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

        questions = new String[]{string[0], string[1]};
        label1 = string[14];
        label2 = string[15];
        label3 = string[16];
        label4 = string[17];
        labels[0] = label1;
        labels[1] = label2;
        labels[2] = label3;
        labels[3] = label4;
    }

    // Fully qualified URL of the Flask upload script on the server.
    public static final String UPLOAD_BASE_URL = "http://ec2-52-5-43-17.compute-1.amazonaws.com/upload";

    // Hour and minute representing the time at which the notification should be generated. Important: 24-hour format (i.e., 8 PM is hour 20 and minute 0)
    public static final int NOTIFICATION_HOUR = 14;
    public static final int NOTIFICATION_MINUTE = 14;

    // Title and message of the notification.
    public static final String NOTIFICATION_TITLE = "Mood and Activity Reminder";
    public static final String NOTIFICATION_MESSAGE = "How are you doing today?";

    // Name of the SQL table storing this data. This must match the name of the table on the server!
    public static final String TABLE_NAME = "mood";

    // String representations of the names of the parameters to be logged. Each parameter is allowed a 0-100 scale rating in the user interface.
    // These will be columns in the table TABLE_NAME above.
    public static String[] parameters = {"mood", "activity"};

    public static String[] questions = {"mood", "activity"};

    public static String[] labels = {"tired", "awake", "sad", "happy"};

    public static String label1;
    public static String label2;
    public static String label3;
    public static String label4;
}
