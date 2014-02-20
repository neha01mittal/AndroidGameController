package com.example.gamecontrollerdatatransfer;

import com.example.gamecontrollerdatatransfer.R;
import com.example.gamecontrollerdatatransfer.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ControlsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controls);
		long time1= System.currentTimeMillis();
		
		while(true) {
			if(System.currentTimeMillis()-time1>10000) {
				Intent k = new Intent(ControlsActivity.this, TiltLeftActivity.class);
				startActivity(k);
			}
		}
	}
}
