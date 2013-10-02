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
            
            if(arrowKey.equals("RIGHT"))   
            	robot.keyPress(KeyEvent.VK_RIGHT);       
            if(arrowKey.equals("LEFT"))
                robot.keyPress(KeyEvent.VK_LEFT);
            if(arrowKey.equals("UP"))
                robot.keyPress(KeyEvent.VK_UP);
            if(arrowKey.equals("DOWN"));
                robot.keyPress(KeyEvent.VK_DOWN);         
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
