package edu.rice.moodreminder;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * User interface for asking the user to input their activity and mood levels on a slider.
 * Submit button stores the data locally and immediately uploads it to the server in the background.
 * Last modified 11/13/2014
 *
 * @author Kevin Lin
 * @since 10/23/2014
 */
public class MoodReminderActivity extends ActionBarActivity {

    AlarmReceiver alarm = new AlarmReceiver();
    public static String UUID;
    public static DatabaseHelper dbHelper;
    private static SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //alarmSet();
        //setContentView(R.layout.activity_mood_reminder);

        // Create UI elements
        // Start with LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.setId(0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        // Add a TextView and SeekBar for each parameter set in the configuration file

        for (String parameter : Config.questions) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setText(parameter);
            tv.setTextSize(20);
            tv.setPadding(10, 10, 10, 10);
            linearLayout.addView(tv);
            SeekBar sb = new SeekBar(this);
            sb.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(sb);
        }
        // Create submit button
        Button submit = new Button(this);
        submit.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Upload().execute();
            }
        });
        linearLayout.addView(submit);
        setContentView(linearLayout);

        //MainActivity main = new MainActivity();
        //main.alarmSet();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mood_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

    @SuppressLint("SimpleDateFormat")
    public static String getDate()
    {
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
        return dateFormat.format(today);
    }

    public static void close()
    {
        if(mDatabase != null)
        {
            mDatabase.close();
            MainActivity.dbHelper.semaphore.release();
        }
    }

    public static void waitUntilAvailable()
    {
        try
        {
            MainActivity.dbHelper.semaphore.acquire();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            System.err.println("Interrupted exception!");
        }
    }

    /**
     * Gets the current device timestamp in the format mm/dd/yy h:m:s AM/PM (12-hour)
     *
     * @return timestamp in the form of an easily-legible String.
     */
    public static String getTimestamp() {
        Calendar c = Calendar.getInstance();
        int second = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY)%12;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int ampm = c.get(Calendar.AM_PM);
        String timestamp = "" + month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second;
        if (ampm == Calendar.AM)
            timestamp += " AM";
        else
            timestamp += " PM";
        return timestamp;
    }

    /**
     * Shows a toast.
     *
     * @param s: message to be toasted
     * @param i: 0 for a short-duration toast, anything other integer for a long-duration toast
     */
    private void showToast(String s, int i) {
        if (i == 0) {
            Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Stores the mood and activity levels in a local database, then uploads it to the server.
     *
     * Asynctask to prevent network activity on main thread.
     */
    private class Upload extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Integer> seekBars = new ArrayList<Integer>();
            for (int i = 0; i < ((LinearLayout)findViewById(0)).getChildCount(); i++)
                if (((LinearLayout)findViewById(0)).getChildAt(i) instanceof SeekBar)
                    seekBars.add(((SeekBar)((LinearLayout) findViewById(0)).getChildAt(i)).getProgress());

            // Store in local db
            ContentValues values = new ContentValues();
            for (int i = 0; i < Config.parameters.length; i++) {
                values.put(Config.parameters[i], seekBars.get(i));
                Log.v("Parameter values", Config.parameters[i] + " " + seekBars.get(i).toString());
            }
            values.put(DatabaseHelper.COLUMN_TIMESTAMP, getTimestamp());
            waitUntilAvailable();
            mDatabase = MainActivity.dbHelper.getWritableDatabase();
            mDatabase.insert(Config.TABLE_NAME, null, values);

            // Upload
            if (Uploader.upload(mDatabase, MainActivity.UUID)) {
                cleanTables();
                close();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            showToast("Your response has been submitted!", 0);
        }
    }

    /**
     * Removes all entries from table in local DB.
     */
    public static void cleanTables(){
        Cursor c = mDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Log.v("Cleaned table", c.getString(0));
                mDatabase.delete(c.getString(0), null, null);
                c.moveToNext();
            }
        }
    }
}
