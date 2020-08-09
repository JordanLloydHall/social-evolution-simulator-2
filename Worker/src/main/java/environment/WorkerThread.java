package main.java.environment;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import main.java.entity.Entity;
import main.java.entity.Tree;
import main.java.entity.Wheat;
import main.java.interpreter.Interpreter;

public class WorkerThread extends Thread {

	private LinkedBlockingQueue<Entity> entityStepQueue;
	private LinkedBlockingQueue<int[]> wheatQueue;
	private LinkedBlockingQueue<int[]> treeQueue;
	private Interpreter interpreter;
	private Environment env;
	private Properties properties;
	private Random r;
	public boolean done;
	public boolean environmentRunner;
	
	public int treesAdded = 0;
	public int wheatAdded = 0;
	
	public long timeSpentEntities = 0;
	public long timeSpentWheat = 0;
	public long timeSpentTrees = 0;
	
	private int timestep;
	
	private float wheatDuplication;
	private float treeDuplication;
	
	
	public WorkerThread(LinkedBlockingQueue<Entity> entityStepQueue, LinkedBlockingQueue<int[]> wheatQueue, LinkedBlockingQueue<int[]> treeQueue, Interpreter interpreter, Environment env, Properties properties, Random r) {
		this.entityStepQueue = entityStepQueue;
		this.wheatQueue = wheatQueue;
		this.treeQueue = treeQueue;
		this.interpreter = interpreter;
		this.properties = properties;
		this.env = env;
		this.r = r;
		environmentRunner = false;
		done = false;
		timestep = 0;
		wheatDuplication = Float.parseFloat(properties.getProperty("WHEAT_DUPLICATION"));
	}
	
	@Override
	public void run() {
		Entity currentEntity;
		int[] currentPoint;
		
		long t = 0;
		
		Date d = new Date();
		
		while (!done) {
			try {
				t = new Date().getTime();
				if (environmentRunner && env.ready) {
					
					env.step(timestep);
					timestep += 1;
				} else if (env.wheatListLen > 0) {
					currentPoint = env.wheatList.poll(20, TimeUnit.MILLISECONDS);
					if (currentPoint != null) {
						currentEntity = env.getEntity(currentPoint[0], currentPoint[1]);
						if (currentEntity == null) {
							env.insertEntity(new Wheat(properties, new Point(currentPoint[0], currentPoint[1]), r), currentPoint[0], currentPoint[1]);
							wheatAdded += 1;
						} else if (!(currentEntity instanceof Wheat)) {
							env.wheatList.add(currentPoint);
						}
					}
					env.wheatListLen -= 1;
					timeSpentWheat += d.getTime() - t;
				} else if (env.treeListLen > 0) {
					currentPoint = env.treeList.poll(20, TimeUnit.MILLISECONDS);
					if (currentPoint != null) {
						currentEntity = env.getEntity(currentPoint[0], currentPoint[1]);
						if (currentEntity == null) {
							env.insertEntity(new Tree(properties, new Point(currentPoint[0], currentPoint[1]), r), currentPoint[0], currentPoint[1]);
							treesAdded += 1;
						} else if (!(currentEntity instanceof Tree)) {
							env.treeList.add(currentPoint);
						}
					}
					env.treeListLen -= 1;
					timeSpentTrees += d.getTime() - t;
				} else {
					currentEntity = entityStepQueue.poll(20, TimeUnit.MILLISECONDS);
					if (currentEntity != null) {
						currentEntity.onStep(interpreter, env);
						timeSpentEntities += d.getTime() - t;
					}
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (env.finished) {
				done = true;
			}
					
		}
		
	}

}
