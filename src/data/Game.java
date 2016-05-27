package data;

import static helpers.Artist.DrawQuadTex;
import static helpers.Artist.LoadPNG;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

public class Game {
	
	private Server server;
	private Player player;
	private Texture background;
	
	//Constructor
	public Game(Server server) {
		this.background = LoadPNG("background");
		this.server = server;
		player = new Player(server);
		server.addPlayer(player.getCharacter());
	}
	
	//Draws all of the server's items and characters, and runs the player's inputs.
	public void update() {
		
		//Draws the background image
		DrawQuadTex(0, 0, 2048, 1024, background);
		
		//Updates the player's ship based on the player's inputs, then sends the info to the server
		player.update();
		
		//Draws all of the Server's Items
		ArrayList<Item> items = server.getItems();
		for (int i=0; i<items.size(); i++) {
			if (items.get(i).update()) {
				items.get(i).Draw();
			}
			else {
				items.remove(i);
				i--;
			}
		}
		
		//Draws all of the Server's Characters
		for (Character i : server.getCharacters()) {
			i.Draw();
		}
	
	}

}
