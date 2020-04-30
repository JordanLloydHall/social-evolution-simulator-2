package entity;

import java.awt.Point;
import java.util.Properties;

public abstract class Resource extends Entity {
	
	public Resource(Properties properties, Point newPos) {
		super(properties, newPos);
	}
	
	public abstract String onAction(Actor actor);
	
	public abstract String onStep();
	
}
