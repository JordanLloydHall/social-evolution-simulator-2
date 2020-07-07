package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;
import main.java.neat.Genome;

public class Egg extends Resource {
	
	Actor child;

	public Egg(Properties properties, Point newPos, Random r, Actor child, int gestationCost) {
		super(properties, newPos, r);
		durability = gestationCost;
		this.child = child;
		
		maxDurability = 100;
	}

	@Override
	public Entity getNewChild(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity getNewConvert() {
		// TODO Auto-generated method stub
		if (isDestroyed()) {
			return new Meat(properties, new Point(), r);
		} else {
			return child;
		}
	}
	
	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		durability += 1;
		if (durability == 100) {
			interpreter.addToConvertQueue(getPos());
		}
	}
	
	public Genome getGenome() {
		return child.genome;
	}

}
