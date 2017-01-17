package data;

import static data.ItemType.*;
import static helpers.Clock.*;

public enum InventoryItem {
	
	//Enumerated Types: itemtype, timePerFire, speed
	gunBlaster(Bullet, 8),
	gunAsteroider(Asteroid, 15);
	
	ItemType projectile;
	double timePerFire;
	double cooldown = 0;
	
	//Constructor
	InventoryItem(ItemType projectile, double timePerFire) {
		this.projectile = projectile;
		this.timePerFire = timePerFire;
	}
	
	//Activates the current Inventory Item
	public void activate(Character ship, Client client) {
		cooldown += Delta();
		if (cooldown > timePerFire) {
			cooldown = cooldown - timePerFire;
			//System.out.println(ship.getID());
			Item item = new Item((int)ship.getX(), (int)ship.getY(), ship.getTurretDirection(), ship.getMovementDirection(), (int)ship.getSpeed(), -15, projectile, ship.getID(), client);
			client.addItem(item);
		}
	}

}
