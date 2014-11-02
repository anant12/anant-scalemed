package edu.rice.moodreminder;

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

import java.util.UUID;

/**
 * No UI elements. This class only enables the periodic alarm to remind the user via a notification once daily.
 *
 * @author Kevin Lin
 * @since 10/23/2014
 */
public class MainActivity extends ActionBarActivity {
    AlarmReceiver alarm = new AlarmReceiver();
    private static SQLiteDatabase mDatabase;
    DatabaseHelper dbHelper;
    public static String UUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the device UUID.
        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        UUID = tm.getDeviceId();

        // Enable the periodic alarm.
        alarm.setAlarm(this);

        // Upload?
        upload();
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

    public void upload() {
        mDatabase = dbHelper.getWritableDatabase();
        Uploader.upload(mDatabase, UUID);
    }
}
