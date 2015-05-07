package edu.rice.moodreminder;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    public AlarmManager alarmManager;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    private static SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.setId(0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView test = new TextView(this);
        test.setText("Loading Data...");
        linearLayout.addView(test);
        setContentView(linearLayout);
        //ProgressDialog.show(this, "Loading", "Wait while loading...");


        // Get the device UUID.
        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        UUID = tm.getDeviceId();

        // Enable the periodic alarm.
        //alarm.setAlarm(this);
        setAlarm(this);

        dbHelper = new DatabaseHelper(getApplicationContext(), getDate());

        if (hasConnection()){
            Log.w("main", "network:)");

            Config my_task = new Config();
            my_task.new myTask().execute();


            //dbHelper = new DatabaseHelper(getApplicationContext(), getDate());
            //moodReminder();
        }
        else{
            Log.w("main", "no network");
            showToast("No network available", 1);
            finish();
        }


        // Initialize database helper
        //dbHelper = new DatabaseHelper(getApplicationContext(), getDate());
        /*
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        while (Config.questions[0] == "mood" && Config.questions[1] == "activity"){

            progress.show();

        }

        progress.dismiss();
        */
        //Inserting delay here
        if (Config.questions[0] == "mood" && Config.questions[1] == "activity"){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        moodReminder();

        //LinearLayout linearLayout = new LinearLayout(this);
        /*
        linearLayout.setPadding(10, 10, 10, 10);
        linearLayout.setId(0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        */
        // Add a TextView and SeekBar for each parameter set in the configuration file
        /*for (String parameter : Config.questions) {
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
        // Open mood reminder activity
        //startActivity(new Intent(MainActivity.this, MoodReminderActivity.class));
        */
    }

    protected void moodReminder(){
        if (hasConnection()) {
            Config my_task = new Config();
            my_task.new myTask().execute();
        }
        else{
            Log.w("main", "no network");
            showToast("No network available", 1);
            finish();
        }

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(0);
        relativeLayout.setPadding(10, 10, 10, 10);

        // Add a TextView and SeekBar for each parameter set in the configuration file
        int x = 0;
        //for (String parameter : Config.questions) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            relativeLayout.setLayoutParams(lp);

            TextView tv = new TextView(this);
            tv.setId(R.id.id1);
            tv.setLayoutParams(lp);
            tv.setText(Config.questions[0]);
            tv.setTextSize(20);
            tv.setPadding(10, 10, 10, 10);
            relativeLayout.addView(tv);

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            lp2.addRule(RelativeLayout.BELOW,tv.getId());
            SeekBar sb = new SeekBar(this);
            sb.setId(R.id.id2);
            sb.setLayoutParams(lp2);
            relativeLayout.addView(sb);

            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            lp3.addRule(RelativeLayout.BELOW,sb.getId());
            TextView t1 = new TextView(this);
            t1.setId(R.id.id3);
            t1.setLayoutParams(lp3);
            t1.setText(Config.labels[x]);
            t1.setTextSize(15);
            t1.setPadding(10, 0, 10, 5);
            relativeLayout.addView(t1);

            x++;

            RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            lp4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,t1.getId());
            lp4.addRule(RelativeLayout.BELOW, sb.getId());
            TextView t2 = new TextView(this);
            t2.setId(R.id.id4);
            t2.setLayoutParams(lp4);
            t2.setText(Config.labels[x]);
            t2.setTextSize(15);
            t2.setPadding(10, 0, 10, 5);

            relativeLayout.addView(t2);

        RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        x++;
        TextView tv2 = new TextView(this);
        tv2.setId(R.id.id6);
        tv2.setLayoutParams(lp5);
        tv2.setText(Config.questions[1]);
        tv2.setTextSize(20);
        tv2.setPadding(10, 60, 10, 10);
        lp5.addRule(RelativeLayout.BELOW, t2.getId());
        relativeLayout.addView(tv2);

        RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp6.addRule(RelativeLayout.BELOW,tv2.getId());
        SeekBar sb2 = new SeekBar(this);
        sb2.setId(R.id.id7);
        sb2.setLayoutParams(lp6);
        relativeLayout.addView(sb2);

        RelativeLayout.LayoutParams lp7 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp7.addRule(RelativeLayout.BELOW,sb2.getId());
        TextView t3 = new TextView(this);
        t3.setId(R.id.id8);
        t3.setLayoutParams(lp7);
        t3.setText(Config.labels[x]);
        t3.setTextSize(15);
        t3.setPadding(10, 0, 10, 45);
        relativeLayout.addView(t3);

        x++;

        RelativeLayout.LayoutParams lp8 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp8.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,t3.getId());
        lp8.addRule(RelativeLayout.BELOW, sb2.getId());
        TextView t4 = new TextView(this);
        t4.setId(R.id.id9);
        t4.setLayoutParams(lp8);

        t4.setText(Config.labels[x]);
        t4.setTextSize(15);
        t4.setPadding(10, 0, 10, 45);

        relativeLayout.addView(t4);

        RelativeLayout.LayoutParams lp9 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        Button submit = new Button(this);
        submit.setId(R.id.id10);
        lp9.addRule(RelativeLayout.BELOW,t4.getId());
        submit.setLayoutParams(lp9);
        submit.setText("Submit");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Upload().execute();
            }
        });
        relativeLayout.addView(submit);
        setContentView(relativeLayout);
    }

    @Override
    protected void onNewIntent( Intent intent ) {
        Log.i("tag", "onNewIntent(), intent = " + intent);
        if (intent.getExtras() != null)
        {
            Log.i("tag", "in onNewIntent = " + intent.getExtras().getString("test"));
        }
        super.onNewIntent( intent );
        setIntent( intent );
    }

    public void triggerAlarm(View v){
        setAlarm(this);

    }

    /**
     * Checks if the device has Internet connection.
     *
     * @return <code>true</code> if the phone is connected to the Internet.
     */
    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager)
        getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (null == ni){
            return false;
        }
        return ni.isConnectedOrConnecting();
    }

    public void setAlarm(Context context){
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(  MainActivity.this, 0, alarmIntent, 0);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.add(Calendar.MINUTE, 1440);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i("tag","Alarms set every two minutes.");


        // Re-set the alarm after device reboot.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();


        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


    }

    private int getInterval(){
        int seconds = 60;
        int milliseconds = 1000;
        int repeatMS = seconds * 1 * milliseconds;
        return repeatMS;
    }

    @Override
    protected void onStart(){
        super.onStart();
        updateUI();
    }

    public void updateUI(){
        //MyAlarm app = (MyAlarm)getApplicationContext();

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(this.getIntent().getExtras() != null){
            Log.i("tag","extras: " + this.getIntent().getExtras());
            updateUI();

        }
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
     * Creates a Calendar object at the given hour and minute.
     *
     * @param hour: Integer representing the hour for the Calendar (24-hour time)
     * @param minute: Integer representing the minute for the Calendar
     * @return Calendar object at hour and time
     */
    public Calendar getCalendarAtTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
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
            for (int i = 0; i < ((RelativeLayout)findViewById(0)).getChildCount(); i++)
                if (((RelativeLayout)findViewById(0)).getChildAt(i) instanceof SeekBar)
                    seekBars.add(((SeekBar)((RelativeLayout) findViewById(0)).getChildAt(i)).getProgress());

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
