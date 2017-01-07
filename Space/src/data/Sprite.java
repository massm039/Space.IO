package data;

import static helpers.Clock.*;
import static helpers.Artist.*;

import org.newdawn.slick.opengl.Texture;

public class Sprite {
	
	private int animTimer = 0;
	private String anim = "";
	private SpriteAnim animation;
	private Texture idle;
	private Texture preFrame = idle;
	
	//constructor for a player's sprite
	public Sprite(String spriteName, int animLength) {
		this.idle = LoadPNG(spriteName);
		this.animation = new SpriteAnim(spriteName, animLength);
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
			anim = "";
		}
		float delta = Delta();
		switch(anim) {
		case "":
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
