package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class WheatGrain extends Edible {

	private String resourceName = "WHEAT_GRAIN";
	
	/**
	 * Class that deals with the wheat grain resource.
	 * When consumed, it increases the durability of the actor by a set amount.
	 * @author Jordan
	 * @since 1.0
	 */
	public WheatGrain(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		nourishment = Integer.parseInt(properties.getProperty(resourceName + "_NOURISHMENT"));
	}

}
