package com.example.gamecontrollerdatatransfer;

import gc.common_resources.*;

public class MovementTracker {
	
	public static CommandType processVector8D(Vector vector) throws InterruptedException {
		
		double angle = vector.angle(false);
		double threshold = 22.5;
		CommandType currCommand = CommandType.DEFAULT;
		
		System.out.println("ANGLE"+angle);
		
		if(Math.abs(angle)>(135+threshold))
			currCommand = CommandType.KEYBOARD_UP;
		else if(angle>(-135-threshold)&&angle<(-90-threshold))
			currCommand = CommandType.KEYBOARD_UP_RIGHT;
		else if(angle>(-90-threshold)&&angle<(-45-threshold))
			currCommand = CommandType.KEYBOARD_RIGHT;
		else if(angle>(-45-threshold)&&angle<(-threshold))
			currCommand = CommandType.KEYBOARD_DOWN_RIGHT;
		else if(Math.abs(angle)<threshold)
			currCommand = CommandType.KEYBOARD_DOWN;
		else if(angle>(threshold)&&angle<(45+threshold))
			currCommand = CommandType.KEYBOARD_DOWN_LEFT;
		else if(angle>(45+threshold)&&angle<(90+threshold))
			currCommand = CommandType.KEYBOARD_LEFT;
		else if(angle>(90+threshold)&&angle<(135+threshold))
			currCommand = CommandType.KEYBOARD_UP_LEFT;
		
		return currCommand;
	}
public static CommandType processVector4D(Vector vector) throws InterruptedException {
		
		double angle = vector.angle(false);
		double threshold = 22.5;
		CommandType currCommand = CommandType.DEFAULT;
		
		System.out.println("ANGLE"+angle);
		
		if(Math.abs(angle)>(135))
			currCommand = CommandType.SWIPEUP_NOTILT;
		else if(angle>(-135)&&angle<(-45-threshold))
			currCommand = CommandType.SWIPERIGHT_NOTILT;
		else if(Math.abs(angle)<(45))
			currCommand = CommandType.SWIPEDOWN_NOTILT;
		else if(angle>(45)&&angle<(135))
			currCommand = CommandType.SWIPELEFT_NOTILT;
		
		return currCommand;
	}
	
	public static CommandType processTilt (int tiltState, CommandType preProcCommand) {

		CommandType currCommand = CommandType.DEFAULT;
		
		switch(tiltState) {
		case 0: //NO TILT
			currCommand = preProcCommand;
			break;
		case 1:	//TILT UP
			switch(preProcCommand){
			case TAP_NOTILT: currCommand = CommandType.TAP_TILTUP;
			break;
			case SWIPEUP_NOTILT: currCommand = CommandType.SWIPEUP_TILTUP;
			break;
			case SWIPEDOWN_NOTILT: currCommand = CommandType.SWIPEDOWN_TILTUP;
			break;
			case SWIPELEFT_NOTILT: currCommand = CommandType.SWIPELEFT_TILTUP;
			break;
			case SWIPERIGHT_NOTILT: currCommand = CommandType.SWIPERIGHT_TILTUP;
			break;
			}
			
			break;
		case 2: //TILT DOWN
			switch(preProcCommand){
			case TAP_NOTILT: currCommand = CommandType.TAP_TILTDOWN;
			break;
			case SWIPEUP_NOTILT: currCommand = CommandType.SWIPEUP_TILTDOWN;
			break;
			case SWIPEDOWN_NOTILT: currCommand = CommandType.SWIPEDOWN_TILTDOWN;
			break;
			case SWIPELEFT_NOTILT: currCommand = CommandType.SWIPELEFT_TILTDOWN;
			break;
			case SWIPERIGHT_NOTILT: currCommand = CommandType.SWIPERIGHT_TILTDOWN;
			break;
			}
			break;
		case 3: //TILT LEFT
			switch(preProcCommand){
			case TAP_NOTILT: currCommand = CommandType.TAP_TILTLEFT;
			break;
			case SWIPEUP_NOTILT: currCommand = CommandType.SWIPEUP_TILTLEFT;
			break;
			case SWIPEDOWN_NOTILT: currCommand = CommandType.SWIPEDOWN_TILTLEFT;
			break;
			case SWIPELEFT_NOTILT: currCommand = CommandType.SWIPELEFT_TILTLEFT;
			break;
			case SWIPERIGHT_NOTILT: currCommand = CommandType.SWIPERIGHT_TILTLEFT;
			break;
			}
			break;
		case 4: //TILT RIGHT
			switch(preProcCommand){
			case TAP_NOTILT: currCommand = CommandType.TAP_TILTRIGHT;
			break;
			case SWIPEUP_NOTILT: currCommand = CommandType.SWIPEUP_TILTRIGHT;
			break;
			case SWIPEDOWN_NOTILT: currCommand = CommandType.SWIPEDOWN_TILTRIGHT;
			break;
			case SWIPELEFT_NOTILT: currCommand = CommandType.SWIPELEFT_TILTRIGHT;
			break;
			case SWIPERIGHT_NOTILT: currCommand = CommandType.SWIPERIGHT_TILTRIGHT;
			break;
			}
			break;
		}
		
		return currCommand;
	}
}
