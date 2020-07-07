package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

public abstract class Resource extends Entity {
	/**
	 * Abstract class that describes the general behaviour of resources within the environment.
	 * @author Jordan
	 * @since 1.0
	 */
	
	public boolean pickedUp;
	
	public Resource(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		
		pickedUp = false;
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		/*
		 * Damages the resource by the tool level of the actor that acted upon the resource.
		 * If the resource is destroyed by the action, add it to the convert queue.
		 */
		damageEntity(actor.getToolLevel());
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
		
	}
	
	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		/*
		 * Randomly checks for if the resource should reproduce.
		 * If there exists an empty space in the surrounding 8 spaces, add to the spawn queue of the interpreter.
		 */
//		if (r.nextFloat() < chanceOfDuplication) {
//			for (int x=this.getPos()[0]-1; x<=this.getPos()[0]+1; x++) {
//				for (int y=this.getPos()[1]-1; y<=this.getPos()[1]+1; y++) {
//					if (env.isValidPosition(x, y) && !(this.getPos()[0] == x && this.getPos()[1] == y) && env.getEntity(x, y) == null) {
//						interpreter.addToSpawnQueue(getPos(), new int[] {x,y});
//						return;
//					}
//				}
//			}
//		}
	}
}
