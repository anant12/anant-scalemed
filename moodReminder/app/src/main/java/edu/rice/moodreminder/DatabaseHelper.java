package edu.rice.moodreminder;

import android.content.Context; 
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.util.Log;

/*import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;*/
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String COLUMN_ID = "_id";
	
	private static final String DATABASE_NAME = "LiveLab.db";
	private static final int DATABASE_VERSION = 1;

	public static HashMap<String, String[]> tables = new HashMap<String, String[]>();
	
	// used to restrict simultaneous access to the database from the service and the activity
	public final Semaphore semaphore = new Semaphore(1, true);
	
	public static final String TABLE_MOOD = "mood";
	public static final String COLUMN_MOOD_MOOD = "moodlevel";
	public static final String COLUMN_MOOD_ACTIVITY = "activitylevel";
	private static final String MOOD_CREATE = "create table if not exists "
			+ TABLE_MOOD + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_MOOD_MOOD
			+ " text not null, " + COLUMN_MOOD_ACTIVITY
			+ " text not null, ";
	String[] moodCols = {COLUMN_ID, COLUMN_MOOD_MOOD, COLUMN_MOOD_ACTIVITY};

	public DatabaseHelper(Context context, String date) 
	{
        //TODO: Figure out what's wrong with UUID
		super(context, MainActivity.UUID + "_"+DATABASE_NAME, null, DATABASE_VERSION);
		create(this.getWritableDatabase());
		
		tables.put(TABLE_MOOD, moodCols);
	}
	
	
/*	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		tables.put(TABLE_CALLS, callsCols);
		tables.put(TABLE_SMS, smsCols);
		tables.put(TABLE_WEB, webCols);
		tables.put(TABLE_LOC, locCols);
		tables.put(TABLE_APP, appCols);
		tables.put(TABLE_WIFI, wifiCols);
		tables.put(TABLE_CELLULAR_CONNECTIONS, cellularCols);
		tables.put(TABLE_NETWORK, networkCols);
		tables.put(TABLE_DEVICE_STATUS, deviceCols);
		tables.put(TABLE_SCREEN_STATUS, screenCols);
		
		// uncomment the following in the first run ONLY IF you are uploading a readily available database
		// to a device
		try
		{
	    	//Open your local db as the input stream
	    	InputStream myInput = context.getAssets().open(DATABASE_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = "/data/data/edu.ucla.hssrp/databases/" + DATABASE_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
		}
		catch(IOException o)
		{
			throw new Error("Error copying database");
		}
	}*/

	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		create(database);
	}
	
	public static void create(SQLiteDatabase database)
	{
		System.out.println("SQL CREATE");
		try{database.execSQL(MOOD_CREATE);}catch(Exception e){}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD);
		onCreate(db);
	}

} 