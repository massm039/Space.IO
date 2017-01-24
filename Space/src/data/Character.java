package data;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import helpers.Artist;

import static helpers.Artist.*;
import static helpers.Clock.*;

public class Character extends Collidable{
	protected int maxHealth, maxMovement, range;
	protected Sprite turretSprite, shipSprite;
	protected String turretString, shipString;
	protected Player player;
	protected Client client;
	protected int id;
	protected boolean visible, dead;
	
	protected int animTimer;
	protected int health;
	protected double accel, xSpeed, ySpeed;
	
	private double shipAngle;
	private double turretAngle;
	private double collisionCooldown;
	private double collisionTimer;
	
	//Default character constructor for a new ship
	//TODO: update this, it would be very nice to have a decent default.
	public Character() {
		x = 100;
		y = 100;
		width = 64;
		height = 64;
		maxHealth = 100;
		health = 100;
		xSpeed = 0;
		ySpeed = 0;
		accel = .3;
		maxMovement = 20;
		this.turretSprite = new Sprite("turret", 1);
		this.shipSprite = new Sprite("ship", 1);
		id = (int)(Math.random()*10000);
		collisionDamage = 50;
		visible = true;
		dead = false;
		collisionCooldown = 10;
		collisionTimer = 10;
	}
	
	//Character constructor for a new ship
	public Character(double x, double y, int width, int height, int maxHealth, double accel, int maxMovement, String shipSprite, String turretSprite, int animLength, Player player, Client client) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.maxHealth = maxHealth;
		this.health = 100;
		this.accel = accel;
		this.maxMovement = maxMovement;
		this.animTimer = animLength;
		this.shipSprite = new Sprite(shipSprite, animLength);
		this.turretSprite = new Sprite(turretSprite, animLength);
		xSpeed = 0;
		ySpeed = 0;
		id = (int)(Math.random()*10000);
		collisionDamage = 50;
		visible = true;
		dead = false;
		collisionCooldown = 10;
		collisionTimer = 10;
	}
	
	//takes a Character.toString() and constructs an identical Character in the new client.
	public Character(String data, Player player, Client client) {
		String[] datapoints = data.split(" ");
		if (datapoints.length != 19) {
			System.out.println( "Provided String is not of desired structure in Character(String data): ");
			System.out.println(data);
		}
		else {
			x = Double.parseDouble(datapoints[1]);
			y = Double.parseDouble(datapoints[2]);
			width = Integer.parseInt(datapoints[3]);
			height = Integer.parseInt(datapoints[4]);
			maxHealth = Integer.parseInt(datapoints[5]);
			maxMovement = Integer.parseInt(datapoints[6]);
			shipAngle = Double.parseDouble(datapoints[7]);
			turretAngle = Double.parseDouble(datapoints[8]);
			health = Integer.parseInt(datapoints[9]);
			shipString = datapoints[10];
			//System.out.println(datapoints[10] + " :: " + shipString);
			//shipSprite = new Sprite(datapoints[10]);
			turretString = datapoints[11];
			//turretSprite = new Sprite(datapoints[11]);
			animTimer = Integer.parseInt(datapoints[12]);
			id = Integer.parseInt(datapoints[13]);
			collisionDamage = Integer.parseInt(datapoints[14]);
			visible = Boolean.parseBoolean(datapoints[15]);
			dead = Boolean.parseBoolean(datapoints[16]);
			accel = Double.parseDouble(datapoints[17]);
			collisionTimer = Double.parseDouble(datapoints[18]);
			this.player = player;
			this.client = client;
		}
		collisionCooldown = 10;
	}

	//Called on every iteration of the player's program on the player's ship.
	//Updates the sprite and location of the character
	public void update() {
		
		updateTurretDirection();
		updateSpeed();
		
		move();
		
		collisionTimer += Delta();
		
		if (dead) {
			respawn();
		}
	}
	
	public void move() {
		double dt = Delta();
		x += xSpeed*dt;
		y += ySpeed*dt;
	}
	
	public double getMovementDirection() {
		//set the image angle to be looking at the cursor
		double moveDirection = Math.toDegrees(Math.atan((ySpeed/xSpeed*(-1))));
		if (xSpeed >= 0) {
			moveDirection += 180;
		}
		return moveDirection;
	}
	
	//Directs the character in a direction based on the mouse's position relative to the player
	private void updateTurretDirection() {
		double xDist = Mouse.getX() - ((int)x+(width/2));
		double yDist = Mouse.getY() - (HEIGHT - ((int)y+(height/2)));
		
		//set the image angle to be looking at the cursor
		turretAngle = (90) + Math.toDegrees(Math.atan((yDist/xDist*(-1))));
		if (xDist >= 0) {
			turretAngle += 180;
		}
	}
	
	//set ship to face away from its velocity when it is breaking
	private void updateShipDirection(double xAccel, double yAccel) {
		//System.out.println( Math.toDegrees(Math.atan((yAccel/xAccel))));
		shipAngle = (90) + Math.toDegrees(Math.atan((yAccel/xAccel)));
		if (xAccel >= 0) {
			shipAngle += 180;
		}
	}
	
	private void updateSpeed() {
		
		double tempX = xSpeed;
		double tempY = ySpeed;
		double dt = Delta();
		double xPercent = Math.abs(xSpeed)/(Math.abs(xSpeed)+Math.abs(ySpeed));
		double yPercent = Math.abs(ySpeed)/(Math.abs(xSpeed)+Math.abs(ySpeed));
		double partialAccel = Math.pow(accel*accel/2, .5);
		double partialAccelX = Math.pow(accel*accel/xPercent, .5);
		double partialAccelY = Math.pow(accel*accel/yPercent, .5);

		
		//SPACEBAR to stop
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (xSpeed > 0) {
				xSpeed -= partialAccelX*dt;
				if (ySpeed > 0) {
					ySpeed -= partialAccelY*dt;
					if (ySpeed < 0) {
						ySpeed = 0;
					}
				}
				else if (ySpeed < 0) {
					ySpeed += partialAccelY*dt;
					if (ySpeed > 0) {
						ySpeed = 0;
					}
				}
				else {
					xSpeed -= (accel-partialAccelX)*dt;
					if (xSpeed < 0) {
						xSpeed = 0;
					}
				}
			}
			else if (xSpeed < 0) {
				xSpeed += partialAccelX*dt;
				if (xSpeed > 0) {
					xSpeed = 0;
				}
				if (ySpeed > 0) {
					ySpeed -= partialAccelY*dt;
					if (ySpeed < 0) {
						ySpeed = 0;
					}
				}
				else if (ySpeed < 0) {
					ySpeed += partialAccelY*dt;
					if (ySpeed > 0) {
						ySpeed = 0;
					}
				}
				else {
					xSpeed += (accel-partialAccelX)*dt;
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
					shipAngle = 0;
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
	//Graphic operations are allowed
	//Thread Safe
	public void Draw() {
		makeThreadSafe();
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
	public int getID() {
		return id;
	}
	
	//Set Character name
	public void setID(int id) {
		this.id = id;
	}
	
	//Get Acceleration
	public double getAccel() {
		return accel;
	}
	
	//Get current speed
	public double getSpeed() {
		if (xSpeed*xSpeed + ySpeed*ySpeed == 0) {
			return 0;
		}
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
	
	public void handleCollision(int damage, boolean resetTimer) {
		if (collisionTimer >= collisionCooldown) {
			setHealth(health - damage);
			if (resetTimer) {
				collisionTimer = 0;
			}
			//System.out.println("damage delt: " +  damage);
			//System.out.println("health left: " + health);
			if (health == 0) {
				die();
			}
		}
	}
	
	//returns a string representation of the character
	public String toString() {
		String reVal;
		if (shipString == null || turretString == null) {
			reVal = "ship " + Double.toString(x) + " " + Double.toString(y) + " " + Integer.toString(width) + " " + Integer.toString(height);
			reVal += " " + Integer.toString(maxHealth) + " " + maxMovement + " " + Double.toString(shipAngle) + " " + Double.toString(turretAngle);
			reVal += " " + Integer.toString(health) + " " +  shipSprite.toString() + " " + turretSprite.toString() + " " + Integer.toString(animTimer) + " ";
			reVal += this.getID() + " " + collisionDamage + " " + visible + " " + dead + " " + accel + " " + collisionTimer;
		}
		else {
			reVal = "ship " + Double.toString(x) + " " + Double.toString(y) + " " + Integer.toString(width) + " " + Integer.toString(height);
			reVal += " " + Integer.toString(maxHealth) + " " + maxMovement + " " + Double.toString(shipAngle) + " " + Double.toString(turretAngle);
			reVal += " " + Integer.toString(health) + " " +  shipString + " " + turretString + " " + Integer.toString(animTimer) + " ";
			reVal += this.getID() + " " + collisionDamage + " " + visible + " " + dead + " " + accel + " " + collisionTimer;
		}
		return reVal;
	}
	
	//draws the character's healthbar above the character
	//graphic operations are allowed
	public void drawHealthBar() {
		int baseX = (int)(x+2);
		int baseY = (int)(y-8);
		DrawQuadTex(baseX, baseY, width-4, 4, LoadPNG("darkBar"));
		DrawQuadTex(baseX, baseY, ((float)health/(float)maxHealth)*(width-4), 4, LoadPNG("healthBar"));
	}
	
	//removes the ship from the client's list of ships to update, as well as giving player a new ship.
	//Garbage collects the current object **
	public void die() {
		dead = true;
	}
	
	//Call when the current ship dies, respawning as a new character
	public void respawn() {
		//System.out.println("Respawning");
		health = maxHealth;
		xSpeed = 0;
		ySpeed = 0;
		x = Math.random()*Artist.WIDTH;
		y = Math.random()*Artist.HEIGHT;
		dead = false;
	}
	
	//changes the texture of the sprite based on the current frame
	//graphic operations are allowed
	//thread safe
	protected void updateSprite() {
		makeThreadSafe();
		DrawQuadTexRot((int)x, (int)y, width, height, (int)shipAngle, shipSprite.updateTex());
		DrawQuadTexRot((int)x+width/4, (int)y+height/4, width/2, height/2, (int)turretAngle, turretSprite.updateTex());
	}
	
	//checks if sprites have been initialized (required before using any sprite functions)
	private void makeThreadSafe() {
		if (shipSprite == null) {
			shipSprite = new Sprite(shipString);
		}
		if (turretSprite == null) {
			turretSprite = new Sprite(turretString);
		}
	}
		
}
