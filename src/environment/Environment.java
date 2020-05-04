package environment;

import java.awt.Point;
import java.util.Arrays;
import java.util.Properties;

import entity.Actor;
import entity.Entity;
import entity.EntityFactory;
import entity.Wheat;
import interpreter.Interpreter;
import net.sf.javaml.core.kdtree.KDTree;

public class Environment {
	private Properties properties;
	private final int worldWidth;
	private final int worldHeight;
	private Entity[][] worldGrid;
	private KDTree worldKDTree;
	private Interpreter interpreter;
	
	public Environment(Properties properties, Interpreter interpreter) {
		this.properties = properties;
		this.interpreter = interpreter;
		worldWidth = Integer.parseInt(properties.getProperty("WORLD_WIDTH"));
		worldHeight = Integer.parseInt(properties.getProperty("WORLD_HEIGHT"));
		
		EntityFactory entityFactory = new EntityFactory(properties);
		
		worldGrid = new Entity[worldWidth][worldHeight];
		worldKDTree = new KDTree(2);
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				Entity newEntity = entityFactory.selectEntity(Math.random());
						
				if (newEntity != null) {
					insertEntity(newEntity, x, y);
				}
			}
		}
		
//		resetWorld();
//		Wheat newWheat = new Wheat(properties, new Point(1,0));
//		Actor newactor = new Actor(properties, new Point(1,1));
//		insertEntity(newWheat, 1, 8);
//		insertEntity(newactor, 1, 1);
		
	}
	
	public void generateKDTree() {
		worldKDTree = new KDTree(2);
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				
				if (worldGrid[x][y] != null) {
					worldKDTree.insert(new double[] {x, y}, worldGrid[x][y]);
				}
			}
		}
	}

	public int getWorldWidth() { return worldWidth; }
	
	public int getWorldHeight() { return worldHeight; }
	
	public void resetWorld() {
		worldGrid = new Entity[worldWidth][worldHeight];
		worldKDTree = new KDTree(2);
	}

	public void insertEntity(Entity entity, int x, int y) {
		if (isValidPosition(x, y)) {
			if (entity != null) {
				entity.setPos(x, y);
			}
			worldGrid[x][y] = entity;
			worldKDTree.insert(new double[] {x,  y}, entity);
		}
	}
	
	public Entity removeEntity(int x, int y) {
		
		Entity returnedEntity = worldGrid[x][y];
		
		worldGrid[x][y] = null;
		worldKDTree.delete(new double[] {x, y});
		
		return returnedEntity;
	}
	
	public Entity getEntity(int x, int y) {
		if (isValidPosition(x,y)) {
			return worldGrid[x][y];
		}
		return null;
	}

	public Entity[][] getWorldGrid() {
		return worldGrid;
	}

	public void step() {
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				Entity newEntity = getEntity(x,y);
				if (newEntity != null) {
					newEntity.onStep(interpreter, this);
				}
			}
		}
		
		interpreter.interpretStep(this);
		generateKDTree();
	}
	
	public boolean isValidPosition(int x, int y) {
		return (x >= 0 && y >= 0 && x < getWorldWidth() && y < getWorldHeight());
	}
	
	public Entity[] getEntitiesWithinRange(double[] pos, double viewRadius) {
		
		
		Object[] objectArray = worldKDTree.range(new double[] {pos[0]-viewRadius, pos[1]-viewRadius}, new double[] {pos[0]+viewRadius, pos[1]+viewRadius});
		Entity[] entityArray = new Entity[objectArray.length];
		
//		System.out.println(Arrays.toString(objectArray));
		for (int i=0; i<entityArray.length; i++) {
			entityArray[i] = (Entity)objectArray[i];
		}
		
		return entityArray;
	}
}
