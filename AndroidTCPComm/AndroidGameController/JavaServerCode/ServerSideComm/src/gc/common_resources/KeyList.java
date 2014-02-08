package gc.common_resources;

import java.awt.event.KeyEvent;

public class KeyList {
	
	public KeyList() {
		
	}
	

	public int getKeyEntry(String stringEntry) {
		int entryLength = stringEntry.length();
		int ascii = 0, keyCode = 0;
		
		switch(entryLength) {
			case 1: // 0 ~ 9, A ~ Z and all special characters such as !@#$%^&^&*()_+-=[];',./{}:"<>?\|
					// For these characters the ascii int value corresponds to their keyEvent int value
					ascii = (int)stringEntry.charAt(0);
					
					// Refer Ascii table, this ensures that these are ascii values for 
					// single characters, starting at ! and ending at ~
					if(ascii > 32 && ascii < 177) {
						if(ascii > 96 && ascii < 123) {
							// a ~ z , convert these to their Upper Caps equivalent
							keyCode = ascii - 32;
						}else {						
							//Otherwise do nothing
							keyCode = ascii;
						}
					}else {
						System.out.println("Invalid keyEntry detected. Entry: " + stringEntry + "  Ascii: " + ascii);
						keyCode = 0;
					}
				break;
			case 2:
				if(stringEntry.equals("F1"))
					keyCode = KeyEvent.VK_F1;
				else if(stringEntry.equals("F2"))
					keyCode = KeyEvent.VK_F2;
				else if(stringEntry.equals("F3"))
					keyCode = KeyEvent.VK_F3;
				else if(stringEntry.equals("F4"))
					keyCode = KeyEvent.VK_F4;
				else if(stringEntry.equals("F5"))
					keyCode = KeyEvent.VK_F5;
				else if(stringEntry.equals("F6"))
					keyCode = KeyEvent.VK_F6;
				else 	if(stringEntry.equals("F7"))
					keyCode = KeyEvent.VK_F7;
				else 	if(stringEntry.equals("F8"))
					keyCode = KeyEvent.VK_F8;
				else if(stringEntry.equals("F9"))
					keyCode = KeyEvent.VK_F9;
				else if(stringEntry.equals("UP"))
					keyCode = KeyEvent.VK_UP;
				break;
			case 3: 
				if(stringEntry.equals("F10"))
					keyCode = KeyEvent.VK_F10;
				else if(stringEntry.equals("F11"))
					keyCode = KeyEvent.VK_F11;
				else if(stringEntry.equals("F12"))
					keyCode = KeyEvent.VK_F12;
				else if(stringEntry.equals("TAB"))
					keyCode = KeyEvent.VK_TAB;
				else if(stringEntry.equals("ESC"))
					keyCode = KeyEvent.VK_ESCAPE;
				else if(stringEntry.equals("END"))
					keyCode = KeyEvent.VK_END;
				else if(stringEntry.equals("ALT"))
					keyCode = KeyEvent.VK_ALT;
				else if(stringEntry.equals("DEL"))
					keyCode = KeyEvent.VK_DELETE;
				break;
			case 4:
				if(stringEntry.equals("HOME"))
					keyCode = KeyEvent.VK_HOME;
				else if(stringEntry.equals("LEFT"))
					keyCode = KeyEvent.VK_LEFT;
				else if(stringEntry.equals("DOWN"))
					keyCode = KeyEvent.VK_DOWN;
				else if(stringEntry.equals("CAPS"))
					keyCode = KeyEvent.VK_CAPS_LOCK;
				else if(stringEntry.equals("CTRL"))
					keyCode = KeyEvent.VK_CONTROL;
				break;
			case 5:
				if(stringEntry.equals("SHIFT"))
					keyCode = KeyEvent.VK_SHIFT;
				else if(stringEntry.equals("ENTER"))
					keyCode = KeyEvent.VK_ENTER;
				else if(stringEntry.equals("PG UP"))
					keyCode = KeyEvent.VK_PAGE_UP;
				else if(stringEntry.equals("SPACE"))
					keyCode = KeyEvent.VK_SPACE;
				break;
			default:
				if(stringEntry.equals("BACKSPACE"))
					keyCode = KeyEvent.VK_BACK_SPACE;
				else {
					System.out.println("Invalid keyEntry detected. Entry: " + stringEntry + "  Ascii: " + ascii);
					keyCode = 0;
				}
			break;
		}
		return keyCode;
	}
}
