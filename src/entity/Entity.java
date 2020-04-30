package entity;

import java.awt.Point;
import java.util.Properties;

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
	
	public double[] getPos() { return new double[] {pos.x, pos.y}; }

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
	
}
