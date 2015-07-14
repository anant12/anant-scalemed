package edu.rice.moodreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by anant_000 on 7/9/2015.
 */
public class CheckConnectivity extends BroadcastReceiver {

    public static boolean connected;

    @Override
    public void onReceive(Context context, Intent arg1) {
        boolean isConnected = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if (isConnected) {
            connected = false;
            //Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();
        }
        else {
            connected = true;
            //Toast.makeText(context, "Internet Connected", Toast.LENGTH_LONG).show();
            Log.i("tag", "connected " + connected);

        }
    }

}


