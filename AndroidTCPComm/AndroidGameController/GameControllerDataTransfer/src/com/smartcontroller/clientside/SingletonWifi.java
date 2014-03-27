package com.smartcontroller.clientside;

import gc.common_resources.CommandType;

import java.io.ObjectOutputStream;
import java.net.Socket;

import android.util.Log;

public class SingletonWifi {

	private static SingletonWifi mInstance = null;
	private static String ipAddress;

	private ClientSocketThread mThread = null;

	private SingletonWifi() {
	}

	public static SingletonWifi getInstance() {
		if (mInstance == null) {
			mInstance = new SingletonWifi();
		}
		return mInstance;
	}

	public void setIPAddress(String ipString) {
		ipAddress = new String(ipString);
	}

	public void sendToDevice(CommandType currCommand) {
		// Initiate a new thread
		mThread = new ClientSocketThread(currCommand, ipAddress);
		mThread.start();
	}

	/**
	 * Thread class to send commands to the Server
	 * 
	 * @author neha01mittal
	 * 
	 */
	public class ClientSocketThread extends Thread {

		private CommandType commandToSend;
		private Socket socket = null;
		private ObjectOutputStream objOutputStream;
		private String ipAddress;

		public ClientSocketThread(CommandType currCommand, String ipString) {
			commandToSend = currCommand;
			ipAddress = new String(ipString);

			Log.d("PlayActivity - ThreadConstructor", "commandToSend: "
					+ commandToSend + " " + commandToSend.getX() + " "
					+ commandToSend.getY());
		}

		/**
		 * wrap and send commands from client to server over socket connection
		 */
		public void run() {
			try {
				socket = new Socket(ipAddress, 8888);
				objOutputStream = new ObjectOutputStream(
						socket.getOutputStream());

				try {
					Log.d("SingletonWifi", "Sending: " + commandToSend + " "
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
					Log.e("SingletonWifi", "S: Error", e);
				}
				// Close outputstream and socket
				objOutputStream.close();
				socket.close();

			} catch (Exception e) {
				Log.e("SingletonWifi", "C: Error", e);
			}
		}
	}
}
