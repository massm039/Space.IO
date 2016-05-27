package helpers;

import data.Server;
import data.MainMenu;
import data.Game;

public class StateManager {
	
	public static enum GameState {
		MAINMENU, GAME;
	}
	
	public static GameState gameState = GameState.MAINMENU;
	public static MainMenu mainMenu;
	public static Game game;
	
	public static void update() {
		switch(gameState) {
		case MAINMENU:
			if (mainMenu == null) {
				game = null;
				mainMenu = new MainMenu();
			}
			mainMenu.update();
			break;
		case GAME:
			if (game == null) {
				Server server = connectServer();
				mainMenu = null;
				game = new Game(server);
			}
			game.update();
			break;
		}
	}
	
	//sets the game state to a new state. Leaving a main menu, or returning to it.
	public static void setState(GameState newState) {
		mainMenu = null;
		game = null;
		gameState = newState;
	}
	
	//Generates an object that represents the server, allowing inter-process communication
	private static Server connectServer() { //TODO: Implement server connection
		return new Server("MainServer");
	}
	
}
