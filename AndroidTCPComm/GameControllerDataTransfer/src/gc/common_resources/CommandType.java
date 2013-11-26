package gc.common_resources;

public enum CommandType {
	DEFAULT, ACTION, VIEW, VIEW_INIT, KEYBOARD_UP, KEYBOARD_DOWN, KEYBOARD_RIGHT, KEYBOARD_LEFT, KEYBOARD_UP_RIGHT, KEYBOARD_DOWN_RIGHT, KEYBOARD_UP_LEFT, KEYBOARD_DOWN_LEFT;
	
	private float x;
	private float y;
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
	
	public void setX(float X){
		this.x = X;
	}

	public void setY(float Y){
		this.y = Y;
	}
}
