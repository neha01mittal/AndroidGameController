package com.example.gamecontrollerdatatransfer;

import com.example.gamecontrollerdatatransfer.R;
import com.example.gamecontrollerdatatransfer.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TiltDownActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tiltdown);
		long time1= System.currentTimeMillis();
		
		while(true) {
			if(System.currentTimeMillis()-time1>5000) {
				Intent k = new Intent(TiltDownActivity.this, ButtonsActivity.class);
				startActivity(k);
			}
		}
	}
}
