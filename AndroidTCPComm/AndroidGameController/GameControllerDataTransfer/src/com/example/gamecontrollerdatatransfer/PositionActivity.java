package com.example.gamecontrollerdatatransfer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class PositionActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position);	
		
		startTutorialAnimation();
	}

	private void startTutorialAnimation() {
		AnimationDrawable animation = new AnimationDrawable();
	    animation.addFrame(getResources().getDrawable(R.drawable.position), 5000);
	    animation.addFrame(getResources().getDrawable(R.drawable.controls), 4000);
	    animation.addFrame(getResources().getDrawable(R.drawable.tiltdown), 3000);
	    animation.addFrame(getResources().getDrawable(R.drawable.tiltup), 3000);
	    animation.addFrame(getResources().getDrawable(R.drawable.tiltleft), 3000);
	    animation.addFrame(getResources().getDrawable(R.drawable.tiltright), 3000);
	    animation.addFrame(getResources().getDrawable(R.drawable.buttons), 5000);
	    animation.setOneShot(true);

	    ImageView imageAnim =  (ImageView) findViewById(R.id.image);
	    imageAnim.setBackgroundDrawable(animation);

	    // start the animation!
	    animation.start();
	    checkIfAnimationDone(animation);
	 }
	
	 private void checkIfAnimationDone(AnimationDrawable anim){
	        final AnimationDrawable a = anim;
	        int timeBetweenChecks = 300;
	        Handler h = new Handler();
	        h.postDelayed(new Runnable(){
	            public void run(){
	                if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)){
	                    checkIfAnimationDone(a);
	                } else{
	                    Intent k = new Intent(PositionActivity.this, TransitionActivity.class);
	                    startActivity(k);
	                }
	            }
	        }, timeBetweenChecks);
	    };
}
