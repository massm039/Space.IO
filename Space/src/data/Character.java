package data;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static helpers.Artist.*;
import static helpers.Clock.*;

public class Character {
	protected double x, y;
	protected int width, height, maxHealth, maxMovement, damage = 10, range;
	protected Sprite turretSprite;
	protected Sprite shipSprite;
	protected Player player;
	protected ServerSender server;
	protected String name = "Character";
	protected boolean visible = true;
	
	protected String animation = "";
	protected int animTimer;
	protected int health;
	protected double accel, xSpeed, ySpeed;
	
	private double shipAngle;
	private double turretAngle;
	
	//Default character constructor for a new ship
	public Character() {
		x = 100;
		y = 100;
		width = 64;
		height = 64;
		maxHealth = 100;
		health = maxHealth;
		xSpeed = 0;
		ySpeed = 0;
		accel = .3;
		maxMovement = 20;
		this.turretSprite = new Sprite("turret", 1);
		this.shipSprite = new Sprite("ship", 1);
	}
	
	//Constructor for a character (ship) with given details
	public Character(double x, double y, int width, int height, int maxHealth, double accel, int maxMovement, String shipSprite, String turretSprite, int animLength, ServerSender server) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.accel = accel;
		this.maxMovement = maxMovement;
		this.shipSprite = new Sprite(shipSprite, animLength);
		this.turretSprite = new Sprite(turretSprite, animLength);
		xSpeed = 0;
		ySpeed = 0;
	}

	//Called on every iteration of the player's program on the player's ship.
	//Updates the sprite and location of the character
	public void update() {
		updateTurretDirection();
		updateSpeed();
		
		double dt = Delta();
		
		x += xSpeed*dt;
		y += ySpeed*dt;
	}
	
	//Directs the character in a direction based on the mouse's position relative to the player
	private void updateTurretDirection() {
		double xDist = Mouse.getX() - ((int)x+(width/2));
		double yDist = Mouse.getY() - (HEIGHT - ((int)y+(height/2)));
		
		//set the image angle to be looking at the cursor
		turretAngle = (-90) + Math.toDegrees(Math.atan((yDist/xDist*(-1))));
		if (xDist < 0) {
			turretAngle += 180;
		}
	}
	
	private void updateShipDirection(double xAccel, double yAccel) {
		shipAngle = Math.cos(xAccel/(Math.pow(xAccel*xAccel+yAccel*yAccel, .5)));
	}
	
	private void updateSpeed() {
		
		double tempX = xSpeed;
		double tempY = ySpeed;
		double dt = Delta();
		
		double partialAccel = (accel*accel)/2;
		partialAccel = (double)Math.pow((accel*accel)/2, .5);

		
		//SPACEBAR to stop
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (xSpeed > 0) {
				xSpeed -= partialAccel*dt;
				if (ySpeed > 0) {
					ySpeed -= partialAccel*dt;
					if (ySpeed < 0) {
						ySpeed = 0;
					}
				}
				else if (ySpeed < 0) {
					ySpeed += partialAccel*dt;
					if (ySpeed > 0) {
						ySpeed = 0;
					}
				}
				else {
					xSpeed -= partialAccel*dt;
					if (xSpeed < 0) {
						xSpeed = 0;
					}
				}
			}
			else if (xSpeed < 0) {
				xSpeed += partialAccel*dt;
				if (xSpeed > 0) {
					xSpeed = 0;
				}
				if (ySpeed > 0) {
					ySpeed -= partialAccel*dt;
					if (ySpeed < 0) {
						ySpeed = 0;
					}
				}
				else if (ySpeed < 0) {
					ySpeed += partialAccel*dt;
					if (ySpeed > 0) {
						ySpeed = 0;
					}
				}
				else {
					xSpeed += partialAccel*dt;
					if (xSpeed > 0) {
						xSpeed = 0;
					}
				}
			}
			else {
				if (ySpeed > 0) {
					ySpeed -= accel*dt;
					if (ySpeed < 0) {
						ySpeed = 0;
					}
				}
				else if (ySpeed < 0){
					ySpeed += accel*dt;
					if (ySpeed > 0) {
						ySpeed = 0;
					}
				}
			}
		}	
		else {
			if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_S)) {
				//if moving up and right
				if (Keyboard.isKeyDown(Keyboard.KEY_A) && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tempX -= partialAccel*dt;
					tempY -= partialAccel*dt;
					shipAngle = 135;
				}
				//if moving up and left
				else if (Keyboard.isKeyDown(Keyboard.KEY_D) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
					tempX += partialAccel*dt;
					tempY -= partialAccel*dt;
					shipAngle = 225;
				}
				//if moving up
				else {
					tempY -= accel*dt;
					shipAngle = 180;
				}
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_S) && !Keyboard.isKeyDown(Keyboard.KEY_W)) {
				//if moving down and right
				if (Keyboard.isKeyDown(Keyboard.KEY_A) && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tempX -= partialAccel*dt;
					tempY += partialAccel*dt;
					shipAngle = 45;
				}
				//if moving down and left
				else if (Keyboard.isKeyDown(Keyboard.KEY_D) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
					tempX += partialAccel*dt;
					tempY += partialAccel*dt;
					shipAngle = -45;
				}
				//if moving down
				else {
					tempY += accel*dt;
				}
			}
			//if moving left
			else if (Keyboard.isKeyDown(Keyboard.KEY_A) && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
				tempX -= accel*dt;
				shipAngle = 90;
			}
			//if moving right 
			else if (Keyboard.isKeyDown(Keyboard.KEY_D) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
				tempX += accel*dt;
				shipAngle = -90;
			}
			
			if (tempX*tempX+tempY*tempY > maxMovement*maxMovement) {
				double total = Math.abs(tempX)+Math.abs(tempY);
				xSpeed = (maxMovement/total)*tempX;
				ySpeed = (maxMovement/total)*tempY;
			}
			else {
				xSpeed = tempX;
				ySpeed = tempY;
			}
		}
		if (xSpeed*xSpeed+ySpeed*ySpeed > maxMovement*maxMovement) {
			double total = Math.abs(xSpeed) + Math.abs(ySpeed);
			xSpeed = maxMovement/total*xSpeed;
			ySpeed = maxMovement/total*ySpeed;
		}
	}
	
	public int getTurretDirection() {
		return (int)turretAngle+90;
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
	
	//Get Acceleration
	public double getAccel() {
		return accel;
	}
	
	//Get current speed
	public double getSpeed() {
		return Math.pow(xSpeed*xSpeed + ySpeed*ySpeed, 1/2);
	}
	
	//Get movement
	public int getMovSpeed() {
		return maxMovement;
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
	
	//changes the texture of the sprite based on the current frame
	protected void updateSprite() {
		DrawQuadTexRot((int)x, (int)y, width, height, (int)shipAngle, shipSprite.updateTex());
		DrawQuadTexRot((int)x+width/4, (int)y+height/4, width/2, height/2, (int)turretAngle, turretSprite.updateTex());
	}
		
}
