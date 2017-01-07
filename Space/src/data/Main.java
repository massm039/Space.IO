package data;

import org.lwjgl.opengl.Display;

import static helpers.Artist.*;
import static helpers.Clock.*;
import helpers.StateManager;

public class Main {
	
	public static void main(String args[]) {
		new Main();
	}
	
	public Main() {
		
		BeginSession();
		
		while(!Display.isCloseRequested()) {
			
			update(); //Update Time
			
			StateManager.update();
			
			Display.update();
			Display.sync(60);
		}
		
		Display.destroy();
	}
}