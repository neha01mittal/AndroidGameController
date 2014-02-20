package com.example.gamecontrollerdatatransfer;

import gc.common_resources.CommandType;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MultiTouchView extends View {

	private static final int THRESHOLD = 70;
	private static final int TILTTHRESHOLD = 150;
	private static final int MOUSETHRESHOLD = 5;
	private static final int MOUSEBOXTHRESHOLD = 100;
	private final int SIZE;
	private final int SCREENCENTREX, SCREENCENTREY, SCREENWIDTH, SCREENHEIGHT;
	private long prevLeftTime = 0, prevRightTime = 0;
	private Context m_context;

	private boolean leftTouchRegistered = false;
	/*
	 * private boolean mouseRegistered = false; private boolean leftMouseLifted
	 * = false; private boolean rightMouseLifted = false; private Vector[]
	 * mouseBoxVectors = new Vector[4];
	 */

	private WindowManager wm;
	private Display screenDisplay;
	private Point screenSize;
	private Vibrator v;

	private SparseArray<PointF> mActivePointers;
	private SparseArray<Float> startPointerX, startPointerY;

	private TouchCoordinates drawPoint;

	// Mouse Pointer Array
	// 0 is left mouse button, // 1 is right mouse button
	// private int mousePointer = 0;
	// private TouchCoordinates leftMousePoint, rightMousePoint;

	// private float mouseBoxCentreX = 0, mouseBoxCentreY = 0;

	private Paint mPaint;
	private int[] colors = { Color.BLUE, Color.YELLOW, Color.GREEN,
			Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
			Color.LTGRAY, Color.YELLOW };

	private int[] bigColors = { Color.LTGRAY, Color.WHITE, Color.GRAY,
			Color.RED, Color.DKGRAY, Color.YELLOW, Color.BLUE, Color.GREEN,
			Color.GREEN, Color.CYAN };

	private Paint textPaint;

	protected Bitmap mBitmap;
	protected Canvas currentCanvas;
	protected Paint p;

	protected int canvasWidth;
	protected int canvasHeight;

	public MultiTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);

		v = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);

		m_context = context;

		wm = (WindowManager) m_context.getSystemService(Context.WINDOW_SERVICE);
		screenDisplay = wm.getDefaultDisplay();

		screenSize = new Point();
		screenDisplay.getSize(screenSize);
		SCREENWIDTH = screenSize.x;
		SCREENHEIGHT = screenSize.y;

		SCREENCENTREX = SCREENWIDTH / 2;
		SCREENCENTREY = SCREENHEIGHT / 2;
		SIZE = SCREENWIDTH / 30;

		// Init Arrays
		for (int i = 0; i < 2; i++)

			initView();
	}

	private void initView() {
		mActivePointers = new SparseArray<PointF>();
		startPointerX = new SparseArray<Float>();
		startPointerY = new SparseArray<Float>();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// set painter color to a color you like
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(20);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		v.vibrate(100);
		// get pointer index from the event object
		int pointerIndex = event.getActionIndex();

		// get pointer ID
		int pointerId = event.getPointerId(pointerIndex);

		// get masked (not specific to a pointer) action
		int maskedAction = event.getActionMasked();

		switch (maskedAction) {

		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN: {
			// We have a new pointer. Lets add it to the list of pointers
			PointF point = new PointF();
			point.x = event.getX(pointerIndex);
			point.y = event.getY(pointerIndex);

			if (isLeftScreen(point.y)) {
				if (!leftTouchRegistered) {
					mActivePointers.put(pointerId, point);
					startPointerX.put(pointerId, new Float(point.x));
					startPointerY.put(pointerId, new Float(point.y));
					drawPoint = new TouchCoordinates(point.x, point.y,
							pointerId);
					leftTouchRegistered = true;
				} else {
					leftTouchRegistered = leftTouchRegistered;
				}
			} else {

				mActivePointers.put(pointerId, point);
				startPointerX.put(pointerId, new Float(point.x));
				startPointerY.put(pointerId, new Float(point.y));
				/*
				 * if(!mouseRegistered) { mActivePointers.put(pointerId, point);
				 * updateMouse(point.x, point.y, pointerId, 0);
				 * Log.d("MultiTouch"," mousePointer: "+mousePointer); }else {
				 * if(leftMouseLifted || rightMouseLifted) {
				 * Log.d("MultiTouch","Sending mouse click");
				 * doRightScreenProcess(0, 0, 2); mActivePointers.put(pointerId,
				 * point); updateMouse(point.x, point.y, pointerId, 0); }else
				 * Log.d("MultiTouch","Registered! Mouse Down mousePointer: "+
				 * mousePointer); }
				 */
			}

			break;
		}

		case MotionEvent.ACTION_MOVE: { // a pointer was moved

			// Ensure that the pointer is registered in mActivePointers array

			for (int i = 0; i < mActivePointers.size(); i++) {
				int pId = event.getPointerId(i);
				PointF point = mActivePointers.get(pId);
				if (point != null) {

					float displacementX, displacementY;
					PointF startPoint = new PointF(startPointerX.get(pId),
							startPointerY.get(pId));

					point.x = event.getX(i);
					point.y = event.getY(i);

					if (isLeftScreen(point.y)) {

						// Get the displacement of the pointer from its
						// startpoint
						displacementX = point.x - startPoint.x;
						displacementY = point.y - startPoint.y;

						double netMovement = Math.sqrt(Math.pow(displacementX,
								2) + Math.pow(displacementY, 2));

						if (netMovement > THRESHOLD) {
							// Update the draw point if the pointer exceeds the
							// threshold
							// So that it will only appear within the threshold
							// circle
							double multiplier = (double) THRESHOLD
									/ netMovement;
							drawPoint = new TouchCoordinates(
									(float) (startPoint.x + multiplier
											* displacementX),
									(float) (startPoint.y + multiplier
											* displacementY), pointerId);

							// This function will package the values to be sent
							// to the server
							doLeftScreenProcess(displacementX, displacementY,
									point, 0);
						} else
							drawPoint = new TouchCoordinates(point.x, point.y,
									pId);
					} else { // isRightScreen
						/*
						 * if(mouseRegistered) updateMouse(point.x, point.y,
						 * pId, 2);
						 */
					}
				}
			}
			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL: {
			PointF point = mActivePointers.get(pointerId);

			if (point != null) {

				int pointersSize = mActivePointers.size() - 1;
				float displacementX, displacementY;
				PointF startPoint = new PointF(startPointerX.get(pointerId),
						startPointerY.get(pointerId));

				point.x = event.getX(pointerIndex);
				point.y = event.getY(pointerIndex);

				if (isLeftScreen(point.y)) {
					leftTouchRegistered = false;
					drawPoint = new TouchCoordinates(0, 0, pointerId);
				} else {// isRightScreen
						// updateMouse(0, 0, 0, 1);
						// Log.d("MultiTouch","Mouse Up mousePointer: "+mousePointer);

					// Get the displacement of the pointer from its startpoint
					displacementX = point.x - startPoint.x;
					displacementY = point.y - startPoint.y;

					double netMovement = Math.sqrt(Math.pow(displacementX, 2)
							+ Math.pow(displacementY, 2));

					// Package the values to be sent to the server
					if (netMovement > THRESHOLD) {
						// This action is a swipe
						doRightScreenProcess(displacementX, displacementY,
								point, 1);
					} else {
						// This action is a tap
						doRightScreenProcess(displacementX, displacementY,
								point, 0);
					}

				}

				mActivePointers.remove(pointerId);
				startPointerX.remove(pointerId);
				startPointerY.remove(pointerId);
			}

			// Log.d("Multi-Touch",
			// "ID of Pointer: "+pointerId+" No. of Active Pointers: "+mActivePointers.size());
			break;
		}
		}
		invalidate();

		return true;
	}

	public boolean isLeftScreen(float pointX) {

		if (pointX > SCREENCENTREY)
			return true;
		else
			return false;
	}

	public void doLeftScreenProcess(float displacementX, float displacementY,
			PointF point, int operation) {

		// In this process, the distance moved by the pointer is used to
		// interpret the direction that the
		// player wants to press the ^v<> directional keys
		// So the commandtypes used are KEYBOARD_UP, KEYBOARD_DOWN,
		// KEYBOARD_LEFT, KEYBOARD_RIGHT

		// Only send a packet if the last packet sent was >100mseconds ago
		if (System.currentTimeMillis() - prevLeftTime > 50) {

			prevLeftTime = System.currentTimeMillis();

			Vector vec = new Vector(displacementX, displacementY);
			vec.normalise();
			CommandType touchCommand = CommandType.DEFAULT; // Init as DEFAULT

			try {
				// This function is used to determine the UP, DOWN, LEFT, RIGHT
				// direction of displacement
				touchCommand = MovementTracker.processVector8D(vec);
				wrapCoordinates(point.x, point.y, operation, touchCommand);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void doRightScreenProcess(float x, float y, PointF point,
			int operation) {

		CommandType touchCommand = CommandType.DEFAULT; // Init as DEFAULT
		if (System.currentTimeMillis() - prevRightTime > 20) {

			prevRightTime = System.currentTimeMillis();

			if (m_context instanceof PlayActivity) {

				PlayActivity activity = (PlayActivity) m_context;

				switch (operation) {
				case 0:
					// This action is a tap
					touchCommand = MovementTracker.processTilt(
							activity.tiltState, CommandType.TAP_NOTILT);

					wrapCoordinates(point.x, point.y, operation, touchCommand);
					break;
				case 1:
					// This action is a swipe
					Vector vec = new Vector(x, y);
					vec.normalise();

					// This function is used to determine the UP, DOWN, LEFT,
					// RIGHT direction of displacement

					try {
						// This function is used to determine the UP, DOWN,
						// LEFT, RIGHT direction of displacement
						touchCommand = MovementTracker.processTilt(
								activity.tiltState,
								MovementTracker.processVector4D(vec));
						wrapCoordinates(point.x, point.y, operation,
								touchCommand);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}
					break;
				}
			}
		}
	}

	/*
	 * public void doMouseProcess(float x, float y, int operation) { CommandType
	 * touchCommand; switch(operation) { case 0: touchCommand=
	 * CommandType.VIEW_INIT; wrapCoordinates(x, y, operation, touchCommand);
	 * break; case 1: if(System.currentTimeMillis()-prevRightTime>10){
	 * 
	 * prevRightTime = System.currentTimeMillis();
	 * 
	 * //Vector vec = new Vector(displacementX, displacementY); //
	 * vec.normalise(); touchCommand = CommandType.VIEW;
	 * 
	 * //touchCommand = MovementTracker.processVector(vec); wrapCoordinates(x,
	 * y, operation, touchCommand); } break; case 2: //Left Mouse clicked
	 * System.out.println("ACTION SENT"); touchCommand = CommandType.ACTION;
	 * wrapCoordinates(x, y, operation, touchCommand);
	 * Log.d("MultiTouch","SentMouseclick"); break; default: break; } } /*
	 * public void arrangeMouseArray(){ //Left mouse button always further from
	 * y = 0, so check this // and swap if the second touch is further from y =
	 * 0 if(rightMousePoint.getY() > leftMousePoint.getY()) { TouchCoordinates
	 * temp = new TouchCoordinates(leftMousePoint.getX(), leftMousePoint.getY(),
	 * leftMousePoint.getPointerCount()); leftMousePoint = new
	 * TouchCoordinates(rightMousePoint.getX(), rightMousePoint.getY(),
	 * rightMousePoint.getPointerCount()); rightMousePoint = new
	 * TouchCoordinates(temp.getX(), temp.getY(), temp.getPointerCount()); } }
	 * public void updateMouseBox(int operation) { switch(operation) { case 0:
	 * //Update the mousebox position mouseBoxCentreX = (leftMousePoint.getX() +
	 * rightMousePoint.getX())/2; mouseBoxCentreY = (leftMousePoint.getY() +
	 * rightMousePoint.getY())/2;
	 * 
	 * /*Vector vec = new Vector(leftMousePoint.getX() - mouseBoxCentreX,
	 * leftMousePoint.getY() - mouseBoxCentreY);
	 * 
	 * vec.normalise();
	 * 
	 * mouseBoxVectors[0] =
	 * vec.multiplyEq(getDisplacement(leftMousePoint.getX(),
	 * leftMousePoint.getY(), mouseBoxCentreX, mouseBoxCentreY)*1.5);
	 * mouseBoxVectors[1] = vec.reverse(); vec.normalise(); mouseBoxVectors[2] =
	 * vec.rotate(90, false).multiplyEq(MOUSEBOXTHRESHOLD); mouseBoxVectors[3] =
	 * vec.rotate(180, false);
	 */
	/*
	 * doRightScreenProcess(mouseBoxCentreX, mouseBoxCentreY, 1); break; case 1:
	 * //Reset mousebox mouseBoxCentreX = 0; mouseBoxCentreY = 0; break;
	 * default: break; } }
	 * 
	 * public void updateMouse(float x, float y, int pointerId, int operation) {
	 * switch(operation) { case 0: //Add button switch(mousePointer) {
	 * 
	 * case 0: //no mouse buttons detected yet
	 * 
	 * leftMousePoint = new TouchCoordinates(x, y, pointerId); mousePointer++;
	 * 
	 * // right screen touch-> shooting CommandType touchCommand =
	 * CommandType.SHOOT; wrapCoordinates(x, y, operation, touchCommand);
	 * Log.d("MultiTouch","Sending SHOOT");
	 * 
	 * break; case 1: // 1 mouse button detected rightMousePoint = new
	 * TouchCoordinates(x, y, pointerId); arrangeMouseArray();
	 * 
	 * Log.d("MultiTouch","Init MouseBox"); updateMouseBox(0); mousePointer++;
	 * 
	 * // if(leftMouseLifted) { // }
	 * 
	 * leftMouseLifted = false; rightMouseLifted = false; mouseRegistered =
	 * true; break; default: //Both mouse buttons already detected, do nothing
	 * break; } break; case 1: //Remove Button
	 * 
	 * switch(mousePointer) {
	 * 
	 * case 2: //one finger is lifted if(isLeftMouse(x, y)){//left mousebutton
	 * leftMouseLifted = true; } else { rightMouseLifted = true; }
	 * //Log.d("MultiTouch","1 of 2 mouse lifted"); mousePointer--; break; case
	 * 1: //both fingers lifted, unregister the mouse leftMousePoint = new
	 * TouchCoordinates(0,0,0); rightMousePoint = new TouchCoordinates(0,0,0);
	 * 
	 * //Log.d("MultiTouch","Both mouse lifted"); updateMouseBox(1);
	 * mousePointer--; mouseRegistered = false;
	 * Log.d("MultiTouch","Close MouseBox"); break; default: //Both mouse
	 * buttons already detected, do nothing break; }
	 * 
	 * break; case 2: //Update the mouse position if(isLeftMouse(x, y)){//left
	 * mousebutton leftMousePoint = new TouchCoordinates(x, y, pointerId); }
	 * else { rightMousePoint = new TouchCoordinates(x, y, pointerId); }
	 * //Log.d("MultiTouch","Updating MouseBox"); updateMouseBox(0); break;
	 * default: break; } }
	 * 
	 * public boolean isLeftMouse(float x, float y) {
	 * 
	 * //Check if this is the left mousebutton if(getDisplacement(x, y,
	 * leftMousePoint.getX(), leftMousePoint.getY()) < MOUSETHRESHOLD)//left
	 * mousebutton return true; else return false; }
	 */

	public double getDisplacement(float x1, float y1, float x2, float y2) {

		float displacementX, displacementY;

		displacementX = x1 - x2;
		displacementY = y1 - y2;

		double netMovement = Math.sqrt(Math.pow(displacementX, 2)
				+ Math.pow(displacementY, 2));

		return netMovement;
	}

	@Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mBitmap == null) {
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
		mBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight,
				Bitmap.Config.RGB_565);
		currentCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
    }
    
    currentCanvas.drawPaint(mPaint);
	currentCanvas.setBitmap(mBitmap);

	currentCanvas.drawBitmap(mBitmap, 0, 0, mPaint);
	
    currentCanvas.drawLine(0, SCREENCENTREY, SCREENWIDTH, SCREENCENTREY, mPaint);
    
   /*( if(mouseRegistered) {
    	mPaint.setStyle(Paint.Style.STROKE);
    	
    	//canvas.drawLine(mouseBoxCentreX+mouseBoxVectors[0]., startY, stopX, stopY, paint)
    }*/
    
    // draw all pointers
    for (int size = mActivePointers.size(), i = 0; i < size; i++) {
      
	      PointF point = mActivePointers.valueAt(i);
	      PointF startPoint = new PointF(startPointerX.valueAt(i), startPointerY.valueAt(i));
	      	
	      if(isLeftScreen(point.y)) {      
		      if (point != null) {
		    	mPaint.setColor(bigColors[i % 9]);
		    	mPaint.setStyle(Paint.Style.STROKE);
			    mPaint.setStrokeWidth(20);
			    canvas.drawCircle(startPoint.x, startPoint.y, THRESHOLD, mPaint);
		        //mPaint.setColor(Color.BLACK);
		        //canvas.drawLine(point.x, point.y, startPoint[i].getX(), startPoint[i].getY(), mPaint);
		        mPaint.setColor(colors[i % 9]);
		        mPaint.setStyle(Paint.Style.FILL);
		      	canvas.drawCircle(drawPoint.getX(), drawPoint.getY(), SIZE, mPaint);
		      }
	      }
	      else { //isRightScreen
	    	  //ACTION
	    	  canvas.drawCircle(point.x, point.y, SIZE, mPaint);
		      mPaint.setStyle(Paint.Style.FILL);
		      mPaint.setColor(colors[(i+1) % 9]);
		  }
    }
   /* if(m_context instanceof PlayActivity)
    {
		PlayActivity activity = (PlayActivity)m_context;
	    PointF tiltDrawPoint = new PointF((SCREENCENTREX + (activity.tiltX)/4*TILTTHRESHOLD), (SCREENCENTREY - (activity.tiltY)/4*TILTTHRESHOLD));
	
		mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setColor(Color.WHITE);
	    mPaint.setStrokeWidth(10);
	    canvas.drawCircle(SCREENCENTREX, SCREENCENTREY, TILTTHRESHOLD, mPaint);
	    mPaint.setStyle(Paint.Style.FILL);
	  	canvas.drawCircle(tiltDrawPoint.x, tiltDrawPoint.y, SIZE/2, mPaint);
	  	Log.d("Drawing","TiltX: " + activity.tiltX + " " + tiltDrawPoint.x + "  TiltY: " + activity.tiltY + "" + tiltDrawPoint.y );
    }*/
    canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40 , textPaint);
  }

	public void wrapCoordinates(float x, float y, int pointCount,
			CommandType touchCommand) {
		TouchCoordinates tc = new TouchCoordinates(x, y, pointCount);

		if (m_context instanceof PlayActivity) {
			PlayActivity activity = (PlayActivity) m_context;
			activity.updateCoordinates(tc, touchCommand);
			// Then call the method in the activity.
		}
	}

}