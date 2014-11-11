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
			+ " text not null)";
	String[] moodCols = {COLUMN_ID, COLUMN_MOOD_MOOD, COLUMN_MOOD_ACTIVITY};

	public DatabaseHelper(Context context, String date) 
	{
		super(context, MainActivity.UUID + "_"+DATABASE_NAME, null, DATABASE_VERSION);
		create(this.getWritableDatabase());
		
		tables.put(TABLE_MOOD, moodCols);
	}

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