package data;

import static helpers.Clock.*;
import static helpers.Artist.*;

import org.newdawn.slick.opengl.Texture;

public class Sprite {
	
	private int animTimer = 0;
	private String anim = "none";
	private SpriteAnim animation;
	private Texture idle;
	private Texture preFrame = idle;
	
	//constructor for a player's sprite
	public Sprite(String spriteName, int animLength) {
		this.idle = LoadPNG(spriteName);
		this.animation = new SpriteAnim(spriteName, animLength);
	}
	
	public Sprite(String data) {
		String[] datapoints = data.split("-");
		if (datapoints.length != 3) {
			for (String i : datapoints)
				System.out.println(i);
			System.out.println("Error with String format in Sprite(String data)");
		}
		else {
			animTimer = Integer.parseInt(datapoints[0]);
			anim = datapoints[1];
			animation = new SpriteAnim(datapoints[2]);
		}
	}
	
	public String toString() {
		return animTimer + "-" + anim + "-" + animation.toString();
	}
	
	//sets the current animation (attacking, moving, idle)
	public void setAnimation(String animation, int animTimer) {
		anim = animation;
		this.animTimer = animTimer;
	}
	
	//sets the current animation without a animTimer
	public void setAnimation(String animation) {
		anim = animation;
		animTimer = 0;
	}
	
	//returns the name of the current animation that is rendering
	public String getAnimation() {
		return anim;
	}
	
	//updates the sprite's texture that gets returned to the player.
	public Texture updateTex() {
		Texture reTex;
		if (animTimer == 0) {
			anim = "none";
		}
		float delta = Delta();
		switch(anim) {
		case "none":
			reTex = animation.getFrame(delta);
			break;
		case "attack":
			reTex = animation.getFrame(delta); //TODO: implement attack animations
			break;
		default:
			return idle;
		}
		if (reTex != preFrame) {
			animTimer--;
			if (animTimer < 0) {
				animTimer = 0;
			}
		}
		return reTex;
	}

}
