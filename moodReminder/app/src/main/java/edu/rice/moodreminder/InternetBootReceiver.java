package edu.rice.moodreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by anant_000 on 7/14/2015.
 */
public class InternetBootReceiver extends BootReceiver {
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

            alarmIntent = new Intent(context, InternetAlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            Calendar alarmStartTime = Calendar.getInstance();
            alarmStartTime.add(Calendar.MINUTE, 1);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), getInterval(), pendingIntent);
            Log.i("tag", "Alarms set every minute.");
        }
    }
    private int getInterval(){
        int seconds = 60;
        int milliseconds = 1000;
        int repeatMS = seconds * 1 * milliseconds;
        return repeatMS;
    }
}
