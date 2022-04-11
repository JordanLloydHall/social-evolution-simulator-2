package main.java.entity;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

	public float viewRadius;
	private float viewRadiusStepSize;
	public float fov;
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
	
	public int age;
	public boolean asexual;
	
	public float size;
	private float sizeStepSize;
	
	public float speed;
	private float speedStepSize;
	
	public float gestationCost;
	private float gestationCostStepSize;
	
	public int numActorsSeen;
	public float totalGeneticDistance;
	
	public Genome genome;
	public Genome speciesGenome;
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

	public Actor(Properties properties, Point newPos, Counter counter, Genome inheritedGenome, Random r, Genome speciesGenome) {
		super(properties, newPos, r);
		
		age = 0;
		
		viewRadius = r.nextFloat()*10+1;
		viewRadiusStepSize = 1f;
		fov = 2f*(float)Math.PI*r.nextFloat();
		if(fov < Math.PI/2f) {
			fov = (float) (Math.PI/2f);
		}
		fovStepSize = 0.1f;
		
		size = r.nextFloat()*2+1;
		sizeStepSize = 0.5f;
		
		speed = r.nextFloat()*2+1;
		speedStepSize = 0.5f;
		
		gestationCost = r.nextInt(20)+50;
		gestationCostStepSize = 1f;
		
		
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
		// OUTPUTS : turn left, turn right, action forward, Move forward, pickup/putdown, asexually reproduce, sexually reproduce, communication outward
		numberOfDifferentEntities = 9;
		numberOfDifferentTools = 1;
		dimensionsPerRaycast = 3 + numberOfDifferentEntities;
		dimensionsBeforeRaycast = 2 + numberOfDifferentTools + numberOfDifferentEntities - 2;
		totalNetworkInputs = dimensionsBeforeRaycast + rayCastCount * dimensionsPerRaycast;
		totalNetworkOutputs = 7;
		
		
		durability = Integer.parseInt(properties.getProperty("ACTOR_DURABILITY"));
		maxDurability = durability;
		
		currentDirection = r.nextInt(4);
//		currentDirection = 3;
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
//				for (int j=0; j<totalNetworkInputs; j++) {
//					ConnectionGene newConn = new ConnectionGene(j, i+totalNetworkInputs, (float) r.nextGaussian(), 0.1f, true, -1);
//					genome.addNewConnection(newConn);
//				}
//			}
			
			for (int i=0; i<5+rayCastCount*2; i++) {
				genome.addNode(TYPE.HIDDEN, 0.5f);
			}
			
			for (int i=totalNetworkOutputs; i<totalNetworkOutputs+5; i++) {
				for (int j=0; j<dimensionsBeforeRaycast; j++) {
					ConnectionGene newConn = new ConnectionGene(j, i+totalNetworkInputs, (float) r.nextGaussian(), 0.1f, true, -1);
					genome.addNewConnection(newConn);
				}
			}
			
			for (int i=totalNetworkInputs; i<totalNetworkInputs+totalNetworkOutputs; i++) {
				for (int j=totalNetworkInputs+totalNetworkOutputs; j<totalNetworkInputs+totalNetworkOutputs+5; j++) {
					ConnectionGene newConn = new ConnectionGene(j, i, (float) r.nextGaussian(), 0.1f, true, -1);
					genome.addNewConnection(newConn);
				}
			}
			
			for (int i=0; i<2; i++) {
				for (int ray=0; ray<rayCastCount; ray++) {
				
					for (int j=dimensionsBeforeRaycast+ray*dimensionsPerRaycast; j<dimensionsBeforeRaycast+(ray+1)*dimensionsPerRaycast; j++) {
						ConnectionGene newConn = new ConnectionGene(j, totalNetworkInputs+totalNetworkOutputs+5+ray*2+i, (float) r.nextGaussian(), 0.1f, true, -1);
						genome.addNewConnection(newConn);
				
					}
				}
			}
			
			for (int i=0; i<2; i++) {
				for (int ray=0; ray<rayCastCount; ray++) {
				
					for (int j=totalNetworkInputs; j<totalNetworkInputs+totalNetworkOutputs; j++) {
						ConnectionGene newConn = new ConnectionGene(totalNetworkInputs+totalNetworkOutputs+5+ray*2+i, j, (float) r.nextGaussian(), 0.1f, true, -1);
						genome.addNewConnection(newConn);
				
					}
				}
			}
			
//			for (int i=0;i<50;i++) {
//				int inNode = r.nextInt(totalNetworkInputs);
//				int outNode = totalNetworkInputs + r.nextInt(totalNetworkOutputs);
//				ConnectionGene newConn = new ConnectionGene(inNode, outNode, (float) r.nextGaussian(), 1f, true, -1);
//				genome.addNewConnection(newConn);
//			}
			
			
			
			if (Math.random() < 0.0005) {genome.visualize(new Random(), "newActor");}
			
		}
		
		this.speciesGenome = counter.speciate(genome, speciesGenome);
		
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
		
		numActorsSeen = 0;
		totalGeneticDistance = 0;
		
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
					numActorsSeen += 1;
					totalGeneticDistance += inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 3];
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
				} else if (entity instanceof Egg) {
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 11] = 1;
					entityIndex = dimensionsBeforeRaycast+i*dimensionsPerRaycast + 11;
					inputs[dimensionsBeforeRaycast+i*dimensionsPerRaycast + 3] = genome.calculateGeneticDistance(((Egg)entity).getGenome());
				}
				
				inputs[entityIndex] /= entity.calcDist(pos);
			}
		}
		
		Resource entity = heldResource;
		if (entity != null) {
			if (entity instanceof Wheat) {
				inputs[2 + numberOfDifferentTools + 0] = 1;
			} else if (entity instanceof WheatGrain) {
				inputs[2 + numberOfDifferentTools + 1] = 1;
			} else if (entity instanceof Meat) {
				inputs[2 + numberOfDifferentTools + 2] = 1;
			} else if (entity instanceof Tree) {
				inputs[2 + numberOfDifferentTools + 3] = 1;
			} else if (entity instanceof Wood) {
				inputs[2 + numberOfDifferentTools + 4] = 1;
			} else if (entity instanceof Egg) {
				inputs[2 + numberOfDifferentTools + 5] = 1;
			}
		}
		
		Tool tool = equippedTool;
		
		if (tool != null) {
			if (tool instanceof WoodTool) {
				inputs[2 + 0] = equippedTool.getUsesLeft() / equippedTool.getMaxUsesLeft();
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
		float rand = ThreadLocalRandom.current().nextFloat();
		int randomWeightedIndex = 0;
		for (int i=0; i<softmaxOutputs.length; i++) {
			if (rand < softmaxOutputs[i]) {
				randomWeightedIndex = i;
				break;
			} else {
				rand -= softmaxOutputs[i];
			}
		}
		
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
			
//			moveForward(interpreter);
			float speedFound = (int)speed;
			boolean positionFound = false;
			while (speedFound >= 1 && !positionFound) {
				int[] newPos = new int[] {(int) Math.round(getPos()[0] + speedFound*Math.cos(currentDirection*Math.PI/2f)),(int) Math.round(getPos()[1] + speedFound*Math.sin(currentDirection*Math.PI/2f))};
				if (env.getEntity(newPos[0], newPos[1]) == null) {
					interpreter.addToMoveQueue(getPos(), newPos);
					damageEntity((int)Math.round(Math.pow(speedFound,2)));
					positionFound = true;
				} else {
					speedFound -= 1;
				}
			}
			
			
			
		} else if (randomWeightedIndex == 3) {
//			actionForward(interpreter);
			for (Entity e : entityRayCasts) {
				if (e != null && e.calcDist(pos) <= size) {
					interpreter.addToActionQueue(getPos(), e.getPos());
					break;
				}
			}
		} else if (randomWeightedIndex == 4) {
			if (heldResource == null) {
				pickupForward(interpreter);
				for (Entity e : entityRayCasts) {
					if (e != null && !(e instanceof Resource) && e.calcDist(pos) <= size) {
						interpreter.addToPickupQueue(getPos(), e.getPos());
						break;
					}
				}
			} else {
				placeForward(interpreter);
				for (Entity e : entityRayCasts) {
					if (e != null && !(e instanceof Resource) && e.calcDist(pos) <= size) {
						interpreter.addToPlaceQueue(getPos(), e.getPos());
						break;
					}
				}
			}
		} else if (randomWeightedIndex == 5 && durability > 4*gestationCost) {
			matingPartner = null;
			for (Entity entity1 : entityRayCasts) {
				if (entity1 != null && entity1 instanceof Actor && genome.calculateGeneticDistance(((Actor)entity1).genome) <= 0.1) {
					matingPartner = (Actor)entity1;
					break;
				}
			}
			if (matingPartner == null) {
				matingPartner = this;
			}
			boolean spawned = false;
			for (int x=this.getPos()[0]-1; x<=this.getPos()[0]+1; x++) {
				for (int y=this.getPos()[1]-1; y<=this.getPos()[1]+1; y++) {
					if (!spawned && env.isValidPosition(x, y) && !(this.getPos()[0] == x && this.getPos()[1] == y) && env.getEntity(x, y) == null) {
						interpreter.addToSpawnQueue(getPos(), new int[] {x,y});
						spawned = true;
						asexual = false;
					}
				}
			}
			if (matingPartner == this) {
				asexual = true;
			}
		}
		
		damageEntity((int)Math.round(Math.pow(size,2) + Math.floor(Math.max(0,(genome.getNumberOfConnections()-400f)/100f))));
		
		age += 1;
		if (age >= maxAge) {
			durability = 0;
			destroyed = true;
			interpreter.addToConvertQueue(getPos());
			
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
		
		for (int i=0; i<(int)size; i++) {
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

		if (durability > 250 && matingPartner != null) {
			Genome newGenome;
			newGenome = genome.crossOverAndMutate(matingPartner.genome, r);
			
			Actor newActor = new Actor(properties, new Point(0,0), counter, newGenome, r, speciesGenome);
			
			// Mutate and crossover the view radius and step size.
			float viewStepSizePrime = viewRadiusStepSize;
			if (r.nextFloat() > 0.5) {
				viewStepSizePrime = matingPartner.viewRadiusStepSize;
			}
			viewStepSizePrime *= Math.exp(0.001*r.nextGaussian() + 0.001*r.nextGaussian());
			if (viewStepSizePrime < 0.01) viewStepSizePrime = 0.01f;
			
			float viewRadiusPrime = viewRadius;
			if (r.nextFloat() > 0.5) {
				viewRadiusPrime = matingPartner.viewRadius;
			}
			
			viewRadiusPrime += viewStepSizePrime*r.nextGaussian();
			if(viewRadiusPrime < 3) {
				viewRadiusPrime = 3;
			} else if (viewRadiusPrime > 10) {
				viewRadiusPrime = 10;
			}
			
			newActor.viewRadius = viewRadiusPrime;
			newActor.viewRadiusStepSize = viewStepSizePrime;
			
			// Mutate and crossover the view fov and step size.
			float fovStepSizePrime = fovStepSize;
			if (r.nextFloat() > 0.5) {
				fovStepSize = matingPartner.fovStepSize;
			}
			fovStepSizePrime *= Math.exp(0.001*r.nextGaussian() + 0.001*r.nextGaussian());
			if (fovStepSizePrime < 0.01) fovStepSizePrime = 0.01f;
			
			float fovPrime = fov;
			if (r.nextFloat() > 0.5) {
				fovPrime = matingPartner.fov;
			}
			fovPrime += fovStepSizePrime*r.nextGaussian();
			if(fovPrime < Math.PI/2f) {
				fovPrime = (float) (Math.PI/2f);
			} else if (fovPrime > 2*Math.PI) {
				fovPrime = (float) (2*Math.PI);
			}
			
			newActor.fov = fovPrime;
			newActor.fovStepSize = fovStepSizePrime;
			
			
			// Mutate and crossover the view radius and step size.
//			float sizeStepPrime = sizeStepSize;
//			if (r.nextFloat() > 0.5) {
//				sizeStepPrime = matingPartner.sizeStepSize;
//			}
//			sizeStepPrime *= Math.exp(0.001*r.nextGaussian() + 0.001*r.nextGaussian());
//			if (sizeStepPrime < 0.01) sizeStepPrime = 0.01f;
			
//			float sizePrime = size;
//			if (r.nextFloat() > 0.5) {
//				sizePrime = matingPartner.size;
//			}
			
//			sizePrime += sizeStepPrime*r.nextGaussian();
//			if(sizePrime < 1) {
//				sizePrime = 1;
//			}
			
			newActor.size = 1;
			newActor.sizeStepSize = 0;
			
			
			// Mutate and crossover the view radius and step size.
//			float speedStepPrime = speedStepSize;
//			if (r.nextFloat() > 0.5) {
//				speedStepPrime = matingPartner.speedStepSize;
//			}
//			speedStepPrime *= Math.exp(0.001*r.nextGaussian() + 0.001*r.nextGaussian());
//			if (speedStepPrime < 0.01) speedStepPrime = 0.01f;
//			
//			float speedPrime = speed;
//			if (r.nextFloat() > 0.5) {
//				speedPrime = matingPartner.speed;
//			}
//			
//			speedPrime += speedStepPrime*r.nextGaussian();
//			if(speedPrime < 1) {
//				speedPrime = 1;
//			}
			
			newActor.speed = 1;
			newActor.speedStepSize = 0;
			
			// Mutate and crossover the view radius and step size.
			float gcStepPrime = gestationCostStepSize;
			if (r.nextFloat() > 0.5) {
				gcStepPrime = matingPartner.gestationCostStepSize;
			}
			gcStepPrime *= Math.exp(0.001*r.nextGaussian() + 0.001*r.nextGaussian());
			if (gcStepPrime < 0.01) gcStepPrime = 0.01f;
			
			float gcPrime = gestationCost;
			if (r.nextFloat() > 0.5) {
				gcPrime = matingPartner.gestationCost;
			}
			
			gcPrime += gcStepPrime*r.nextGaussian();
			if(gcPrime < 50) {
				gcPrime = 50;
			}
			
			newActor.gestationCost = gcPrime;
			newActor.gestationCostStepSize = gcStepPrime;
			
			
			damageEntity((int)gestationCost*4);
			newActor.setDurability((int)gestationCost);
			
			return new Egg(properties, new Point(), r, newActor, (int)gestationCost);
		} else {
			return null;
		}
	}

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
			return (int)Math.round(equippedTool.getToolLevel()*size);
		} else {
			return (int)Math.round(25*size);
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
		
		ArrayList<Entity> withinRange = env.getEntitiesWithinRange(Entity.copyFromIntArray(getPos()), (float)viewRadius);
		
		for(int ray=0; ray<rayCastCount;ray++) {
			float angle;
			if (ray % 2 == 0) {
				angle = (float) (-Math.ceil(ray/2f)*fov/(rayCastCount-1) + currentDirection*Math.PI/2);
			} else {
				angle = (float) (Math.ceil(ray/2f)*fov/(rayCastCount-1) + currentDirection*Math.PI/2);
			}
			if (angle < 0) {
				angle += (float) (2f*Math.PI);
			} else if (angle > 2f*Math.PI) {
				angle -= (float) (2f*Math.PI);
			}

			Entity closest = null;
			float closestDist = viewRadius + 1;
			
			for (int pTargetEntity=0; pTargetEntity<withinRange.size();pTargetEntity++) {
				Entity e = withinRange.get(pTargetEntity);
				if (e != null && !Arrays.equals(getPos(), e.getPos()) && isIntersectingAngle(angle, Entity.copyFromIntArray(e.getPos()))) {

					if (getPoint().distance(e.getPoint()) < closestDist) {
						closest = e;
						closestDist = (float) getPoint().distance(e.getPoint());
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
