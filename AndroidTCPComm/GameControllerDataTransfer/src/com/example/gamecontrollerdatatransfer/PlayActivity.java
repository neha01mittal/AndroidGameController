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

	public void clientConnection() {
		
		
		try {
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy); 
	        
			
			socket = new Socket(ipAddress, 8888);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			
			//pointer count||X||Y
			//
			//dataOutputStream.writeUTF(getCoordinates().getPointerCount()+"||"+getCoordinates().getX()+"||"+getCoordinates().getY()+"||"+arrowKey);
			//textIn.setText(dataInputStream.readUTF());
			
			dataOutputStream.writeUTF(arrowKey);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (dataOutputStream != null){
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (dataInputStream != null){
				try {
					dataInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		  }
	}

		public void setCoordinates(TouchCoordinates tc,String arrowKey){
			this.tc=tc;
			clientConnection();
			this.arrowKey= arrowKey;
		}
		public TouchCoordinates getCoordinates(){
			System.out.println("CHECKKKK at getCoordinates"+tc.getX()+" "+tc.getY()+" "+tc.getPointerCount());
			return tc;
		}
		
}
