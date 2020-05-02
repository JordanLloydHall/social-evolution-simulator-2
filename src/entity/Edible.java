package entity;

import java.awt.Point;
import java.util.Properties;

import interpreter.Interpreter;

public abstract class Edible extends Resource {

	protected int nourishment;
	
	
	public Edible(Properties properties, Point newPos) {
		super(properties, newPos);
		durability = 0;
		chanceOfDuplication = 0;
	}
	
	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		actor.addDurability(nourishment);
		interpreter.addToConvertQueue(getPos());
	}
	
	@Override
	public void onStep(Interpreter interpreter) {}

	@Override
	public Entity getNewChild(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getNewConvert() {
		// TODO Auto-generated method stub
		return null;
	}

}
