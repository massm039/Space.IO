package data;

import org.lwjgl.input.Mouse;
import static helpers.Clock.*;
import static helpers.Artist.*;

public class Item {
	
	protected int x, y, width, height, speed, angle=0;
	protected double XSpeed, YSpeed, lifetime=-100;
	protected SpriteAnim spriteAnim;
	protected String name;
	protected boolean passable = false, visible;
	
	//makes an Item without an animated sprite (the texture has a single frame)
	public Item(int x, int y, int width, int height, String imageFile) {
		this.x = x-(width/2);
		this.y = y-(height/2);
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, 1);
	}
	
	//makes an Item with a specified animation length
	public Item(int x, int y, int width, int height, String imageFile, int animLength) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spriteAnim = new SpriteAnim(imageFile, animLength);
	}
	
	//specifies the time per frame on the animated Item
	public Item(int x, int y, int width, int height, String imageFile, int speed, int animLength, int frameTime) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.spriteAnim = new SpriteAnim(imageFile, animLength, frameTime);
	}
	
	//makes an Item based on an ItemType
	public Item(int x, int y, double direction, int lifespan, ItemType type) {
		this.x = x;
		this.y = y;
		this.width = type.width;
		this.height = type.height;
		this.angle = (int)direction;
		this.XSpeed = Math.cos(Math.toRadians(direction));
		this.YSpeed = Math.sin(Math.toRadians(direction));
		this.lifetime = lifespan;
		this.speed = type.speed;
		this.spriteAnim = new SpriteAnim(type.tex, type.animLength, type.frameTime);
		this.name = type.name;
	}
	
	//returns the item's name
	public String getName() {
		return name;
	}
	
	//move items in their given direction, destroy on the end of lifespan, if given a lifespan;
	public boolean update() {
		x += XSpeed*speed*Delta();
		y += YSpeed*speed*Delta();
		//
		if (lifetime == -100) {
			return true;
		}
		lifetime -= Delta();
		if (lifetime <= 0) {
			return false;
		}
		return true;
	}
	
	//draws the item on the map
	public void Draw() {
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
		return "Item " + Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(width) + " " + Integer.toString(height) + " " + spriteAnim.toString() + " " + name;
	}

}
