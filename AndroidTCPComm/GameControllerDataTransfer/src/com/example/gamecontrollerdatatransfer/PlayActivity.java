package com.example.gamecontrollerdatatransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

public class PlayActivity extends Activity {
	
	private TouchCoordinates tc;
	 /* put this into your activity class */
	private String ipAddress;
	private String arrowKey="";
	Socket socket = null;
    private boolean connected = false;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;
	  
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
	
	public void updateCoordinates(TouchCoordinates tc,String arrowKey){
		setCoordinates(tc,arrowKey);
		sendPacketToServer();
	}

	public void sendPacketToServer(){
		if (!connected) {
             Thread cThread = new Thread(new ClientSocketThread());
             cThread.start();
		}
	}
	
	public synchronized void setCoordinates(TouchCoordinates tc,String arrowKey){
		this.tc=tc;
		this.arrowKey= arrowKey;
	}
	
	public TouchCoordinates getCoordinates(){
		System.out.println("CHECKKKK at getCoordinates"+tc.getX()+" "+tc.getY()+" "+tc.getPointerCount());
		return tc;
	}
	
	public class ClientSocketThread implements Runnable {
		 
		public void run() {
	        try {
		        Socket socket = new Socket(ipAddress, 8888);
		        dataOutputStream = new DataOutputStream(socket.getOutputStream());
		        connected = true;
	            i++;
				try {		                
		                synchronized(arrowKey) {
		                	dataOutputStream.writeUTF(arrowKey);
		                }
	    				
	                   	Log.d("ClientActivity", "C: Sent." + arrowKey);
	            } catch (Exception e) {
	                Log.e("ClientActivity", "S: Error", e);
	            }
				dataOutputStream.close();
		        socket.close();
		        connected = false;
		    } catch (Exception e) {
		        Log.e("ClientActivity", "C: Error", e);
		    }
		}
	}			
}
