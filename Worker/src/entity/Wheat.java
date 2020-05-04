package entity;

import java.awt.Point;
import java.util.Properties;

import interpreter.Interpreter;

public class Wheat extends Resource {
	
	private String resourceName = "WHEAT";
	
	public Wheat(Properties properties, Point newPos) {
		super(properties, newPos);
		
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		chanceOfGeneration = Double.parseDouble(properties.getProperty(resourceName + "_GENERATION"));
		chanceOfDuplication = Double.parseDouble(properties.getProperty(resourceName + "_DUPLICATION"));
	}	

	@Override
	public Entity getNewChild(int x, int y) {
		// TODO Auto-generated method stub
		return new Wheat(properties, new Point(x, y));
	}

	@Override
	public Entity getNewConvert() {
		return new WheatGrain(properties, getPoint());
	}

}
