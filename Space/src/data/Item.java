package data;

import org.lwjgl.input.Mouse;
import static helpers.Clock.*;
import static helpers.Artist.*;

public class Item extends Collidable {
	
	protected int health;
	protected double direction, XSpeed, YSpeed, lifetime, rotationalVelocity;
	protected SpriteAnim spriteAnim;
	protected String stringAnim;
	protected String name;
	protected int ID, ownerID;
	protected boolean passable = false, visible;
	
	protected Client client;
	
	//makes an Item without an animated sprite (the texture has a single frame)
	public Item(double x, double y, int width, int height, String imageFile, Client client) {
		this.x = x-(width/2);
		this.y = y-(height/2);
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, 1);
		this.lifetime = -15.0;
		double speed = 10;
		this.direction = 0;
		this.XSpeed = speed*Math.cos(Math.toRadians(direction));
		this.YSpeed = speed*Math.sin(Math.toRadians(direction));
		this.rotationalVelocity = 0;
		ID = (int)(Math.random()*10000);
		this.client = client;
		this.health = 100;
		this.collisionDamage = 10;
	}
	
	//makes an Item with a specified animation length
	public Item(double x, double y, int width, int height, String imageFile, int animLength, Client client) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, animLength);
		this.lifetime = -15.0;
		this.direction = 0;
		this.rotationalVelocity = 0;
		double speed = 10;
		this.XSpeed = speed*Math.cos(Math.toRadians(direction));
		this.YSpeed = speed*Math.sin(Math.toRadians(direction));
		this.ID = (int)(Math.random()*10000);
		this.client = client;
		this.health = 100;
		this.collisionDamage = 10;
	}
	
	//specifies the time per frame on the animated Item
	public Item(double x, double y, int width, int height, String imageFile, int speed, int animLength, int frameTime, Client client) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, animLength, frameTime);
		this.lifetime = -15.0;
		this.direction = 0;
		this.rotationalVelocity = 0;
		this.XSpeed = speed*Math.cos(Math.toRadians(direction));
		this.YSpeed = speed*Math.sin(Math.toRadians(direction));
		this.ID = (int)(Math.random()*10000);
		this.client = client;
		this.health = 100;
		this.collisionDamage = 10;
	}
	
	//makes an Item based on an ItemType
	public Item(double x, double y, double direction, int lifespan, ItemType type, int ownerID, Client client) {
		this.x = x;
		this.y = y;
		this.width = type.width;
		this.height = type.height;
		this.direction = direction;
		this.rotationalVelocity = 0;
		this.XSpeed = type.speed*Math.cos(Math.toRadians(direction));
		this.YSpeed = type.speed*Math.sin(Math.toRadians(direction));
		this.lifetime = lifespan;
		this.spriteAnim = new SpriteAnim(type.tex, type.animLength, type.frameTime);
		this.name = type.name;
		this.lifetime = -15.0;
		ID = (int)(Math.random()*10000);
		this.ownerID = ownerID;
		this.client = client;
		this.health = type.health;
		this.collisionDamage = type.damage;
	}
	
	//makes an Item based on an ItemType with a rotational velocity
	public Item(double x, double y, double direction, int lifespan, ItemType type, int ownerID, double rotVel, Client client) {
		this.x = x;
		this.y = y;
		this.width = type.width;
		this.height = type.height;
		this.direction = direction;
		this.rotationalVelocity = rotVel;
		this.XSpeed = type.speed*Math.cos(Math.toRadians(direction));
		this.YSpeed = type.speed*Math.sin(Math.toRadians(direction));
		this.spriteAnim = new SpriteAnim(type.tex, type.animLength, type.frameTime);
		this.name = type.name;
		this.lifetime = lifespan;
		ID = (int)(Math.random()*10000);
		this.ownerID = ownerID;
		this.client = client;
		this.health = type.health;
		this.collisionDamage = type.damage;
	}
	
	//makes an Item based on an ItemType, and an owner ship
	public Item(double x, double y, double launchDirection, double shipDir, double shipSpeed, int lifespan, ItemType type, int ownerID, Client client) {
		this.x = x;
		this.y = y;
		this.width = type.width;
		this.height = type.height;
		this.direction = (int)launchDirection;
		this.rotationalVelocity = 0;
		
		if (shipSpeed != 0) {
			
		}
		this.XSpeed = type.speed*Math.cos(Math.toRadians(launchDirection));
		this.YSpeed = type.speed*Math.sin(Math.toRadians(launchDirection));
		if (shipSpeed != 0) {
			XSpeed += shipSpeed*Math.cos(Math.toRadians(shipDir));
			YSpeed += shipSpeed*Math.sin(Math.toRadians(shipDir));
		}
		
		this.lifetime = lifespan;
		this.spriteAnim = new SpriteAnim(type.tex, type.animLength, type.frameTime);
		this.name = type.name;
		this.lifetime = -15.0;
		ID = (int)(Math.random()*10000);
		this.ownerID = ownerID;
		this.client = client;
		this.health = type.health;
		this.collisionDamage = type.damage;
	}
	
	//item constructor using data from a socket
	public Item(String data, Client client) {
		String[] datapoints = data.split(" ");
		if (datapoints.length != 17) {
			System.out.println("Input error in Item(String data) :: " + data);
		}
		else {
			//datapoints[0] is "item"
			x = Double.parseDouble(datapoints[1]);
			y = Double.parseDouble(datapoints[2]);
			width = Integer.parseInt(datapoints[3]);
			height = Integer.parseInt(datapoints[4]);
			direction = Double.parseDouble(datapoints[5]);
			XSpeed = Double.parseDouble(datapoints[6]);
			YSpeed = Double.parseDouble(datapoints[7]);
			lifetime = Double.parseDouble(datapoints[8]);
			stringAnim = datapoints[9];
			passable = Boolean.parseBoolean(datapoints[10]);
			name = datapoints[11];
			ID = Integer.parseInt(datapoints[12]);
			rotationalVelocity = Double.parseDouble(datapoints[13]);
			health = Integer.parseInt(datapoints[14]);
			this.collisionDamage = Integer.parseInt(datapoints[15]);
			this.ownerID = Integer.parseInt(datapoints[16]);
			this.client = client;
		}
	}
	
	//returns the item's name
	/*public String getName() {
		return name;
	}*/
	
	public int getID() {
		return ID;
	}
	
	public int getOwnerID() {
		return ownerID;
	}
	
	//move items in their given direction, destroy on the end of lifespan, if given a lifespan;
	//lifetime == 100 means infinite lifetime;
	public boolean update() {
		x += XSpeed*Delta();
		y += YSpeed*Delta();

		direction += rotationalVelocity*Delta();
		if (direction > 360 || direction < -360) {
			direction = direction%360;
		}
		
		//System.out.println(lifetime);
		
		//lifetime == 100 means the item never dies without player interaction
		if (lifetime > 30) {
			return true;
		}
		//otherwise increment lifetime until lifetime >0 and destroy it by returning false
		lifetime += Delta();
		if (lifetime <= 0) {
			return true;
		}
		return false;
	}
	
	//draws the item on the map
	//graphic operations
	public void Draw() {
		if (spriteAnim == null) {
			spriteAnim = new SpriteAnim(stringAnim);
		}
		DrawQuadTexRot((int)x, (int)y, width, height, (int)direction, spriteAnim.getFrame(Delta()));
	}
	
	//returns whether the mouse is in contact with the item
	public boolean mouseContact() {
		if (Mouse.getX() >= x && Mouse.getX() <= x+width) {
			if (HEIGHT-1 - Mouse.getY() >= y && HEIGHT-1 - Mouse.getY() <= y+height) {
				return true;
			}
		}
		return false;
	}
	
	public boolean triggersCollisionCD() {
		switch(name) {
		case "Asteroid":
			return true;
		default:
			return false;
		}
	}
	
	public void handleCollision(int damage) {
		switch(name) {
		case "Bullet":
			destroy();
			break;
		case "Pickup":
			destroy();
			break;
		case "Asteroid":
			health -= damage;
			if (health <= 0) {
				destroy();
			}
			break;
		default:
			destroy();
			break;
		}
	}
	
	public void destroy() {
		lifetime = 0;
	}
	
	//returns a string representation of the object
	public String toString() {
		String reVal;
		if (spriteAnim == null) {
			reVal = "item " + x + " " + y + " " + width + " " + height + " " + Double.toString(direction) + " " + Double.toString(XSpeed) + " " + Double.toString(YSpeed) + " " + lifetime + " " + stringAnim + " " + passable + " " + name + " " + Integer.toString(ID) + " " + rotationalVelocity + " " + health + " " + collisionDamage + " " + ownerID;
		}
		else {
			reVal = "item " + Double.toString(x) + " " + Double.toString(y) + " " + Integer.toString(width) + " " + Integer.toString(height) + " " + Double.toString(direction) + " " + Double.toString(XSpeed) + " " + Double.toString(YSpeed) + " " + lifetime + " " + spriteAnim.toString() + " " + passable + " " + name + " " + Integer.toString(ID) + " " + rotationalVelocity + " " + health + " " + collisionDamage + " " + ownerID;
		}
		return reVal;
	}

}
