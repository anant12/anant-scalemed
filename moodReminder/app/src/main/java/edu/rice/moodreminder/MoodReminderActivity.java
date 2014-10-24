package edu.rice.moodreminder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

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
}
