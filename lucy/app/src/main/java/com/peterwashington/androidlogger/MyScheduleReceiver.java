package com.peterwashington.androidlogger;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyScheduleReceiver extends BroadcastReceiver {
	
	// Restart service every 1200 seconds (20 minutes)
	// Change this time later, obviously
	private static final long REPEAT_TIME = 1000 * 1200;

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, StartMyServiceAtBootReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar cal = Calendar.getInstance();
		// Start 30 seconds after boot completed
		cal.add(Calendar.SECOND, 30);
		// Fetch every 30 seconds
		// InexactRepeating allows Android to optimize the energy consumption
	    service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		cal.getTimeInMillis(), REPEAT_TIME, pending);
	  }

}
