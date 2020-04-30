package entity;

import java.awt.Point;
import java.util.Properties;

public class EntityFactory {
	
	private Properties properties;
	
	private double wheatGen;
	private double actorGen;
	
	public EntityFactory(Properties properties) {
		this.properties = properties;
		
		
		wheatGen = Double.parseDouble(properties.getProperty("WHEAT_GENERATION"));
		actorGen = Double.parseDouble(properties.getProperty("ACTOR_GENERATION"));
	}
	
	public Entity selectEntity(double generatedNumber) {
		
		if (generatedNumber <= wheatGen) {
			return new Wheat(properties, new Point(0,0));
		} else {
			generatedNumber -= wheatGen;
			if (generatedNumber <= actorGen) {
				return new Actor(properties, new Point(0,0));
			}
		}
		
		
		return null;
	}
}
