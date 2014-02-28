import gc.common_resources.*;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class KeyTouch {

	public void identifyKey(CommandType command, ArrayList<String> keyMap,
			float mouseRatio) {
		try {
			Robot robot = new Robot();
			KeyList keyList = new KeyList();

			switch (command) {

			case SHOOT:
				pressOneKey(KeyEvent.VK_SPACE, 10);
				break;
			case ACTION:
				pressOneKey(KeyEvent.BUTTON1_MASK, 10);
				break;
			case VIEW: // Should get X and Y from phone for moving the mouse
				// X: 10 - 300, Y: 210 - 500
				Point mousePos = new Point(MouseInfo.getPointerInfo()
						.getLocation());
				System.out.println("Received - X: " + command.getY() + " Y: "
						+ command.getX());
				float x = mousePos.x + (command.getY() * -1) * mouseRatio;
				float y = mousePos.y + command.getX() * mouseRatio;
				robot.mouseMove((int) x, (int) y);
				// System.out.println("View - X: "+x+" Y: "+y);
				break;
			case ACCELEROMETER: // x is for up down and y is for left right
				System.out.println("Received Accelerometer values- Y: "
						+ command.getY() + " X: " + command.getX() + " W: "
						+ command.getW() + " Z: " + command.getZ());

				Dimension dimension = java.awt.Toolkit.getDefaultToolkit()
						.getScreenSize();
				double screenWidth = dimension.getWidth();
				double screenHeight = dimension.getHeight();

				System.out.println("SCREEN WIDTH AND HEIGHT" + screenWidth
						+ " " + screenHeight);
				double newMouseXLocation = (screenHeight / (1.0))
						* (command.getZ() - command.getX()) / 2; // values[2]
				double newMouseYLocation = (screenWidth / (0.7 + 0.2))
						* (command.getW() - command.getY()); // values[1]

				System.out.println("New Mouse delta X,Y " + newMouseXLocation
						+ " " + newMouseYLocation);

				// current mouse pointer location
				Point point = MouseInfo.getPointerInfo().getLocation();
				int mouseX = (int) point.getX();
				int mouseY = (int) point.getY();
				System.out.println(mouseX + " " + mouseY);

				/*
				 * if(mouseY>=screenHeight) mouseY=0; else
				 * if(mouseX>=screenWidth) mouseX=0; else if(mouseY<=0) mouseY=
				 * (int) screenHeight; else if(mouseX<=0) mouseX=(int)
				 * screenWidth;
				 */

				robot.mouseMove(mouseX + (int) newMouseXLocation, mouseY
						+ (int) newMouseYLocation);
				// robot .mouseMove(mouseX,mouseY+(int)newMouseXLocation);
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

			// No Tilt
			case TAP_NOTILT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(0)), 10);
				break;
			case SWIPEUP_NOTILT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(1)), 10);
				break;
			case SWIPEDOWN_NOTILT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(2)), 10);
				break;
			case SWIPELEFT_NOTILT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(3)), 10);
				break;
			case SWIPERIGHT_NOTILT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(4)), 10);
				break;

			// Tilt Up
			case TAP_TILTUP:
				pressOneKey(keyList.getKeyEntry(keyMap.get(5)), 10);
				break;
			case SWIPEUP_TILTUP:
				pressOneKey(keyList.getKeyEntry(keyMap.get(6)), 10);
				break;
			case SWIPEDOWN_TILTUP:
				pressOneKey(keyList.getKeyEntry(keyMap.get(7)), 10);
				break;
			case SWIPELEFT_TILTUP:
				pressOneKey(keyList.getKeyEntry(keyMap.get(8)), 10);
				break;
			case SWIPERIGHT_TILTUP:
				pressOneKey(keyList.getKeyEntry(keyMap.get(9)), 10);
				break;

			// Tilt Down
			case TAP_TILTDOWN:
				pressOneKey(keyList.getKeyEntry(keyMap.get(10)), 10);
				break;
			case SWIPEUP_TILTDOWN:
				pressOneKey(keyList.getKeyEntry(keyMap.get(11)), 10);
				break;
			case SWIPEDOWN_TILTDOWN:
				pressOneKey(keyList.getKeyEntry(keyMap.get(12)), 10);
				break;
			case SWIPELEFT_TILTDOWN:
				pressOneKey(keyList.getKeyEntry(keyMap.get(13)), 10);
				break;
			case SWIPERIGHT_TILTDOWN:
				pressOneKey(keyList.getKeyEntry(keyMap.get(14)), 10);
				break;

			// Tilt Left
			case TAP_TILTLEFT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(15)), 10);
				break;
			case SWIPEUP_TILTLEFT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(16)), 10);
				break;
			case SWIPEDOWN_TILTLEFT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(17)), 10);
				break;
			case SWIPELEFT_TILTLEFT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(18)), 10);
				break;
			case SWIPERIGHT_TILTLEFT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(19)), 10);
				break;

			// Tilt Right
			case TAP_TILTRIGHT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(20)), 10);
				break;
			case SWIPEUP_TILTRIGHT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(21)), 10);
				break;
			case SWIPEDOWN_TILTRIGHT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(22)), 10);
				break;
			case SWIPELEFT_TILTRIGHT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(23)), 10);
				break;
			case SWIPERIGHT_TILTRIGHT:
				pressOneKey(keyList.getKeyEntry(keyMap.get(24)), 10);
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
			
			if (keyCode > 0) {
				robot.keyPress(keyCode);
				robot.delay(duration);
				robot.keyRelease(keyCode);
			} else {
				switch(keyCode) {
					case -1:	//LEFT-MOUSE-BUTTON
						robot.mousePress(InputEvent.BUTTON1_MASK );
						robot.mouseRelease(InputEvent.BUTTON1_MASK );
						break;
					case -2:	//RIGHT-MOUSE-BUTTON
						robot.mousePress(InputEvent.BUTTON3_MASK );
						robot.mouseRelease(InputEvent.BUTTON3_MASK );
						break;
				}
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private void pressTwoKeys(int keyCode1, int keyCode2, int duration) {
		try {
			if (keyCode1 > 0 && keyCode2 > 0 ) {
			Robot robot = new Robot();

			robot.keyPress(keyCode1);
			robot.keyPress(keyCode2);
			robot.delay(duration);
			robot.keyRelease(keyCode1);
			robot.keyRelease(keyCode2);
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}