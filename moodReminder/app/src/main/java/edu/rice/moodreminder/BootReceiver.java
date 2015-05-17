package edu.rice.moodreminder;

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
 * @author Kevin Lin, Anant Tibrewal
 * @since 10/23/2014
 */
public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver alarm = new AlarmReceiver();
    MainActivity mainActivity = new MainActivity();
    public AlarmManager alarmManager;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmIntent = new Intent(context, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            Calendar alarmStartTime = Calendar.getInstance();
            alarmStartTime.add(Calendar.MINUTE, 1440);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("tag", "Alarms set every two minutes.");
        }
    }
    private int getInterval(){
        int seconds = 60;
        int milliseconds = 1000;
        int repeatMS = seconds * 1 * milliseconds;
        return repeatMS;
    }
}