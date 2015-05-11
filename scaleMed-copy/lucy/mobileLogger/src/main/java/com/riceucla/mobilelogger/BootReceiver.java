package com.riceucla.mobilelogger;

/**
 * Created by anant_000 on 3/28/2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Automatically re-sets the alarm after device reboot.
 * Requires BOOT_COMPLETED permission for obvious reasons.
 *
 * @author Kevin Lin
 * @since 10/23/2014
 */
public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver alarm = new AlarmReceiver();
    Intent alarmIntent;
    PendingIntent pendingIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


            alarmIntent = new Intent(context, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getCalendarAtTime(Config.NOTIFICATION_HOUR, Config.NOTIFICATION_MINUTE).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("tag", "Alarms set every two minutes.");
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
        Log.w("calendar", "getcalendar");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }
}