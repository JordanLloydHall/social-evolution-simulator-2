package entity;

import java.awt.Point;
import java.util.Properties;

import interpreter.Interpreter;

public abstract class Entity {
	protected Point pos;
	protected int durability;
	protected Properties properties;
	protected boolean destroyed;
	
	protected boolean portability;
	protected double chanceOfGeneration;
	protected double chanceOfDuplication;
	
	public Entity(Properties properties, Point newPos) {
		this.properties = properties;
		pos = newPos;
		destroyed = false;
	}
	
	public float calcDist(Point otherPos) { return (float)pos.distance(otherPos); }
		
	public Point getPoint() { return pos; }
	
	public int[] getPos() { return new int[] {pos.x, pos.y}; }

	public void setPos(int x, int y) {

		if (x >= 0 && y >= 0 && x < Integer.parseInt(properties.getProperty("WORLD_WIDTH")) 
				&& y < Integer.parseInt(properties.getProperty("WORLD_HEIGHT"))) {
			pos.setLocation(x, y);
		}
	}

	public int getDurability() { return durability; }

	public void damageEntity(int damage) {
		if (durability <= damage) {
			destroyed = true;
			durability = 0;
		} else {
			durability -= damage;
		}
	}

	public boolean isDestroyed() { return destroyed; }
	
	public void addDurability(int inc) { durability += inc; };
	
	public abstract void onAction(Interpreter interpreter, Actor actor);
	public abstract void onStep(Interpreter interpreter);
	public abstract Entity getNewChild(int x, int y);
	public abstract Entity getNewConvert();

	
}
