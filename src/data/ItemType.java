package data;

public enum ItemType {
	
	//Asteroid has a single frame with no animation
	Asteroid("Asteroid", 64, 64, "asteroid", 1),
	//Pickup has 5 frames, and a rotation time of 3 units
	Pickup("Pickup", 32, 32, "pickup", 0, 5, 3),
	//Other enumerated types
	Bullet("Bullet", 8, 3, "bullet", 15);
	
	int width, height, speed, animLength = 1, frameTime = 1;
	String name, tex;
	
	//For items with no animations
	ItemType (String name, int width, int height, String texture, int speed) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.tex = texture;
		this.speed = speed;
	}
	
	//For items with multiple frames in their animation (all animated textures)
	ItemType (String name, int width, int height, String texture, int speed, int animLength, int frameTime) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.tex = texture;
		this.speed = speed;
		this.animLength = animLength;
		this.frameTime = frameTime;
	}
	
	//Returns the name of the ItemType
	public String toString() {
		return name;
	}
	
}
