package edu.rice.moodreminder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.CallLog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * User interface for asking the user to input their activity and mood levels.
 * Currently, the UI is not functional. Waiting for server-side implementation first.
 *
 * TODO: Add functionality to UI elements
 * TODO: Server upload
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

    /**
     * Store that shit in a local db, and upload it now.
     *
     * @param view
     */
    public void submitOnClick(View view) {
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
        System.out.println("upload reached");
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
}
