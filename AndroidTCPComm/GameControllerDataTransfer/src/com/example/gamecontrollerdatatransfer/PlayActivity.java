package com.example.gamecontrollerdatatransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import gc.common_resources.*;


public class PlayActivity extends Activity {
	
	private String ipAddress;
	Socket socket = null;
    private boolean connected = false;
	  
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        /* do this in onCreate */
        Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			ipAddress = extras.getString("ipAddress");
        //clientConnection();
		}
	}
	
	public void updateCoordinates(TouchCoordinates tc, CommandType newCommand){
		// We don't keep a set of tc and commandtype in this class anymore
		// So this function only packs the command with fields and 
		// passes the data to sendPacketToServer()
		CommandType updateCommand = newCommand;
		updateCommand.setX(tc.getX());
		updateCommand.setY(tc.getY());
		
		sendPacketToServer(updateCommand);
	}

	// Create the threads and pass the commands to them
	public void sendPacketToServer(CommandType updateCommand){
		if (!connected) {
             Thread cThread = new Thread(new ClientSocketThread(updateCommand));
             cThread.start();
		}
	}
	
	//Thread class to send commands to the Server
	public class ClientSocketThread implements Runnable {
		
		private CommandType commandToSend;
		
		public ClientSocketThread(CommandType currCommand){
			commandToSend = currCommand;
			
			Log.d("PlayActivity - ThreadConstructor", "commandToSend: "+commandToSend+" "+ commandToSend.getX() +" "+ commandToSend.getY());
		}
		 
		public void run() {
	        try {
				Socket socket = new Socket(ipAddress, 8888);
		        ObjectOutputStream objOutputStream = new ObjectOutputStream(socket.getOutputStream());
		        connected = true;
		      		        
				try {		                
					Log.d("PlayActivity", "Sending: "+commandToSend+" "+ commandToSend.getX() +" "+ commandToSend.getY());
					
					// Send the enum(commandToSend) and the fields(X and Y) separately
					// as the serializing and deserializing of enum through ObjectOutputStream
					// and ObjectInputStream will not save the fields in the enum
					objOutputStream.writeObject(commandToSend);
					objOutputStream.writeFloat(commandToSend.getX());
					objOutputStream.writeFloat(commandToSend.getY());
	            } catch (Exception e) {
	                Log.e("PlayActivity", "S: Error", e);
	            }
				//Close outputstream and socket
				objOutputStream.close();
		        socket.close();
		        connected = false;
		    } catch (Exception e) {
		        Log.e("PlayActivity", "C: Error", e);
		    }
		}
	}			
}
