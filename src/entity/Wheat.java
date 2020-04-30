package entity;

import java.awt.Point;
import java.util.Properties;

public class Wheat extends Resource {
	
	private String resourceName = "WHEAT";
	
	public Wheat(Properties properties, Point newPos) {
		super(properties, newPos);
		
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		chanceOfGeneration = Double.parseDouble(properties.getProperty(resourceName + "_GENERATION"));
		chanceOfDuplication = Double.parseDouble(properties.getProperty(resourceName + "_DUPLICATION"));
	}

	@Override
	public String onAction(Actor actor) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String onStep() {
		// TODO Auto-generated method stub
		return "";
	}

}
