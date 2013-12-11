package gc.common_resources;

public enum CommandType {
	DEFAULT, ACTION, SHOOT, VIEW, VIEW_INIT, ACCELEROMETER, KEYBOARD_UP, KEYBOARD_DOWN, KEYBOARD_RIGHT, KEYBOARD_LEFT, KEYBOARD_UP_RIGHT, KEYBOARD_DOWN_RIGHT, KEYBOARD_UP_LEFT, KEYBOARD_DOWN_LEFT;
	
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
