package com.riceucla.mobilelogger;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity opened by notification which opens the mood reminder dialog
 * This is a temporary implementation. Obviously it seems very counterintuitive for the notification to open an activity, which then opens a fragment.
 * I have not worked with notifications before; I don't know if there is a more straightforward way of doing this.
 *
 * @author Kevin Lin
 * @since 10/12/2014
 */
public class MoodReminder extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_reminder);

        FragmentManager frgManager = getSupportFragmentManager();
        MoodReminderDialog moodReminderDialog = new MoodReminderDialog();
        moodReminderDialog.show(frgManager, "fragment_mood_reminder");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mood_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
