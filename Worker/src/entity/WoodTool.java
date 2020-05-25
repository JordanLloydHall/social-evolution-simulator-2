package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class WoodTool extends Tool {

	private String resourceName = "WOOD_TOOL";

	
	public WoodTool(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		maxDurability = durability;
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		chanceOfDuplication = Float.parseFloat(properties.getProperty(resourceName + "_DUPLICATION"));
		toolUses = Integer.parseInt(properties.getProperty(resourceName + "_USES"));
		maxToolUses = toolUses;
		toolLevel = Integer.parseInt(properties.getProperty(resourceName + "_LEVEL"));
	}

}
