package com.example.gamecontrollerdatatransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity{
	
	private String ipAddress;
	EditText userInput;
	public String getIpAddress() {
		
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress){
		this.ipAddress= ipAddress;
	}
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        userInput = (EditText)findViewById(R.id.user_input);
        Button buttonPlay = (Button)findViewById(R.id.play);
        buttonPlay.setOnClickListener(buttonSendOnClickListener);
        
    }
    
    Button.OnClickListener buttonSendOnClickListener
    = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
				Intent k = new Intent(MainActivity.this, PlayActivity.class);
				k.putExtra("ipAddress", userInput.getText().toString());
				startActivity(k);
		}
    };
}
    
