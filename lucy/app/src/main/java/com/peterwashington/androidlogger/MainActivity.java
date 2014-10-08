package com.peterwashington.androidlogger;

import edu.rice.recg.livelab.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    //Box client ID and client secret; unique to an account
    private static final String CLIENT_ID = "mzjzj8rlsqm39ukjfhwol63b3ibakq6v";
    private static final String CLIENT_SECRET = "aVDMgDxYvndOjxGWboAX7yYLXFRpKvdY";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent myIntent = new Intent(getApplicationContext(), MainService.class);
	    startService(myIntent);
	    
	    final SharedPreferences URIpref= getApplicationContext().getSharedPreferences("URIpref", MODE_PRIVATE);
		String server = URIpref.getString("server", null);
	    Uploader.setServer(server);
	    final TextView tv = (TextView)findViewById(R.id.servername);
	    tv.setText(server);
	    final EditText tv2 = (EditText)findViewById(R.id.serverURI);
	    tv2.setText(server);
	    Button button = (Button)findViewById(R.id.setServer);
	    
	    button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String server = tv2.getText().toString();
            	Uploader.setServer(server);
         	    tv.setText(server);
         	    
         	    Editor editor = URIpref.edit();
         	    editor.putString("server", server);
         	    editor.commit();
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
