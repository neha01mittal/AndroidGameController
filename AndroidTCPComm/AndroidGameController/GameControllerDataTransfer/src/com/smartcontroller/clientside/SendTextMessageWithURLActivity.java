package com.smartcontroller.clientside;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.sec.chaton.clientapi.ChatONAPI;
import com.sec.chaton.clientapi.MessageAPI;

public class SendTextMessageWithURLActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message_text_with_url);
		clickHandler();
	}

	public void clickHandler() {

		String text = "Enjoying the game play experience with SmartController";
		String url = "http://www.strikingly.com/smartcontroller";

		int nResult = MessageAPI.sendTextMessageWithURL(this, text, url);

		// check result
		switch (nResult) {
		case ChatONAPI.RESULT_CODE_FAIL_EXCEPTION_ILLEGAL_ARGUMENT:
			Toast.makeText(this, "Illegal Argument!!\nPlease, check argument",
					Toast.LENGTH_SHORT).show();
			break;
		case ChatONAPI.RESULT_CODE_FAIL_EXCEPTION:
			Toast.makeText(this, "Exception!!\nPlease, check argument",
					Toast.LENGTH_SHORT).show();
			break;
		case ChatONAPI.RESULT_CODE_FAIL_TEXT_LIMIT_EXCEEDED:
			Toast.makeText(
					this,
					"Message(Text + Url)'s length must be greater than or equals to 1 and be less than or equals to 2000",
					Toast.LENGTH_SHORT).show();
			break;
		case ChatONAPI.RESULT_CODE_FAIL_API_NOT_AVAILABLE:
			Toast.makeText(this,
					"API isn't availble. please check your ChatON version.",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
