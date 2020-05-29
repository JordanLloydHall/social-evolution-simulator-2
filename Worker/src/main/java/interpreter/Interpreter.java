package main.java.interpreter;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import main.java.entity.Actor;
import main.java.entity.Entity;
import main.java.entity.Resource;
import main.java.environment.Environment;

public class Interpreter {
	
	private Properties properties;
	
	private ConcurrentLinkedQueue<int[][]> spawnQueue;
	private ConcurrentLinkedQueue<int[][]> actionQueue; 
	private ConcurrentLinkedQueue<int[]> convertQueue; 
	private ConcurrentLinkedQueue<int[][]> placeQueue; 
	private ConcurrentLinkedQueue<int[][]> pickupQueue; 
	private ConcurrentLinkedQueue<int[][]> moveQueue;
	
	public Interpreter(Properties properties) {
		this.properties = properties;
		
		spawnQueue = new ConcurrentLinkedQueue<int[][]>();
		actionQueue = new ConcurrentLinkedQueue<int[][]>();
		convertQueue = new ConcurrentLinkedQueue<int[]>();
		placeQueue = new ConcurrentLinkedQueue<int[][]>();
		pickupQueue = new ConcurrentLinkedQueue<int[][]>();
		moveQueue = new ConcurrentLinkedQueue<int[][]>();
	}
	
	public void addToSpawnQueue(int[] entityPos, int[] newEntityPos) {
		spawnQueue.add(new int[][] {entityPos, newEntityPos});
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
	
	public void interpretStep(Environment env, LinkedBlockingQueue<Entity> checkSurroundingsProcessingQueue) {
		
		int[] entityPos;
		int[] newEntityPos;
		int[] actorPos;
		int[] newActorPos;
		int[] resourcePos;
		
		
		while (!spawnQueue.isEmpty()) {
			int[][] removedCommand = spawnQueue.remove();
			entityPos = removedCommand[0];
			newEntityPos = removedCommand[1];
			
			if (env.isValidPosition(newEntityPos[0], newEntityPos[1]) && env.getEntity(newEntityPos[0], newEntityPos[1]) == null && env.getEntity(entityPos[0], entityPos[1]) != null) {
				env.insertEntity(env.getEntity(entityPos[0], entityPos[1]).getNewChild(newEntityPos[0], newEntityPos[1]), newEntityPos[0], newEntityPos[1]);
				if (env.getEntity(newEntityPos[0], newEntityPos[1]) != null) {
					env.checkSurroundings(env.getEntity(newEntityPos[0], newEntityPos[1]));
				}
			}

		}
		
		while (!actionQueue.isEmpty()) {
			int[][] removedCommand = actionQueue.remove();
			actorPos = removedCommand[0];
			entityPos = removedCommand[1];
			
			Entity targetEntity = env.getEntity(entityPos[0], entityPos[1]);
			Entity actor = env.getEntity(actorPos[0], actorPos[1]);
			
			if (actor != null && targetEntity != null && !targetEntity.isDestroyed() && actor instanceof Actor) {	
				targetEntity.onAction(this, (Actor)actor);
				((Actor)actor).useTool();
			}
		}
		
		while (!convertQueue.isEmpty()) {
			entityPos = convertQueue.remove();
			
			Entity targetEntity = env.getEntity(entityPos[0], entityPos[1]);
			if (targetEntity != null) {
				env.insertEntity(targetEntity.getNewConvert(), entityPos[0], entityPos[1]);
				checkSurroundingsProcessingQueue.add(targetEntity);
			}
		}
		
		while (!placeQueue.isEmpty()) {
			int[][] removedCommand = placeQueue.remove();
			
			actorPos = removedCommand[0];
			resourcePos = removedCommand[1];
			int x = resourcePos[0];
			int y = resourcePos[1];
			
			Entity actor = env.getEntity(actorPos[0], actorPos[1]);
			if (actor != null && actor instanceof Actor) {
				Resource targetResource = ((Actor)actor).getHeldResource();
				if (targetResource != null) {
					if (env.isValidPosition(x, y) && env.getEntity(x, y) == null) {
						((Actor)actor).holdResource(null);
						env.insertEntity(targetResource, resourcePos[0], resourcePos[1]);
						checkSurroundingsProcessingQueue.add(targetResource);
					}
					
				}
			}
			
		}
		
		while (!pickupQueue.isEmpty()) {
			
			int[][] removedCommand = pickupQueue.remove();
			
			actorPos = removedCommand[0];
			resourcePos = removedCommand[1];
			
			Entity targetResource = env.getEntity(resourcePos[0], resourcePos[1]);
			Entity actor = env.getEntity(actorPos[0], actorPos[1]);
			if (actor != null && targetResource != null && targetResource instanceof Resource && actor instanceof Actor) {
				((Actor)actor).holdResource((Resource)targetResource);
				env.removeEntity(resourcePos[0], resourcePos[1]);
				checkSurroundingsProcessingQueue.add(targetResource);
			}
		}
		
		while (!moveQueue.isEmpty()) {
			int[][] removedCommand = moveQueue.remove();
			
			actorPos = removedCommand[0];
			newActorPos = removedCommand[1];
			
			if (env.isValidPosition(newActorPos[0], newActorPos[1]) && env.getEntity(newActorPos[0], newActorPos[1]) == null) {
				Entity actor = env.getEntity(actorPos[0], actorPos[1]);
				if (actor != null && actor instanceof Actor) {
					env.removeEntity(actorPos[0], actorPos[1]);
					env.insertEntity(actor, newActorPos[0], newActorPos[1]);
				}
			}
		}
		
		while (!checkSurroundingsProcessingQueue.isEmpty()) {
			
		}
	
		
	}
	
}
