package com.example.gamecontrollerdatatransfer;

import gc.common_resources.*;

public class MovementTracker {
	
	public static CommandType processVector(Vector vector) throws InterruptedException {
		
		double angle = vector.angle(false);
		double threshold = 22.5;
		CommandType command = CommandType.DEFAULT;
		
		System.out.println("ANGLE"+angle);
		
		if(Math.abs(angle)>(135+threshold))
			command = CommandType.KEYBOARD_UP;
		else if(angle>(-135-threshold)&&angle<(-90-threshold))
			command = CommandType.KEYBOARD_UP_RIGHT;
		else if(angle>(-90-threshold)&&angle<(-45-threshold))
			command = CommandType.KEYBOARD_RIGHT;
		else if(angle>(-45-threshold)&&angle<(-threshold))
			command = CommandType.KEYBOARD_DOWN_RIGHT;
		else if(Math.abs(angle)<threshold)
			command = CommandType.KEYBOARD_DOWN;
		else if(angle>(threshold)&&angle<(45+threshold))
			command = CommandType.KEYBOARD_DOWN_LEFT;
		else if(angle>(45+threshold)&&angle<(90+threshold))
			command = CommandType.KEYBOARD_LEFT;
		else if(angle>(90+threshold)&&angle<(135+threshold))
			command = CommandType.KEYBOARD_UP_LEFT;
		
		return command;
	}

}
