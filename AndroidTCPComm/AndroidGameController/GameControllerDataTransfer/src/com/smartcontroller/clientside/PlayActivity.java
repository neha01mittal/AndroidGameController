package com.smartcontroller.clientside;

import gc.common_resources.CommandType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.samsung.android.sdk.multiwindow.SMultiWindow;
import com.samsung.android.sdk.multiwindow.SMultiWindowActivity;

public class PlayActivity extends FragmentActivity implements OnTouchListener {

	private SensorManager mSensorManager;
	private MyRenderer mRenderer;
	private View commonView;
	private boolean doubleBackToExitPressedOnce = false;
	private String connectType;
	private SoundPool soundPool;
	private int soundID;
	boolean loaded = false;

	private final float NOISE = (float) 2.0;
	private float startX = 0, startY = 0, startZ = 0;
	private boolean flag = false, isPaused = false;
	private boolean isStartPositionRegistered, tiltStateChanged = true;

	public int tiltState = 0;
	public float tiltX, tiltY;
	
	//HelperTextViews
	//com.smartcontroller.clientside.VerticalTextView textTap, textSwipeUp, textSwipeDown, textSwipeLeft, textSwipeRight;
	TextView textTap, textSwipeUp, textSwipeDown, textSwipeLeft, textSwipeRight;
	private ArrayList<String> keyMapList = new ArrayList<String>();

	private int bgColor = Color.TRANSPARENT;
	private HashMap<Integer, Integer> soundTracks = new HashMap<Integer, Integer>() {
		{
			put(0, R.raw.gunshot);
			put(1, R.raw.bigwave);
			put(2, R.raw.chargedlightning);
			put(3, R.raw.chargedsonicboom);
			put(4, R.raw.drainmagic);
			put(5, R.raw.funkyzap);
			put(6, R.raw.stormmagic);
			put(7, R.raw.thundermagic);
		}
	};
	
	final String[] items = {"Gun Shot","Big Wave","Charged Lightning","Charged Sonic Boom","Drain Magic","Funky Zap","Storm Magic","Thunder Magic"};

	// timer
	private CountDownTimer countDownTimer;
	public TextView text;
	private long startTime = 0;
	private final long interval = 1 * 1000;

	private static final int MENU_SET_STATE_CHANGE_LISTENER = 6;
	
	private SMultiWindow mMultiWindow = null;
	private SMultiWindowActivity mMultiWindowActivity = null;
	private List<ResolveInfo> mMultiWindowAppList = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		// Create our Preview view and set it as the content of our
		// Activity
		mRenderer = new MyRenderer();
		isStartPositionRegistered = false;
		setContentView(R.layout.activity_play);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// sound
		(findViewById(R.id.multi_touch_view)).setOnTouchListener(this);
		
		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// Load the sound
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		soundID = soundPool.load(this, R.raw.gunshot, 1);

		/* do this in onCreate */
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			connectType = extras.getString("connectType");
		}

		addListenerOnTiltDetectionButton();

		final ImageButton twitterButton = (ImageButton) findViewById(R.id.twitter);
		twitterButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri imageUri = Uri.parse("android.resource://"
						+ getPackageName() + "/" + R.drawable.gameplay);

				Intent shareIntent = findTwitterClient();
				if (shareIntent != null) {
					shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
					shareIntent
							.putExtra(
									Intent.EXTRA_TEXT,
									"Enjoying the game play experience! #SmartController http://www.strikingly.com/smartcontroller ");
					startActivity(Intent
							.createChooser(shareIntent, "Share via"));
				} else {
					Toast.makeText(getApplicationContext(),
							"Unable to find Twitter", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		final ImageButton instaButton = (ImageButton) findViewById(R.id.instagram);
		instaButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri imageUri = Uri.parse("android.resource://"
						+ getPackageName() + "/" + R.drawable.gameplay);

				if (verificaInstagram()) {
					Intent shareIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					shareIntent.setType("image/*");
					// shareIntent.putExtra(Intent.EXTRA_TEXT,
					// "Enjoying the game play experience! #SmartController http://www.strikingly.com/smartcontroller ");
					shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
					shareIntent.setPackage("com.instagram.android");
					startActivity(Intent
							.createChooser(shareIntent, "Share via"));
				} else {
					Toast.makeText(getApplicationContext(),
							"Unable to find Instagram", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		final ImageButton timerButton = (ImageButton) findViewById(R.id.timer);
		timerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getSupportFragmentManager(), "timePicker");
			}

		});

		final ImageButton soundButton = (ImageButton) findViewById(R.id.sound);
		soundButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);

				builder.setTitle("Choose sound track")
		           .setItems(items, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		               // The 'which' argument contains the index position
		               // of the selected item
		            	   Toast.makeText(PlayActivity.this,
									"You Clicked : " + soundTracks.get(which),
									Toast.LENGTH_SHORT).show();
							soundID = soundPool.load(getApplicationContext(),
									soundTracks.get(which), 1);
		           }
		    });
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		final ImageButton stoptimerButton = (ImageButton) findViewById(R.id.stoptimer);
		stoptimerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stoptimerButton.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				countDownTimer.cancel();
			}

		});

		final ImageButton chatOnButton = (ImageButton) findViewById(R.id.chaton);
		chatOnButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent k = new Intent(PlayActivity.this,SendTextMessageWithURLActivity.class);
				startActivity(k);
			}

		});

		final ImageButton multiWindowButton = (ImageButton) findViewById(R.id.multiwindow);
		multiWindowButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				multiWindowInit();
			}

		});

		
		final ImageButton resetButton = (ImageButton) findViewById(R.id.buttonreset);
		resetButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = false;
				isStartPositionRegistered = false;
				findViewById(R.id.imageButton1).setVisibility(View.VISIBLE);
				addListenerOnTiltDetectionButton();
			}
		});

		final ImageButton fbshare = (ImageButton) findViewById(R.id.fbicon);
		fbshare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent k = new Intent(PlayActivity.this,
						FacebookShareActivity.class);
				startActivity(k);
			}
		});

		final ImageButton playPauseButton = (ImageButton) findViewById(R.id.buttonplaypause);
		playPauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isPaused) {
					// Detection enabled, show the pause button
					playPauseButton
							.setBackgroundResource(R.drawable.pausebutton);
					Toast.makeText(getApplicationContext(),
							"Tilt detection enabled", Toast.LENGTH_SHORT)
							.show();
					isPaused = false;
					onResume();
				} else {
					// Detection disabled, show the pause button
					playPauseButton
							.setBackgroundResource(R.drawable.playbutton);
					Toast.makeText(getApplicationContext(),
							"Tilt detection disabled", Toast.LENGTH_SHORT)
							.show();
					isPaused = true;
					onPause();
				}
			}
		});

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Getting the user sound settings
			float x= event.getX();
			float y= event.getY();
			System.out.println("coordinates "+x+" "+y);
			AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
			float actualVolume = (float) audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			float maxVolume = (float) audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volume = actualVolume / maxVolume;
			// Is the sound loaded already?
			if (loaded) {
				soundPool.play(soundID, volume, volume, 1, 0, 1f);
				Log.e("Test", "Played sound");
			}
		}
		return false;
	}

	public Intent findTwitterClient() {
		final String[] twitterApps = {
				// package // name - nb installs (thousands)
				"com.twitter.android", // official - 10 000
				"com.twidroid", // twidroid - 5 000
				"com.handmark.tweetcaster", // Tweecaster - 5 000
				"com.thedeck.android" }; // TweetDeck - 5 000 };
		Intent tweetIntent = new Intent();
		tweetIntent.setType("text/plain");
		final PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		for (int i = 0; i < twitterApps.length; i++) {
			for (ResolveInfo resolveInfo : list) {
				String p = resolveInfo.activityInfo.packageName;
				if (p != null && p.startsWith(twitterApps[i])) {
					tweetIntent.setPackage(p);
					return tweetIntent;
				}
			}
		}
		return null;

	}

	private boolean verificaInstagram() {
		boolean instalado = false;

		try {
			ApplicationInfo info = getPackageManager().getApplicationInfo(
					"com.instagram.android", 0);
			instalado = true;
		} catch (NameNotFoundException e) {
			instalado = false;
		}
		return instalado;
	}

	/**
	 * register position for accelerometer tilt detection
	 */
	public void addListenerOnTiltDetectionButton() {

		final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton1);

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Toast.makeText(PlayActivity.this, "Rest position recorded",
						Toast.LENGTH_SHORT).show();
				imageButton.setVisibility(View.GONE);
				System.out.println("Register x and y here");
				flag = true;
				System.out.println("START VALUES" + startX + " " + startY);

			}

		});

	}

	public boolean isTiltDetectionOn() {
		return !isPaused;
	}

	public int getTiltState() {
		return tiltState;
	}
	
	public void initKeyConfig() {
		//This function sends a request to the server to request for keymapping configuration
		if (connectType.equals("wifi")) {
			SingletonWifi.getInstance().setPlayActivityObj(this);
		} else if (connectType.equals("bluetooth")) {
			SingletonBluetooth.getInstance().setPlayActivityObj(this);
		}
		sendPacketToServer(CommandType.CONFIG);
	}
	
	public synchronized void updateKeyMapList(ArrayList<String> keyMapping) {
		int listSize = keyMapping.size();		
		keyMapList.clear();
		
		for(int i = 0; i < listSize; i++)
			keyMapList.add(keyMapping.get(i));		
	}
	
	public synchronized ArrayList<String> getKeyMapList(){
		return keyMapList;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (connectType.equals("bluetooth"))
			SingletonBluetooth.getInstance().closeConnection();
	}

	@Override
	public void onBackPressed() {
		// disable back button
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please tap BACK again to exit",
				Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;

			}
		}, 2000);

	}

	/**
	 * wrap command to be sent to server
	 * 
	 * @param tc
	 * @param newCommand
	 */
	public void updateCoordinates(TouchCoordinates tc, CommandType newCommand) {
		// We don't keep a set of tc and commandtype in this class anymore
		// So this function only packs the command with fields and
		// passes the data to sendPacketToServer()
		CommandType updateCommand = newCommand;
		updateCommand.setX(tc.getX());
		updateCommand.setY(tc.getY());
		updateCommand.setZ(tc.getZ());
		updateCommand.setW(tc.getW());

		sendPacketToServer(updateCommand);
	}

	// Create the threads and pass the commands to them
	public void sendPacketToServer(CommandType updateCommand) {
		if (connectType.equals("wifi")) {
			SingletonWifi.getInstance().sendToDevice(updateCommand);
		} else if (connectType.equals("bluetooth")) {
			SingletonBluetooth.getInstance().sendToDevice(updateCommand);
		}
	}
	
	public void updateHelperTexts() {
		int tiltOffset = 0;
		
		switch (getTiltState()) {
		case 1: // Tilt UP
			tiltOffset = 5;
			break;
		case 2: // Tilt DOWN
			tiltOffset = 10;
			break;
		case 3: // Tilt LEFT
			tiltOffset = 15;
			break;
		case 4: // Tilt RIGHT
			tiltOffset = 20;
			break;
		}
		textTap = (TextView) findViewById(R.id.textTap);
		textSwipeUp = (TextView) findViewById(R.id.textSwipeUp);
		textSwipeDown = (TextView) findViewById(R.id.textSwipeDown);
		textSwipeLeft = (TextView) findViewById(R.id.textSwipeLeft);
		textSwipeRight = (TextView) findViewById(R.id.textSwipeRight);
		
		synchronized(this) {
			textTap.setText(keyMapList.get(tiltOffset));
			textSwipeUp.setText(keyMapList.get(tiltOffset+1));
			textSwipeDown.setText(keyMapList.get(tiltOffset+2));
			textSwipeLeft.setText(keyMapList.get(tiltOffset+3));
			textSwipeRight.setText(keyMapList.get(tiltOffset+4));
		}
		tiltStateChanged = false;
	}
	
	public void showImageHelper(boolean showHelper){
		ImageView iv = (ImageView) findViewById(R.id.imagetapswipehelper);
		textTap = (TextView) findViewById(R.id.textTap);	
		textSwipeUp = (TextView) findViewById(R.id.textSwipeUp);
		textSwipeDown = (TextView) findViewById(R.id.textSwipeDown);
		textSwipeLeft = (TextView) findViewById(R.id.textSwipeLeft);
		textSwipeRight = (TextView) findViewById(R.id.textSwipeRight);	
		
		if(showHelper){
			iv.setVisibility(View.VISIBLE);

			if(!keyMapList.isEmpty()) {
			
				if(tiltStateChanged)
				updateHelperTexts();			
			
				//textTap = (com.smartcontroller.clientside.VerticalTextView) findViewById(R.id.textTap);
				textTap.setVisibility(View.VISIBLE);	
				textSwipeUp.setVisibility(View.VISIBLE);	
				textSwipeDown.setVisibility(View.VISIBLE);	
				textSwipeLeft.setVisibility(View.VISIBLE);	
				textSwipeRight.setVisibility(View.VISIBLE);	
			}
		}else {
			iv.setVisibility(View.INVISIBLE);	
			textTap.setVisibility(View.INVISIBLE);	
			textSwipeUp.setVisibility(View.INVISIBLE);	
			textSwipeDown.setVisibility(View.INVISIBLE);	
			textSwipeLeft.setVisibility(View.INVISIBLE);	
			textSwipeRight.setVisibility(View.INVISIBLE);		
		}		
	}

	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();

		initKeyConfig();
		showImageHelper(true);
		
		commonView = findViewById(R.id.playActivity);
		commonView.setBackgroundColor(bgColor);
		commonView.invalidate();
		mRenderer.start();
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		bgColor = Color.TRANSPARENT;

		commonView = findViewById(R.id.playActivity);
		commonView.setBackgroundColor(bgColor);
		commonView.invalidate();
		mRenderer.stop();
	}

	class MyRenderer implements SensorEventListener {
		private Sensor mRotationVectorSensor;

		public MyRenderer() {
			// find the rotation-vector sensor
			mRotationVectorSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		}

		public void start() {
			// enable our sensor when the activity is resumed, ask for
			// 10 ms updates.
			mSensorManager.registerListener(this, mRotationVectorSensor, 10000);
		}

		public void stop() {
			// make sure to turn our sensor off when the activity is paused
			mSensorManager.unregisterListener(this);
		}

		/**
		 * detect tilt based on noise
		 */
		public void onSensorChanged(SensorEvent event) {

			ImageView iv = (ImageView) findViewById(R.id.image);
			TextView tvX = (TextView) findViewById(R.id.x_axis);
			TextView tvY = (TextView) findViewById(R.id.y_axis);
			TextView tvZ = (TextView) findViewById(R.id.z_axis);

			commonView = findViewById(R.id.playActivity);
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			if (flag) {
				if (!isPaused) {
					if (!isStartPositionRegistered) {

						startX = x;
						startY = y;
						startZ = z;
						isStartPositionRegistered = true;
					}

					else {
						float deltaX = Math.abs(startX - x);
						float deltaY = Math.abs(startY - y);
						float deltaZ = Math.abs(startZ - z);
						tiltX = startX - x;
						tiltY = startY - y;
						if (deltaX < NOISE)
							deltaX = (float) 0.0;
						if (deltaY < NOISE)
							deltaY = (float) 0.0;
						if (deltaZ < NOISE)
							deltaZ = (float) 0.0;

						// change to visible for testing/ debugging
						tvX.setVisibility(View.INVISIBLE);
						tvY.setVisibility(View.INVISIBLE);
						tvZ.setVisibility(View.INVISIBLE);

						iv.setVisibility(View.INVISIBLE);

						if (deltaX > deltaY) {
							iv.setImageResource(R.drawable.horizontal);

							if ((startX - x) < 0) {
								// down tilt
								// bgColor = Color.parseColor("#ff669966");
								if(tiltState != 1) 
									tiltStateChanged = true;
								
								tiltState = 1; // TILT UP
							} else {
								// bgColor = Color.parseColor("#ff668899");
								if(tiltState != 2) 
									tiltStateChanged = true;
								tiltState = 2; // TILT DOWN
							}

//							System.out.println("Delta 1=" + (startX - x)
//									+ " Delta 2=" + (startY - y));

						} else if (deltaX < deltaY) {
							iv.setImageResource(R.drawable.vertical);

							if ((startY - y) < 0) {
								// bgColor = Color.parseColor("#ff6E6699");
								if(tiltState != 3) 
									tiltStateChanged = true;
								tiltState = 3; // TILT LEFT
							} else {
								// bgColor = Color.parseColor("#ff886699");
								if(tiltState != 4) 
									tiltStateChanged = true;
								tiltState = 4; // TILT RIGHT
							}

//							System.out.println("Delta 1=" + (startX - x)
//									+ " Delta 2=" + (startY - y));

						} else {
							// NOP
							iv.setVisibility(View.INVISIBLE);
							// bgColor = Color.TRANSPARENT;
							if(tiltState != 0) 
								tiltStateChanged = true;
							tiltState = 0; // NO TILT
						}
						// commonView.setBackgroundColor(bgColor);
						commonView.invalidate();
					}
				} else {
					// Don't detect tilt
					tiltState = 0; // NO TILT
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}

	// timer class
	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			text.setText("Time's up! Disconnecting ...");
			onBackPressed();
			onBackPressed();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			text.setText("Seconds Left: " + millisUntilFinished / 1000);
		}
	}

	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			int hour = 0; // c.get(Calendar.HOUR_OF_DAY);
			int minute = 0; // c.get(Calendar.MINUTE);

			TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
					this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
			timePicker.setTitle("Control your play time! Set a timer!");
			timePicker.setMessage("Enter in hh/mm format");
			// Create a new instance of TimePickerDialog and return it
			return timePicker;
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user

			startTime = (hourOfDay * 3600 + minute * 60) * 1000;
			text = (TextView) findViewById(R.id.timertext);

			if (countDownTimer != null) {
				countDownTimer.cancel();
			}
			countDownTimer = new MyCountDownTimer(startTime, interval);
			text.setText(text.getText() + "Seconds left: "
					+ String.valueOf(startTime / 1000));
			text.setVisibility(View.VISIBLE);
			((ImageButton) findViewById(R.id.stoptimer))
					.setVisibility(View.VISIBLE);
			countDownTimer.start();
			Toast.makeText(getApplicationContext(), "Timer started!",
					Toast.LENGTH_SHORT).show();
		}
	}
	
    private void displayAppList(int position) {
    	ArrayList<String> appListLabels = new ArrayList<String>();
    	if (mMultiWindowAppList != null) {
    		int appListCount = mMultiWindowAppList.size();
    		for (int i = 0 ; i < appListCount ; i++) {
    			appListLabels.add((String) mMultiWindowAppList.get(i).loadLabel(getPackageManager()));
    		}
    	}
    	
    	String[] listItems = new String[0]; 
    	listItems = appListLabels.toArray(listItems);
    	AlertDialog.Builder appListDialog = new AlertDialog.Builder(this);
    	appListDialog.setTitle("Is Multi Window")
    	.setItems(listItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				displayLaunchType(which);
			}
		}).show();
    }
    
    public void multiWindowInit() {
        mMultiWindow = new SMultiWindow();
        mMultiWindowActivity = new SMultiWindowActivity(this);
        
 		mMultiWindowAppList = new ArrayList<ResolveInfo>();
 		Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
 		List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER|PackageManager.GET_META_DATA);
 		
 		for (ResolveInfo r : resolveInfos) {
 			if (r.activityInfo != null && r.activityInfo.applicationInfo.metaData != null) {
 				if (r.activityInfo.applicationInfo.metaData.getBoolean("com.sec.android.support.multiwindow")
 						|| r.activityInfo.applicationInfo.metaData.getBoolean("com.samsung.android.sdk.multiwindow.enable")) {
 					boolean bUnSupportedMultiWinodw = false; 
 					if (r.activityInfo.metaData != null) {
 						String activityWindowStyle = r.activityInfo.metaData.getString("com.sec.android.multiwindow.activity.STYLE");
 		            	if (activityWindowStyle != null) {
 		            		ArrayList<String> activityWindowStyles = new ArrayList<String>(Arrays.asList(activityWindowStyle.split("\\|")));
 		            		if (!activityWindowStyles.isEmpty()) {
	 		                    if (activityWindowStyles.contains("fullscreenOnly")) {
	 		                    	bUnSupportedMultiWinodw = true;
	 		                    }
 		            		}
 		                }
 		            }

 		            if (!bUnSupportedMultiWinodw) {
 		            	mMultiWindowAppList.add(r);
 		            }
 				}
 			}
 		}
		displayAppList(MENU_SET_STATE_CHANGE_LISTENER);
    }
    private void displayLaunchType(int appPosition) {
    	final ArrayList<String> launchTypes = new ArrayList<String>();
    	final int selectedApp = appPosition;
    	launchTypes.add("Zone A");
	    launchTypes.add("Zone B");
    	
    	String[] listItems = new String[0]; 
    	listItems = launchTypes.toArray(listItems);
    	AlertDialog.Builder launchTypeDialog = new AlertDialog.Builder(this);
    	launchTypeDialog.setTitle("Select Launch Type")
    	.setItems(listItems, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String launchType = launchTypes.get(which);
				ResolveInfo selectApp = mMultiWindowAppList.get(selectedApp);
				ComponentInfo selectAppInfo = selectApp.activityInfo != null ? selectApp.activityInfo : selectApp.serviceInfo;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setComponent(new ComponentName(selectAppInfo.packageName, selectAppInfo.name));
				if ("Zone A".equals(launchType)) {
					SMultiWindowActivity.makeMultiWindowIntent(intent, SMultiWindowActivity.ZONE_A);
				} else if("Zone B".equals(launchType)) {
					SMultiWindowActivity.makeMultiWindowIntent(intent, SMultiWindowActivity.ZONE_B);
				} else {
					return;
				}

				startActivity(intent);
			}
		}).show();
    }
}
