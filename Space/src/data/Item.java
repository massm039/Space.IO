package data;

import org.lwjgl.input.Mouse;
import static helpers.Clock.*;
import static helpers.Artist.*;

public class Item {
	
	protected int x, y, width, height, speed, angle;
	protected double XSpeed, YSpeed, lifetime;
	protected SpriteAnim spriteAnim;
	protected String stringAnim;
	protected String name;
	protected int ID, ownerID;
	protected boolean passable = false, visible;
	
	//makes an Item without an animated sprite (the texture has a single frame)
	public Item(int x, int y, int width, int height, String imageFile) {
		this.x = x-(width/2);
		this.y = y-(height/2);
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, 1);
		lifetime = -15.0;
		angle = 0;
		ID = (int)(Math.random()*10000);
	}
	
	//makes an Item with a specified animation length
	public Item(int x, int y, int width, int height, String imageFile, int animLength) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, animLength);
		lifetime = -15.0;
		angle = 0;
		ID = (int)(Math.random()*10000);
	}
	
	//specifies the time per frame on the animated Item
	public Item(int x, int y, int width, int height, String imageFile, int speed, int animLength, int frameTime) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.spriteAnim = new SpriteAnim(imageFile, animLength, frameTime);
		lifetime = -15.0;
		angle = 0;
		ID = (int)(Math.random()*10000);
	}
	
	//makes an Item based on an ItemType
	public Item(int x, int y, double direction, int lifespan, ItemType type, int ownerID) {
		this.x = x;
		this.y = y;
		this.width = type.width;
		this.height = type.height;
		angle = (int)direction;
		this.XSpeed = Math.cos(Math.toRadians(direction));
		this.YSpeed = Math.sin(Math.toRadians(direction));
		this.lifetime = lifespan;
		this.speed = type.speed;
		this.spriteAnim = new SpriteAnim(type.tex, type.animLength, type.frameTime);
		this.name = type.name;
		lifetime = -15.0;
		ID = (int)(Math.random()*10000);
		this.ownerID = ownerID;
	}
	
	public Item(String data) {
		String[] datapoints = data.split(" ");
		if (datapoints.length != 14) {
			System.out.println("Input error in Item(String data) :: " + data);
		}
		else {
			//datapoints[0] is "item"
			x = Integer.parseInt(datapoints[1]);
			y = Integer.parseInt(datapoints[2]);
			width = Integer.parseInt(datapoints[3]);
			height = Integer.parseInt(datapoints[4]);
			speed = Integer.parseInt(datapoints[5]);
			angle = (int)Double.parseDouble(datapoints[6]);
			XSpeed = Double.parseDouble(datapoints[7]);
			YSpeed = Double.parseDouble(datapoints[8]);
			lifetime = Double.parseDouble(datapoints[9]);
			stringAnim = datapoints[10];
			//spriteAnim = new SpriteAnim(datapoints[10]);
			passable = Boolean.parseBoolean(datapoints[11]);
			name = datapoints[12];
			ID = Integer.parseInt(datapoints[13]);
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
	public boolean update() {
		x += XSpeed*speed*Delta();
		y += YSpeed*speed*Delta();
		lifetime += Delta();
		if (lifetime <= 0) {
			return true;
		}
		return false;
	}
	
	//draws the item on the map
	//graphic operations are allowed
	//not thread safe
	public void Draw() {
		if (spriteAnim == null) {
			spriteAnim = new SpriteAnim(stringAnim);
		}
		DrawQuadTexRot(x, y, width, height, angle, spriteAnim.getFrame(Delta()));
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
	
	//returns a string representation of the object
	public String toString() {
		/*
		System.out.println("X = " + x);
		System.out.println("Y = " + y);
		System.out.println("width = " + width);
		System.out.println("height = " + height);
		System.out.println("Lifetime = " + lifetime);
		System.out.println("Angle = " + angle);
		System.out.println("Speed = " + speed);
		System.out.println("Xspd = " + XSpeed);
		System.out.println("Yspd = " + YSpeed);
		*/
		String reVal;
		if (spriteAnim == null) {
			reVal = "item " + x + " " + y + " " + width + " " + height + " " + speed + " " + Integer.toString(angle) + " " + Double.toString(XSpeed) + " " + Double.toString(YSpeed) + " " + lifetime + " " + stringAnim + " " + passable + " " + name + " " + Integer.toString(ID);
		}
		reVal = "item " + Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(width) + " " + Integer.toString(height) + " " + Integer.toString(speed) + " " + Integer.toString(angle) + " " + Double.toString(XSpeed) + " " + Double.toString(YSpeed) + " " + lifetime + " " + spriteAnim.toString() + " " + passable + " " + name + " " + Integer.toString(ID);
		//System.out.println(reVal);
		return reVal;
	}

}
