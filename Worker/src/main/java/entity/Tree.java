package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

public class Tree extends Resource {
	
	private String resourceName = "TREE";

	/**
	 * Class that deals with the tree resource. Used in the path to creating a wood tool.
	 * Is replaced by the wood resource. Is able to reproduce and created more trees.
	 * @author Jordan
	 * @since 1.0
	 */
	public Tree(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		portability = Boolean.parseBoolean(properties.getProperty(resourceName + "_PORTABLE"));
		durability = Integer.parseInt(properties.getProperty(resourceName + "_DURABILITY"));
		maxDurability = durability;
		chanceOfGeneration = Float.parseFloat(properties.getProperty(resourceName + "_GENERATION"));
		chanceOfDuplication = Float.parseFloat(properties.getProperty(resourceName + "_DUPLICATION"));
	}

	@Override
	public Entity getNewChild(int x, int y) {
		return new Tree(properties, new Point(x,y), r);
	}

	@Override
	public Entity getNewConvert() {
		return new Wood(properties, getPoint(), r);
	}

}
