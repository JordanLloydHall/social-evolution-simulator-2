package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class Wood extends Resource {

	private String resourceName = "WOOD";
	
	/**
	 * Class that deals with the wood resource. Used in the path to creating a wood tool.
	 * Is replaced by the wood tool.
	 * @author Jordan
	 * @since 1.0
	 */
	public Wood(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		maxDurability = durability;
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		chanceOfDuplication = Float.parseFloat(properties.getProperty(resourceName + "_DUPLICATION"));
	}

	@Override
	public Entity getNewChild(int x, int y) {
		return null;
	}

	@Override
	public Entity getNewConvert() {
		return new WoodTool(properties, pos, r);
	}

}
