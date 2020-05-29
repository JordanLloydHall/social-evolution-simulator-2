package main.java.entity;

import java.awt.Point;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;
import main.java.neat.ConnectionGene;
import main.java.neat.Counter;
import main.java.neat.Genome;
import main.java.neat.NodeGene.TYPE;

/**
 *
 * @author Jordan
 */

public class Actor extends Entity {
	
	private Resource heldResource;
	private Tool equippedTool;
	
	private boolean killedByActor;

	private final int rayCastCount;
	private final int maxAge;

	private float viewRadius;
	private float viewRadiusStepSize;
	private float fov;
	private float fovStepSize;
	
	private int currentDirection;
	
	private int totalNetworkInputs;
	private int totalNetworkOutputs;
	private int dimensionsPerRaycast;
	private int dimensionsBeforeRaycast;
	private int numberOfDifferentEntities;
	private int numberOfDifferentTools;
	
	private Actor matingPartner;
	private float communicationOut;
	
	private int age;
	
	private Genome genome;
	private Counter counter;
	
	public enum Directions {
		UP(1),
		DOWN(3),
		LEFT(2),
		RIGHT(0);
		
		public final int label;
		
		private Directions(int label) {
			this.label = label;
		}
	}
	
	/**
	 * This is the class that deals with the main actors of the simulation; 
	 * that is, those who evolve and interact with the environment dynamically.
	 * @param properties the properties object shared by all entity classes.
	 * @param newPos Point object describing the entity's location on the grid.
	 * @param counter Counter object used in the actor's Genome.
	 * @param r Random object shared by all entity classes.
	 * @since 1.0
	 */

	public Actor(Properties properties, Point newPos, Counter counter, Genome inheritedGenome, Random r) {
		super(properties, newPos, r);
		
		viewRadius = Float.parseFloat(properties.getProperty("ACTOR_VIEW_RADIUS"));
		viewRadiusStepSize = 0.1f;
		fov = (float) Math.PI;
		fovStepSize = 0.1f;
		
		
		rayCastCount = Integer.parseInt(properties.getProperty("ACTOR_VIEW_CASTS"));
		maxAge = Integer.parseInt(properties.getProperty("ACTOR_MAX_AGE"));
		
		// INPUTS : constant, Internal durability %,
		//			number of raycasts * (
		//		durability of entity %,
		//		
		//		type of entity (6 types),
		// 		communication output from actor,
		//		genetic distance from actor)
		//			
		// OUTPUTS : turn left, turn right, action forward, Move forward, pickup/putdown, reproduce, communication outward
		numberOfDifferentEntities = 8;
		numberOfDifferentTools = 1;
		dimensionsPerRaycast = 3 + numberOfDifferentEntities;
		dimensionsBeforeRaycast = 2 + numberOfDifferentTools + numberOfDifferentEntities - 2;
		totalNetworkInputs = dimensionsBeforeRaycast + rayCastCount * dimensionsPerRaycast;
		totalNetworkOutputs = 7;
		
		
		durability = Integer.parseInt(properties.getProperty("ACTOR_DURABILITY"));
		maxDurability = durability;
		
		currentDirection = (int)(r.nextFloat()*4);
		this.counter = counter;
		
		if (inheritedGenome != null) {
			genome = inheritedGenome;
			if (Math.random() < 0.0005) {genome.visualize(new Random(), "newActor");}
		} else {
			genome = new Genome(counter);
			
			for (int i=0; i<totalNetworkInputs; i++) {
				genome.addNode(TYPE.INPUT, 0);
			}
			for (int i=0; i<totalNetworkOutputs; i++) {
				genome.addNode(TYPE.OUTPUT, 1);
			}
			
//			for (int i=0; i<totalNetworkOutputs; i++) {
//				if (i != 4) {
//					for (int j=0; j<totalNetworkInputs; j++) {
//						if ((j < 2 || j>= 2 + numberOfDifferentEntities - 2) && j<numberOfDifferentEntities + dimensionsPerRaycast) {
//							ConnectionGene newConn = new ConnectionGene(j, i+totalNetworkInputs, (float) r.nextGaussian(), 0.1f, true, -1);
//							genome.addNewConnection(newConn);
//						}
//					}
//				}
//			}
			
			for (int i=0; i<totalNetworkOutputs; i++) {
				for (int j=0; j<totalNetworkInputs; j++) {
					ConnectionGene newConn = new ConnectionGene(j, i+totalNetworkInputs, (float) r.nextGaussian(), 0.1f, true, -1);
					genome.addNewConnection(newConn);
				}
			}
			if (Math.random() < 0.0005) {genome.visualize(new Random(), "newActor");}

		}
		
		killedByActor = false;
	}

	/** 
	 * 
	 */
	
	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		damageEntity(actor.getToolLevel());
		
		if (isDestroyed()) {
			killedByActor = true;
			interpreter.addToConvertQueue(getPos());
		}
	}

	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		Entity[] entityRayCasts = getRayCasts(env);
		
		float[] inputs = new float[totalNetworkInputs];
		for (int i=0; i<entityRayCasts.length;i++) {
			Entity entity = entityRayCasts[i];
			
			if (entity != null) {
				int entityIndex = 0;
				inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 0] = (float)entity.durability/(float)entity.maxDurability;
				if (entity instanceof Actor) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 1] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 1;
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 2] = ((Actor)entity).communicationOut;
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 3] = genome.calculateGeneticDistance(((Actor)entity).genome);
				} else if (entity instanceof Wheat) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 4] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 4;
				} else if (entity instanceof WheatGrain) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 5] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 5;
				} else if (entity instanceof Water) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 6] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 6;
				} else if (entity instanceof Meat) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 7] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 7;
				} else if (entity instanceof Tree) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 8] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 8;
				} else if (entity instanceof Wood) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 9] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 9;
				} else if (entity instanceof WoodTool) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 10] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 10;
				}
				
				inputs[entityIndex] /= entity.calcDist(pos);
			}
		}
		
		Resource entity = heldResource;
		if (entity != null) {
			if (entity instanceof Wheat) {
				inputs[2 + 0] = 1;
			} else if (entity instanceof WheatGrain) {
				inputs[2 + 1] = 1;
			} else if (entity instanceof Meat) {
				inputs[2 + 2] = 1;
			} else if (entity instanceof Tree) {
				inputs[2 + 3] = 1;
			} else if (entity instanceof Wood) {
				inputs[2 + 4] = 1;
			}
		}
		
		Tool tool = equippedTool;
		
		if (tool != null) {
			if (tool instanceof WoodTool) {
				inputs[dimensionsBeforeRaycast - numberOfDifferentTools + 0] = equippedTool.getUsesLeft() / equippedTool.getMaxUsesLeft();
			}
		}
		
		
		inputs[1] = durability/maxDurability;
		inputs[0] = 1;
		
		float[] outputs = genome.feedForward(inputs);
		communicationOut = outputs[6];
		outputs[6] = 0;
		float maxVal = outputs[0];
		
		float actionOutputs[] = Arrays.copyOfRange(outputs, 0, 6);
		float softmaxOutputs[] = Genome.calculateSoftmax(actionOutputs);
//		System.out.println(Arrays.toString(softmaxOutputs));
		float rand = r.nextFloat();
		int randomWeightedIndex = 0;
		for (int i=0; i<softmaxOutputs.length; i++) {
			if (rand < softmaxOutputs[i]) {
				randomWeightedIndex = i;
				break;
			} else {
				rand -= softmaxOutputs[i];
			}
		}
//		System.out.println(randomWeightedIndex);
		
//		for (int i=1; i<actionOutputs.length-1; i++) {
//			
//		}
		
		if (randomWeightedIndex == 0) {
			currentDirection += 1;
			currentDirection = currentDirection % 4;
		} else if (randomWeightedIndex == 1) {
			currentDirection -= 1;
			if (currentDirection < 0) {
				currentDirection = 3;
			}
			currentDirection = currentDirection % 4;
		} else if (randomWeightedIndex == 2) {
			moveForward(interpreter);
		} else if (randomWeightedIndex == 3) {
			actionForward(interpreter);
		} else if (randomWeightedIndex == 4) {
			if (heldResource == null) {
				pickupForward(interpreter);
			} else {
				placeForward(interpreter);
			}
		} else if (randomWeightedIndex == 5) {
			matingPartner = null;
			for (Entity entity1 : entityRayCasts) {
				if (entity1 != null && entity1 instanceof Actor && entity1.calcDist(pos) <= 1) {
					matingPartner = (Actor)entity1;
					break;
				}
			}
			boolean spawned = false;
			for (int x=this.getPos()[0]-1; x<=this.getPos()[0]+1; x++) {
				for (int y=this.getPos()[1]-1; y<=this.getPos()[1]+1; y++) {
					if (!spawned && env.isValidPosition(x, y) && !(this.getPos()[0] == x && this.getPos()[1] == y) && env.getEntity(x, y) == null) {
						interpreter.addToSpawnQueue(getPos(), new int[] {x,y});
						spawned = true;
					}
				}
			}
		}
		
		damageEntity(1);
		
		age += 1;
		if (age >= maxAge) {
			durability = 0;
			
		}
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
	}
	
	private void moveForward(Interpreter interpreter) {
		if (currentDirection == 0) {
			interpreter.addToMoveQueue(getPos(), new int[] {getPos()[0] + 1,getPos()[1]});
		} else if (currentDirection == 1) {
			interpreter.addToMoveQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
		} else if (currentDirection == 2) {
			interpreter.addToMoveQueue(getPos(), new int[] {getPos()[0] - 1,getPos()[1]});
		} else if (currentDirection == 3) {
			interpreter.addToMoveQueue(getPos(), new int[] {getPos()[0],getPos()[1] - 1});
		}
	}
	
	private void actionForward(Interpreter interpreter) {
		if (currentDirection == 0) {
			interpreter.addToActionQueue(getPos(), new int[] {getPos()[0] + 1,getPos()[1]});
		} else if (currentDirection == 1) {
			interpreter.addToActionQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
		} else if (currentDirection == 2) {
			interpreter.addToActionQueue(getPos(), new int[] {getPos()[0] - 1,getPos()[1]});
		} else if (currentDirection == 3) {
			interpreter.addToActionQueue(getPos(), new int[] {getPos()[0],getPos()[1] - 1});
		}
	}
	
	private void pickupForward(Interpreter interpreter) {
		if (currentDirection == 0) {
			interpreter.addToPickupQueue(getPos(), new int[] {getPos()[0] + 1,getPos()[1]});
		} else if (currentDirection == 1) {
			interpreter.addToPickupQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
		} else if (currentDirection == 2) {
			interpreter.addToPickupQueue(getPos(), new int[] {getPos()[0] - 1,getPos()[1]});
		} else if (currentDirection == 3) {
			interpreter.addToPickupQueue(getPos(), new int[] {getPos()[0],getPos()[1] - 1});
		}
	}
	
	private void placeForward(Interpreter interpreter) {
		if (currentDirection == 0) {
			interpreter.addToPlaceQueue(getPos(), new int[] {getPos()[0] + 1,getPos()[1]});
		} else if (currentDirection == 1) {
			interpreter.addToPlaceQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
		} else if (currentDirection == 2) {
			interpreter.addToPlaceQueue(getPos(), new int[] {getPos()[0] - 1,getPos()[1]});
		} else if (currentDirection == 3) {
			interpreter.addToPlaceQueue(getPos(), new int[] {getPos()[0],getPos()[1] - 1});
		}
	}

	@Override
	public Entity getNewChild(int x, int y) {

		if (durability > 200 && matingPartner != null) {
			Genome newGenome;
			newGenome = genome.crossOverAndMutate(matingPartner.genome, r);
			
			Actor newActor = new Actor(properties, new Point(0,0), counter, newGenome, r);
			
			// Mutate and crossover the view radius and step size.
			float viewStepSizePrime = (viewRadiusStepSize + matingPartner.viewRadiusStepSize) / 2f;
			viewStepSizePrime *= Math.exp(0.5*r.nextGaussian() + 0.5*r.nextGaussian());
			if (viewStepSizePrime < 0.01) viewStepSizePrime = 0.01f;
			
			float viewRadiusPrime = (viewRadius + matingPartner.viewRadius) / 2f;
			viewRadiusPrime += viewStepSizePrime*r.nextGaussian();
			if(viewRadiusPrime < 1) {
				viewRadiusPrime = 1;
			} else if (viewRadiusPrime > 10) {
				viewRadiusPrime = 10;
			}
			
			newActor.viewRadius = viewRadiusPrime;
			newActor.viewRadiusStepSize = viewStepSizePrime;
			
			// Mutate and crossover the view fov and step size.
			float fovStepSizePrime = (fovStepSize + matingPartner.fovStepSize) / 2f;
			fovStepSizePrime *= Math.exp(0.5*r.nextGaussian() + 0.5*r.nextGaussian());
			if (fovStepSizePrime < 0.01) fovStepSizePrime = 0.01f;
			
			float fovPrime = (fov + matingPartner.fov) / 2f;
			fovPrime += fovStepSizePrime*r.nextGaussian();
			if(fovPrime < 0) {
				fovPrime = 0;
			} else if (fovPrime > 2*Math.PI) {
				fovPrime = (float) (2*Math.PI);
			}
			
			newActor.fov = fovPrime;
			newActor.fovStepSize = fovStepSizePrime;
			
			damageEntity(200);
			newActor.setDurability(50);
			
			return newActor;
		} else {
			return null;
		}
	}
	
//	public float getViewRadius() {return viewRadius;}
//	
//	public float getViewRadiusSS() {return viewRadiusStepSize;}

	@Override
	public Entity getNewConvert() {
		if (!killedByActor) {
			return null;
		} else {
			return new Meat(properties, pos, r);
		}
		
	}
	
	public void holdResource(Resource resource) {
		heldResource = resource;
	}
	
	public Resource getHeldResource() {
		return heldResource;
	}
	
	protected void setDurability(int d) {
		durability = d;
	}
	
	public void equipTool(Tool tool) {
		equippedTool = tool;
	}
	
	public int getToolUsesLeft() {
		if (equippedTool != null) { 
			return equippedTool.getUsesLeft();
		} else {
			return 0;
		}
	}

	public int getToolLevel() {
		if (equippedTool != null) { 
			return equippedTool.getToolLevel();
		} else {
			return 10;
		}
	}
	
	public void useTool() {
		if (equippedTool != null) { 
			equippedTool.useTool();
			if (equippedTool.getUsesLeft() == 0) {
				equippedTool = null;
			}
		}
	}
	
	private float ccw(float[] A, float[] B, float[] C) {
		return ((B[0]-A[0])*(C[1]-A[1]) - (B[1]-A[1])*(C[0]-A[0]));
	}
	
	public boolean isIntersectingAngle(float angle, float targetPos[]) {
		float[] actorCenter = new float[] {(float) (pos.getX() + 0.5), (float) (pos.getY() + 0.5)};
		float[] projectedPos = new float[] {(float) (viewRadius*Math.cos(angle) + actorCenter[0]), (float) (viewRadius*Math.sin(angle) + actorCenter[1])};
		
		float[] topLeft = new float[] {targetPos[0], targetPos[1] + 1};
		float[] topRight = new float[] {targetPos[0] + 1, targetPos[1] + 1};
		float[] bottomLeft = new float[] {targetPos[0], targetPos[1]};
		float[] bottomRight = new float[] {targetPos[0] + 1, targetPos[1]};
	
		if ((ccw(actorCenter,projectedPos,topLeft) * ccw(actorCenter,projectedPos,topRight) < 0) && 
				(ccw(actorCenter,topLeft,topRight) * ccw(projectedPos,topLeft,topRight)) < 0) {
			return true;
		} else if ((ccw(actorCenter,projectedPos,topLeft) * ccw(actorCenter,projectedPos,bottomLeft) < 0) && 
				(ccw(actorCenter,topLeft,bottomLeft) * ccw(projectedPos,topLeft,bottomLeft)) < 0) {
			return true;
		} else if ((ccw(actorCenter,projectedPos,topRight) * ccw(actorCenter,projectedPos,bottomRight) < 0) && 
				(ccw(actorCenter,topRight,bottomRight) * ccw(projectedPos,topRight,bottomRight)) < 0) {
			return true;
		} else if ((ccw(actorCenter,projectedPos,bottomLeft) * ccw(actorCenter,projectedPos,bottomRight) < 0) && 
				(ccw(actorCenter,bottomLeft,bottomRight) * ccw(projectedPos,bottomLeft,bottomRight)) < 0) {
			return true;
		} else {
			return false;
		}
		
	}

	public Entity[] getRayCasts(Environment env) {
		
		Entity[] rayCasts = new Entity[rayCastCount];
		
		Entity[] withinRange = env.getEntitiesWithinRange(Entity.copyFromIntArray(getPos()), (float)viewRadius);
		
		for(int ray=0; ray<rayCastCount;ray++) {
			float angle;
			if (ray % 2 == 0) {
				angle = (float) (-Math.ceil(ray/2f)*fov/(rayCastCount-1) + currentDirection*Math.PI/2);
			} else {
				angle = (float) (Math.ceil(ray/2f)*fov/(rayCastCount-1) + currentDirection*Math.PI/2);
			}

			Entity closest = null;
			float closestDist = viewRadius + 1;
			
			for (int pTargetEntity=0; pTargetEntity<withinRange.length;pTargetEntity++) {
				if (withinRange[pTargetEntity] != null && !Arrays.equals(getPos(), withinRange[pTargetEntity].getPos()) && isIntersectingAngle(angle, Entity.copyFromIntArray(withinRange[pTargetEntity].getPos()))) {

					if (getPoint().distance(withinRange[pTargetEntity].getPoint()) < closestDist) {
						closest = withinRange[pTargetEntity];
						closestDist = (float) getPoint().distance(withinRange[pTargetEntity].getPoint());
					}
				} 
			}
			if (closest != null) {
				rayCasts[ray] = closest;
			}
		}
		
		return rayCasts;
	}
	
	public int getDirection() {
		return currentDirection;
	}
}
