package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import environment.Environment;
import interpreter.Interpreter;

public abstract class Resource extends Entity {
	
	public Resource(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		damageEntity(actor.getToolLevel());
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
		
	}
	
	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		if (r.nextFloat() < chanceOfDuplication) {
			for (int x=this.getPos()[0]-1; x<=this.getPos()[0]+1; x++) {
				for (int y=this.getPos()[1]-1; y<=this.getPos()[1]+1; y++) {
					if (env.isValidPosition(x, y) && !(this.getPos()[0] == x && this.getPos()[1] == y) && env.getEntity(x, y) == null) {
						interpreter.addToSpawnQueue(getPos(), new int[] {x,y});
						return;
					}
				}
			}
		}
	}
}
