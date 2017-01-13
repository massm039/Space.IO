package data;

import static helpers.Artist.DrawQuadTex;
import static helpers.Artist.LoadPNG;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

public class Game {
	
	private Client client;
	private Player player;
	private Texture background;
	
	//Constructor
	public Game(Client client) {
		this.background = LoadPNG("background");
		this.client = client;
		player = new Player(client);
		client.addPlayer(player.getCharacter());
		client.start();
	}
	
	//Draws all of the client's items and characters, and runs the player's inputs.
	public void update() {
		
		//Draws the background image
		DrawQuadTex(0, 0, 2048, 1024, background);
		
		//Updates the player's ship based on the player's inputs, then sends the info to the client
		player.update();
		
		//Draws all of the client's Items
		ArrayList<Item> items = client.getItems();
		for (int i=0; i<items.size(); i++) {
			if (items.get(i).update()) {
				items.get(i).Draw();
			}
			else {
				items.remove(i);
				i--;
			}
		}
		
		//Draws all of the client's Characters
		for (Character i : client.getCharacters()) {
			i.Draw();
		}
	
	}

}
