package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import environment.Environment;
import interpreter.Interpreter;

public class Water extends Entity {
	
	public Water(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		return;
	}

	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		return;
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
