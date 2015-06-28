package edu.rice.moodreminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * @author Kevin Lin, Anant Tibrewal
 * @since 12/12/2014
 */
public class Config {

    String textResult = "";
    String url = "http://ec2-52-5-43-17.compute-1.amazonaws.com/read";

    // gets question and label data from server
    public class myTask extends AsyncTask<Void, Void, Void> {
        Context context;
        private Context mCtx;
        public myTask(Context context) {
            this.context = context.getApplicationContext();
            mCtx = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // username and password for server credential authentication
            String credentials = "j2bnJmjVNP2M" + ":" + "SEHdtpCD23Bamk2d";
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            DefaultHttpClient client = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", "Basic " + credBase64);


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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*
            MainActivity main = new MainActivity();
            MoodReminderActivity moodReminderActivity = new MoodReminderActivity();
            Intent intent = new Intent(context, MoodReminderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(new Intent(myTask.this, MoodReminderActivity.class));
            context.startActivity(intent);
            //startActivity(new Intent(MainActivity.this, MoodReminderActivity.class));
            */
            Intent intent = new Intent(mCtx, MoodReminderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mCtx.startActivity(intent);


            //MainActivity main = new MainActivity();
            //main.moodReminder(mCtx);
        }
    }

    // parses data from server and gets needed data
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
    // These will be columns in the table TABLE_NAME above. These must match the names of the columns on the server!
    public static String[] parameters = {"moodlevel", "activitylevel"};

    public static String[] questions = {"mood", "activity"};

    public static String[] labels = {"tired", "awake", "sad", "happy"};

    public static String label1;
    public static String label2;
    public static String label3;
    public static String label4;
}
