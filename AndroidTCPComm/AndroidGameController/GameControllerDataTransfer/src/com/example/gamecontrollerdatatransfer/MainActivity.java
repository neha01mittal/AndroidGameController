package com.example.gamecontrollerdatatransfer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ImageButton wifiConnect = (ImageButton) findViewById(R.id.wifi);
		wifiConnect.setOnClickListener(buttonWifiConnect);
		ImageButton bluetooth = (ImageButton) findViewById(R.id.bluetooth);
		bluetooth.setOnClickListener(buttonBluetooth);
		ImageButton usb = (ImageButton) findViewById(R.id.usb);
		usb.setOnClickListener(buttonUsb);
		
		Button goToTutorials = (Button) findViewById(R.id.tutorials);
		goToTutorials.setOnClickListener(buttonTutorials);
		

	}
	
	Button.OnClickListener buttonTutorials = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent k = new Intent(MainActivity.this, PositionActivity.class);
			startActivity(k);
		}
	};

	ImageButton.OnClickListener buttonWifiConnect = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			Intent k = new Intent(MainActivity.this, WifiConnectActivity.class);
			startActivity(k);
		}
	};

	ImageButton.OnClickListener buttonBluetooth = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			Intent k = new Intent(MainActivity.this,
					BluetoothConnectActivity.class);
			startActivity(k);
		}
	};

	ImageButton.OnClickListener buttonUsb = new ImageButton.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			Intent k = new Intent(MainActivity.this, UsbConnectActivity.class);
			startActivity(k);
		}
	};

}
