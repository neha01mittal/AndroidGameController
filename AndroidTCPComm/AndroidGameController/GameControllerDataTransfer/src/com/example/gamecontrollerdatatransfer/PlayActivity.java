package com.example.gamecontrollerdatatransfer;

import gc.common_resources.CommandType;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {

	private String ipAddress;
	private final float NOISE = (float) 2.0;
	Socket socket = null;
	private boolean connected = false;
	private SensorManager mSensorManager;
	private MyRenderer mRenderer;
	private boolean isStartPositionRegistered;
	private float startX = 0;
	private float startY = 0;
	private float startZ = 0;
	private boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Create our Preview view and set it as the content of our
		// Activity
		mRenderer = new MyRenderer();
		isStartPositionRegistered = false;
		setContentView(R.layout.activity_play);
		/* do this in onCreate */
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ipAddress = extras.getString("ipAddress");
			// clientConnection();
		}

		// record initial coordinates

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) {
								Toast.makeText(getApplicationContext(),
										"Tilt Detection disabled",
										Toast.LENGTH_SHORT).show();
							}
						});

		builder.show();

	}

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
		if (!connected) {
			Thread cThread = new Thread(new ClientSocketThread(updateCommand));
			cThread.start();
		}
	}

	// Thread class to send commands to the Server
	public class ClientSocketThread implements Runnable {

		private CommandType commandToSend;

		public ClientSocketThread(CommandType currCommand) {
			commandToSend = currCommand;

			Log.d("PlayActivity - ThreadConstructor", "commandToSend: "
					+ commandToSend + " " + commandToSend.getX() + " "
					+ commandToSend.getY());
		}

		public void run() {
			try {
				Socket socket = new Socket(ipAddress, 8888);
				ObjectOutputStream objOutputStream = new ObjectOutputStream(
						socket.getOutputStream());
				connected = true;

				try {
					Log.d("PlayActivity", "Sending: " + commandToSend + " "
							+ commandToSend.getX() + " " + commandToSend.getY());

					// Send the enum(commandToSend) and the fields(X and Y)
					// separately
					// as the serializing and deserializing of enum through
					// ObjectOutputStream
					// and ObjectInputStream will not save the fields in the
					// enum
					objOutputStream.writeObject(commandToSend);
					objOutputStream.writeFloat(commandToSend.getX());
					objOutputStream.writeFloat(commandToSend.getY());
				} catch (Exception e) {
					Log.e("PlayActivity", "S: Error", e);
				}
				// Close outputstream and socket
				objOutputStream.close();
				socket.close();
				connected = false;
			} catch (Exception e) {
				Log.e("PlayActivity", "C: Error", e);
			}
		}
	}

	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		mRenderer.start();
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
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

		public void onSensorChanged(SensorEvent event) {

			ImageView iv = (ImageView) findViewById(R.id.image);
			TextView tvX = (TextView) findViewById(R.id.x_axis);
			TextView tvY = (TextView) findViewById(R.id.y_axis);
			TextView tvZ = (TextView) findViewById(R.id.z_axis);

			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			/*
			 * System.out.println("X= " + values[0] + " Y= " + values[1] +
			 * " Z= " + values[2]);
			 */
			if (flag) {
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
					System.out.println("Delta 1=" + deltaX + " Delta 2="
							+ deltaY + " Delta 3=" + deltaZ);

					if (deltaX < NOISE)
						deltaX = (float) 0.0;
					if (deltaY < NOISE)
						deltaY = (float) 0.0;
					if (deltaZ < NOISE)
						deltaZ = (float) 0.0;

					tvX.setText(Float.toString(deltaX));
					tvY.setText(Float.toString(deltaY));
					tvZ.setText(Float.toString(deltaZ));
					iv.setVisibility(View.VISIBLE);
/*					if (deltaZ > deltaX && deltaZ > deltaY) {
						Toast.makeText(getApplicationContext(),
								"Z axis movement", Toast.LENGTH_SHORT).show();
						iv.setImageResource(R.drawable.ic_launcher);
					} else {*/
						if (deltaX > deltaY) {
							Toast.makeText(getApplicationContext(),
									"X axis movement", Toast.LENGTH_SHORT)
									.show();
							iv.setImageResource(R.drawable.horizontal);
						} else if (deltaX < deltaY) {
							Toast.makeText(getApplicationContext(),
									"Y axis movement", Toast.LENGTH_SHORT)
									.show();
							iv.setImageResource(R.drawable.vertical);
						} else {
							// NOP
							iv.setVisibility(View.INVISIBLE);
						}
				}
			}

		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	}

}