package com.smartcontroller.clientside;

import gc.common_resources.IPAddressFormatValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class WifiConnectActivity extends ListActivity {

	private EditText userInput;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ArrayList<String> listItems = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private ListView lv = null;

	private boolean bAskedToEnableWifi = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_wificonnect);

		// to store previous IPs
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		userInput = (EditText) findViewById(R.id.user_input);

		
		//userInput.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
		// to display as a list
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice, listItems);

		// setListAdapter(adapter);

		lv = (ListView) findViewById(android.R.id.list);
		lv.setAdapter(adapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		for (String key : preferences.getAll().keySet()) {
			listItems.add(key);
			Log.d("preference key=", key); // only keys with true are shown
		}
		adapter.notifyDataSetChanged();

		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = (String) lv.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(),
						"You selected : " + item, Toast.LENGTH_SHORT).show();
				userInput.setText(item);
			}
		});

		Button buttonPlay = (Button) findViewById(R.id.play);
		buttonPlay.setOnClickListener(buttonSendOnClickListener);
		Button buttonAddToFav = (Button) findViewById(R.id.addToFavourites);
		buttonAddToFav.setOnClickListener(buttonAddToFavourites);
		Button buttonDeleteAll = (Button) findViewById(R.id.clearList);
		buttonDeleteAll.setOnClickListener(buttonClearList);
		Button buttonDeleteSelected = (Button) findViewById(R.id.delete);
		buttonDeleteSelected.setOnClickListener(buttonDelSelected);
	}

	@Override
	public void onResume() {
		super.onResume();

		//Check if wifi is enabled
		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if (!wifi.isWifiEnabled()){
			
			//Already asked user to enable wifi, but it is still not enabled
			//Assume that user don't want to use wifi
			if(bAskedToEnableWifi){
				Toast.makeText(getApplicationContext(),
					"Wifi must be enabled to continue.", Toast.LENGTH_LONG).show();
			
				super.onBackPressed();
			}else{			
				//enable wifi
				wifi.setWifiEnabled(true);
				
				Intent enableWifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				startActivity(enableWifiIntent);
	
				Toast.makeText(getApplicationContext(),
						"Ensure that your device is connected to the same Wifi network as your computer.", Toast.LENGTH_LONG).show();
			}
			bAskedToEnableWifi = !bAskedToEnableWifi;
		} else {
			//Wifi already turned on, don't ask user anything
			bAskedToEnableWifi = false;
		}
	}
	
	Button.OnClickListener buttonDelSelected = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int len = lv.getCount();
			SparseBooleanArray checked = lv.getCheckedItemPositions();
			List<String> temp = new ArrayList<String>();
			for (int i = 0; i < len; i++)
				if (checked.get(i)) {
					String item = (String) lv.getItemAtPosition(i);
					temp.add(item);
					Log.d("Delete this one", item);
				}

			for (String element : temp) {
				listItems.remove(element);
				editor.remove(element).commit();
			}
			adapter.notifyDataSetChanged();
			setListToDefault();
		}
	};

	Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			IPAddressFormatValidator iadfv = new IPAddressFormatValidator();
			if (iadfv.validate(userInput.getText().toString())) {
			// Start the Wifi Singleton with this ipAddress
				SingletonWifi.getInstance().connectToIP(userInput.getText().toString());
				
				//Go to PlayActivity
				Intent k = new Intent(WifiConnectActivity.this, PlayActivity.class);				
				k.putExtra("connectType", "wifi");
				startActivity(k);
			} else {
				Toast.makeText(getApplicationContext(),
						"Incorrect IP address format", Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	Button.OnClickListener buttonAddToFavourites = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			IPAddressFormatValidator iadfv = new IPAddressFormatValidator();
			if (iadfv.validate(userInput.getText().toString())) {
				editor.putString(userInput.getText().toString(), "dummy");
				editor.commit();
				addItems(v);
			} else {
				Toast.makeText(getApplicationContext(),
						"Incorrect IP address format", Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	Button.OnClickListener buttonClearList = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			clearlistview();
		}
	};

	/**
	 * called when ip addresses are added to favourites
	 * @param v
	 */
	public void addItems(View v) {

		adapter.clear();
		Map<String, ?> prefMap = preferences.getAll();
		for (String key : prefMap.keySet()) {
			listItems.add(key);
			Log.d("preference key=", key); // only keys with true are shown
		}
		setListToDefault();
		adapter.notifyDataSetChanged();
	}

	public void clearlistview() {
		listItems.clear();
		editor.clear().commit();
		adapter.notifyDataSetChanged();
	}

	public void setListToDefault() {
		for (int i = 0; i < adapter.getCount(); i++) {
			lv.setItemChecked(i, false);
		}
	}
}
