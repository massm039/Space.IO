package data;

public enum ItemType {
	
	//Asteroid has a single frame with no animation
	Asteroid("Asteroid", 64, 64, "asteroid", 0, 50, 50),
	//Pickup has 5 frames, and a rotation time of 3 units
	Pickup("Pickup", 32, 32, "pickup", 0, 1000, 0, 5, 3),
	//Other enumerated types
	Bullet("Bullet", 24, 9, "bullet", 15, 100, 20);
	
	int width, height, speed, animLength, frameTime, health, damage;
	String name, tex;
	
	//For items with no animations
	ItemType (String name, int width, int height, String texture, int speed, int health, int damage) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.tex = texture;
		this.speed = speed;
		this.health = health;
		this.damage = damage;
		this.animLength = 1;
		this.frameTime = 1;
	}
	
	//For items with multiple frames in their animation (all animated textures)
	ItemType (String name, int width, int height, String texture, int speed, int health, int damage, int animLength, int frameTime) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.tex = texture;
		this.speed = speed;
		this.health = health;
		this.damage = damage;
		this.animLength = animLength;
		this.frameTime = frameTime;
	}
	
	//Returns the name of the ItemType
	public String toString() {
		return name;
	}
	
}
