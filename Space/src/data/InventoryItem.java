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
	public void activate(Character ship, ServerSender toServer) {
		cooldown += Delta();
		if (cooldown > timePerFire) {
			cooldown = cooldown - timePerFire;
			//TODO: launch a projectile
			Item item = new Item((int)ship.getX(), (int)ship.getY(), ship.getTurretDirection(), 15+(int)ship.getSpeed(), projectile);
			toServer.addItem(item);
		}
	}

}
