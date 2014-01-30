package gc.common_resources;

public enum CommandType {

	DEFAULT, ACTION, SHOOT, VIEW, VIEW_INIT, ACCELEROMETER, 
	KEYBOARD_UP, KEYBOARD_DOWN, KEYBOARD_RIGHT, KEYBOARD_LEFT, KEYBOARD_UP_RIGHT, KEYBOARD_DOWN_RIGHT, KEYBOARD_UP_LEFT, KEYBOARD_DOWN_LEFT, 
	TAP_NOTILT, TAP_TILTUP, TAP_TILTDOWN, TAP_TILTLEFT, TAP_TILTRIGHT,
	SWIPEUP_NOTILT, SWIPEUP_TILTUP, SWIPEUP_TILTDOWN, SWIPEUP_TILTLEFT, SWIPEUP_TILTRIGHT,
	SWIPEDOWN_NOTILT, SWIPEDOWN_TILTUP, SWIPEDOWN_TILTDOWN, SWIPEDOWN_TILTLEFT, SWIPEDOWN_TILTRIGHT,
	SWIPELEFT_NOTILT, SWIPELEFT_TILTUP, SWIPELEFT_TILTDOWN, SWIPELEFT_TILTLEFT, SWIPELEFT_TILTRIGHT,
	SWIPERIGHT_NOTILT, SWIPERIGHT_TILTUP, SWIPERIGHT_TILTDOWN, SWIPERIGHT_TILTLEFT, SWIPERIGHT_TILTRIGHT;
	
	private float x;
	private float y;
	private double z;
	private double w;
	
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	public double getZ() {
		return this.z;
	}
	
	public double getW() {
		return this.w;
	}
	
	public void setX(float X){
		this.x = X;
	}

	public void setY(float Y){
		this.y = Y;
	}
	
	public void setW(double W){
		this.w = W;
	}

	public void setZ(double Z){
		this.z = Z;
	}
}
