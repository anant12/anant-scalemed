package edu.rice.moodreminder;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * No UI elements. This class only enables the periodic alarm to remind the user via a notification once daily.
 * If the user opens this activity, it will redirect him/her to the mood reminder activity.
 *
 * @author Kevin Lin
 * @since 10/23/2014
 */
public class MainActivity extends ActionBarActivity {

    AlarmReceiver alarm = new AlarmReceiver();
    public static String UUID;
    public static DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(50000);  //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    startActivity(new Intent(MainActivity.this, MoodReminderActivity.class));

                    finish();
                }
            }
        };
        welcomeThread.start();
        */
        Config my_task = new Config();
        my_task.new myTask().execute();


        // Get the device UUID.
        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        UUID = tm.getDeviceId();
        Log.w("tag", "start alarm");
        // Enable the periodic alarm.
        alarm.setAlarm(this);

        // Initialize database helper
        dbHelper = new DatabaseHelper(getApplicationContext(), getDate());

        // Open mood reminder activity
        /*if (Config.parameters == null){
            Config myTask = new Config();
            myTask.startTask();
        }*/


        /*while (Config.parameters == null){
            Thread.sleep(1000);
        }*/
        startActivity(new Intent(MainActivity.this, MoodReminderActivity.class));
        finish();
    }

    public void alarmSet(){
        Config my_task = new Config();
        my_task.startTask();

        // Get the device UUID.
        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        UUID = tm.getDeviceId();
        Log.w("tag", "start alarm");
        // Enable the periodic alarm.
        alarm.setAlarm(this);

        // Initialize database helper
        dbHelper = new DatabaseHelper(getApplicationContext(), getDate());

        // Open mood reminder activity
        //startActivity(new Intent(MainActivity.this, MoodReminderActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate()
    {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
        return dateFormat.format(today);
    }
}
