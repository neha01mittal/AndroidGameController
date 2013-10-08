package com.example.gamecontrollerdatatransfer;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class MultiTouchView extends View {

  private static final int SIZE = 60;
  private static final int THRESHHOLD = 300;
  private String arrowKey="";
  private float temp_x;
  private float temp_y;
  private Context m_context;
  private SparseArray<PointF> mActivePointers;
  private Paint mPaint;
  private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
      Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
      Color.LTGRAY, Color.YELLOW };

  private Paint textPaint;


  public MultiTouchView(Context context, AttributeSet attrs) {
    super(context, attrs);
    m_context= context;
    initView();
  }

  private void initView() {
    mActivePointers = new SparseArray<PointF>();
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // set painter color to a color you like
    mPaint.setColor(Color.BLUE);
    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setTextSize(20);
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

      PointF f = new PointF();
      f.x = event.getX(pointerIndex);
      f.y = event.getY(pointerIndex);
      temp_x=f.x;
      temp_y=f.y;
      mActivePointers.put(pointerId, f);
      //initPointers.put(pointerId, f);
      break;
    }
    case MotionEvent.ACTION_MOVE: { // a pointer was moved
      for (int size = event.getPointerCount(), i = 0; i < size; i++) {
        PointF point = mActivePointers.get(event.getPointerId(i));
        if (point != null) {
          point.x = event.getX(i);
          point.y = event.getY(i);
          //System.out.println("FDFD"+temp_x+"fdfd"+point.x);
          double netMovement= Math.sqrt(Math.pow(point.x-temp_x, 2)+Math.pow(point.y-temp_y, 2));
          if(netMovement>THRESHHOLD){
	        	  //System.out.println("OUTSIDE THE CIRCLE"+netMovement);
	        	  Vector vec = new Vector(point.x-temp_x, point.y-temp_y);
	        	  vec.normalise();
	        	  try {
	        		  arrowKey = MovementTracker.processVector(vec);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	  //send it to fuzzzzzzzzzy logic 
	          }
        }
        
      }
      break;
    }
    case MotionEvent.ACTION_UP:
    case MotionEvent.ACTION_POINTER_UP:
    case MotionEvent.ACTION_CANCEL: {
      mActivePointers.remove(pointerId);
      temp_x=0;
      temp_y=0;
      arrowKey="";
      break;
    }
    }
    invalidate();

    return true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // draw all pointers
    for (int size = mActivePointers.size(), i = 0; i < size; i++) {
      PointF point = mActivePointers.valueAt(i);
      if (point != null)
    	mPaint.setColor(Color.RED);
        canvas.drawCircle(temp_x, temp_y, THRESHHOLD,mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(point.x, point.y, temp_x, temp_y, mPaint);
        mPaint.setColor(colors[i % 9]);
      	canvas.drawCircle(point.x, point.y, SIZE, mPaint);
      	
      	wrapCoordinates(point.x,point.y,i);
    }
    canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40 , textPaint);
  }

  public void wrapCoordinates(float x, float y, int pointCount){
	  TouchCoordinates tc = new TouchCoordinates(x, y, pointCount);
	  //System.out.println("CHECKKKK at wrapCoord"+tc.getX()+" "+tc.getY()+" "+tc.getPointerCount()+"ARROWKEY"+arrowKey);
		
	  if(m_context instanceof PlayActivity)
	  {
	      PlayActivity activity = (PlayActivity)m_context;
	      activity.updateCoordinates(tc, arrowKey);
	      // Then call the method in the activity.
	  }
	  
  }

} 