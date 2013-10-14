import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyTouch {
    
  public void identifyKey(String arrowKey) {      
        try {
            
            Robot robot = new Robot();
            // Creates the delay of 5 sec so that you can open notepad before
            // Robot start writing
            // System.out.println("ROBOT IN ACTION"+arrowKey);
             String[] keys = arrowKey.split(" ");
            
            for(int i = 0; i < keys.length; i++) {
            	System.out.println("Processing "+keys[i]);
            
            	if(keys[i].equals("RIGHT")) {
	            	robot.keyPress(KeyEvent.VK_RIGHT);  
	                robot.delay(100);
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            }
	            
	            if(keys[i].equals("LEFT")) {
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(100);
		        	robot.keyRelease(KeyEvent.VK_LEFT);
		        }
	            
	            if(keys[i].equals("UP")) {
	                robot.keyPress(KeyEvent.VK_UP);
	                robot.delay(100);
	                robot.keyRelease(KeyEvent.VK_UP);
		        }
	            
	            if(keys[i].equals("DOWN")) {
	                robot.keyPress(KeyEvent.VK_DOWN); 
	                robot.delay(100);
	                robot.keyRelease(KeyEvent.VK_DOWN);
	            }       
	        }      
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
