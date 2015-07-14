package edu.rice.moodreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by anant_000 on 7/9/2015.
 */
public class InternetAlarmReceiver extends WakefulBroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    /**
     * Starts the AlarmService when it receives the alarm.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("tag", "BroadcastReceiver has received internet alarm intent.");

        MainActivity mainActivity = new MainActivity();
        MoodReminderActivity moodReminderActivity = new MoodReminderActivity();
        CheckConnectivity checkConnectivity = new CheckConnectivity();
        Log.i("tag", "connected " + checkConnectivity.connected + " uploadData " + moodReminderActivity.uploadData);
        if (checkConnectivity.connected && moodReminderActivity.uploadData == 1){
            Log.i("tag", "call Upload");
            moodReminderActivity.new Upload().execute();
            moodReminderActivity.uploadData = 0;
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
}
