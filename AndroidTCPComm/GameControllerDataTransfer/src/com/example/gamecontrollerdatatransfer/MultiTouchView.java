package com.example.gamecontrollerdatatransfer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class MultiTouchView extends View {

  private static final int SIZE = 60;
  private static final int THRESHHOLD = 150;
  private static final int SCREENCENTRE = 900;
  private String arrowKey="";
  private long prevTime=0;
  private Context m_context;
  private SparseArray<PointF> mActivePointers;
  private TouchCoordinates[] startPoint = new TouchCoordinates[10];
  private Paint mPaint;
  private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
      Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
      Color.LTGRAY, Color.YELLOW };
  
  private int[] bigColors = { Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY, Color.YELLOW, Color.BLUE, Color.GREEN, Color.MAGENTA,
	      Color.BLACK, Color.LTGRAY};

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

      mActivePointers.put(pointerId, f);
      startPoint[pointerId]= new TouchCoordinates(f.x, f.y, pointerId);
      break;
    }
    
    case MotionEvent.ACTION_MOVE: { // a pointer was moved
      for (int size = event.getPointerCount(), i = 0; i < size; i++) {
    	int pId=event.getPointerId(i);
        PointF point = mActivePointers.get(pId);
        if (point != null) {
        	
          point.x = event.getX(i);
          point.y = event.getY(i);
          float displacementX=point.x-startPoint[pId].getX();
          float displacementY=point.y-startPoint[pId].getY();
          
          double netMovement= Math.sqrt(Math.pow(displacementX, 2)+Math.pow(displacementY, 2));
          if(netMovement>THRESHHOLD){
        	  //MOVEMENT
        	  if(startPoint[pId].getY()>SCREENCENTRE) {
	        	  Vector vec = new Vector(displacementX, displacementY);
	        	  vec.normalise();
	        	  try {
	        		  arrowKey = MovementTracker.processVector(vec);
	        		  
	        		  if(System.currentTimeMillis()-prevTime>100){
	        			  wrapCoordinates(point.x,point.y,i);
	        			  prevTime = System.currentTimeMillis();
	        		  }
	        		  
	        	  } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	        	  	}
	        	  //send it to fuzzzzzzzzzy logic 
        	  }
	      }
         else {
        	 //ACTION
        	 //TODO send action to wrap
        	 // after ENUM
         }
       }
        
      }
      break;
    }
    case MotionEvent.ACTION_UP:
    case MotionEvent.ACTION_POINTER_UP:
    case MotionEvent.ACTION_CANCEL: {
      mActivePointers.remove(pointerId);
      startPoint[pointerId]= new TouchCoordinates(0, 0, pointerId);
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
      if(startPoint[i].getY()>SCREENCENTRE) {
      
	      if (point != null) {
	    	mPaint.setColor(bigColors[i % 9]);
	        canvas.drawCircle(startPoint[i].getX(), startPoint[i].getY(), THRESHHOLD,mPaint);
	        mPaint.setColor(Color.BLACK);
	        canvas.drawLine(point.x, point.y, startPoint[i].getX(), startPoint[i].getY(), mPaint);
	        mPaint.setColor(colors[i % 9]);
	      	canvas.drawCircle(point.x, point.y, SIZE, mPaint);
	      }
      }
      else {
    	  //ACTION

    	   canvas.drawCircle(point.x, point.y, SIZE, mPaint);
      }
    }
    canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40 , textPaint);
  }

  public void wrapCoordinates(float x, float y, int pointCount){
	  TouchCoordinates tc = new TouchCoordinates(x, y, pointCount);
		
	  if(m_context instanceof PlayActivity)
	  {
	      PlayActivity activity = (PlayActivity)m_context;
	      activity.updateCoordinates(tc, arrowKey);
	      // Then call the method in the activity.
	  }
	  
  }

} 