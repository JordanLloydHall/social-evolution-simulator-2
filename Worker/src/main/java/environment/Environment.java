package main.java.environment;

import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import main.java.entity.Actor;
import main.java.entity.Edible;
import main.java.entity.Egg;
import main.java.entity.Entity;
import main.java.entity.EntityFactory;
import main.java.entity.SpawningResources;
import main.java.entity.Tree;
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
	public SpawningResources[][] resourceSpawnGrid;
	
	private ArrayList<Entity> entityList;
	public LinkedBlockingQueue<int[]> wheatList;
	public LinkedBlockingQueue<int[]> treeList;
//	private ArrayList<Point> tideList;
	private Interpreter interpreter;
	private Counter counter;
	private Random r;
	
	public int numTrees = 0;
	public int numWheat = 0;
	
	public int deaths;
	public int homicides;
	
	public int timestep = 0;
	
	public boolean ready = false;
	public boolean finished = false;
	
	LinkedBlockingQueue<Entity> entityStepProcessingQueue;
	
	LinkedBlockingQueue<int[]> wheatProcessingQueue;
	LinkedBlockingQueue<int[]> treeProcessingQueue;
	
	WorkerThread[] entityProcessors;
	

	public Environment(Properties properties, Interpreter interpreter, Random r) {
		
		try {
            File file = new File("./data.csv");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("ts,numActors,numWheat,numTrees,fovAvg,viewAvg,deaths,homicides,lifeSpan,sexual,"
            		+ "asexual,genDistAvg,numberOfSpecies,totalSize,totalSpeed,gestationCost");
            bw.flush();
            bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
		
		this.properties = properties;
		this.interpreter = interpreter;
		worldWidth = Integer.parseInt(properties.getProperty("WORLD_WIDTH"));
		worldHeight = Integer.parseInt(properties.getProperty("WORLD_HEIGHT"));
		counter = new Counter(r);
		
		this.r = r;
		
		entityStepProcessingQueue = new LinkedBlockingQueue<Entity>();
		wheatProcessingQueue = new LinkedBlockingQueue<>();
		treeProcessingQueue = new LinkedBlockingQueue<>();
		
		wheatList = new LinkedBlockingQueue<>();
		treeList = new LinkedBlockingQueue<>();
//		tideList = new ArrayList<Point>();
		
		entityProcessors = new WorkerThread[4];
		
		for (int i=0; i<entityProcessors.length; i++) {
			entityProcessors[i] = new WorkerThread(entityStepProcessingQueue, wheatProcessingQueue, treeProcessingQueue, interpreter, this, properties, r);
			entityProcessors[i].start();
		}
		
		
		
		EntityFactory entityFactory = new EntityFactory(properties);
		
		worldGrid = new Entity[worldWidth][worldHeight];
		resourceSpawnGrid = new SpawningResources[worldWidth][worldHeight];
		entityList = new ArrayList<Entity>();
		
		OpenSimplexNoise openSimplex = new OpenSimplexNoise();
		
		
		
		for (int x=0; x<worldWidth; x++) {
			for (int y=0; y<worldHeight; y++) {
				
				float generatedNumber = (float) ((openSimplex.eval(x/40f, y/40f)+1)/2f);
				float distance_x = Math.abs(x - worldWidth * 0.5f);
				float distance_y = Math.abs(y - worldWidth * 0.5f);
				float distance = Math.max(distance_x, distance_y); // Square mask

				float max_width = worldWidth * 0.5f - 0.0f;
				float delta = distance / max_width;
				float gradient = (float) Math.pow(delta, 3);

				generatedNumber *= Math.max(0.0f, 1.0f - gradient);
				Entity newEntity = entityFactory.selectEnvironmentEntity(generatedNumber, r, false);
//				if (newEntity == null && entityFactory.selectEnvironmentEntity(generatedNumber - 1/(float)Math.pow(distance_x,2), r, false) != null) {
////					newEntity = entityFactory.selectEnvironmentEntity(generatedNumber - 2/(float)distance_x, r, false);
//					tideList.add(new Point(x,y));
//				}
				
				if (newEntity == null) {
					
					generatedNumber = (float) ((openSimplex.eval((x+100)/20f, (y+100)/20f)+1)/2f);
					if (generatedNumber < Float.parseFloat(properties.getProperty("WHEAT_GENERATION"))) {
//						wheatList.add(new Point(x,y));
						resourceSpawnGrid[x][y] = SpawningResources.WHEAT;
						if (r.nextFloat() < Float.parseFloat(properties.getProperty("ACTOR_GENERATION"))) {
							newEntity = new Actor(properties, new Point(0,0), counter, null, r, null);
							wheatList.add(new int[] {x,y});
						} else {
							newEntity = new Wheat(properties, new Point(0,0), r);
							numWheat += 1;
						}
						
					}
					
					if (newEntity == null) {
						
						generatedNumber = (float) ((openSimplex.eval((x+500)/20f, (y+500)/20f)+1)/2f);
						
						if (generatedNumber < Float.parseFloat(properties.getProperty("TREE_GENERATION"))) {
//							treeList.add(new Point(x,y));
							resourceSpawnGrid[x][y] = SpawningResources.TREE;
							newEntity = new Tree(properties, new Point(0,0), r);
							numTrees += 1;
						}
					}
					
//					if (newEntity == null) {
//						
//						newEntity = entityFactory.selectEntity(r.nextFloat(), counter, r);
//					}
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
		
		
		entityProcessors[0].environmentRunner = true;
		
	}

	public int getWorldWidth() { return worldWidth; }
	
	public int getWorldHeight() { return worldHeight; }
	
	public void resetWorld() {
		worldGrid = new Entity[worldWidth][worldHeight];
	}
	
//	public void checkSurroundings(Entity e) {
//		for (int x=e.getPos()[0]-1; x<=e.getPos()[0]+1; x++) {
//			for (int y=e.getPos()[1]-1; y<=e.getPos()[1]+1; y++) {
//				if (getEntity(x,y) != null && !(getEntity(x,y) instanceof Actor)) {
//					try {
//						boolean wasVisable = getEntity(x,y).getIsVisible();
//						boolean isNowVisable = getEntity(x,y).checkIfVisable(this);
//						if (isNowVisable != wasVisable) {
//							checkSurroundingsProcessingQueue.add(getEntity(x,y));
//						}
//					} catch(NullPointerException ex) {
//						
//					}
//				}
//			}
//		}
//	}

	public void insertEntity(Entity entity, int x, int y) {
		if (isValidPosition(x, y)) {
			removeEntity(x,y);
			if (entity != null) {
				entity.setPos(x, y);
				
				if ((entity instanceof Actor || entity instanceof Egg) && !entityList.contains(entity)) {
					entityList.add(entity);
				}
			}
			worldGrid[x][y] = entity;
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
		long t = new Date().getTime();
		int numActors = 0;
		double fov = 0;
		double viewRadius = 0;
		int numActorsSeen = 0;
		double totalGeneticDistance = 0;
		double sizeTotal = 0;
		double totalSpeed = 0;
		float lifeSpan = 0;
		float gestationCost = 0;
		deaths = 0;
		homicides = 0;
		timestep = time;
		float sexual = 0;
		float asexual = 0;
		
//		if ((time+1) % 50000 == 0) {
//			for (Point p : tideList) {
//				if (tideUp) {
//					removeEntity(p.x,p.y);
//				} else {
//					insertEntity(new Water(properties, new Point(p.x,p.y), r), p.x, p.y);
//				}
//			}
//			
//			tideUp = !tideUp;
//		}
		
		entityStepProcessingQueue.addAll(entityList);
//		ArrayList<int[]> wheatListClone = (ArrayList<int[]>) wheatList.clone();
//		wheatList.clear();
		wheatProcessingQueue.addAll(wheatList);
//		ArrayList<int[]> treeListClone = (ArrayList<int[]>) treeList.clone();
//		treeList.clear();
		treeProcessingQueue.addAll(treeList);
//		treeList.clear();
		
		

		Actor actor;
		for (Entity newEntity : entityList) {
			if (newEntity instanceof Actor) {
				actor = (Actor)newEntity;
				numActors += 1;
				fov += actor.fov;
				viewRadius += actor.viewRadius;
				lifeSpan += actor.age;
				numActorsSeen += actor.numActorsSeen;
				totalGeneticDistance += actor.totalGeneticDistance;
				sizeTotal += actor.size;
				totalSpeed += actor.speed;
				if (actor.asexual) {
					asexual += 1;
				} else {
					sexual += 1;
				}
				gestationCost += actor.gestationCost;
				
			}
		}
		
		while (!entityStepProcessingQueue.isEmpty() || !wheatProcessingQueue.isEmpty() || !treeProcessingQueue.isEmpty()) {
		}
		
		for (int i=0; i<entityProcessors.length; i++) {
			numWheat += entityProcessors[i].wheatAdded;
			numTrees += entityProcessors[i].treesAdded;
			entityProcessors[i].wheatAdded = 0;
			entityProcessors[i].treesAdded = 0;
		}
	
		interpreter.interpretStep(this);
		
		System.out.println("Timestep " + time + " completed. Time taken: " + Math.round((new Date().getTime() - t)*100f/1000f)/100f + " " + numActors);
//		System.out.println(wheatList.size());
		if (numActors == 0 || finished) {
			System.out.print(numActors);
			finish();
		}
		
		try {
            File file = new File("./data.csv");
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("\n"+time+","+numActors+","+numWheat+","+numTrees+","+fov/(float)numActors+","+viewRadius/(float)numActors+","+deaths+","+homicides+
            		","+lifeSpan+","+sexual+","+asexual+","+totalGeneticDistance/(float)numActorsSeen+","+counter.numOfSpecies+","+sizeTotal+","+totalSpeed+
            		","+gestationCost);
            bw.flush();
            bw.close();
        } catch(IOException e) {
        	e.printStackTrace();
        }
				
		return false;
	}
	
	public boolean isValidPosition(int x, int y) {
		return (x >= 0 && y >= 0 && x < getWorldWidth() && y < getWorldHeight());
	}
	
	public ArrayList<Entity> getEntitiesWithinRange(float[] pos, float viewRadius) {		
		
		ArrayList<Entity> entityArray = new ArrayList<Entity>();
		
		int startX = (int) Math.floor(pos[0] - viewRadius);
		int endX = (int) Math.ceil(pos[0] + viewRadius);
		int startY = (int) Math.floor(pos[1] - viewRadius);
		int endY = (int) Math.ceil(pos[1] + viewRadius);
		
		for (int y=startY; y<=endY; y++) {
			for (int x=startX; x<=endX; x++) {
				Entity e = getEntity(x,y);
				if (e != null && !(x==pos[0] && y==pos[1])) {
					entityArray.add(e);
				}
			}
		}
		
		return entityArray;
	}
	
	public void finish() {
		finished = true;
		for (int i=0; i<entityProcessors.length; i++) {
			entityProcessors[i].done = true;
		}
	}
}
