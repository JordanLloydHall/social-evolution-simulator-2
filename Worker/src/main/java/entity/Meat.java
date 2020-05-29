package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class Meat extends Edible {

	private String resourceName = "MEAT";
	/**
	 * Class that deals with the meat edible. Dropped by actors when killed by another actor.
	 * @author Jordan
	 * @since 1.0
	 */
	public Meat(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		nourishment = Integer.parseInt(properties.getProperty(resourceName + "_NOURISHMENT"));
	}

}
