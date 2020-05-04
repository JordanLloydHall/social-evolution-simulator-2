package interpreter;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import entity.Actor;
import entity.Entity;
import entity.Resource;
import environment.Environment;

public class Interpreter {
	
	private Properties properties;
	
	private ConcurrentLinkedQueue<int[]> spawnQueue;
	private ConcurrentLinkedQueue<int[][]> actionQueue; 
	private ConcurrentLinkedQueue<int[]> convertQueue; 
	private ConcurrentLinkedQueue<int[][]> placeQueue; 
	private ConcurrentLinkedQueue<int[][]> pickupQueue; 
	private ConcurrentLinkedQueue<int[][]> moveQueue;
	
	public Interpreter(Properties properties) {
		this.properties = properties;
		
		spawnQueue = new ConcurrentLinkedQueue<int[]>();
		actionQueue = new ConcurrentLinkedQueue<int[][]>();
		convertQueue = new ConcurrentLinkedQueue<int[]>();
		placeQueue = new ConcurrentLinkedQueue<int[][]>();
		pickupQueue = new ConcurrentLinkedQueue<int[][]>();
		moveQueue = new ConcurrentLinkedQueue<int[][]>();
	}
	
	public void addToSpawnQueue(int[] entityPos) {
		spawnQueue.add(entityPos);
	}
	
	public void addToActionQueue(int[] actorPos, int[] entityPos) {
		actionQueue.add(new int[][] {actorPos, entityPos});
	}
	
	public void addToConvertQueue(int[] entityPos) {
		convertQueue.add(entityPos);
	}
	
	public void addToPlaceQueue(int[] actorPos, int[] resourcePos) {
		placeQueue.add(new int[][] {actorPos, resourcePos});
	}
	
	public void addToPickupQueue(int[] actorPos, int[] resourcePos) {
		pickupQueue.add(new int[][] {actorPos, resourcePos});
	}
	
	public void addToMoveQueue(int[] actorPos, int[] newActorPos) {
		moveQueue.add(new int[][] {actorPos, newActorPos});
	}
	
	public void interpretStep(Environment env) {
		
		int[] entityPos;
		int[] actorPos;
		int[] newActorPos;
		int[] resourcePos;
		
		
		while (!spawnQueue.isEmpty()) {
			entityPos = spawnQueue.remove();
			boolean hasSpawned = false;
			for (int x=entityPos[0]-1; x<=entityPos[0]+1; x++) {
				for (int y=entityPos[1]-1; y<=entityPos[1]+1; y++) {
					if (!hasSpawned && env.isValidPosition(x, y) && new int[] {x,y} != entityPos && env.getEntity(x, y) == null) {
						hasSpawned = true;
						env.insertEntity(env.getEntity(entityPos[0], entityPos[1]).getNewChild(x,y), x, y);
					}
				}
			}
		}
		
		while (!actionQueue.isEmpty()) {
			int[][] removedCommand = actionQueue.remove();
			actorPos = removedCommand[0];
			entityPos = removedCommand[1];
			
			Entity targetEntity = env.getEntity(entityPos[0], entityPos[1]);
			Actor actor = (Actor)env.getEntity(actorPos[0], actorPos[1]);
			
			if (targetEntity != null && !targetEntity.isDestroyed()) {	
				targetEntity.onAction(this, actor);
			}
		}
		
		while (!convertQueue.isEmpty()) {
			entityPos = convertQueue.remove();
			
			Entity targetEntity = env.getEntity(entityPos[0], entityPos[1]);
			if (targetEntity != null) {
				env.insertEntity(targetEntity.getNewConvert(), entityPos[0], entityPos[1]);
			}
		}
		
		while (!placeQueue.isEmpty()) {
			int[][] removedCommand = placeQueue.remove();
			
			actorPos = removedCommand[0];
			resourcePos = removedCommand[1];
			
			Actor actor = (Actor)env.getEntity(actorPos[0], actorPos[1]);
			Resource targetResource = actor.getHeldResource();
			if (targetResource != null) {
				actor.holdResource(null);
				env.insertEntity(targetResource, resourcePos[0], resourcePos[1]);
			}
			
		}
		
		while (!pickupQueue.isEmpty()) {
			int[][] removedCommand = pickupQueue.remove();
			
			actorPos = removedCommand[0];
			resourcePos = removedCommand[1];
			
			Entity targetResource = env.getEntity(resourcePos[0], resourcePos[1]);
			Actor actor = (Actor)env.getEntity(actorPos[0], actorPos[1]);
			if (targetResource instanceof Resource) {
				actor.holdResource((Resource)targetResource);
				env.removeEntity(resourcePos[0], resourcePos[1]);
			}
			
		}
		
		while (!moveQueue.isEmpty()) {
			int[][] removedCommand = moveQueue.remove();
			
			actorPos = removedCommand[0];
			newActorPos = removedCommand[1];
			
			
			
			if (env.isValidPosition(newActorPos[0], newActorPos[1]) && env.getEntity(newActorPos[0], newActorPos[1]) == null) {
				Actor actor = (Actor)env.getEntity(actorPos[0], actorPos[1]);
				env.removeEntity(actorPos[0], actorPos[1]);
				env.insertEntity(actor, newActorPos[0], newActorPos[1]);
			}
		}
	
		
	}
	
}
