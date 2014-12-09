package com.riceucla.mobilelogger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import android.util.Log;

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
		dbHelper = new DatabaseHelper(getApplicationContext(), UUID, getDate());
		
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
        int id = item.getItemId();
        if (id == R.id.action_lastsynctime) {
            lastSyncDialog();
            return true;
        }
        //force sync on a seperate thread
        if (id == R.id.action_sync){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        MainService.forceUpload();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
            return true;
        }
        if (id == R.id.action_uuid){
            uuidDialog();
            return true;
        }
		return false;
	}

    /**
     * Converts a Calendar object to a String-represented timestamp.
     *
     * @param c: Calendar object
     * @return: Human-readable timestamp as a String
     *
     * @author Kevin Lin
     * @since 11/28/2014
     */
    public static String getTimestamp(Calendar c) {
        int second = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY)%12;
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int ampm = c.get(Calendar.AM_PM);
        String timestamp = "" + month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second;
        if (ampm == Calendar.AM)
            timestamp += " AM";
        else
            timestamp += " PM";
        return timestamp;
    }

    /**
     * Pop-up dialog informing the user of the last time a sync was performed.
     *
     * @author Kevin Lin
     * @since 11/28/2014
     */
    private void lastSyncDialog() {
        //Get the last sync time
        long lastSyncTime = getApplicationContext().getSharedPreferences("LastCheck", MODE_PRIVATE).getLong("uploadSuccess", System.currentTimeMillis()-5*60000);
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastSyncTime);
        String lastSyncTimeString = formatter.format(calendar.getTime());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Last sync performed on " + lastSyncTimeString).setTitle("Last sync time");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();
    }

    private void uuidDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your UUID is: " + UUID).setTitle("UUID");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();
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
