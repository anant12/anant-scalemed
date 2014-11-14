package edu.rice.moodreminder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Calendar;

/**
 * User interface for asking the user to input their activity and mood levels on a slider.
 * Submit button stores the data locally and immediately uploads it to the server in the background.
 * Last modified 11/13/2014
 *
 * @author Kevin Lin
 * @since 10/23/2014
 */
public class MoodReminderActivity extends ActionBarActivity {

    private static SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_reminder);
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

    public void submitOnClick(View view) {
        new Upload().execute();
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
            // Get seekbar values
            // In the future, can make this process simpler with a for-each loop for arbitrary number of seekbars
            SeekBar moodSeekBar = (SeekBar)findViewById(R.id.moodSeekBar);
            SeekBar activitySeekBar = (SeekBar)findViewById(R.id.activeSeekBar);
            int mood = moodSeekBar.getProgress();
            int activity = activitySeekBar.getProgress();

            // Store in local db
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_MOOD_MOOD, mood);
            values.put(DatabaseHelper.COLUMN_MOOD_ACTIVITY, activity);
            values.put(DatabaseHelper.COLUMN_TIMESTAMP, getTimestamp());
            waitUntilAvailable();
            mDatabase = MainActivity.dbHelper.getWritableDatabase();
            mDatabase.insert(DatabaseHelper.TABLE_MOOD, null, values);

            // Upload
            if (Uploader.upload(mDatabase, MainActivity.UUID))
                close();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            showToast("Your response has been submitted!", 0);
        }
    }
}
