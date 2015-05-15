package com.riceucla.mobilelogger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.net.InetAddress;
import java.util.Calendar;

/**
 * Created by anant_000 on 3/28/2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("onReceive", "received alarm");
        if (isNetworkAvailable(context)) {
            Config my_task = new Config();
            my_task.startTask();
            Log.w("onRecieve", "internet");
        }
        Intent config = new Intent(context, Config.class);
        startWakefulService(context, config);
    }
    /**
     * Checks if the device has Internet connection.
     *
     * @return <code>true</code> if the phone is connected to the Internet.
     */

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    /**
     * Sets an alarm for 8 PM daily. Called from MainActivity.class.
     *
     * @param context: application context
     */

    public void setAlarm(Context context) {
        Log.w("setalarm", "received");
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Log.w("setalarm", "received2");
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.add(Calendar.MINUTE, 1440);
        // Currently, the alarm is set for *precisely* 8 PM. The tradeoff is potentially higher battery consumption.
        // Use setInexactRepeating() for an alarm that will go off at *approximately* 8 PM.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

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
