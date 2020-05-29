package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class Wheat extends Resource {
	
	private String resourceName = "WHEAT";
	
	/**
	 * Class that deals with the wheat resource. Used in the path to creating wheat grain, which can be consumed.
	 * Is replaced by the wheat grain edible. Is able to reproduce and created more wheat.
	 * @author Jordan
	 * @since 1.0
	 */
	public Wheat(Properties properties, Point newPos,Random r) {
		super(properties, newPos, r);
		
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		maxDurability = durability;
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		chanceOfDuplication = Float.parseFloat(properties.getProperty(resourceName + "_DUPLICATION"));
	}	

	@Override
	public Entity getNewChild(int x, int y) {
		return new Wheat(properties, new Point(x, y), r);
	}

	@Override
	public Entity getNewConvert() {
		return new WheatGrain(properties, getPoint(), r);
	}

}
