import gc.common_resources.CommandType;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyTouch {
    
  private static final int DELAY= 100;
  public void identifyKey(CommandType command) {      
        try {
             Robot robot = new Robot();
            
            switch(command) {
	            case ACTION: //E.g. Shooting
	            	robot.keyPress(KeyEvent.VK_Z);
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_Z);
	            	break;
	            case VIEW:	//Should get X and Y from phone gyrometer/gravity for moving the mouse
	            	
	            	Point point =MouseInfo.getPointerInfo().getLocation();
	            	
	            	int x= (int) point.getX();
	            	int y= (int) point.getY();
	            	System.out.println(x+" "+y);
	            	/*if(command.getX()==0&&command.getY()==0)
	            		//robot.mouseMove(x+300,y);
	            		
	            	else*/ if(command.getX()==0&&command.getY()==1) //right
	            		robot.mouseMove(x+300,y);
	            	else if(command.getX()==1&&command.getY()==0) //left
	            		robot.mouseMove(x-300,y);
	            	//else
	            		//robot.mouseMove(x-300,y);	            	
	            	break;
	            //From here on all keyboard cases
	            case KEYBOARD_UP:	
	                robot.keyPress(KeyEvent.VK_UP);
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_UP);
	            	break;
	            case KEYBOARD_DOWN:
		            robot.keyPress(KeyEvent.VK_DOWN); 
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_DOWN);
	            	break;
	            case KEYBOARD_RIGHT:
	            	robot.keyPress(KeyEvent.VK_RIGHT);  
	                robot.delay(DELAY);
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            	break;
	            case KEYBOARD_LEFT:
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_LEFT);
	            	break;
	            case KEYBOARD_UP_RIGHT:
	                robot.keyPress(KeyEvent.VK_UP);
	            	robot.keyPress(KeyEvent.VK_RIGHT); 
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_UP); 
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            	break;
	            case KEYBOARD_DOWN_RIGHT:
		            robot.keyPress(KeyEvent.VK_DOWN); 
	            	robot.keyPress(KeyEvent.VK_RIGHT); 
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_DOWN); 
	            	robot.keyRelease(KeyEvent.VK_RIGHT);
	            	break;
	            case KEYBOARD_UP_LEFT:
	                robot.keyPress(KeyEvent.VK_UP);
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(DELAY);
		        	robot.keyRelease(KeyEvent.VK_UP);
		        	robot.keyRelease(KeyEvent.VK_LEFT);
	            	break;
	            case KEYBOARD_DOWN_LEFT:
		            robot.keyPress(KeyEvent.VK_DOWN); 
	                robot.keyPress(KeyEvent.VK_LEFT);
	                robot.delay(DELAY);
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