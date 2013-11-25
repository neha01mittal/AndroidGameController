import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import gc.common_resources.*;

public class KeyTouch {
    
  public void identifyKey(CommandType command) {      
        try {            
            Robot robot = new Robot();
            
            switch(command) {
	            case ACTION: //E.g. Shooting
	            	robot.mousePress(InputEvent.BUTTON1_MASK);
	                robot.delay(10);
	                robot.mouseRelease(InputEvent.BUTTON1_MASK);
	            	break;
	            case VIEW:	//Should get X and Y from phone for moving the mouse
	            	//X: 10 - 300, Y: 210 - 500
	            	System.out.println("Received - X: "+command.getY()+" Y: "+command.getX());
	            	float x = 1366 - ((command.getY()-110)/290 * 1366);
	            	float y = (command.getX()-210)/290 * 768;
	            	robot.mouseMove((int)x, (int)y);
	            	//System.out.println("View - X: "+x+" Y: "+y);
	            	break;
	            //From here on all keyboard cases
	            case KEYBOARD_UP:	
	                robot.keyPress(KeyEvent.VK_UP);
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_UP);
	            	break;
	            case KEYBOARD_DOWN:
		            robot.keyPress(KeyEvent.VK_DOWN); 
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_DOWN);
	            	break;
	            case KEYBOARD_RIGHT:
	            	robot.keyPress(KeyEvent.VK_RIGHT);  
	                robot.delay(100);
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            	break;
	            case KEYBOARD_LEFT:
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_LEFT);
	            	break;
	            case KEYBOARD_UP_RIGHT:
	                robot.keyPress(KeyEvent.VK_UP);
	            	robot.keyPress(KeyEvent.VK_RIGHT); 
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_UP); 
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            	break;
	            case KEYBOARD_DOWN_RIGHT:
		            robot.keyPress(KeyEvent.VK_DOWN); 
	            	robot.keyPress(KeyEvent.VK_RIGHT); 
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_DOWN); 
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            	break;
	            case KEYBOARD_UP_LEFT:
	                robot.keyPress(KeyEvent.VK_UP);
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_UP);
		        	robot.keyRelease(KeyEvent.VK_LEFT);
	            	break;
	            case KEYBOARD_DOWN_LEFT:
		            robot.keyPress(KeyEvent.VK_DOWN); 
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_DOWN);
		        	robot.keyRelease(KeyEvent.VK_LEFT);
	            	break;
	            default: //Should not come here Print the type
	            		System.out.println("In keyTouch. Unexpected CommandType: "+command);
	            	break;
            }            
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
