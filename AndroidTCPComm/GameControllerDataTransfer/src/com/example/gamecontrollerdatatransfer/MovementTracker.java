package com.example.gamecontrollerdatatransfer;

public class MovementTracker {
	
	public static String processVector(Vector vector) throws InterruptedException {
		
		//Thread.sleep(1000);
		double angle = vector.angle(false);
		String direction="";
		System.out.println("ANGLE"+angle);
		if(Math.abs(angle)>(135+22.5))
			direction="UP";
		else if(angle>(-22.5-135)&&angle<(-90-22.5))
			direction="UP RIGHT";
		else if(angle>(-90-22.5)&&angle<(-45-22.5))
			direction="RIGHT";
		else if(angle>(-45-22.5)&&angle<(-22.5))
			direction="DOWN RIGHT";
		else if(Math.abs(angle)<22.5)
			direction="DOWN";
		else if(angle>(22.5)&&angle<(45+22.5))
			direction="DOWN LEFT";
		else if(angle>(45+22.5)&&angle<(90+22.5))
			direction="LEFT";
		else if(angle>(90+22.5)&&angle<(135+22.5))
			direction="TOP LEFT";
		
		System.out.println("DIRECTION="+direction);
		return direction;
	}

}
