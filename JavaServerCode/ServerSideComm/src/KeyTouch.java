import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import gc.common_resources.*;

public class KeyTouch {
    
  public void identifyKey(CommandType command) {      
        try {
             Robot robot = new Robot();
            
            switch(command) {
	            case ACTION: //E.g. Shooting
	            	break;
	            case VIEW:	//Should get X and Y from phone gyrometer/gravity for moving the mouse
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
