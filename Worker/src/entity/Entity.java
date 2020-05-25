package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import environment.Environment;
import interpreter.Interpreter;

public abstract class Entity {
	protected Point pos;
	protected int durability;
	protected int maxDurability = 1;
	protected Properties properties;
	protected boolean destroyed;
	
	protected boolean portability;
	protected float chanceOfGeneration;
	protected float chanceOfDuplication;
	
	private boolean isVisable;
	
	protected Random r;
	
	public Entity(Properties properties, Point newPos, Random r) {
		this.properties = properties;
		pos = newPos;
		destroyed = false;
		this.r = r;
		isVisable = true;
	}
	
	public boolean isVisable() {
		return isVisable;
	}
	
	public boolean checkIfVisable(Environment env) {
		isVisable = false;
		
		if (this instanceof Actor) {
			isVisable = true;
			return isVisable;
		}
		
		for (int x=this.getPos()[0]-1; x<=this.getPos()[0]+1; x++) {
			for (int y=this.getPos()[1]-1; y<=this.getPos()[1]+1; y++) {
				if (!isVisable && env.isValidPosition(x, y) && !(this.getPos()[0] == x 
						&& this.getPos()[1] == y) && (env.getEntity(x, y) == null || env.getEntity(x, y) instanceof Actor)) {
					
					isVisable = true;
					return isVisable;
				}
			}
		}
		
		
		return isVisable;
	}
	
	public float calcDist(Point otherPos) { return (float)pos.distance(otherPos); }
		
	public Point getPoint() { return pos; }
	
	public static float[] copyFromIntArray(int[] source) {
	    float[] dest = new float[source.length];
	    for(int i=0; i<source.length; i++) {
	        dest[i] = source[i];
	    }
	    return dest;
	}
	
	public int[] getPos() { return new int[] {pos.x, pos.y}; }

	public void setPos(int x, int y) {

		if (x >= 0 && y >= 0 && x < Integer.parseInt(properties.getProperty("WORLD_WIDTH")) 
				&& y < Integer.parseInt(properties.getProperty("WORLD_HEIGHT"))) {
			pos.setLocation(x, y);
		}
	}

	public int getDurability() { return durability; }
	
	public int getMaxDurability() { return maxDurability; }

	public void damageEntity(int damage) {
		if (durability <= damage) {
			destroyed = true;
			durability = 0;
		} else {
			durability -= damage;
		}
	}

	public boolean isDestroyed() {
		if (durability <= 0) {
			destroyed = true;
		}
		return destroyed;
	}
	
	public void addDurability(int inc) { durability += inc; };
	
	public abstract void onAction(Interpreter interpreter, Actor actor);
	public abstract void onStep(Interpreter interpreter, Environment env);
	public abstract Entity getNewChild(int x, int y);
	public abstract Entity getNewConvert();

	
}
