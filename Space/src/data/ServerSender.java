package data;

import java.util.ArrayList;

public class ServerSender {
	
	private String serverName;
	private ArrayList<Item> items;
	private ArrayList<Character> characters;
	
	//Constructor
	public ServerSender(String name) {
		this.serverName = name;
		items = new ArrayList<Item>();
		characters = new ArrayList<Character>();
	}
	
	//Draw Functions
	public void drawHealthBars(){
		for (Character i : characters) {
			if (i.isVisible()) {
				i.drawHealthBar();
			}
		}
	}
	
	//Adds a player's ship to the list of ships in the game
	public void addPlayer(Character ship) {
		characters.add(ship);
	}
	
	//Adds an item to the list of items in the game
	public void addItem(Item item) {
		items.add(item);
	}
	
	//Updates the given ship's location and statistics
	public void updateShip(Character ship) {
		for (Character i : characters) {
			if (i.getName().equals(ship.getName())) {
				characters.remove(i);
				characters.add(ship);
				break;
			}
		}
	}
	
	//Getters and Setters
	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
	}
	
	public String getName() {
		return serverName;
	}

}
