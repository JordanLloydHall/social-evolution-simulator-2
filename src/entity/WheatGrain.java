package entity;

import java.awt.Point;
import java.util.Properties;

import interpreter.Interpreter;

public class WheatGrain extends Edible {

	private String resourceName = "WHEAT_GRAIN";
	
	public WheatGrain(Properties properties, Point newPos) {
		super(properties, newPos);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		chanceOfGeneration = Double.parseDouble(properties.getProperty(resourceName + "_GENERATION"));
		nourishment = Integer.parseInt(properties.getProperty(resourceName + "_NOURISHMENT"));
	}

}
