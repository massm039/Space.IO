package data;

import java.util.ArrayList;
import org.lwjgl.input.Mouse;

public class Player {
	
	private Server server;
	private Character ship;
	private ArrayList<InventoryItem> inventory;
	
	//Constructor
	Player(Server server) {
		this.server = server;
		inventory = new ArrayList<InventoryItem>();
		this.ship = new Character();
		addItem(InventoryItem.gunBlaster);
	}
	
	//Get method for the player's ship object
	public Character getCharacter() {
		return ship;
	}
	
	//Update the player's position based on inputs
	public void update() {
		//TODO: update direction facing !
		if (Mouse.isButtonDown(0)) { //left click held -> shoot
			activateItems(); //makes bullets based on the weapons in the character's inventory
		}
		while(Mouse.next())
			if (Mouse.getEventButtonState() && Mouse.getEventButton() == 1) { //right click -> toggle engine
				ship.toggleEngine();
			}
		
		//update the ships location
		ship.update();
		
		//send new data about the ship to the server
		updateServer(ship);
	}
	
	//Adds an item to the player's inventory, modifying the ship
	public void addToInventory(InventoryItem item) {
		if (!inventory.contains(item)) {
			inventory.add(item);
		}
	}
	
	//Uses all of the ship's weapons and other pickups
	public void activateItems() {
		for (InventoryItem i : inventory) {
			i.activate(ship, server);
		}
	}
	
	//Adds an item to the inventory (guns ect)
	public void addItem(InventoryItem item) {
		inventory.add(item);
	}
	
	//Call when the current ship dies, respawning as a new character
	public void respawn() {
		//TODO: implement some random location algorithm based on the server size
		ship = new Character(0, 0, 32, 32, 100, 10, "MyShip", 1, server);
	}
	
	//gives the updated Character to the server to draw
	private void updateServer(Character ship) {
		//TODO: implement server
		server.updateShip(ship);
	}

}
