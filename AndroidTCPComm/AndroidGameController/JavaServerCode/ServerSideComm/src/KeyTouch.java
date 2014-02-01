import gc.common_resources.CommandType;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class KeyTouch {

	public void identifyKey(CommandType command) {
		try {
			Robot robot = new Robot();

			switch (command) {
			
			case SHOOT:
				pressOneKey(KeyEvent.VK_SPACE, 10);
				break;
			case ACTION: 
				pressOneKey(KeyEvent.BUTTON1_MASK, 10);
				break;
			case VIEW: // Should get X and Y from phone for moving the mouse
				// X: 10 - 300, Y: 210 - 500
				System.out.println("Received - X: " + command.getY() + " Y: "
						+ command.getX());
				float x = 1366 - ((command.getY() - 110) / 290 * 1366);
				float y = (command.getX() - 210) / 290 * 768;
				robot.mouseMove((int) x, (int) y);
				// System.out.println("View - X: "+x+" Y: "+y);
				break;
			case ACCELEROMETER: // x is for up down and y is for left right
				System.out.println("Received Accelerometer values- Y: " + command.getY() + " X: "
						+ command.getX()+" W: " + command.getW() + " Z: "
								+ command.getZ());

				Dimension dimension= java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				double screenWidth= dimension.getWidth();
				double screenHeight= dimension.getHeight();
				
				System.out.println("SCREEN WIDTH AND HEIGHT"+screenWidth+" "+screenHeight);
				double newMouseXLocation= (screenHeight/(1.0))*(command.getZ() -command.getX())/2; // values[2]
				double newMouseYLocation= (screenWidth/(0.7+0.2))*(command.getW()-command.getY()); // values[1]
				
				System.out.println("New Mouse delta X,Y "+newMouseXLocation+" "+newMouseYLocation);
				
				//current mouse pointer location
				Point point = MouseInfo.getPointerInfo().getLocation();
				int mouseX = (int) point.getX();
				int mouseY = (int) point.getY();
				System.out.println(mouseX + " " + mouseY);
				
				/*if(mouseY>=screenHeight)
					mouseY=0;
				else if(mouseX>=screenWidth)
					mouseX=0;
				else if(mouseY<=0)
					mouseY= (int) screenHeight;
				else if(mouseX<=0)
					mouseX=(int) screenWidth;*/
					
				robot.mouseMove(mouseX+(int)newMouseXLocation,mouseY+(int)newMouseYLocation);
				//robot	.mouseMove(mouseX,mouseY+(int)newMouseXLocation);
				// Should get X and Y from phone
								// gyrometer/gravity for moving the mouse
				 
				break;	
				
			// From here on all keyboard cases
			case KEYBOARD_UP:
				pressOneKey(KeyEvent.VK_UP, 100);
				break;
			case KEYBOARD_DOWN:
				pressOneKey(KeyEvent.VK_DOWN, 100);
				break;
			case KEYBOARD_RIGHT:
				pressOneKey(KeyEvent.VK_RIGHT, 100);
				break;
			case KEYBOARD_LEFT:
				pressOneKey(KeyEvent.VK_LEFT, 100);
				break;
			case KEYBOARD_UP_RIGHT:
				pressTwoKeys(KeyEvent.VK_UP, KeyEvent.VK_RIGHT, 100);
				break;
			case KEYBOARD_DOWN_RIGHT:
				pressTwoKeys(KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, 100);
				break;
			case KEYBOARD_UP_LEFT:
				pressTwoKeys(KeyEvent.VK_UP, KeyEvent.VK_LEFT, 100);
				break;
			case KEYBOARD_DOWN_LEFT:
				pressTwoKeys(KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, 100);
				break;
				
			case TAP_NOTILT: 
				pressOneKey(KeyEvent.VK_SPACE, 100);
				break;
			case SWIPEUP_NOTILT: 
				pressOneKey(KeyEvent.VK_SPACE, 120);
				break;
			default: // Should not come here Print the type
				System.out.println("In keyTouch. Unexpected CommandType: "
						+ command);
				break;
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	private void pressOneKey(int keyCode, int duration) {
		try {
			Robot robot = new Robot();
			
			robot.keyPress(keyCode);
			robot.delay(duration);
			robot.keyRelease(keyCode);
			
		} catch (AWTException e) {
			e.printStackTrace();
		}		
	}
	
	private void pressTwoKeys(int keyCode1, int keyCode2, int duration) {
		try {
			Robot robot = new Robot();
			
			robot.keyPress(keyCode1);
			robot.keyPress(keyCode2);
			robot.delay(duration);
			robot.keyRelease(keyCode1);
			robot.keyRelease(keyCode2);
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}