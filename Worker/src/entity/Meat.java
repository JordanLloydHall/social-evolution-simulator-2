package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class Meat extends Edible {

	private String resourceName = "MEAT";
	
	public Meat(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		nourishment = Integer.parseInt(properties.getProperty(resourceName + "_NOURISHMENT"));
	}

}
