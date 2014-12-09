package com.riceucla.mobilelogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class Uploader {

	public static String urlServer = "ec2-54-85-147-87.compute-1.amazonaws.com/upload";
    private String UUID = "";

	public static void setServer(String server) 
	{
		urlServer = server;
	}

	public static boolean upload(SQLiteDatabase database, String UUID)
	{
			try {
                //for testing purpose
                final String UPLOAD_BASE_URL = "http://ec2-54-85-147-87.compute-1.amazonaws.com/upload";

                for (String table : DatabaseHelper.tables.keySet()) {

                    Cursor c = database.query(table, null, null, null, null, null, null);
                    JSONArray json = cur2Json(c);

                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(UPLOAD_BASE_URL);

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("uuid", UUID));
                    nameValuePairs.add(new BasicNameValuePair("data_type", table));
                    nameValuePairs.add(new BasicNameValuePair("json", json.toString()));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    Log.v("mobilelogger uploader headers:", getHeadersAsString(httppost.getAllHeaders()));
                    Log.v("mobilelogger uploader response", response.toString());
                }

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("mobilelogger upload exception : " + e.getMessage());
				return false;
			}
			return true;
	}

    /**
     * convert the given database cursor into a JSONArray object
     * @param cursor
     * @return
     */
    public static JSONArray cur2Json(Cursor cursor) {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("TEST", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }

    /**
     * Print http headers. Useful for debugging.
     *
     * @param headers
     */
    public static String getHeadersAsString(Header[] headers) {

        StringBuffer s = new StringBuffer("Headers:");
        s.append("------------");
        for (Header h : headers)
            s.append(h.toString());
        s.append("------------");
        return s.toString();
    }

}
