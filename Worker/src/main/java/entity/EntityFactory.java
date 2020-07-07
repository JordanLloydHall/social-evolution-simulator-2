package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.neat.Counter;

public class EntityFactory {
	
	private Properties properties;
	
	private float wheatGen;
	private float actorGen;
	private float treeGen;
	private float waterGenThreshold;
	
	/**
	 * This class is used to configure the world by returning appropriate entities when called.
	 * @author Jordan
	 * @param properties The global Properties object to acquire the specific generation probabilities of each entity.
	 * @since 1.0
	 */
	public EntityFactory(Properties properties) {
		this.properties = properties;
		
		
		wheatGen = Float.parseFloat(properties.getProperty("WHEAT_GENERATION"));
		actorGen = Float.parseFloat(properties.getProperty("ACTOR_GENERATION"));
		treeGen = Float.parseFloat(properties.getProperty("TREE_GENERATION"));
		waterGenThreshold = Float.parseFloat(properties.getProperty("WATER" + "_THRESHOLD"));
	}
	
	/**
	 * Used in the second pass of the environment. Selects, creates and returns an entity depending on the generation probabilities and generatedNumber
	 * @param generatedNumber number to be used in determining which threshold has been passed.
	 * @param r to be passed to the created entity. This function does not directly call the Random class.
	 * @param counter the global counter to be passed onto actor objects 
	 * @return the generated entity or null if no threshold has been reached.
	 */
	public Entity selectEntity(float generatedNumber, Counter counter, Random r) {
		

		if (generatedNumber <= actorGen) {
			return new Actor(properties, new Point(0,0), counter, null, r, null);
		}
		
		
		return null;
	}
	
	/**
	 * Used in the first pass of the environment. As of now this only includes the water entity, but is subject to change.
	 * @param generatedNumber number to be used in determining which threshold has been passed.
	 * @param r to be passed to the created entity. This function does not directly call the Random class.
	 * @return the generated entity or null if no threshold has been reached.
	 */
	public Entity selectEnvironmentEntity(float generatedNumber, Random r, boolean resources) {
		if (generatedNumber <= waterGenThreshold) {
			return new Water(properties, new Point(0,0), r);
		} else if (resources) {
			generatedNumber -= waterGenThreshold;
			if (generatedNumber <= wheatGen) {
				return new Wheat(properties, new Point(0,0), r);
			} else {
				generatedNumber -= wheatGen;
				if (generatedNumber <= treeGen) {
					return new Tree(properties, new Point(0,0), r);
				}
			}
		}
		
		return null;
	}
}
