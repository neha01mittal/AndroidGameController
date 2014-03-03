package com.example.gamecontrollerdatatransfer;

import gc.common_resources.CommandType;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MultiTouchView extends View {

	private static final int THRESHOLD = 70, TILTTHRESHOLD = 120;
	private static final int MINDRAGTIMETHRESHOLD = 170;
	private final int SIZE, TILTSIZE;
	private final int SCREENCENTREX, SCREENCENTREY, SCREENWIDTH, SCREENHEIGHT;
	private long prevLeftTime = 0, prevRightTime = 0, prevDragTime = 0;
	private Context m_context;

	private boolean leftTouchRegistered = false;
	private static int leftScreenPointerId;

	private WindowManager wm;
	private Display screenDisplay;
	private Point screenSize;
	private Vibrator v;
	private static Handler leftScreenHandler;

	private SparseArray<PointF> mActivePointers;
	private SparseArray<Float> startPointerX, startPointerY;
	private SparseArray<Float> lastSentPointerX, lastSentPointerY;
	private SparseArray<Long> pointerStartDragTime;
	private SparseArray<Boolean> registeredDragPointer;

	private TouchCoordinates leftDrawPoint;
	
	private static RectF tiltCircleRect;
	private static PointF tiltCircleLineStart, tiltCircleLineEnd, tiltCircleDotStart, tiltCircleDotEnd;

	private Paint mPaint, glowPaint, textPaint;
	private int[] colors = { Color.BLUE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.RED, Color.GRAY, Color.DKGRAY,
			Color.LTGRAY, Color.YELLOW,
			Color.BLACK };

	private int[] bigColors = { Color.LTGRAY, Color.WHITE, Color.GRAY,
			Color.RED, Color.DKGRAY, Color.YELLOW, Color.BLUE, Color.GREEN,
			Color.GREEN, Color.CYAN };

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
		leftScreenHandler = new Handler();

		screenSize = new Point();
		screenDisplay.getSize(screenSize);
		SCREENWIDTH = screenSize.x;
		SCREENHEIGHT = screenSize.y;
		
		SCREENCENTREX = SCREENWIDTH / 2;
		SCREENCENTREY = SCREENHEIGHT / 2;
		SIZE = SCREENWIDTH / 40;
		TILTSIZE = SCREENWIDTH / 80;
		
		tiltCircleRect = new RectF(SCREENCENTREX - TILTTHRESHOLD, SCREENCENTREY - TILTTHRESHOLD, SCREENCENTREX + TILTTHRESHOLD, SCREENCENTREY + TILTTHRESHOLD);
		tiltCircleLineStart = new PointF((float)Math.cos(Math.toRadians(60)) * (TILTTHRESHOLD + 8), (float)Math.sin(Math.toRadians(60)) * (TILTTHRESHOLD + 8));
		tiltCircleLineEnd = new PointF((float)Math.cos(Math.toRadians(60)) * (TILTTHRESHOLD + 12), (float)Math.sin(Math.toRadians(60)) * (TILTTHRESHOLD + 12));
		tiltCircleDotStart = new PointF((float)Math.cos(Math.toRadians(45)) * (TILTTHRESHOLD + 8), (float)Math.sin(Math.toRadians(45)) * (TILTTHRESHOLD + 8));
		tiltCircleDotEnd = new PointF((float)Math.cos(Math.toRadians(45)) * (TILTTHRESHOLD + 12), (float)Math.sin(Math.toRadians(45)) * (TILTTHRESHOLD + 12));
		
		// Init Arrays
		for (int i = 0; i < 2; i++)
			initView();
	}

	private void initView() {
		mActivePointers = new SparseArray<PointF>();
		startPointerX = new SparseArray<Float>();
		startPointerY = new SparseArray<Float>();
		lastSentPointerX = new SparseArray<Float>();
		lastSentPointerY = new SparseArray<Float>();
		pointerStartDragTime = new SparseArray<Long>();
		registeredDragPointer = new SparseArray<Boolean>();
		
		// Set Painter properties
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setDither(true);
		//mPaint.setColor(Color.BLUE);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));
		
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(20);
		
		glowPaint = new Paint();
		glowPaint.set(mPaint);
		glowPaint.setColor(Color.argb(235, 74, 138, 255));
		glowPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

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
					leftDrawPoint = new TouchCoordinates(point.x, point.y,
							pointerId);
					leftTouchRegistered = true;

					leftScreenPointerId = pointerId;

					if (prevLeftTime == 0)
						prevLeftTime = System.currentTimeMillis();

					leftScreenHandler.removeCallbacks(mUpdateTask);
					leftScreenHandler.postDelayed(mUpdateTask, 100);
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

					// If point was detected to be on leftScreen when it started
					if (isLeftScreen(startPoint.y)) {
						processDirectionalMovement(point, startPoint);
					} else { // isRightScreen
						Long time = pointerStartDragTime.get(pId);

						// If this pointer's drag time has not been tracked yet
						if (time == null) {

							displacementX = point.x - startPoint.x;
							displacementY = point.y - startPoint.y;

							double netMovement = Math.sqrt(Math.pow(
									displacementX, 2)
									+ Math.pow(displacementY, 2));

							// Only start tracking this pointer if the pointer
							// has moved outside of the threshold
							if (netMovement > THRESHOLD)
								pointerStartDragTime.put(pId,
										new Long(System.currentTimeMillis()));

						} else {
							// This pointer is currently being tracked
							Log.d("PointerTracking",
									"Time tracked: "
											+ (System.currentTimeMillis() - time));
							if (durationHasPassed(time,
									System.currentTimeMillis(),
									MINDRAGTIMETHRESHOLD)) {
								// This pointer is dragging
								Boolean bDragPoint = registeredDragPointer
										.get(pId);

								// If this pointer has not been registered as a
								// drag pointer
								if (bDragPoint == null) {
									// Register it
									processDragPoint(point, startPoint, pId);

									registeredDragPointer.put(pId, true);
									lastSentPointerX.put(pId, point.x);
									lastSentPointerY.put(pId, point.y);
								} else {
									PointF lastSentPoint = new PointF(
											lastSentPointerX.get(pId),
											lastSentPointerY.get(pId));

									processDragPoint(point, lastSentPoint, pId);
								}

							}
						}
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

				// If point was detected to be on leftScreen when it started
				if (isLeftScreen(startPoint.y)) {
					leftTouchRegistered = false;
					leftDrawPoint = new TouchCoordinates(0, 0, pointerId);
					leftScreenHandler.removeCallbacks(mUpdateTask);
				} else {// isRightScreen

					Long time = pointerStartDragTime.get(pointerId);
					// Check if it is a drag
					if (time != null
							&& durationHasPassed(time,
									System.currentTimeMillis(),
									MINDRAGTIMETHRESHOLD)) {
						// This action is a drag
						pointerStartDragTime.remove(pointerId);

						registeredDragPointer.remove(pointerId);
						lastSentPointerX.remove(pointerId);
						lastSentPointerY.remove(pointerId);

					} else {
						// Get the displacement of the pointer from its
						// startpoint
						displacementX = point.x - startPoint.x;
						displacementY = point.y - startPoint.y;

						double netMovement = Math.sqrt(Math.pow(displacementX,
								2) + Math.pow(displacementY, 2));

						// Package the values to be sent to the server
						if (netMovement > THRESHOLD) {
							// This action is a swipe
							processTapSwipe(displacementX, displacementY,
									point, 1);
							;
						} else {
							// This action is a tap
							processTapSwipe(displacementX, displacementY,
									point, 0);
						}
					}

					// If this pointer's drag time is being tracked
					if (time != null) {
						// Remove the track
						pointerStartDragTime.remove(pointerId);
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

	private Runnable mUpdateTask = new Runnable() {
		public void run() {

			// Perform the task only if the left touch is still registered
			if (leftTouchRegistered) {
				// If a 50msec has passed since last directional command was
				// sent
				if (durationHasPassed(prevLeftTime, System.currentTimeMillis(),
						70)) {
					// Prompts a check to see if the leftscreen touch
					// displacement needs to be sent to the
					// server. This circumstance is usually triggered when the
					// lefttouch is registered but
					// has not moved enough to trigger a touch move event
					PointF point = new PointF(
							mActivePointers.get(leftScreenPointerId).x,
							mActivePointers.get(leftScreenPointerId).y);
					PointF startPoint = new PointF(
							startPointerX.get(leftScreenPointerId),
							startPointerY.get(leftScreenPointerId));

					processDirectionalMovement(point, startPoint);
				}
				leftScreenHandler.postDelayed(this, 90);
			}
		}
	};

	public boolean isLeftScreen(float pointX) {

		if (pointX > SCREENCENTREY)
			return true;
		else
			return false;
	}

	public boolean durationHasPassed(long prevTime, long currTime,
			double duration) {
		if ((currTime - prevTime) > duration) {
			Log.d("Timer", "Duration: " + (currTime - prevTime));
			return true;
		} else
			return false;
	}

	public void processDirectionalMovement(PointF point, PointF startPoint) {

		float displacementX, displacementY;

		// Get the displacement of the pointer from its startpoint
		displacementX = point.x - startPoint.x;
		displacementY = point.y - startPoint.y;

		double netMovement = Math.sqrt(Math.pow(displacementX, 2)
				+ Math.pow(displacementY, 2));

		if (netMovement > THRESHOLD) {
			// Update the draw point if the pointer exceeds the threshold
			// So that it will only appear within the threshold circle
			double multiplier = (double) THRESHOLD / netMovement;
			leftDrawPoint = new TouchCoordinates(
					(float) (startPoint.x + multiplier * displacementX),
					(float) (startPoint.y + multiplier * displacementY), 1);

			// This function will package the values to be sent to the server
			sendDirectionalCommand(displacementX, displacementY, point, 0);
		} else
			leftDrawPoint = new TouchCoordinates(point.x, point.y, 1);
	}

	public void sendDirectionalCommand(float displacementX,
			float displacementY, PointF point, int operation) {

		// In this process, the distance moved by the pointer is used to
		// interpret the direction that the
		// player wants to press the ^v<> directional keys
		// So the commandtypes used are KEYBOARD_UP, KEYBOARD_DOWN,
		// KEYBOARD_LEFT, KEYBOARD_RIGHT

		// Only send a packet if the last packet sent was >50mseconds ago
		if (durationHasPassed(prevLeftTime, System.currentTimeMillis(), 70)) {

			prevLeftTime = System.currentTimeMillis();

			Vector vec = new Vector(displacementX, displacementY);
			vec.normalise();
			CommandType touchCommand = CommandType.DEFAULT; // Init as DEFAULT

			try {
				// This function is used to determine the UP, DOWN, LEFT, RIGHT
				// direction of displacement
				v.vibrate(100);
				touchCommand = MovementTracker.processVector8D(vec);
				wrapCoordinates(point.x, point.y, operation, touchCommand);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void processDragPoint(PointF point, PointF lastSentPoint, int pId) {
		float displacementX, displacementY;

		// Get the displacement of the pointer from its startpoint
		displacementX = point.x - lastSentPoint.x;
		displacementY = point.y - lastSentPoint.y;

		double netMovement = Math.sqrt(Math.pow(displacementX, 2)
				+ Math.pow(displacementY, 2));

		// This function will package the values to be sent to the server
		if (sendDragCommand(displacementX, displacementY))
			updateLastSentDragPoint(point.x, point.y, pId);
	}

	public boolean sendDragCommand(float displacementX, float displacementY) {
		if (durationHasPassed(prevDragTime, System.currentTimeMillis(), 20)) {
			prevDragTime = System.currentTimeMillis();

			v.vibrate(100);
			wrapCoordinates(displacementX, displacementY, 1, CommandType.VIEW);
			Log.d("Dragpointer", "Sent Dragpoint: " + displacementX + " : "
					+ displacementY);
			return true;
		} else
			return false;
	}

	public void updateLastSentDragPoint(float pointX, float pointY, int pId) {
		lastSentPointerX.setValueAt(pId, pointX);
		lastSentPointerY.setValueAt(pId, pointY);
	}

	public void processTapSwipe(float x, float y, PointF point, int operation) {

		CommandType touchCommand = CommandType.DEFAULT; // Init as DEFAULT
		if (System.currentTimeMillis() - prevRightTime > 20) {

			prevRightTime = System.currentTimeMillis();

			if (m_context instanceof PlayActivity) {

				PlayActivity activity = (PlayActivity) m_context;

				switch (operation) {
				case 0:
					// This action is a tap
					v.vibrate(100);
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
						v.vibrate(100);
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

		//If Tilt Detection is on, draw the Accelerometer
		if (m_context instanceof PlayActivity) {
			
			PlayActivity activity = (PlayActivity) m_context;
			
			if(activity.isTiltDetectionOn()){
				
				PointF tiltDrawPoint = new PointF((SCREENCENTREX + (float)((activity.tiltX)/2.5*TILTTHRESHOLD)), (SCREENCENTREY - (float)((activity.tiltY)/2.5*TILTTHRESHOLD)));
	
				// Set paint colours and visibility for accelerometer circle here
				glowPaint.setColor(Color.argb(150, 124, 188, 255));
				mPaint.setColor(Color.argb(150, 255, 255, 255));
				
				//glowPaint: Draw Ring
				glowPaint.setStrokeJoin(Paint.Join.BEVEL);
				glowPaint.setStrokeCap(Paint.Cap.SQUARE);
				glowPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
				glowPaint.setStyle(Paint.Style.STROKE);
				glowPaint.setStrokeWidth(10f);
				canvas.drawLine(SCREENCENTREX - tiltCircleLineStart.x, SCREENCENTREY - tiltCircleLineStart.y, SCREENCENTREX - tiltCircleLineEnd.x, SCREENCENTREY - tiltCircleLineEnd.y, glowPaint);
				canvas.drawLine(SCREENCENTREX - tiltCircleLineStart.x, SCREENCENTREY + tiltCircleLineStart.y, SCREENCENTREX - tiltCircleLineEnd.x, SCREENCENTREY + tiltCircleLineEnd.y, glowPaint);
				
				canvas.drawLine(SCREENCENTREX - tiltCircleLineStart.y, SCREENCENTREY - tiltCircleLineStart.x, SCREENCENTREX - tiltCircleLineEnd.y, SCREENCENTREY - tiltCircleLineEnd.x, glowPaint);
				canvas.drawLine(SCREENCENTREX - tiltCircleLineStart.y, SCREENCENTREY + tiltCircleLineStart.x, SCREENCENTREX - tiltCircleLineEnd.y, SCREENCENTREY + tiltCircleLineEnd.x, glowPaint);
				
				canvas.drawLine(SCREENCENTREX + tiltCircleLineStart.x, SCREENCENTREY - tiltCircleLineStart.y, SCREENCENTREX + tiltCircleLineEnd.x, SCREENCENTREY - tiltCircleLineEnd.y, glowPaint);
				canvas.drawLine(SCREENCENTREX + tiltCircleLineStart.x, SCREENCENTREY + tiltCircleLineStart.y, SCREENCENTREX + tiltCircleLineEnd.x, SCREENCENTREY + tiltCircleLineEnd.y, glowPaint);

				canvas.drawLine(SCREENCENTREX + tiltCircleLineStart.y, SCREENCENTREY - tiltCircleLineStart.x, SCREENCENTREX + tiltCircleLineEnd.y, SCREENCENTREY - tiltCircleLineEnd.x, glowPaint);
				canvas.drawLine(SCREENCENTREX + tiltCircleLineStart.y, SCREENCENTREY + tiltCircleLineStart.x, SCREENCENTREX + tiltCircleLineEnd.y, SCREENCENTREY + tiltCircleLineEnd.x, glowPaint);
				
				canvas.drawLine(SCREENCENTREX - tiltCircleDotStart.x, SCREENCENTREY - tiltCircleDotStart.y, SCREENCENTREX - tiltCircleDotEnd.x, SCREENCENTREY - tiltCircleDotEnd.y, glowPaint);
				canvas.drawLine(SCREENCENTREX - tiltCircleDotStart.x, SCREENCENTREY + tiltCircleDotStart.y, SCREENCENTREX - tiltCircleDotEnd.x, SCREENCENTREY + tiltCircleDotEnd.y, glowPaint);
				canvas.drawLine(SCREENCENTREX + tiltCircleDotStart.x, SCREENCENTREY - tiltCircleDotStart.y, SCREENCENTREX + tiltCircleDotEnd.x, SCREENCENTREY - tiltCircleDotEnd.y, glowPaint);
				canvas.drawLine(SCREENCENTREX + tiltCircleDotStart.x, SCREENCENTREY + tiltCircleDotStart.y, SCREENCENTREX + tiltCircleDotEnd.x, SCREENCENTREY + tiltCircleDotEnd.y, glowPaint);

				
				canvas.drawArc(tiltCircleRect, (float)60, (float)60, false, glowPaint);
				canvas.drawArc(tiltCircleRect, (float)150, (float)60, false, glowPaint);
				canvas.drawArc(tiltCircleRect, (float)240, (float)60, false, glowPaint);
				canvas.drawArc(tiltCircleRect, (float)330, (float)60, false, glowPaint);
	
				//glowPaint: Draw Point
				glowPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
				glowPaint.setStyle(Paint.Style.FILL);	
				canvas.drawLine(tiltDrawPoint.x - TILTSIZE, tiltDrawPoint.y, tiltDrawPoint.x - 5, tiltDrawPoint.y, glowPaint);
				canvas.drawLine(tiltDrawPoint.x + 5, tiltDrawPoint.y, tiltDrawPoint.x + TILTSIZE, tiltDrawPoint.y, glowPaint);
				canvas.drawLine(tiltDrawPoint.x, tiltDrawPoint.y - TILTSIZE, tiltDrawPoint.x, tiltDrawPoint.y - 5, glowPaint);
				canvas.drawLine(tiltDrawPoint.x, tiltDrawPoint.y + 5, tiltDrawPoint.x, tiltDrawPoint.y + TILTSIZE, glowPaint);				
				
				Log.d("Drawing","TiltX: " + activity.tiltX + " " + tiltDrawPoint.x + "  TiltY: " + activity.tiltY + "" + tiltDrawPoint.y );
			
			} else {
				//Otherwise draw a line to divide the two screens into halves
				// Set paint colours and visibility for line here
				glowPaint.setColor(Color.argb(150, 114, 178, 255));
				glowPaint.setStrokeJoin(Paint.Join.BEVEL);
				glowPaint.setStrokeCap(Paint.Cap.SQUARE);
				glowPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
				glowPaint.setStyle(Paint.Style.STROKE);
				glowPaint.setStrokeWidth(10f);
				
				canvas.drawLine(0, SCREENCENTREY, SCREENWIDTH, SCREENCENTREY,
						glowPaint);
			}
		}

		// Draw all Pointers on the left and right screen
		for (int size = mActivePointers.size(), i = 0; i < size; i++) {

			PointF point = mActivePointers.valueAt(i);
			PointF startPoint = new PointF(startPointerX.valueAt(i),
					startPointerY.valueAt(i));
			

			if (isLeftScreen(startPoint.y)) {
				if (point != null) {			
					// Set paint colours and visibility for pointers and rings here				
					glowPaint.setColor(Color.argb(255, 144, 208, 255));
					glowPaint.setAlpha(255);
					glowPaint.setStrokeJoin(Paint.Join.ROUND);
					glowPaint.setStrokeCap(Paint.Cap.ROUND);
					glowPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
					mPaint.setColor(Color.argb(255, 255, 255, 255));
					
					//glowPaint: Draw Ring
					glowPaint.setStyle(Paint.Style.STROKE);
					glowPaint.setStrokeWidth(15f);
					canvas.drawCircle(startPoint.x, startPoint.y, THRESHOLD, glowPaint);

					//glowPaint: Draw Point
					glowPaint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(leftDrawPoint.getX(), leftDrawPoint.getY(), SIZE+2, glowPaint);	

					//mPaint: Draw Ring
					mPaint.setStyle(Paint.Style.STROKE);
					mPaint.setStrokeWidth(8f);
					canvas.drawCircle(startPoint.x, startPoint.y, THRESHOLD, mPaint);

					//mPaint: Draw Point				
					mPaint.setStyle(Paint.Style.FILL);
					canvas.drawCircle(leftDrawPoint.getX(), leftDrawPoint.getY(), SIZE-3, mPaint);
				}
			} else { // isRightScreen

				// Set paint colours and visibility for pointers here				
				glowPaint.setColor(colors[i % 9]);	
				glowPaint.setAlpha(255);
				glowPaint.setStrokeJoin(Paint.Join.ROUND);
				glowPaint.setStrokeCap(Paint.Cap.ROUND);
				glowPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
				mPaint.setColor(Color.argb(255, 255, 255, 255));
				
				//glowPaint: Draw Point
				glowPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(point.x, point.y, SIZE+7, glowPaint);	
				
				//mPaint: Draw Point				
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(point.x, point.y, SIZE, mPaint);
			}
		}				
	}

	public void wrapCoordinates(float x, float y, int operation,
			CommandType touchCommand) {
		TouchCoordinates tc = new TouchCoordinates(x, y, operation);

		if (m_context instanceof PlayActivity) {
			PlayActivity activity = (PlayActivity) m_context;
			activity.updateCoordinates(tc, touchCommand);
			// Then call the method in the activity.
		}
	}

}