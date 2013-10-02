package com.example.gamecontrollerdatatransfer;

public class TouchCoordinates {
	
	private float x;
	private float y;
	private float init_x;
	private float init_y;
	
	private int pointerCount;
	
	public TouchCoordinates(float x,float y, int pointerCount){
		
		this.x= x;
		this.y=y;
		this.pointerCount=pointerCount;
	}
	
	public TouchCoordinates() {
		//NOP
	}
	
	public void setX(float x) {
		this.x=x;
	}
	
	public void setY(float y) {
		this.y=y;
	}
	
	public void setInit_X(float init_x) {
		this.init_x=init_x;
	}
	
	public void setInit_Y(float init_y) {
		this.init_y=init_y;
	}
	
	public void setPointerCount(int pointerCount) {
		this.pointerCount=pointerCount;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getInit_X() {
		return this.init_x;
	}
	
	public float getInit_Y() {
		return this.init_y;
	}
	
	public int getPointerCount() {
		return this.pointerCount;
	}

}
