package entity;

import java.awt.Point;
import java.util.Properties;

import interpreter.Interpreter;

public abstract class Resource extends Entity {
	
	public Resource(Properties properties, Point newPos) {
		super(properties, newPos);
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		//damageEntity(actor.);
		// TODO add damage based on actor tool level.
				
		damageEntity(1);
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
		
	}
	
	@Override
	public void onStep(Interpreter interpreter) {
		if (Math.random() < chanceOfDuplication) {
			interpreter.addToSpawnQueue(new int[] {pos.x, pos.y});
		}
	}
}
