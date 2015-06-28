package edu.rice.moodreminder;

import android.app.ActionBar;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * User interface for asking the user to input their activity and mood levels on a slider.
 * Submit button stores the data locally and immediately uploads it to the server in the background.
 *
 * @author Kevin Lin, Anant Tibrewal
 * @since 10/23/2014
 */

public class MoodReminderActivity extends ActionBarActivity {

    private static SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mood_reminder);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(0);
        relativeLayout.setPadding(10, 10, 10, 10);

        // Add a TextView and SeekBar for each parameter set in the configuration file
        int x = 0;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mood_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
