package com.riceucla.mobilelogger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Uploader {

	public static String urlServer = "http://ec2-54-164-148-215.compute-1.amazonaws.com/receive.php";

	public static void setServer(String server) 
	{
		urlServer = server;
	}

	public static boolean upload(SQLiteDatabase database)
	{
		//String fileName = sourceFileUri;

		HttpURLConnection conn = null;
        /**
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);
		if (!sourceFile.isFile()) {
			System.out.println("Source File not exist :"
					+ fileName);
			return false;
		} else {**/
			try {
				// open a URL connection to the Servlet
				//FileInputStream fileInputStream = new FileInputStream(
				//		sourceFile);
				URL url = new URL(urlServer);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Connection", "Keep-Alive");
                //for testing purpose, log the result
                for (String table : DatabaseHelper.tables.keySet()) {
                    Cursor c = database.query(table, null, null, null, null, null, null);
                    JSONArray json = cur2Json(c);
                    //for testing purpose, just log the the json array
                    Log.v("Json result " + table, json.toString());
                }
                /** old code, update with the json converter
				//conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				//conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				//conn.setRequestProperty("uploaded_file", fileName);

				dos = new DataOutputStream(conn.getOutputStream());

				//dos.writeBytes(twoHyphens + boundary + lineEnd);
				//dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);

				//dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				int serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				System.out.println("HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {
					System.out.println("SUCCESSFUL UPLOAD!");
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();**/

			} catch (MalformedURLException ex) {
				System.err.println("MalformedURLException Exception : check script url.");
				ex.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Exception : " + e.getMessage());
				return false;
			}
			return true;
		//} // End else block
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

}
