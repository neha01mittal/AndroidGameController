package com.example.gamecontrollerdatatransfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TransitionActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transition);	
		
		Button iAmReady = (Button) findViewById(R.id.iamready);
		iAmReady.setOnClickListener(iAmReadyButton);
		Button repeatTut = (Button) findViewById(R.id.repeattut);
		repeatTut.setOnClickListener(repeatTutButton);
		Button faq = (Button) findViewById(R.id.faq);
		faq.setOnClickListener(faqButton);
	}
	
	Button.OnClickListener iAmReadyButton = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent k = new Intent(TransitionActivity.this, MainActivity.class);
			startActivity(k);
		}
	};
	
	Button.OnClickListener repeatTutButton = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent k = new Intent(TransitionActivity.this, TutorialActivity.class);
			startActivity(k);
		}
	};
	
	Button.OnClickListener faqButton = new Button.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// build this page
			Intent k = new Intent(TransitionActivity.this, MainActivity.class);
			startActivity(k);
		}
	};
	
}
