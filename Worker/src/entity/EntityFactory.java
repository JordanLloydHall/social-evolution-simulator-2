package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import neat.Counter;

public class EntityFactory {
	
	private Properties properties;
	
	private float wheatGen;
	private float actorGen;
	private float treeGen;
	private float waterGenThreshold;
	
	public EntityFactory(Properties properties) {
		this.properties = properties;
		
		
		wheatGen = Float.parseFloat(properties.getProperty("WHEAT_GENERATION"));
		actorGen = Float.parseFloat(properties.getProperty("ACTOR_GENERATION"));
		treeGen = Float.parseFloat(properties.getProperty("TREE_GENERATION"));
		waterGenThreshold = Float.parseFloat(properties.getProperty("WATER" + "_THRESHOLD"));
	}
	
	public Entity selectEntity(float generatedNumber, Counter counter, Random r) {
		
		if (generatedNumber <= wheatGen) {
			return new Wheat(properties, new Point(0,0), r);
		} else {
			generatedNumber -= wheatGen;
			if (generatedNumber <= actorGen) {
				return new Actor(properties, new Point(0,0), counter, null, r);
			} else {
				generatedNumber -= actorGen;
				if (generatedNumber <= treeGen) {
					return new Tree(properties, new Point(0,0), r);
				}
			}
		}
		
		
		return null;
	}
	
	public Entity selectEnvironmentEntity(float generatedNumber, Random r) {
		if (generatedNumber <= waterGenThreshold) {
			return new Water(properties, new Point(0,0), r);
		}
		
		return null;
	}
}
