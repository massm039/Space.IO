package data;

import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import static helpers.Artist.*;
import static helpers.Clock.*;

public class Character {
	protected double x, y;
	protected int width, height, maxHealth, movement, damage = 10, range;
	protected Sprite sprite;
	protected Player player;
	protected Server server;
	protected String name = "Character";
	protected boolean visible = true;
	
	protected String animation = "";
	protected int animTimer;
	protected boolean engineOn = false;
	protected int health;
	protected double angle = 0;
	protected double percentXSpeed = 1;
	protected double percentYSpeed = 0;
	
	//Default character constructor for a new ship
	public Character() {
		x = 100;
		y = 100;
		width = 32;
		height = 32;
		maxHealth = 100;
		health = maxHealth;
		movement = 10;
		this.sprite = new Sprite("ship", 1);
	}
	
	//Constructor for a character (ship) with given details
	public Character(int x, int y, int width, int height, int maxHealth, int movement, String sprite, int animLength, Server server) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.movement = movement;
		this.sprite = new Sprite(sprite, animLength);
	}

	//Called on every iteration of the player's program on the player's ship.
	//Updates the sprite and location of the character
	public void update() {
		updateDirection();
		if (engineOn) {
			x += percentXSpeed*movement*Delta();
			y += percentYSpeed*movement*Delta();
		}
	}
	
	//Toggles the ship's movement in the current direction
	public void toggleEngine() {
		if (engineOn) {
			System.out.println("Engine Toggled: Off");
			engineOn = false;
		}
		else {
			System.out.println("Engine Toggled: On");
			engineOn = true;
		}
	}
	
	//Directs the character in a direction based on the mouse's position relative to the player
	private void updateDirection() {
		double xDist = Mouse.getX() - ((int)x+(width/2));
		double yDist = Mouse.getY() - (HEIGHT - ((int)y+(height/2)));
		
		//set the image angle to be looking at the cursor
		angle = (-90) + Math.toDegrees(Math.atan((yDist/xDist*(-1))));
		if (xDist < 0) {
			angle += 180;
		}
		
		percentXSpeed = Math.cos(Math.toRadians(angle+90));
		percentYSpeed = Math.sin(Math.toRadians(angle+90));
		
		if (percentXSpeed*percentXSpeed*movement*movement+percentYSpeed*percentYSpeed*movement*movement > xDist*xDist+yDist*yDist) {
			percentXSpeed = 0;
			percentYSpeed = 0;
		}
	}
	
	public int getDirection() {
		return (int)angle+90;
	}
	
	//Draws the sprite to the screen based on its current frame
	public void Draw() {
		if (visible) {
			updateSprite();
		}
	}
	
	//returns whether the character is stealthed
	public boolean isVisible() {
		return visible;
	}
	
	//sets whether the character is stealthed
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	//get X position
	public double getX() {
		return x+(width/2);
	}
	
	//get Y position
	public double getY() {
		return y+(height/2);
	}
	
	//Get Character Name
	public String getName() {
		return name;
	}
	
	//Set Character name
	public void setName(String name) {
		this.name = name;
	}
	
	//Get movement
	public int getMovSpeed() {
		return movement;
	}
	
	//Get health
	public int getHealth() {
		return health;
	}
	
	//Set health
	public void setHealth(int newHealth) {
		health = newHealth;
	}
	
	//returns a string representation of the character
	public String toString() {
		return Integer.toString((int)x) + " " + Integer.toString((int)y) + " " + getName();
	}
	
	//draws the character's healthbar above the character
	public void drawHealthBar() {
		int baseX = (int)(x+2);
		int baseY = (int)(y-8);
		DrawQuadTex(baseX, baseY, width-4, 4, LoadPNG("darkBar"));
		DrawQuadTex(baseX, baseY, ((float)health/(float)maxHealth)*(width-4), 4, LoadPNG("healthBar"));
	}
	
	//removes the ship from the server's list of ships to update, as well as giving player a new ship.
	//Garbage collects the current object **
	public void die() {
		ArrayList<Character> chars = server.getCharacters();
		for (Character i : chars) {
			if (i.equals(this)) {
				chars.remove(i);
			}
		}
		player.respawn();
	}
	
	//returns the x portion of the movement speed;
	public double getXSpeed() {
		return percentXSpeed;
	}
	
	//returns the y portion of the movement speed;
	public double getYSpeed() {
		return percentYSpeed;
	}
	
	//changes the texture of the sprite based on the current frame
	protected void updateSprite() {
		DrawQuadTexRot((int)x, (int)y, width, height, (int)angle, sprite.updateTex());
	}
		
}
