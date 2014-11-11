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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * No UI elements. This class only enables the periodic alarm to remind the user via a notification once daily.
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

        // Get the device UUID.
        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        UUID = tm.getDeviceId();

        // Enable the periodic alarm.
        alarm.setAlarm(this);

        // Initialize database helper
        dbHelper = new DatabaseHelper(getApplicationContext(), getDate());

        // TEMPORARY DEBUG IMPLEMENTATION: Generate a notification on app launch
        generateMoodNotification("a", "t");
    }

    private void generateMoodNotification(String title, String message) {
        Context context = this;
        int icon = R.drawable.ic_launcher; //TODO: Change this icon
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        Intent notificationIntent = new Intent(context, MoodReminderActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
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
