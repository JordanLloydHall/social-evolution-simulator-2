package environment;

import java.util.Properties;

import entity.Entity;
import entity.EntityFactory;
import net.sf.javaml.core.kdtree.KDTree;

public class Environment {
	private Properties properties;
	private final int worldWidth;
	private final int worldHeight;
	private Entity[][] worldGrid;
	private KDTree worldKDTree;
	
	public Environment(Properties properties) {
		this.properties = properties;
		worldWidth = Integer.parseInt(properties.getProperty("WORLD_WIDTH"));
		worldHeight = Integer.parseInt(properties.getProperty("WORLD_HEIGHT"));
		
		EntityFactory entityFactory = new EntityFactory(properties);
		
		worldGrid = new Entity[worldWidth][worldHeight];
		worldKDTree = new KDTree(2);
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				
				insertEntity(entityFactory.selectEntity(Math.random()), x, y);
			}
		}	
		
	}

	public Entity[][] getWorldGrid() {
		return worldGrid;
	}

	public void insertEntity(Entity entity, int x, int y) {
		if (x >= 0 && x < worldWidth && y >= 0 && y < worldHeight) {
			worldGrid[x][y] = entity;
			worldKDTree.insert(new double[] {x,  y}, entity);
		}
		
		
	}
	
}
