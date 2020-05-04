package entity;

import java.awt.Point;
import java.util.Properties;

import environment.Environment;
import interpreter.Interpreter;

public abstract class Resource extends Entity {
	
	public Resource(Properties properties, Point newPos) {
		super(properties, newPos);
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		//damageEntity(actor.);
		// TODO add damage based on actor tool level.
				
		damageEntity(actor.getToolLevel());
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
		
	}
	
	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		if (Math.random() < chanceOfDuplication) {
			interpreter.addToSpawnQueue(new int[] {pos.x, pos.y});
		}
		
		if (Math.random() < 0.005) {
			interpreter.addToConvertQueue(new int[] {pos.x, pos.y});
		}
	}
}
