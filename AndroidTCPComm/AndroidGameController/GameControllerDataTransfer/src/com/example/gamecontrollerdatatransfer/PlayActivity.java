package com.example.gamecontrollerdatatransfer;

import gc.common_resources.CommandType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

	private final Context context = this;

	private SensorManager mSensorManager;
	private MyRenderer mRenderer;
	private View commonView;
	private boolean doubleBackToExitPressedOnce = false;
	private String connectType;

	private final float NOISE = (float) 2.0;
	private float startX = 0, startY = 0, startZ = 0;
	private boolean flag = false, isPaused = false;
	private boolean isStartPositionRegistered;

	public int tiltState = 0;
	public float tiltX, tiltY;

	private int bgColor = Color.TRANSPARENT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Create our Preview view and set it as the content of our
		// Activity
		mRenderer = new MyRenderer();
		isStartPositionRegistered = false;
		setContentView(R.layout.activity_play);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		/* do this in onCreate */
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			connectType = extras.getString("connectType");
		}

		registerRestPosition();

		final ImageButton resetButton = (ImageButton) findViewById(R.id.buttonreset);
		resetButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = false;
				isStartPositionRegistered = false;
				registerRestPosition();
			}
		});

		final ImageButton playPauseButton = (ImageButton) findViewById(R.id.buttonplaypause);
		playPauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isPaused) {
					// Detection enabled, show the pause button
					playPauseButton
							.setBackgroundResource(R.drawable.pausebutton);
					Toast.makeText(getApplicationContext(),
							"Tilt detection enabled", Toast.LENGTH_SHORT)
							.show();
					isPaused = false;
					onResume();
				} else {
					// Detection disabled, show the pause button
					playPauseButton
							.setBackgroundResource(R.drawable.playbutton);
					Toast.makeText(getApplicationContext(),
							"Tilt detection disabled", Toast.LENGTH_SHORT)
							.show();
					isPaused = true;
					onPause();
				}
			}
		});

	}

	/**
	 * register position for accelerometer tilt detection
	 */
	public void registerRestPosition() {
		// record initial coordinates

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose your position");
		builder.setMessage(R.string.get_rest_position_message)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) { //
								// User clicked OK, so save the mSelectedItems
								// results somewhere // or
								// return them to the component that opened the
								// dialog

								System.out.println("Register x and y here");
								flag = true;
								System.out.println("START VALUES" + startX
										+ " " + startY);
							}
						});
		builder.show();
	}

	public boolean isTiltDetectionOn() {
		return !isPaused;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (connectType.equals("bluetooth"))
			SingletonBluetooth.getInstance().closeConnection();
	}

	@Override
	public void onBackPressed() {
		// disable back button
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please tap BACK again to exit",
				Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);

	}

	/**
	 * wrap command to be sent to server
	 * @param tc
	 * @param newCommand
	 */
	public void updateCoordinates(TouchCoordinates tc, CommandType newCommand) {
		// We don't keep a set of tc and commandtype in this class anymore
		// So this function only packs the command with fields and
		// passes the data to sendPacketToServer()
		CommandType updateCommand = newCommand;
		updateCommand.setX(tc.getX());
		updateCommand.setY(tc.getY());
		updateCommand.setZ(tc.getZ());
		updateCommand.setW(tc.getW());

		sendPacketToServer(updateCommand);
	}

	// Create the threads and pass the commands to them
	public void sendPacketToServer(CommandType updateCommand) {
		if (connectType.equals("wifi")) {
			SingletonWifi.getInstance().sendToDevice(updateCommand);
		} else if (connectType.equals("bluetooth")) {
			SingletonBluetooth.getInstance().sendToDevice(updateCommand);
		}
	}

	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();

		commonView = findViewById(R.id.playActivity);
		commonView.setBackgroundColor(bgColor);
		commonView.invalidate();
		mRenderer.start();
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		bgColor = Color.TRANSPARENT;
		
		System.out.println("Setting background");
		commonView = findViewById(R.id.playActivity);
		commonView.setBackgroundColor(bgColor);
		commonView.invalidate();
		mRenderer.stop();
	}

	class MyRenderer implements SensorEventListener {
		private Sensor mRotationVectorSensor;

		public MyRenderer() {
			// find the rotation-vector sensor
			mRotationVectorSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		}

		public void start() {
			// enable our sensor when the activity is resumed, ask for
			// 10 ms updates.
			mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
		}

		public void stop() {
			// make sure to turn our sensor off when the activity is paused
			mSensorManager.unregisterListener(this);
		}

		/**
		 * detect tilt based on noise
		 */
		public void onSensorChanged(SensorEvent event) {

			ImageView iv = (ImageView) findViewById(R.id.image);
			TextView tvX = (TextView) findViewById(R.id.x_axis);
			TextView tvY = (TextView) findViewById(R.id.y_axis);
			TextView tvZ = (TextView) findViewById(R.id.z_axis);

			commonView = findViewById(R.id.playActivity);
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			if (flag) {
				if (!isPaused) {
					if (!isStartPositionRegistered) {
						tvX.setText("0.0");
						tvY.setText("0.0");
						tvZ.setText("0.0");
						startX = x;
						startY = y;
						startZ = z;
						isStartPositionRegistered = true;
					}

					else {
						float deltaX = Math.abs(startX - x);
						float deltaY = Math.abs(startY - y);
						float deltaZ = Math.abs(startZ - z);
						tiltX = startX - x;
						tiltY = startY - y;
						if (deltaX < NOISE)
							deltaX = (float) 0.0;
						if (deltaY < NOISE)
							deltaY = (float) 0.0;
						if (deltaZ < NOISE)
							deltaZ = (float) 0.0;

						tvX.setText(Float.toString(deltaX));
						tvY.setText(Float.toString(deltaY));
						tvZ.setText(Float.toString(deltaZ));
						// change to visible for testing/ debugging
						tvX.setVisibility(View.INVISIBLE);
						tvY.setVisibility(View.INVISIBLE);
						tvZ.setVisibility(View.INVISIBLE);

						iv.setVisibility(View.INVISIBLE);

						if (deltaX > deltaY) {
							iv.setImageResource(R.drawable.horizontal);

							if ((startX - x) < 0) {
								// down tilt
								bgColor = Color.parseColor("#ff669966");
								tiltState = 1; // TILT UP
							} else {
								bgColor = Color.parseColor("#ff668899");
								tiltState = 2; // TILT DOWN
							}

							System.out.println("Delta 1=" + (startX - x)
									+ " Delta 2=" + (startY - y));

						} else if (deltaX < deltaY) {
							iv.setImageResource(R.drawable.vertical);

							if ((startY - y) < 0) {
								bgColor = Color.parseColor("#ff6E6699");
								tiltState = 3; // TILT LEFT
							} else {
								bgColor = Color.parseColor("#ff886699");
								tiltState = 4; // TILT RIGHT
							}

							System.out.println("Delta 1=" + (startX - x)
									+ " Delta 2=" + (startY - y));

						} else {
							// NOP
							iv.setVisibility(View.INVISIBLE);
							bgColor = Color.TRANSPARENT;
							tiltState = 0; // NO TILT
						}
						commonView.setBackgroundColor(bgColor);
						commonView.invalidate();
					}
				} else {
					// Don't detect tilt
					tiltState = 0; // NO TILT
				}
			}

		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	}

}