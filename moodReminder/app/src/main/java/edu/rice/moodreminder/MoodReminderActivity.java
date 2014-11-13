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

/**
 * User interface for asking the user to input their activity and mood levels.
 * Currently, the UI is not functional. Waiting for server-side implementation first.
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

        // Debug - allow network IO on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
}
