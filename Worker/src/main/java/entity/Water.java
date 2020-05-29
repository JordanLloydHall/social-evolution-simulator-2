package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

public class Water extends Entity {
	
	/**
	 * Class that deals with the water entity. Indestructible, does not reproduce, Acts only as a barrier that the actors can percieve.
	 * @author Jordan
	 * @since 1.0
	 */
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
