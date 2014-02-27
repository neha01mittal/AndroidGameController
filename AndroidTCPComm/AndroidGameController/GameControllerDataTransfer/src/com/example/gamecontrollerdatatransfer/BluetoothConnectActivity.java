package com.example.gamecontrollerdatatransfer;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import com.example.gamecontrollerdatatransfer.R.id;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

public class BluetoothConnectActivity extends Activity implements
		OnItemClickListener {

	private Context context = this;
	private final static int REQUEST_ENABLE_BT = 1;
	private final static int RESULT_CANCELED = 0;

	private ArrayAdapter<String> adapterPaired;
	private ListView lvPaired = null;
	private ArrayAdapter<String> adapterDiscovered;
	private ListView lvDiscovered = null;

	private ArrayList<BluetoothDevice> deviceListPaired, deviceListDiscovered;

	private IntentFilter btFilter;
	private BroadcastReceiver btReceiver;

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

	public void enableBT() {
		Intent enableBtIntent = new Intent(SingletonBluetooth.getInstance()
				.getBTAdapter().ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

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
				adapterPaired.add(device.getName() + "\n" + device.getAddress());
				Log.d("BT Paired Device: ", device.getName() + "\n" + device.getAddress()); // only
																		// keys
																		// with
																		// true
																		// are
																		// shown
			}
		}
	}

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
				}else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
						.equals(action)) {
					Toast.makeText(getApplicationContext(), "Started bluetooth discovery...", Toast.LENGTH_SHORT);
					
				}else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {
					Toast.makeText(getApplicationContext(), "Bluetooth discovery ended", Toast.LENGTH_SHORT);
					
				}
			}
		};
		// Register the BroadcastReceiver
		btFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(btReceiver, btFilter);
		btFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
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
		
		if(SingletonBluetooth.getInstance().isDiscovering())
			SingletonBluetooth.getInstance().cancelDiscovery();
		
		// The item clicked is from paired device listview
		if(arg0.getId() == lvPaired.getId()) {
			BluetoothDevice device = deviceListPaired.get(arg2);
			
			Toast.makeText( getApplicationContext(),
					"Clicked device " + device.getName() + "\n" + device.getAddress(),
					Toast.LENGTH_SHORT).show();
			
			SingletonBluetooth.getInstance().connectToDevice(deviceListPaired.get(arg2));
			

			Intent k = new Intent(BluetoothConnectActivity.this, PlayActivity.class);
			k.putExtra("connectType", "bluetooth");
			startActivity(k);
		}
		
		if(arg0.getId() == lvDiscovered.getId()) {
			Toast.makeText(
					getApplicationContext(),
					"Clicked a discovered device",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showInstructions() {
		// record initial coordinates

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Important!");
		builder.setMessage(R.string.bluetoothinstructions)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int id) { //
								// User clicked OK, so save the mSelectedItems
								// results somewhere // or
								// return them to the component that opened the
								// dialog

								//NOP
							}
						});
		builder.show();
	}

}
