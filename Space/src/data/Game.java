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
		client.addCharacter(player.getCharacter());
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
		ArrayList<Item> toBeRemoved = new ArrayList<Item>();
		synchronized (items) {
			for (Item i : items) {
				if (i.getOwnerID() == player.getCharacter().getID()) {
					if (i.update()) {
						i.Draw();
					}
					else {
						toBeRemoved.add(i);
					}
				}
				else {
					i.update();
					i.Draw();
				}
			}
			for (Item i : toBeRemoved) {
				client.sendRemovalMessage(i);
				items.remove(i);
			}
		}
		
		//Draws all of the client's Characters
		ArrayList<Character> characters = client.getCharacters();
		synchronized (characters) {
			for (Character i : characters) {
				if (i != player.getCharacter()) {
					//System.out.println("calls move");
					i.move();
				}
				i.Draw();
			}
		}
		
		//TODO: client.drawHealthBars();
	
	}

}
