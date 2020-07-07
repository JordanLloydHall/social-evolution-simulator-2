package main.java.entity;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

public abstract class Entity {
	protected Point pos;
	protected int durability;
	protected int maxDurability = 1;
	protected Properties properties;
	public boolean destroyed;
	
	protected boolean portability;
	protected float chanceOfGeneration;
	protected float chanceOfDuplication;
	
	private boolean isVisible;
	
	protected Random r;
	
	/**
	 * Abstract class that provides a basis for how all entities interact with the environment.
	 * 
	 * @param properties the properties object shared by all entity classes.
	 * @param newPos Point object describing the entity's location on the grid.
	 * @param r Random object shared by all entity classes.
	 * @since 1.0
	 */
	public Entity(Properties properties, Point newPos, Random r) {
		this.properties = properties;
		pos = newPos;
		destroyed = false;
		this.r = r;
		isVisible = true;
	}
	
	/**
	 * Getter for the isVisible variable.
	 * @return Whether or not the entity is visible.
	 */
	public boolean getIsVisible() {
		return isVisible;
	}
	
	/**
	 * Checks the surrounding 8 positions. If one of them is blank or an actor, then the entity is visible.
	 * This allows the environment's KD tree, which must be built at each time-step, to discriminate between
	 * what can be seen by the actors and what can't. It also allows the entity onStep() call to be skipped to
	 * increase speeds.
	 * @param env
	 * @return Whether or not the entity is visible after the check.
	 */
	public boolean checkIfVisable(Environment env) {
		isVisible = false;
		
		if (this instanceof Actor) {
			isVisible = true;
			return isVisible;
		}
		
		for (int x=this.getPos()[0]-1; x<=this.getPos()[0]+1; x++) {
			for (int y=this.getPos()[1]-1; y<=this.getPos()[1]+1; y++) {
				if (!isVisible && env.isValidPosition(x, y) && !(this.getPos()[0] == x 
						&& this.getPos()[1] == y) && (env.getEntity(x, y) == null || env.getEntity(x, y) instanceof Actor)) {
					
					isVisible = true;
					return isVisible;
				}
			}
		}
		
		
		return isVisible;
	}
	
	/**
	 * Calculates euclidean distance between this entity and another.
	 * @param otherPos The Point object of the entity in order to check the distance between the two.
	 * @return Calculated euclidean distance.
	 */
	public float calcDist(Point otherPos) { return (float)pos.distance(otherPos); }
	
	/**
	 * Getter for the Point object attached to this class.
	 * @return Point object attached to this class.
	 */
	public Point getPoint() { return pos; }
	
	/**
	 * Converts an int array to a float array with the same values.
	 * @param source The int array to be converted.
	 * @return The corresponding float array.
	 */
	public static float[] copyFromIntArray(int[] source) {
	    float[] dest = new float[source.length];
	    for(int i=0; i<source.length; i++) {
	        dest[i] = source[i];
	    }
	    return dest;
	}
	
	/**
	 * Similar to getPoint(), but returns the corresponding int array instead of a Point object.
	 * @return An int[2] array with the corresponding x and y pos of the entity.
	 */
	public int[] getPos() { return new int[] {pos.x, pos.y}; }

	/**
	 * Simple setter for the entity's position.
	 * @param x
	 * @param y
	 */
	public void setPos(int x, int y) {
		/*
		 * Ensure's the new position is a legal position in the environment.
		 */
		if (x >= 0 && y >= 0 && x < Integer.parseInt(properties.getProperty("WORLD_WIDTH")) 
				&& y < Integer.parseInt(properties.getProperty("WORLD_HEIGHT"))) {
			pos.setLocation(x, y);
		}
	}

	/**
	 * Simple getter for the durability of the entity.
	 * @return The entity's durability.
	 */
	public int getDurability() { return durability; }
	
	/**
	 * Returns the max durability of the entity. Useful to scale the neural network inputs between 0 and 1
	 * (higher for actors, as they can surpass the durability limit).
	 * @return the max durability of the entity.
	 */
	public int getMaxDurability() { return maxDurability; }

	/**
	 * Decreases the durability of the entity by the input damage. 
	 * If the durability of the entity becomes 0 or less, then the entity is flagged as destroyed.
	 * @param damage The amount to decrease the durability by.
	 */
	public void damageEntity(int damage) {
		if (durability <= damage) {
			destroyed = true;
			durability = 0;
		} else {
			durability -= damage;
		}
	}
	
	/**
	 * Checks if the entity is destroyed, setting the flag if so, and then returning the flag.
	 * @return Whether or not the entity is destroyed.
	 */
	public boolean isDestroyed() {
		if (durability <= 0) {
			destroyed = true;
		}
		return destroyed;
	}
	
	/**
	 * Adds to the durability of the entity. Useful for actors, that consume Consumables and gain durability.
	 * @param inc the amount to increment the durability by.
	 */
	public void addDurability(int inc) { durability += inc; };
	
	/**
	 * Abstract function describing what happens when an action is performed on an entity by an actor.
	 * To be implemented by inheriting classes.
	 * @param interpreter Allows the entity to change the state of the environment as a result to being acted upon.
	 * @param actor The actor that has acted upon the entity. Useful if the entity changes the state of the actor as a result of being acted upon.
	 */
	public abstract void onAction(Interpreter interpreter, Actor actor);
	
	/**
	 * Abstract function describing what happens when an the environment is stepped.
	 * This function is not called on entities that are not visible.
	 * To be implemented by inheriting classes.
	 * @param interpreter Allows the entity to change the state of the environment during a step.
	 * @param env Used for cases where the entity's behaviour changes depending on environmental factors.
	 */
	public abstract void onStep(Interpreter interpreter, Environment env);
	
	/**
	 * Abstract function that returns the new child of the entity during reproduction. 
	 * This extends to all types of entities that can reproduce, like wheat, trees and actors.
	 * To be implemented by inheriting classes.
	 * @param x The x position of the new entity.
	 * @param y The y position of the new entity.
	 * @return The child entity.
	 */
	public abstract Entity getNewChild(int x, int y);
	
	/**
	 * Abstract function that returns what replaces the entity once it has been destroyed.
	 * To be implemented by inheriting classes.
	 * @return The entity that replaces this one.
	 */
	public abstract Entity getNewConvert();
}
