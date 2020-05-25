package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class Wheat extends Resource {
	
	private String resourceName = "WHEAT";
	
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
