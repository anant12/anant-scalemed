package edu.rice.moodreminder;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
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
public class Config{

    //protected void onHandleIntent(Intent intent){
     //   doInBackground();
    //}

    String textResult = "";
    String url = "http://ec2-52-4-23-108.compute-1.amazonaws.com/read";

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

    public static String NOTIFICATION_TITLE;
    public static String NOTIFICATION_MESSAGE;
    //public static String[] parameters;
    public void text_edit(String new_text)
    {
        NOTIFICATION_TITLE = new_text;
       NOTIFICATION_MESSAGE = new_text;
        questions = new String[]{new_text, new_text};
    }




    // Fully qualified URL of the Flask upload script on the server.
    public static final String UPLOAD_BASE_URL = "http://ec2-54-85-147-87.compute-1.amazonaws.com/upload";

    // Hour and minute representing the time at which the notification should be generated. Important: 24-hour format (i.e., 8 PM is hour 20 and minute 0)
    public static final int NOTIFICATION_HOUR = 13;
    public static final int NOTIFICATION_MINUTE = 42;

    // Title and message of the notification.
    //public static final String NOTIFICATION_TITLE = "Mood and Activity Reminder";
    //NOTIFICATION_TITLE = textResult;
    //public static final String NOTIFICATION_MESSAGE = "How are you doing today?";

    // Name of the SQL table storing this data. This must match the name of the table on the server!
    public static final String TABLE_NAME = "mood";

    // String representations of the names of the parameters to be logged. Each parameter is allowed a 0-100 scale rating in the user interface.
    // These will be columns in the table TABLE_NAME above.
    public static String[] parameters = {"mood", "activity"};

    public static String[] questions = {"mood", "activity"};
}
