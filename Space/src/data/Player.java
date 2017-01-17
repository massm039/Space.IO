package data;

import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import helpers.Artist;

public class Player {
	
	private Client client;
	private Character ship;
	private ArrayList<InventoryItem> inventory;
	
	//Constructor
	Player(Client client) {
		this.client = client;
		client.setPlayer(this);
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
		if (Mouse.isButtonDown(0)) { //left click held -> shoot
			activateItems(); //makes bullets based on the weapons in the character's inventory
		}
		
		//update the ships location
		ship.update();
		
		if (ship.dead) {
			respawn();
		}
		
		//send new data about the ship to the server
		updateClient(ship);
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
			i.activate(ship, client);
		}
	}
	
	//Adds an item to the inventory (guns ect)
	public void addItem(InventoryItem item) {
		inventory.add(item);
	}
	
	//Call when the current ship dies, respawning as a new character
	public void respawn() {
		ship.health = ship.maxHealth;
		ship.xSpeed = 0;
		ship.ySpeed = 0;
		ship.x = Math.random()*Artist.WIDTH;
		ship.y = Math.random()*Artist.HEIGHT;
		ship.dead = false;
	}
	
	//gives the updated Character to the server to draw
	private void updateClient(Character ship) {
		client.updateShip(ship);
	}

}
