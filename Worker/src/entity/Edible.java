package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import environment.Environment;
import interpreter.Interpreter;

public abstract class Edible extends Resource {

	protected int nourishment;
	
	
	public Edible(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		durability = 1;
		chanceOfDuplication = 0;
	}
	
	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		damageEntity(actor.getToolLevel());
		actor.addDurability(nourishment);
		interpreter.addToConvertQueue(getPos());
	}
	

	@Override
	public Entity getNewChild(int x, int y) {
		return null;
	}

	@Override
	public Entity getNewConvert() {
		return null;
	}

}
