package com.example.gamecontrollerdatatransfer;

//@CLEANED
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	private final Context context = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		ImageButton wifiConnect = (ImageButton) findViewById(R.id.wifi);
		wifiConnect.setOnClickListener(buttonWifiConnect);
		ImageButton bluetooth = (ImageButton) findViewById(R.id.bluetooth);
		bluetooth.setOnClickListener(buttonBluetooth);

		Button goToTutorials = (Button) findViewById(R.id.tutorials);
		goToTutorials.setOnClickListener(buttonTutorials);
		showInstructions();
	}

	Button.OnClickListener buttonTutorials = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent k = new Intent(MainActivity.this, TutorialActivity.class);
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

	public void showInstructions() {
		// record initial coordinates

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Important!");
		builder.setMessage(R.string.instructions).setPositiveButton(
				R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) { //
						// User clicked OK, so save the mSelectedItems
						// results somewhere // or
						// return them to the component that opened the
						// dialog

						// NOP
					}
				});
		builder.show();
	}
}
