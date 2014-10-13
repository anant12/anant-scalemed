package com.riceucla.mobilelogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity 
{
	// public static final String SDCardDBdir = "/hssrp/data/";  //default SD Card directory for storing database
	
	// the parameters to be displayed on home screen. these are to be set from the
	// settings page later.
	public static boolean showWiFiSignalStrength = true;
	public static boolean showWiFiConnectionSpeed = true;
	public static boolean showWiFiSSID = true;
	public static boolean showCellularSignalStrength = true;
	public static boolean showCellularNetworkType = true;
	public static boolean showLocationCoordinates = true;
	public static boolean showLocationSpeed = false;
	public static boolean showDeviceBatteryLevel = true;
	
	public static DatabaseHelper dbHelper;
	public static String UUID;
	
	// Navigation Drawer related variables here
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	CustomDrawerAdapter adapter;
	List<DrawerItem> dataList;
	public static String myPackageName;
	
	//google maps related variables here
	public static FragmentManager fragmentManager;  // for google maps use
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setUUID();
		
		// create the database helper
		dbHelper = new DatabaseHelper(getApplicationContext(), getDate());
		
		// call the logging service immediately
	    startService(new Intent(getApplicationContext(), MainService.class));
	    
		// start the home fragment
		FragmentManager frgManager = getSupportFragmentManager();
		fragmentManager = frgManager;
		frgManager.beginTransaction().replace(R.id.content_frame, new HomeFragment())
			.commit();
		
		// Retrieve package name for database import/export use
		myPackageName = getApplicationContext().getPackageName();

		// Initializing navigation drawer
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		dataList.add(new DrawerItem("Home", R.drawable.icon_home));
		dataList.add(new DrawerItem("WiFi", R.drawable.icon_wifi));
		dataList.add(new DrawerItem("Cellular", R.drawable.icon_cellular));
		dataList.add(new DrawerItem("Traffic", R.drawable.icon_traffic_arrow));
		dataList.add(new DrawerItem("Location", R.drawable.icon_location));
		dataList.add(new DrawerItem("Device", R.drawable.icon_device));
		
		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
				dataList);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);


        /*
        Temporary implementation.
        On app launch, generates the mood reminder notification.
        Tapping the notification opens MoodReminder.java which opens the mood reminder fragment.
         */
        generateMoodNotification(getApplicationContext(), "What's your current mood?", "Tap here to answer.");
    }

    /**
     * Generates a notification that opens the dialog reminding the user to log his/her mood.
     * Temporarily in MainActivity.class
     * TODO: Call this method periodically (i.e. once every n hours)
     *
     * Kevin Lin, 10/12/2014
     *
     * @param context Application context, from getApplicationContext()
     * @param title Title of the notification
     * @param message Message of the notification
     */
    private static void generateMoodNotification(Context context, String title, String message) {
        int icon = R.drawable.ic_launcher; //TODO: Change this icon
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        Intent notificationIntent = new Intent(context, MoodReminder.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// fragment selection logic
	public void SelectItem(int position) {

		Fragment fragment = null;
		Bundle args = new Bundle();

		switch (position) 
		{

		case 0: // Home Fragment
			fragment = new HomeFragment();
			args.putString(HomeFragment.ITEM_NAME, dataList.get(position)
					.getItemName());
			args.putInt(HomeFragment.IMAGE_RESOURCE_ID, dataList.get(position)
					.getImgResID());
			break;
		case 1: // WiFi Fragment
			fragment = new WiFiFragment();
			args.putString(WiFiFragment.ITEM_NAME, dataList.get(position)
					.getItemName());
			args.putInt(WiFiFragment.IMAGE_RESOURCE_ID, dataList.get(position)
					.getImgResID());
			break;
		case 2:  // Cellular Fragment
			fragment = new CellularFragment();
			args.putString(CellularFragment.ITEM_NAME, dataList.get(position)
					.getItemName());
			args.putInt(CellularFragment.IMAGE_RESOURCE_ID, dataList.get(position)
					.getImgResID());
			break;
		case 3:  // Traffic Fragment
			fragment = new TrafficFragment();
			args.putString(TrafficFragment.ITEM_NAME, dataList.get(position)
					.getItemName());
			args.putInt(TrafficFragment.IMAGE_RESOURCE_ID, dataList.get(position)
					.getImgResID());
			break;
		case 4: // Location Fragment
			fragment = new LocationFragment();
			args.putString(LocationFragment.ITEM_NAME, dataList.get(position)
					.getItemName());
			args.putInt(LocationFragment.IMAGE_RESOURCE_ID, dataList.get(position)
					.getImgResID());
			break;
		case 5: // Device Fragment
			fragment = new DeviceFragment();
			args.putString(DeviceFragment.ITEM_NAME, dataList.get(position)
					.getItemName());
			args.putInt(DeviceFragment.IMAGE_RESOURCE_ID, dataList.get(position)
					.getImgResID());
			break;
		default:
			break;
		}
		
		// To prevent crash, do the following only when the fragment is not null
		if (fragment != null)
		{ 
			fragment.setArguments(args);
			FragmentManager frgManager = getSupportFragmentManager();
			fragmentManager = frgManager;
			frgManager.beginTransaction().replace(R.id.content_frame, fragment)
				.commit();
		}
		mDrawerList.setItemChecked(position, true);
		setTitle(position==0 ? getTitle() : dataList.get(position).getItemName() );
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (dataList.get(position).getTitle() == null) {
				SelectItem(position);
			}

		}
	}
	
	private void setUUID()
	{
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		UUID=tm.getDeviceId();
		
		// UUID="";
	}
	
	@SuppressLint("SimpleDateFormat") 
	public static String getDate()
	{
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
		return dateFormat.format(today);
	}
}