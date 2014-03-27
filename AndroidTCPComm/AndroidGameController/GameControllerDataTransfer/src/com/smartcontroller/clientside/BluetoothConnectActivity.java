package com.smartcontroller.clientside;

import java.util.ArrayList;

import com.smartcontroller.clientside.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothConnectActivity extends Activity implements
		OnItemClickListener {

	private Context context = this;
	private final static int REQUEST_ENABLE_BT = 1;
	private final static int RESULT_CANCELED = 0;

	private ArrayAdapter<String> adapterPaired;
	private Handler btConnectingHandler;
	private ListView lvPaired = null;
	private ArrayAdapter<String> adapterDiscovered;
	private ListView lvDiscovered = null;

	private ArrayList<BluetoothDevice> deviceListPaired, deviceListDiscovered;

	private IntentFilter btFilter;
	private BroadcastReceiver btReceiver;

	private static BluetoothDevice connDevice;
	private static boolean bConnecting = false, bConnected = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bluetooth);

		initUI();

		deviceListPaired = new ArrayList<BluetoothDevice>();
		deviceListDiscovered = new ArrayList<BluetoothDevice>();

		showInstructions();
		// BT Stuff happening in onResume() below
	}

	private void initUI() {
		// init list for paired devices
		btConnectingHandler = new Handler();
		adapterPaired = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 0);
		lvPaired = (ListView) findViewById(R.id.listBTPaired);
		lvPaired.setAdapter(adapterPaired);
		lvPaired.setOnItemClickListener(this);

		// init list for discovered devices
		adapterDiscovered = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, 0);
		lvDiscovered = (ListView) findViewById(R.id.listBTDiscovered);
		lvDiscovered.setAdapter(adapterDiscovered);
		lvDiscovered.setOnItemClickListener(this);

	}

	/**
	 * enable bluetooth connection
	 */
	public void enableBT() {
		Intent enableBtIntent = new Intent(SingletonBluetooth.getInstance()
				.getBTAdapter().ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

	/**
	 * find the list of paired devices
	 */
	public void getPairedBTDevices() {

		SingletonBluetooth.getInstance().setPairedDevicesList();
		if (SingletonBluetooth.getInstance().getPairedDevicesList().size() > 0) {

			Log.d("BT", "Found "
					+ SingletonBluetooth.getInstance().getPairedDevicesList()
							.size() + " paired BT devices");

			adapterPaired.clear();
			deviceListPaired.clear();

			// Loop through paired devices
			for (android.bluetooth.BluetoothDevice device : SingletonBluetooth
					.getInstance().getPairedDevicesList()) {
				// Add the name and address to an array adapter to show in a
				// ListView
				deviceListPaired.add(device);
				adapterPaired
						.add(device.getName() + "\n" + device.getAddress());
				Log.d("BT Paired Device: ",
						device.getName() + "\n" + device.getAddress());
				// only keys with true value are shown
			}
		}
	}

	/**
	 * update status while turning on bluetooth
	 */
	public void discoverBTDevices() {

		btReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device

				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a
					// ListView
					SingletonBluetooth.getInstance().updateDiscoveredDevices(
							device);
					deviceListDiscovered.add(device);
					adapterDiscovered.add(device.getName() + "\n"
							+ device.getAddress());
					Log.d("BT", "Discovered a BT device");

				} else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED
						.equals(action)) {
					// If someone turned off BT, turn it back on
					if (!SingletonBluetooth.getInstance().BTisEnabled()) {
						enableBT();
					}
				} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {
					Toast.makeText(getApplicationContext(),
							"Started bluetooth discovery...",
							Toast.LENGTH_SHORT).show();

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					Toast.makeText(getApplicationContext(),
							"Bluetooth discovery ended", Toast.LENGTH_SHORT)
							.show();

				} else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

					bConnected = true;

					Intent k = new Intent(BluetoothConnectActivity.this,
							PlayActivity.class);
					k.putExtra("connectType", "bluetooth");
					startActivity(k);
				}
			}
		};
		// Register the BroadcastReceiver
		btFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(
				BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		registerReceiver(btReceiver, btFilter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Request to Enable BT
		if (requestCode == REQUEST_ENABLE_BT) {
			// User cancelled the request
			if (resultCode == RESULT_CANCELED) {
				// Bounce back to main screen
				Toast.makeText(getApplicationContext(),
						"Bluetooth must be enabled to continue.",
						Toast.LENGTH_SHORT).show();
				super.finish();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// Call Singleton to acquire the bluetooth adapter
		if (SingletonBluetooth.getInstance().getBTAdapter() != null) {

			try {
				// Check if bluetooth is enable before turning on discovery
				if (!SingletonBluetooth.getInstance().BTisEnabled()) {
					// BT is not enabled. Enable it.
					enableBT();
					SingletonBluetooth.getInstance().startDiscovery();
				}
			} catch (Exception e) {
				Log.e("BT", "Exception Occured: " + e.toString());
			}

			discoverBTDevices();

			getPairedBTDevices();

		} else {
			// This device does not support bluetooth
			// Do something about this
			Toast.makeText(
					getApplicationContext(),
					"Unable to proceed further as we could not find Bluetooth on your device.",
					Toast.LENGTH_SHORT).show();
			super.finish();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(btReceiver);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		if (SingletonBluetooth.getInstance().isDiscovering())
			SingletonBluetooth.getInstance().cancelDiscovery();

		// The item clicked is from paired device listview
		if (arg0.getId() == lvPaired.getId()) {
			connDevice = deviceListPaired.get(arg2);

			bConnecting = false;
			bConnected = false;

			btConnectingHandler.removeCallbacks(mConnectTask);
			btConnectingHandler.postDelayed(mConnectTask, 100);
		}

		if (arg0.getId() == lvDiscovered.getId()) {
			Toast.makeText(getApplicationContext(),
					"Clicked a discovered device", Toast.LENGTH_SHORT).show();
		}
	}

	private Runnable mConnectTask = new Runnable() {
		public void run() {
			// This task will initiate the connection to the BT device
			// selected by the user and verify that the connection is successful

			// If bConnecting is true, we have already attempted to
			// connect to the BT device, therefore now we need to verify
			// that the connection is successful
			if (bConnecting) {
				if (!bConnected) {
					// Failed to connect to the device
					Toast.makeText(
							context,
							"Failed to connect to " + connDevice.getName()
									+ ".", Toast.LENGTH_LONG).show();
				}
				bConnecting = false;
				bConnected = false;
			} else {
				// bConnecting is false, now attempting to connect to the
				// BT device
				if (connDevice != null) {
					bConnecting = true;
					Toast.makeText(
							context,
							"Attempting to connect to " + connDevice.getName()
									+ "...", Toast.LENGTH_LONG).show();

					SingletonBluetooth.getInstance()
							.connectToDevice(connDevice);

					// Run this task again later to verify the connection
					btConnectingHandler.postDelayed(this, 7000);
				}
			}
		}
	};

	public void showInstructions() {
		// record initial coordinates

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Important!");
		builder.setMessage(R.string.bluetoothinstructions).setPositiveButton(
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