package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

public abstract class Edible extends Resource {

	protected int nourishment;
	
	/**
	 * Describes the behaviour of resources that the actors can consume in some way within the environment.
	 * @author Jordan
	 * @since 1.0
	 */
	public Edible(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		/*
		 * Edibles should not require anymore work to consume.
		 */
		durability = 1;
	}
	
	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		damageEntity(actor.getToolLevel());
		actor.addDurability(nourishment);
		interpreter.addToConvertQueue(getPos());
	}
	
	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		/**
		 * Overridden to decrease compute cost during environment step.
		 */
	}

	@Override
	public Entity getNewChild(int x, int y) {
		/*
		 * Edibles do not reproduce.
		 */
		return null;
	}

	@Override
	public Entity getNewConvert() {
		/*
		 * Edibles are not replaced when destroyed.
		 */
		return null;
	}

}
