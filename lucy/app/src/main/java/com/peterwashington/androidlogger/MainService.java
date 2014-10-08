package com.peterwashington.androidlogger;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Browser.BookmarkColumns;
import android.provider.CallLog;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainService extends Service {
	private static int hour = 3600000;
	private static int min = 60000;
	private static int sec = 1000;
	private static long screenINTERVAL = 2*min;
	private static long networkINTERVAL = 1*hour;
	private static long deviceINTERVAL = 1*hour;
	private static long cellularINTERVAL = 1*hour;
	private static long wifiINTERVAL = 1*hour;
	private static long appINTERVAL = 2*min;
	private static long locINTERVAL = 15*min;
	private static long webINTERVAL = 1*hour;
	private static long smsINTERVAL = 1*hour;
	private static long callINTERVAL = 1*hour;
	private static long uploadINTERVAL = 10*sec;//24*hour;
	private static long uploadSUCCESS_INTERVAL =24*hour;//24*hour;

	private static int INTERVAL = 120*sec;

	private long lastScreenCheck=System.currentTimeMillis()-24*hour;
	private long lastNetworkCheck=System.currentTimeMillis()-24*hour;
	private long lastDeviceCheck=System.currentTimeMillis()-24*hour;
	private long lastCellularCheck=System.currentTimeMillis()-24*hour;
	private long lastWifiCheck=System.currentTimeMillis()-24*hour;
	private long lastAppCheck=System.currentTimeMillis()-24*hour;
	private long lastLocCheck=System.currentTimeMillis()-24*hour;
	private long lastWebCheck=System.currentTimeMillis()-24*hour;
	private long lastSmsCheck=System.currentTimeMillis()-24*hour;
	private long lastCallCheck=System.currentTimeMillis()-24*hour;
	private long lastUploadCheck = System.currentTimeMillis()-24*hour;
	private long lastUploadSuccess = System.currentTimeMillis()-24*hour;

	private Timer mTimer = new Timer();
	ActivityManager mActivityManager;
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDatabase;

	public static String UUID="";

	SharedPreferences lastCheck;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	static int i=0;
	@Override
	public void onCreate() {
		mActivityManager = (ActivityManager) getApplication().getSystemService(ACTIVITY_SERVICE);
		setUUID();
		lastCheck = getApplicationContext().getSharedPreferences("LastCheck", MODE_PRIVATE);
		lastScreenCheck=lastCheck.getLong("screen", System.currentTimeMillis()-24*hour);
		lastNetworkCheck=lastCheck.getLong("network", System.currentTimeMillis()-24*hour);
		lastDeviceCheck=lastCheck.getLong("device", System.currentTimeMillis()-24*hour);
		lastCellularCheck=lastCheck.getLong("cellular", System.currentTimeMillis()-24*hour);
		lastWifiCheck=lastCheck.getLong("wifi", System.currentTimeMillis()-24*hour);
		lastAppCheck=lastCheck.getLong("app", System.currentTimeMillis()-24*hour);
		lastLocCheck=lastCheck.getLong("loc", System.currentTimeMillis()-24*hour);
		lastWebCheck=lastCheck.getLong("web", System.currentTimeMillis()-24*hour);
		lastSmsCheck=lastCheck.getLong("sms", System.currentTimeMillis()-24*hour);
		lastCallCheck=lastCheck.getLong("call", System.currentTimeMillis()-24*hour);
		lastUploadCheck=lastCheck.getLong("upload", System.currentTimeMillis()-24*hour);
		lastUploadSuccess=lastCheck.getLong("uploadSuccess", System.currentTimeMillis()-48*hour);
		
		lastScreenCheck=Math.min(lastScreenCheck, lastUploadSuccess);
		lastNetworkCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastDeviceCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastCellularCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastWifiCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastAppCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastLocCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastWebCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastSmsCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastCallCheck=Math.min(lastAppCheck, lastUploadSuccess);
		lastUploadCheck=Math.min(lastAppCheck, lastUploadSuccess);
		
		System.out.println("LivelabService created");
		mDBHelper = new DatabaseHelper(getApplicationContext(), getDate());
		mDatabase = mDBHelper.getWritableDatabase();
		
		System.out.println("DB Path: " + mDatabase.getPath());
		
		//code to execute when the service is first created
		mTimer.scheduleAtFixedRate( new TimerTask() {

			public void run() {
				System.out.println("=== Collect Logs ===");
				Editor editor = lastCheck.edit();
				if (System.currentTimeMillis()-lastSmsCheck>smsINTERVAL){
					getSMS();
					lastSmsCheck=System.currentTimeMillis();
					editor.putLong("sms", lastSmsCheck);
									}
				if (System.currentTimeMillis()-lastCallCheck>callINTERVAL){
					getCalls();
					lastCallCheck=System.currentTimeMillis();
					editor.putLong("call", lastCallCheck);
				}
				if (System.currentTimeMillis()-lastAppCheck>appINTERVAL){
					getApps();
					lastAppCheck=System.currentTimeMillis();
					editor.putLong("app", lastAppCheck);
				}
				if (System.currentTimeMillis()-lastLocCheck>locINTERVAL){
					getLoc();
					lastLocCheck=System.currentTimeMillis();
					editor.putLong("loc", lastLocCheck);
				}
				if (System.currentTimeMillis()-lastWebCheck>webINTERVAL){
					getWeb();
					lastWebCheck=System.currentTimeMillis();
					editor.putLong("web", lastWebCheck);
				}
				if (System.currentTimeMillis()-lastWebCheck>wifiINTERVAL){
					getWifi();
					lastWifiCheck=System.currentTimeMillis();
					editor.putLong("wifi", lastWifiCheck);
				}
				if (System.currentTimeMillis()-lastCellularCheck>cellularINTERVAL){
					getCellular();
					lastCellularCheck=System.currentTimeMillis();
					editor.putLong("cellular", lastCellularCheck);
				}
				if (System.currentTimeMillis()-lastDeviceCheck>deviceINTERVAL){
					getDevice();
					lastDeviceCheck=System.currentTimeMillis();
					editor.putLong("device", lastDeviceCheck);
				}
				if (System.currentTimeMillis()-lastNetworkCheck>networkINTERVAL){
					getNetwork();
					lastNetworkCheck=System.currentTimeMillis();
					editor.putLong("network", lastNetworkCheck);
				}
				if (System.currentTimeMillis()-lastScreenCheck>screenINTERVAL){
					getScreen();
					lastScreenCheck=System.currentTimeMillis();
					editor.putLong("screen", lastScreenCheck);
				}
				if (System.currentTimeMillis()-lastUploadCheck>uploadINTERVAL){
					upload();
					lastUploadCheck=System.currentTimeMillis();
					editor.putLong("upload", lastUploadCheck);
				}
				editor.commit();
				System.out.println("=== Finished Collecting ===");
			}

		}, 0, INTERVAL);

	}
	public void upload(){
		if (System.currentTimeMillis()-lastUploadSuccess>uploadSUCCESS_INTERVAL){
			if (Uploader.upload(mDatabase.getPath())){
				lastUploadSuccess=System.currentTimeMillis();
				Editor editor = lastCheck.edit();
				editor.putLong("upload", lastUploadSuccess);
				editor.commit();
				mDBHelper = new DatabaseHelper(getApplicationContext(),getDate());
				mDatabase = mDBHelper.getWritableDatabase();
			}
		}
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w("Starting LiveLab","Starting LiveLab Service");
		Context context = getApplicationContext();
		CharSequence text = "LiveLab Started";
		int duration = 50000000;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		return Service.START_NOT_STICKY;
	}



	public void getCalls(){
		System.out.println("getCalls");
		try{
			Uri uri = CallLog.Calls.CONTENT_URI;
			Cursor c = getContentResolver().query(uri, null, null, null, null);
			if (c.moveToFirst()) {
				for (int i=0; i<c.getCount(); i++) {
					String number=c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
					number=number.substring(number.length()-10,number.length());
					String date=""+c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
					String duration=""+c.getLong(c.getColumnIndex(CallLog.Calls.DURATION));
					
					String type = c.getString(c.getColumnIndex(CallLog.Calls.TYPE));

					try{
						if (Long.parseLong(date) > lastCallCheck){
							System.out.println(" + " + hash(number)+":"+date+","+duration);
							ContentValues values = new ContentValues();
							values.put(DatabaseHelper.COLUMN_CALL_NUMBER, hash(number));
							values.put(DatabaseHelper.COLUMN_CALL_TYPE, type);
							values.put(DatabaseHelper.COLUMN_CALL_TIMESTAMP, date);
							values.put(DatabaseHelper.COLUMN_CALL_DURATION, duration);
							mDatabase.insert(DatabaseHelper.TABLE_CALLS, null, values);
						}
					}catch(Exception e){

					}

					c.moveToNext();
				}
			}

			c.close();
		}catch(Exception e){
			System.err.println(e);
			System.out.println("Calls Failed");
		}
	}


	public void getSMS() {
		try{
			System.out.println("getSMS");
			//Uri Calls = Uri.parse("content://call_log/calls");
			Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
			if (c.moveToFirst()) {
				for (int i=0; i<c.getCount(); i++) {
					// Do stuff with calls here! We can do stuff with the following!
					String number=c.getString(c.getColumnIndex("address"));
					if (number.length()>=10){
						number=number.substring(number.length()-10,number.length());
					}
					String date=""+c.getLong(c.getColumnIndex("date"));
					String length=""+c.getString(c.getColumnIndex("body")).length();

					try{
						if (Long.parseLong(date) > lastSmsCheck){
							System.out.println(" + " + hash(number)+":"+date+","+length);
							ContentValues values = new ContentValues();
							values.put(DatabaseHelper.COLUMN_SMS_NUMBER, hash(number));
							values.put(DatabaseHelper.COLUMN_SMS_TIMESTAMP, date);
							values.put(DatabaseHelper.COLUMN_SMS_TYPE, "incoming");
							values.put(DatabaseHelper.COLUMN_SMS_LENGTH, length);
							mDatabase.insert(DatabaseHelper.TABLE_SMS, null, values);
						}
					}catch(Exception e){

					}


					c.moveToNext();
				}
			}
			c.close();
		}catch(Exception e){
			System.err.println(e);
		}
		
		try{
			System.out.println("getSMS");
			//Uri Calls = Uri.parse("content://call_log/calls");
			Cursor c = getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, null);
			if (c.moveToFirst()) {
				for (int i=0; i<c.getCount(); i++) {
					// Do stuff with calls here! We can do stuff with the following!
					String number=c.getString(c.getColumnIndex("address"));
					if (number.length()>=10){
						number=number.substring(number.length()-10,number.length());
					}
					String date=""+c.getLong(c.getColumnIndex("date"));
					String length=""+c.getString(c.getColumnIndex("body")).length();

					try{
						if (Long.parseLong(date) > lastSmsCheck){
							System.out.println(" + " + hash(number)+":"+date+","+length);
							ContentValues values = new ContentValues();
							values.put(DatabaseHelper.COLUMN_SMS_NUMBER, hash(number));
							values.put(DatabaseHelper.COLUMN_SMS_TIMESTAMP, date);
							values.put(DatabaseHelper.COLUMN_SMS_TYPE, "outgoing");
							values.put(DatabaseHelper.COLUMN_SMS_LENGTH, length);
							mDatabase.insert(DatabaseHelper.TABLE_SMS, null, values);
						}
					}catch(Exception e){

					}


					c.moveToNext();
				}
			}
			c.close();
		}catch(Exception e){
			System.err.println(e);
		}

	} 
	public void getWeb() {
		try{
			System.out.println("getWeb");
			Uri uri = Uri.parse("content://browser/bookmarks");
			Cursor c = getContentResolver().query(uri, null, null, null, null);

			if (c.moveToFirst()) {
				for (int i=0; i<c.getCount(); i++) {
					String url=c.getString(c.getColumnIndex(BookmarkColumns.URL));
					String date=c.getString(c.getColumnIndex("date"));
					if (date!=null){
						try{
							if (Long.parseLong(date) > lastWebCheck){
								ContentValues values = new ContentValues();
								int slashslash = url.indexOf("//") + 2;
								String domain = url.substring(slashslash, url.indexOf('/', slashslash));
								System.out.println(domain+":"+date);

								values.put(DatabaseHelper.COLUMN_WEB_DOMAIN, domain);
								values.put(DatabaseHelper.COLUMN_WEB_DATE, date);
								mDatabase.insert(DatabaseHelper.TABLE_WEB, null, values);
							}
						}catch(Exception e){

						}

					}
					c.moveToNext();
				}
			}
			c.close();
		}catch(Exception e){
			System.err.println(e);
			System.out.println("Web Failed");
		}
	} 
	public void getLoc(){
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location loc= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (loc!=null){
			System.out.println("Loc: "+loc.getLatitude()+","+loc.getLongitude());
			try{
				ContentValues values = new ContentValues();
				values.put(DatabaseHelper.COLUMN_LOC_LONG, loc.getLongitude());
				values.put(DatabaseHelper.COLUMN_LOC_LAT, loc.getLatitude());
				values.put(DatabaseHelper.COLUMN_LOC_DATE, System.currentTimeMillis());

				mDatabase.insert(DatabaseHelper.TABLE_LOC, null, values);

			}catch(Exception e){

			}
		}else{
			PendingIntent pi = PendingIntent.getBroadcast(this,
					0,
					new Intent("com.example.livelab.NEW_SINGLE"),
					PendingIntent.FLAG_UPDATE_CURRENT);
			locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, pi);
		}

	}

	public void getApps(){
		List<ActivityManager.RunningAppProcessInfo> apps;
		apps = mActivityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo app : apps){
			System.out.println("PROCESS: "+app.processName+","+app.importance);
			if (app.importance==RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
				try{
					ContentValues values = new ContentValues();
					values.put(DatabaseHelper.COLUMN_APP_NAME, app.processName);
					values.put(DatabaseHelper.COLUMN_APP_PID, app.pid);
					values.put(DatabaseHelper.COLUMN_APP_DATE, app.importance);
					mDatabase.insert(DatabaseHelper.TABLE_APP, null, values);

				}catch(Exception e){

				}
			}
		}
	}
	
	public void getWifi() {
		WifiManager mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    WifiInfo currentWifi = mainWifi.getConnectionInfo();
	    try{
			ContentValues values = new ContentValues();
			if (currentWifi != null) {
				values.put(DatabaseHelper.COLUMN_WIFI_SSID, currentWifi.getSSID());
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECTION_SPEED, currentWifi.getLinkSpeed());
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECTION_QUALITY, currentWifi.getRssi());
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECT_TIME, System.currentTimeMillis());
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECTION_AVAILABLE, "yes");
			} else {
				values.put(DatabaseHelper.COLUMN_WIFI_SSID, "");
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECTION_SPEED, "");
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECTION_QUALITY, "");
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECT_TIME, System.currentTimeMillis());
				values.put(DatabaseHelper.COLUMN_WIFI_CONNECTION_AVAILABLE, "no");
			}

			mDatabase.insert(DatabaseHelper.TABLE_LOC, null, values);

		}catch(Exception e){
		}
	}
	
	public void getCellular() {
		ContentValues values = new ContentValues();
		TelephonyManager manager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

//		try {
//			CellInfoGsm cellinfogsm = (CellInfoGsm)manager.getAllCellInfo().get(0);
//			CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
//			values.put(DatabaseHelper.COLUMN_SIGNAL_STRENGTH, cellSignalStrengthGsm.getDbm());
//		} catch (Exception e) {
//			values.put(DatabaseHelper.COLUMN_SIGNAL_STRENGTH, "Unsupported on this device");
//		}
		
		values.put(DatabaseHelper.COLUMN_SIGNAL_STRENGTH, "Unsupported on this device");
		
		ConnectivityManager conn =  (ConnectivityManager)
		        this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = conn.getActiveNetworkInfo();
		
		
		values.put(DatabaseHelper.COLUMN_CARRIER, manager.getNetworkOperatorName());
		values.put(DatabaseHelper.COLUMN_DOWNLOAD_SPEED, TrafficStats.getTotalRxBytes());
		values.put(DatabaseHelper.COLUMN_UPLOAD_SPEED, TrafficStats.getTotalTxBytes());
		int networkType = manager.getNetworkType();
		values.put(DatabaseHelper.COLUMN_NETWORK_TYPE, getNetworkType(networkType));
		values.put(DatabaseHelper.COLUMN_INTERNET_CONNECTION, networkInfo.getTypeName());
		values.put(DatabaseHelper.COLUMN_INTERNET_TIMESTAMP, System.currentTimeMillis());
		mDatabase.insert(DatabaseHelper.TABLE_CELLULAR_CONNECTIONS, null, values);
	}
	
	public void getDevice() {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_TIME_ZONE, TimeZone.getDefault().getDisplayName());
		// Are we charging / charged?
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
		                     status == BatteryManager.BATTERY_STATUS_FULL;
		values.put(DatabaseHelper.COLUMN_BATTERY_STATUS, isCharging);
		values.put(DatabaseHelper.COLUMN_BATTERY_LEVEL, getBatteryLevel());
//		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//		values.put(DatabaseHelper.COLUMN_TOTAL_MEMORY, memoryInfo.totalMem);
//		values.put(DatabaseHelper.COLUMN_AVAILABLE_MEMORY, memoryInfo.availMem);
		values.put(DatabaseHelper.COLUMN_TOTAL_MEMORY, "Unsupported for this device");
		values.put(DatabaseHelper.COLUMN_AVAILABLE_MEMORY, "Unsupported for this device");
		values.put(DatabaseHelper.COLUMN_OS_NUMBER, android.os.Build.VERSION.SDK_INT);
		values.put(DatabaseHelper.COLUMN_DEVICE_TIMESTAMP, System.currentTimeMillis());
		mDatabase.insert(DatabaseHelper.TABLE_DEVICE_STATUS, null, values);
	}
	
	public void getNetwork() {
		ContentValues values = new ContentValues();
	    TelephonyManager manager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
	    int networkType = manager.getNetworkType();
		values.put(DatabaseHelper.COLUMN_NET_TYPE, getNetworkType(networkType));
		values.put(DatabaseHelper.COLUMN_PACKETS_RECEIVED, TrafficStats.getTotalRxPackets());
		values.put(DatabaseHelper.COLUMN_NETWORK_TIMESTAMP, System.currentTimeMillis());
		mDatabase.insert(DatabaseHelper.TABLE_NETWORK, null, values);
	}
	
	public void getScreen() {
		ContentValues values = new ContentValues();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		values.put(DatabaseHelper.COLUMN_SCREEN_ON, pm.isScreenOn());
		values.put(DatabaseHelper.COLUMN_SCREEN_TIMESTAMP, System.currentTimeMillis());
		mDatabase.insert(DatabaseHelper.TABLE_SCREEN_STATUS, null, values);
	}
	
	private float getBatteryLevel() {
	    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }

	    return ((float)level / (float)scale) * 100.0f; 
	}
	
	private String getNetworkType(int type) {
		switch (type)
		{
		case 7:
		    return "1xRTT";    
		case 4:
		    return "CDMA";     
		case 2:
		    return "EDGE";
		case 14:
		   return "eHRPD";     
		case 5:
		    return "EVDO rev. 0";
		case 6:
		    return "EVDO rev. A";
		case 12:
		    return "EVDO rev. B"; 
		case 1:
		    return "GPRS";     
		case 8:
		    return "HSDPA";      
		case 10:
		    return "HSPA";        
		case 15:
		    return "HSPA+";       
		case 9:
		    return "HSUPA";       
		case 11:
		    return "iDen";
		case 13:
		    return "LTE";
		case 3:
		    return "UMTS";         
		case 0:
		    return "Unknown";
		}
		return "Unknown";
	}


	public static String hash(String input){
		return hash(input,UUID+"salt");
	}

	public static String hash(String input, String salt){
		String result = "";
		try{
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			String message = input+"---"+salt;
			digest.update(message.getBytes());
			byte[] b = digest.digest();

			for (int i=0; i < b.length; i++) {
				result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			}

		}catch(Exception e){
			System.err.println(e);
		}
		return result;

	}
	private void setUUID(){
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		UUID=tm.getDeviceId();
	}
	public String getDate(){
		Date today = new Date();
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
		return DATE_FORMAT.format(today);
	}
	@Override
	public void onDestroy() {
		//code to execute when the service is shutting down
	}

	@Override
	public void onStart(Intent intent, int startid) {
		//code to execute when the service is starting up
	}
}
