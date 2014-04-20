package com.smartcontroller.clientside;

import gc.common_resources.CommandType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class SingletonWifi {

	private static SingletonWifi mInstance = null;
	private static String ipAddress;
	private static final int WifiPORT = 8888;

	private static ClientReceiveSocketThread mRThread = null;
	private ClientSendSocketThread mSThread = null;
	private PlayActivity playActivity = null;

	private SingletonWifi() {
	}

	public static synchronized SingletonWifi getInstance() {
		if (mInstance == null) {
			mInstance = new SingletonWifi();
		}
		return mInstance;
	}

	private void setIPAddress(String ipString) {
		ipAddress = new String(ipString);
	}

	public void setPlayActivityObj(PlayActivity playactivity) {
		playActivity = playactivity;
	}
	
	public void connectToIP(String ipString){
		//Store the ipaddress 
		setIPAddress(ipString);
		
		//Initialise the playactivity object
		playActivity = null;
				
		//Initialise a thread to listen for packets from server
		//If thread exists, stop it and init a new one
		if (mRThread != null) {
			mRThread.stopThread();
		}
		// Initiate a new listening thread
		mRThread = new ClientReceiveSocketThread(ipString, WifiPORT);
		mRThread.start();
	}
	

	public void sendToDevice(CommandType currCommand) {
		// Initiate a new thread
		mSThread = new ClientSendSocketThread(currCommand, ipAddress, WifiPORT);
		mSThread.start();
	}

	/**
	 * Thread class to send commands to the Server
	 * 
	 * @author neha01mittal
	 * 
	 */
	public class ClientSendSocketThread extends Thread {

		private int WifiPORT = 8888;	
		private CommandType commandToSend;
		private Socket socket = null;
		private ObjectOutputStream objOutputStream;
		private String ipAddress;

		public ClientSendSocketThread(CommandType currCommand, String ipString, int numPort) {
			commandToSend = currCommand;
			ipAddress = new String(ipString);
			WifiPORT = numPort;

			Log.d("PlayActivity - ThreadConstructor", "commandToSend: "
					+ commandToSend + " " + commandToSend.getX() + " "
					+ commandToSend.getY());
		}
		/**
		 * wrap and send commands from client to server over socket connection
		 */
		public void run() {
			try {
				socket = new Socket(ipAddress, WifiPORT);
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

	public class ClientReceiveSocketThread extends Thread {

		private int WifiPORT = 8888;		
		private CommandType commandReceived = CommandType.DEFAULT;
		private ServerSocket serverSocket = null;
		private Socket socket = null;
		private ObjectInputStream objInputStream;
		private String ipAddress;
		private PlayActivity playActivity = null;
		
		private boolean bRun = true;
		private boolean bReceivedMessage = false;

		public ClientReceiveSocketThread(String ipString, int numPort) {
			ipAddress = new String(ipString);
			WifiPORT = numPort;
		}

		/**
		 * listen for commands from server to client over socket connection
		 */
		public void run() {
			try {
				serverSocket = new ServerSocket(WifiPORT);
				System.out.println("Listening :" + WifiPORT);
			} catch (IOException e) {
	     				// TODO Auto-generated catch bl ock
			  	e.printStackTrace();
			}
			
			try {
				socket = serverSocket.accept();
				objInputStream = new ObjectInputStream(
						socket.getInputStream());

				while(bRun) {
					Log.d("SingletonWifi", "Listening to: " + ipAddress);
					
					try {	
						commandReceived = (CommandType) objInputStream.readObject();	
						
						Log.d("SingletonWifi", "Received Command: "+ commandReceived.toString());
						
						if(playActivity != null) {
							//Do something
							//Going to receive keymap details here

						}
						
					} catch (Exception e) {
						Log.e("SingletonWifi", "S: Error at listening thread", e);
					}
				}
				// Close inputstream and socket
				objInputStream.close();
				socket.close();

			} catch (Exception e) {
				Log.e("SingletonWifi", "C: Error at listening thread", e);
			}
		}
		
		public void stopThread(){
			bRun = false;
		}

		public void setPlayActivityObj(PlayActivity playactivity) {
			playActivity = playactivity;
		}
		
	}
}
