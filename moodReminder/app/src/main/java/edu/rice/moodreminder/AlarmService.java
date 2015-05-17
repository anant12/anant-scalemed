package edu.rice.moodreminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Handles notification generation behavior. Called via intent by the alarm wakelock.
 *
 * @author Kevin Lin, Anant Tibrewal
 * @since 10/23/2014
 */
public class AlarmService extends IntentService {

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    public AlarmService() {
        super("MoodReminderService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Generate a notification when the intent is received.
        // Can customize title and message as need be.
        Log.i("tag","Alarm Service has started.");
        generateMoodNotification(Config.NOTIFICATION_TITLE, Config.NOTIFICATION_MESSAGE);
    }

    /**
     * Generates a notification with the specified parameters.
     * It starts MoodReminderActivity.class on tap.
     *
     * @param title: title of notification
     * @param message: message of notification
     */
    private void generateMoodNotification(String title, String message) {
        Context context = this.getApplicationContext();
        int icon = R.drawable.ic_launcher; //TODO: Change this icon
        long when = System.currentTimeMillis();
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        Intent notificationIntent = new Intent(this, MainActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("test", "test");
        notificationIntent.putExtras(bundle);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                .setTicker(Config.NOTIFICATION_TITLE)
                .setAutoCancel(true)
                .setContentTitle(Config.NOTIFICATION_TITLE)
                .setContentText(Config.NOTIFICATION_MESSAGE);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
        Log.i("tag", "Notifications sent.");
    }
}
