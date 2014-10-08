package com.peterwashington.androidlogger; 
import android.content.Context; 
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String COLUMN_ID = "_id";
	
	private static final String DATABASE_NAME = "LiveLab.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_CALLS = "calls";
	public static final String COLUMN_CALL_NUMBER = "number";
	public static final String COLUMN_CALL_TIMESTAMP = "timestamp";
	public static final String COLUMN_CALL_TYPE = "type";
	public static final String COLUMN_CALL_DURATION = "duration";
	private static final String CALL_CREATE = "create table "
			+ TABLE_CALLS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_CALL_NUMBER
			+ " text not null, " + COLUMN_CALL_DURATION
			+ " text not null, " + COLUMN_CALL_TYPE
			+ " text not null, " + COLUMN_CALL_TIMESTAMP
			+ " text not null)";

	public static final String TABLE_SMS = "sms";
	public static final String COLUMN_SMS_NUMBER = "number";
	public static final String COLUMN_SMS_TIMESTAMP = "timestamp";
	public static final String COLUMN_SMS_LENGTH = "duration";
	public static final String COLUMN_SMS_TYPE = "type";
	public static final String SMS_CREATE = "create table "
			+ TABLE_SMS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_SMS_NUMBER
			+ " text not null, " + COLUMN_SMS_LENGTH
			+ " text not null, " + COLUMN_SMS_TYPE
			+ " text not null, " + COLUMN_SMS_TIMESTAMP
			+ " text not null)";

	public static final String TABLE_WEB = "web";
	public static final String COLUMN_WEB_DOMAIN = "domain";
	public static final String COLUMN_WEB_DATE = "timestamp";
	public static final String WEB_CREATE = "create table "
			+ TABLE_WEB + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_WEB_DOMAIN
			+ " text not null, " + COLUMN_WEB_DATE
			+ " text not null)";

	public static final String TABLE_LOC = "loc";
	public static final String COLUMN_LOC_LONG = "long";
	public static final String COLUMN_LOC_LAT = "lat";
	public static final String COLUMN_LOC_DATE = "timestamp";
	public static final String LOC_CREATE = "create table "
			+ TABLE_LOC + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_LOC_LONG
			+ " text not null, " + COLUMN_LOC_LAT
			+ " text not null, " + COLUMN_LOC_DATE
			+ " text not null)";

	public static final String TABLE_APP = "app";
	public static final String COLUMN_APP_NAME = "name";
	public static final String COLUMN_APP_PID = "pid";
	public static final String COLUMN_APP_DATE = "timestamp";
	public static final String APP_CREATE = "create table "
			+ TABLE_APP + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_APP_NAME
			+ " text not null, " + COLUMN_APP_PID
			+ " text not null, " + COLUMN_APP_DATE
			+ " text not null)";
	
	public static final String TABLE_WIFI = "wifi";
	public static final String COLUMN_WIFI_CONNECT_TIME = "timestamp";
	public static final String COLUMN_WIFI_SSID = "ssid";
	public static final String COLUMN_WIFI_CONNECTION_SPEED = "speed";
	public static final String COLUMN_WIFI_CONNECTION_QUALITY = "quality";
	public static final String COLUMN_WIFI_CONNECTION_AVAILABLE = "available";
	public static final String WIFI_CREATE = "create table "
			+ TABLE_WIFI + "(" + COLUMN_ID 
			+ " integer primary key autoincrement, " + COLUMN_WIFI_CONNECT_TIME
			+ " text not null, " + COLUMN_WIFI_SSID
			+ " text not null, " + COLUMN_WIFI_CONNECTION_SPEED
			+ " text not null, " + COLUMN_WIFI_CONNECTION_QUALITY
			+ " text not null, " + COLUMN_WIFI_CONNECTION_AVAILABLE
			+ " text not null)";
	
	// === New tables ===
	
	public static final String TABLE_CELLULAR_CONNECTIONS = "cellular";
	public static final String COLUMN_CARRIER = "carrier";
	public static final String COLUMN_SIGNAL_STRENGTH = "signalStrength";
	public static final String COLUMN_DOWNLOAD_SPEED = "downloadSpeed";
	public static final String COLUMN_UPLOAD_SPEED = "uploadSpeed";
	public static final String COLUMN_NETWORK_TYPE = "networkType";
	public static final String COLUMN_INTERNET_CONNECTION = "internetConnection";
	public static final String COLUMN_INTERNET_TIMESTAMP = "timestamp";
	public static final String CELLULAR_CONNECTIONS_CREATE = "create table "
			+ TABLE_CELLULAR_CONNECTIONS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_CARRIER
			+ " text not null, " + COLUMN_SIGNAL_STRENGTH
			+ " text not null, " + COLUMN_DOWNLOAD_SPEED
			+ " text not null, " + COLUMN_UPLOAD_SPEED
			+ " text not null, " + COLUMN_NETWORK_TYPE
			+ " text not null, " + COLUMN_INTERNET_CONNECTION
			+ " text not null, " + COLUMN_INTERNET_TIMESTAMP
			+ " text not null)";
	
	public static final String TABLE_DEVICE_STATUS = "device";
	public static final String COLUMN_TIME_ZONE = "timeZone";
	public static final String COLUMN_BATTERY_STATUS = "batteryStatus";
	public static final String COLUMN_BATTERY_LEVEL = "batteryLevel";
	public static final String COLUMN_TOTAL_MEMORY = "totalMemory";
	public static final String COLUMN_AVAILABLE_MEMORY = "availableMemory";
	public static final String COLUMN_OS_NUMBER = "osNumber";
	public static final String COLUMN_DEVICE_TIMESTAMP = "timestamp";
	public static final String DEVICE_STATUS_CREATE = "create table "
			+ TABLE_DEVICE_STATUS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_TIME_ZONE
			+ " text not null, " + COLUMN_BATTERY_STATUS 
			+ " text not null, " + COLUMN_BATTERY_LEVEL
			+ " text not null, " + COLUMN_TOTAL_MEMORY
			+ " text not null, " + COLUMN_AVAILABLE_MEMORY
			+ " text not null, " + COLUMN_OS_NUMBER
			+ " text not null, " + COLUMN_DEVICE_TIMESTAMP
			+ " text not null)";
	
	public static final String TABLE_NETWORK = "network";
	public static final String COLUMN_NET_TYPE = "networkType";
	public static final String COLUMN_PACKETS_RECEIVED = "packetsReceived";
	public static final String COLUMN_NETWORK_TIMESTAMP = "timestamp";
	public static final String NETWORK_CREATE = "create table "
			+ TABLE_NETWORK + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NET_TYPE
			+ " text not null, " + COLUMN_PACKETS_RECEIVED
			+ " text not null, " + COLUMN_NETWORK_TIMESTAMP
			+ " text not null)";
	
	public static final String TABLE_SCREEN_STATUS = "screen";
	public static final String COLUMN_SCREEN_ON = "screenStatus";
	public static final String COLUMN_SCREEN_TIMESTAMP = "timestamp";
	public static final String SCREEN_CREATE = "create table "
			+ TABLE_SCREEN_STATUS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_SCREEN_ON
			+ " text not null, " + COLUMN_SCREEN_TIMESTAMP
			+ " text not null)";

	public DatabaseHelper(Context context, String date) {
		super(context, MainService.UUID +"_"+ date+"_"+DATABASE_NAME, null, DATABASE_VERSION);
		create(this.getWritableDatabase());
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		create(database);
	}
	public static void create(SQLiteDatabase database){
		System.out.println("SQL CREATE");
		try{database.execSQL(CALL_CREATE);}catch(Exception e){}
		try{database.execSQL(SMS_CREATE);}catch(Exception e){}
		try{database.execSQL(WEB_CREATE);}catch(Exception e){}
		try{database.execSQL(LOC_CREATE);}catch(Exception e){}
		try{database.execSQL(APP_CREATE);}catch(Exception e){}
		try{database.execSQL(WIFI_CREATE);}catch(Exception e){}
		try{database.execSQL(CELLULAR_CONNECTIONS_CREATE);}catch(Exception e){}
		try{database.execSQL(DEVICE_STATUS_CREATE);}catch(Exception e){}
		try{database.execSQL(NETWORK_CREATE);}catch(Exception e){}
		try{database.execSQL(SCREEN_CREATE);}catch(Exception e){}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS);
		onCreate(db);
	}

} 