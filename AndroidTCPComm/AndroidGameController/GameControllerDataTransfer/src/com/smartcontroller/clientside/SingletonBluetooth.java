package com.smartcontroller.clientside;

import gc.common_resources.CommandType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class SingletonBluetooth {

	private static SingletonBluetooth mInstance = null;
	private static ConnectBTThread mThread = null;

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothDevice deviceToConnect;
	private Set<BluetoothDevice> pairedDevices;
	private Set<BluetoothDevice> discoveredDevices;

	private SingletonBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public static SingletonBluetooth getInstance() {
		if (mInstance == null) {
			mInstance = new SingletonBluetooth();
		}
		return mInstance;
	}

	public BluetoothAdapter getBTAdapter() {
		// If the device is not equipped with bluetooth
		// this will return null
		return mBluetoothAdapter;
	}

	public boolean BTisEnabled() {
		return mBluetoothAdapter.isEnabled();
	}

	public void setPairedDevicesList() {
		pairedDevices = mBluetoothAdapter.getBondedDevices();
	}

	public Set<BluetoothDevice> getPairedDevicesList() {
		return pairedDevices;
	}

	public void updateDiscoveredDevices(BluetoothDevice btDevice) {
		discoveredDevices.add(btDevice);
	}

	public void startDiscovery() {
		mBluetoothAdapter.cancelDiscovery();
		mBluetoothAdapter.startDiscovery();
	}

	public void cancelDiscovery() {
		mBluetoothAdapter.cancelDiscovery();
	}

	public boolean isDiscovering() {
		return mBluetoothAdapter.isDiscovering();
	}

	public void connectToDevice(BluetoothDevice device) {
		deviceToConnect = device;

		// If there is an existing thread, tell it to stop
		if (mThread != null) {
			Log.d("BTManagement", "I set bRun false");
			mThread.bRun = false;
		}

		// Initiate a new thread
		mThread = new ConnectBTThread(deviceToConnect);
		mThread.start();
	}

	public void sendToDevice(CommandType commandToSend) {
		// synchronized (mThread.commandToSend) {
		mThread.commandToSend = commandToSend;
		// }
	}

	public void closeConnection() {
		Log.d("BTManagement", "I set bRun false");
		mThread.bRun = false;
	}

	private class ConnectBTThread extends Thread {
		private final UUID MY_UUID = UUID
				.fromString("00001101-0000-1000-8000-00805f9b34fb");
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		public CommandType commandToSend = null;
		public boolean bRun = true;

		private ObjectOutputStream BTOOStream;
		private ObjectInputStream BTOIStream;

		public ConnectBTThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			// mBluetoothAdapter.cancelDiscovery();
			// Log.d("BluetoothManagement", "Cancelled BT Discovery");

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				Log.d("BTManagement", "Trying Socket Connection with "
						+ mmDevice.getName());
				mmSocket.connect();

				BTOOStream = new ObjectOutputStream(mmSocket.getOutputStream());
				BTOOStream.flush();
				BTOIStream = new ObjectInputStream(mmSocket.getInputStream());

				// Do work to manage the connection (in a separate thread)
				while (bRun) {
					// Log.d("BTManagement", "Connected with" +
					// mmDevice.getName());
					manageConnection();
				}

				// Close outputstream and socket
				BTOOStream.close();
				cancel();

			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

		}

		private void manageConnection() {
			// TODO Auto-generated method stub
			// Log.d("BTManagement",
			// "I am in manage Connected socket function");

			if (commandToSend != null) {
				try {
					Log.d("BluetoothManagement",
							"Sending: " + commandToSend + " "
									+ commandToSend.getX() + " "
									+ commandToSend.getY());

					// Send the enum(commandToSend) and the fields(X and Y)
					// separately
					// as the serializing and deserializing of enum through
					// ObjectOutputStream
					// and ObjectInputStream will not save the fields in the
					// enum
					BTOOStream.writeObject(commandToSend);
					BTOOStream.writeFloat(commandToSend.getX());
					BTOOStream.writeFloat(commandToSend.getY());
					BTOOStream.flush();
				} catch (Exception e) {
					Log.e("BluetoothManagement", "S: Error", e);
				}
				// Init CommandtoSend
				commandToSend = null;
			}
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
