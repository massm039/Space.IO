package data;

public class Collidable {
	
	protected double x, y;
	int width, height, collisionDamage;
	
	//checks if two collidable objects are overlapping
	public boolean collision(Collidable other) {
		//checks if x1y1 is inside the bounds
		if ((other.getX1() <= getX1() && getX1() <= other.getX2()) && (other.getY1() <= getY1() && getY1() <= other.getY2())) {
			//System.out.println(other.getX1() + " >= " + getX1() + " and " + other.getX1() + " <= " + getX2());
			return true;
		}
		//checks if x1y2 is inside the bounds
		if ((other.getX1() <= getX1() && getX1() <= other.getX2()) && (other.getY1() <= getY2() && getY2() <= other.getY2())) {
			return true;
		}
		//checks if x2y2 is inside the bounds
		if ((other.getX1() <= getX2() && getX2() <= other.getX2()) && (other.getY1() <= getY2() && getY2() <= other.getY2())) {
			return true;
		}
		//checks if x2y1 is inside the bounds
		if ((other.getX1() <= getX2() && getX2() <= other.getX2()) && (other.getY1() <= getY1() && getY1() <= other.getY2())) {
			return true;
		}
		return false;
	}
	
	public int getCollisionDamage() {
		return collisionDamage;
	}
	
	//Collisions getters and setters
	public double getX1() {
		return x;
	}
	
	//Collisions getters and setters
	public double getY1() {
		return y;
	}
	
	//Collisions getters and setters
	public double getX2() {
		return x + width;
	}
	
	//Collisions getters and setters
	public double getY2() {
		return y + height;
	}

}
