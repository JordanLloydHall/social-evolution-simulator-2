package main.java.environment;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
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
	
	private int timestep;
	
	
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
	}
	
	@Override
	public void run() {
		Entity currentEntity;
		int[] currentPoint;
		
		while (!done) {
			try {
				if (environmentRunner && env.ready) {
					
					env.step(timestep);
					timestep += 1;
				
				} else if (!entityStepQueue.isEmpty()) {
					currentEntity = entityStepQueue.poll(50, TimeUnit.MILLISECONDS);
					if (currentEntity != null && currentEntity.getIsVisible()) {
						currentEntity.onStep(interpreter, env);
					}
				} else if (!wheatQueue.isEmpty()) {
					currentPoint = wheatQueue.poll(50, TimeUnit.MILLISECONDS);
					if (currentPoint != null) {
						if (env.getEntity(currentPoint[0], currentPoint[1]) == null) { 
							if (r.nextFloat() < Float.parseFloat(properties.getProperty("WHEAT_DUPLICATION"))) {
								env.insertEntity(new Wheat(properties, new Point(currentPoint[0], currentPoint[1]), r), currentPoint[0], currentPoint[1]);
								wheatAdded += 1;
								env.wheatList.remove(currentPoint);
							}
						}
					}
				} else {
					currentPoint = treeQueue.poll(50, TimeUnit.MILLISECONDS);
					if (currentPoint != null) {
						if (env.getEntity(currentPoint[0], currentPoint[1]) == null) {
							if (r.nextFloat() < Float.parseFloat(properties.getProperty("TREE_DUPLICATION"))) {
								env.insertEntity(new Tree(properties, new Point(currentPoint[0], currentPoint[1]), r), currentPoint[0], currentPoint[1]);
								treesAdded += 1;
								env.treeList.remove(currentPoint);
							}	
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
					
		}
		
	}

}
