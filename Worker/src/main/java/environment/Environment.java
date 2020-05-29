package main.java.environment;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import main.java.entity.Actor;
import main.java.entity.Edible;
import main.java.entity.Entity;
import main.java.entity.EntityFactory;
import main.java.entity.Water;
import main.java.entity.Wheat;
import main.java.interpreter.Interpreter;
import main.java.kurtSpencer.openSimplex.OpenSimplexNoise;
import main.java.neat.Counter;
import net.sf.javaml.core.kdtree.KDTree;

public class Environment {
	private Properties properties;
	private final int worldWidth;
	private final int worldHeight;
	private Entity[][] worldGrid;
	private KDTree worldKDTree;
	private ArrayList<Entity> entityList;
	private Interpreter interpreter;
	private Counter counter;
	private Random r;
	
	LinkedBlockingQueue<Entity> entityStepProcessingQueue;
	LinkedBlockingQueue<Entity> checkSurroundingsProcessingQueue;
	WorkerThread[] entityProcessors;
	
	public Environment(Properties properties, Interpreter interpreter, Random r) {
		
		this.properties = properties;
		this.interpreter = interpreter;
		worldWidth = Integer.parseInt(properties.getProperty("WORLD_WIDTH"));
		worldHeight = Integer.parseInt(properties.getProperty("WORLD_HEIGHT"));
		counter = new Counter();
		
		this.r = r;
		
		entityStepProcessingQueue = new LinkedBlockingQueue<Entity>();
		checkSurroundingsProcessingQueue = new LinkedBlockingQueue<Entity>();
		
		entityProcessors = new WorkerThread[4];
		
		for (int i=0; i<4; i++) {
			entityProcessors[i] = new WorkerThread(entityStepProcessingQueue, checkSurroundingsProcessingQueue, interpreter, this);
			entityProcessors[i].start();
		}
		
		EntityFactory entityFactory = new EntityFactory(properties);
		
		worldGrid = new Entity[worldWidth][worldHeight];
		entityList = new ArrayList<Entity>();
		worldKDTree = new KDTree(2);
		
		OpenSimplexNoise openSimplex = new OpenSimplexNoise();
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				
				float generatedNumber = (float) ((openSimplex.eval(x/20f, y/20f)+1)/2f);
				float distance_x = Math.abs(x - worldWidth * 0.5f);
				float distance_y = Math.abs(y - worldWidth * 0.5f);
				float distance = Math.max(distance_x, distance_y); // circular mask

				float max_width = worldWidth * 0.5f - 0.0f;
				float delta = distance / max_width;
				float gradient = (float) Math.pow(delta, 3);

				generatedNumber *= Math.max(0.0f, 1.0f - gradient);				
				Entity newEntity = entityFactory.selectEnvironmentEntity(generatedNumber, r);
				if (newEntity == null) {
					newEntity = entityFactory.selectEntity(r.nextFloat(), counter, r);
				}
				if (newEntity != null) {
					insertEntity(newEntity, x, y);
				}
			}
		}
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				
				Entity targetEntity = getEntity(x,y);
				if (targetEntity != null) {
					targetEntity.checkIfVisable(this);
				}
			}
		}
		
	}
	
	public void generateKDTree() {
		worldKDTree = new KDTree(2);
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				
				if (worldGrid[x][y] != null && worldGrid[x][y].getIsVisible()) {
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
	
	public void checkSurroundings(Entity e) {
		for (int x=e.getPos()[0]-1; x<=e.getPos()[0]+1; x++) {
			for (int y=e.getPos()[1]-1; y<=e.getPos()[1]+1; y++) {
				if (getEntity(x,y) != null && !(getEntity(x,y) instanceof Actor)) {
					try {
						boolean wasVisable = getEntity(x,y).getIsVisible();
						boolean isNowVisable = getEntity(x,y).checkIfVisable(this);
						if (isNowVisable != wasVisable) {
							checkSurroundingsProcessingQueue.add(getEntity(x,y));
						}
					} catch(NullPointerException ex) {
						
					}
				}
			}
		}
	}

	public void insertEntity(Entity entity, int x, int y) {
		if (isValidPosition(x, y)) {
			removeEntity(x,y);
			if (entity != null) {
				entity.setPos(x, y);
				if (!(entity instanceof Water) && !(entity instanceof Edible) && !entityList.contains(entity)) {
					entityList.add(entity);
				}
			}
			worldGrid[x][y] = entity;
			worldKDTree.insert(new double[] {x,  y}, entity);
		}
	}
	
	public Entity removeEntity(int x, int y) {
		
		Entity returnedEntity = worldGrid[x][y];
		
		worldGrid[x][y] = null;
		entityList.remove(returnedEntity);
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

	public boolean step(int time) {
		int numActors = 0;

		entityStepProcessingQueue.addAll(entityList);
//		for (Entity e : entityList) {
//			if (!e.getIsVisible()) {
//				entityStepProcessingQueue.add(e)
//			}
//		}
		for (Entity newEntity : entityList) {
			if (newEntity instanceof Actor) {
				numActors +=1;
			}
		}
		
		while (!entityStepProcessingQueue.isEmpty()) {
		}
	
		interpreter.interpretStep(this, checkSurroundingsProcessingQueue);
		int x = r.nextInt(this.worldWidth);
		int y = r.nextInt(this.worldHeight);
		if (isValidPosition(x, y) && getEntity(x, y) == null) {
			insertEntity(new Wheat(properties, new Point(x,y), r), x, y);
		}
		generateKDTree();
		
		if (time == 10000 || numActors == 0) {
			System.out.println(numActors);
			for (int i=0; i<entityProcessors.length; i++) {
				entityProcessors[i].done = true;
				try {
					entityProcessors[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		
		return false;
	}
	
	public boolean isValidPosition(int x, int y) {
		return (x >= 0 && y >= 0 && x < getWorldWidth() && y < getWorldHeight());
	}
	
	public Entity[] getEntitiesWithinRange(float[] pos, float viewRadius) {
		
		
		Object[] objectArray = worldKDTree.range(new double[] {pos[0]-viewRadius, pos[1]-viewRadius}, new double[] {pos[0]+viewRadius, pos[1]+viewRadius});
		Entity[] entityArray = new Entity[objectArray.length];
		
		for (int i=0; i<entityArray.length; i++) {
			entityArray[i] = (Entity)objectArray[i];
		}
		
		return entityArray;
	}
}
