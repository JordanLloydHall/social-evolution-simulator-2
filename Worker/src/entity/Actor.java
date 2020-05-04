package entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;
import java.util.Properties;

import environment.Environment;
import interpreter.Interpreter;

public class Actor extends Entity {
	
	private Resource heldResource;
	
	private Entity equippedTool;
	
	private boolean state;
	
	private double viewRadius;
	private int rayCastCount;
	
	private double fov;
	private int currentDirection;
	
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

	public Actor(Properties properties, Point newPos) {
		super(properties, newPos);
		viewRadius = Double.parseDouble(properties.getProperty("ACTOR_VIEW_RADIUS"));
		rayCastCount = Integer.parseInt(properties.getProperty("ACTOR_VIEW_CASTS"));
		durability = Integer.parseInt(properties.getProperty("ACTOR_DURABILITY"));
		
		fov = Math.PI/2;
		currentDirection = 1;
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		damageEntity(actor.getToolLevel());
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
	}

	@Override
	public void onStep(Interpreter interpreter, Environment env) {
		// TODO Auto-generated method stub
//		if (!state) {
//			interpreter.addToMoveQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
//			
//		} else {
//			interpreter.addToActionQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
//		}
//		
//		state = !state;
		
		Entity[] entityRayCasts = getRayCasts(env);
		
		boolean left = false;
		boolean right = false;
		
		for (int i=0; i<entityRayCasts.length; i++) {
			if (entityRayCasts[(int)(i)] instanceof Wheat) {
				if (i < (int)(entityRayCasts.length/2)) {
					left = true;
				} else {
					right = true;
				}
			}
		}
		
		
		
		if (entityRayCasts[(int)(entityRayCasts.length/2)] instanceof Wheat || entityRayCasts[(int)(entityRayCasts.length/2)] instanceof WheatGrain) {
			if (durability <= 120 && entityRayCasts[(int)(entityRayCasts.length/2)].getPoint().distance(getPoint()) <= 1) {
				actionForward(interpreter);
			} else {
				moveForward(interpreter);
			}
			
		} else if (left) {
			
			currentDirection += 1;
			currentDirection = currentDirection % 4;
			moveForward(interpreter);
			
		} else if (right) {
			
			currentDirection -= 1;
			if (currentDirection < 0) {
				currentDirection = 3;
			}
			currentDirection = currentDirection % 4;
			moveForward(interpreter);
			
		} else {
			currentDirection = (int)(Math.random()*4);
			moveForward(interpreter);
			if (viewRadius < 5) viewRadius += 2;
		}
		
		if (viewRadius > 5) {
			viewRadius -= 1;
		}
		
		if (durability >= 120 && Math.random() < 0.1) {
			interpreter.addToSpawnQueue(getPos());
			damageEntity(20);
		}
		
		damageEntity(1);
		
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

	@Override
	public Entity getNewChild(int x, int y) {
		return new Actor(properties, new Point(0,0));
	}

	@Override
	public Entity getNewConvert() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void holdResource(Resource resource) {
		heldResource = resource;
	}
	
	public Resource getHeldResource() {
		return heldResource;
	}
	
//	public void equipTool(Tool tool) {
//		equippedTool = tool;
//	}
//	
//	public int getToolUsesLeft() {
//		if (equippedTool != null) { 
//			return equippedTool.getUsesLeft();
//		} else {
//			return 0;
//		}
//	}
//	
	public int getToolLevel() {
		if (equippedTool != null) { 
//			return equippedTool.getToolLevel();
		} else {
//			return 1;
		}
		return 1;
	}
//	
//	public void useTool() {
//		if (equippedTool != null) { 
//			equippedTool.useTool();
//			if (equippedTool.getUsesLeft() == 0) {
//				equippedTool = null;
//			}
//		}
//	}
	
	private double ccw(double[] A, double[] B, double[] C) {
		return ((B[0]-A[0])*(C[1]-A[1]) - (B[1]-A[1])*(C[0]-A[0]));
	}
	
	public boolean isIntersectingAngle(double angle, double targetPos[]) {
		double[] actorCenter = new double[] {pos.getX() + 0.5, pos.getY() + 0.5};
		double[] projectedPos = new double[] {viewRadius*Math.cos(angle) + actorCenter[0], viewRadius*Math.sin(angle) + actorCenter[1]};
		
		double[] topLeft = new double[] {targetPos[0], targetPos[1] + 1};
		double[] topRight = new double[] {targetPos[0] + 1, targetPos[1] + 1};
		double[] bottomLeft = new double[] {targetPos[0], targetPos[1]};
		double[] bottomRight = new double[] {targetPos[0] + 1, targetPos[1]};
	
		if ((ccw(actorCenter,projectedPos,topLeft) * ccw(actorCenter,projectedPos,topRight) < 0) && 
				(ccw(actorCenter,topRight,actorCenter) * ccw(topLeft,bottomLeft,projectedPos)) < 0) {
			return true;
		} else if ((ccw(actorCenter,projectedPos,bottomRight) * ccw(actorCenter,projectedPos,topRight) < 0) && 
				(ccw(bottomRight,topRight,actorCenter) * ccw(bottomRight,topRight,projectedPos)) < 0) {
			return true;
		} else if ((ccw(actorCenter,projectedPos,topLeft) * ccw(actorCenter,projectedPos,topRight) < 0) && 
				(ccw(topLeft,topRight,actorCenter) * ccw(topLeft,topRight,projectedPos)) < 0) {
			return true;
		} else if ((ccw(actorCenter,projectedPos,bottomRight) * ccw(actorCenter,projectedPos,bottomLeft) < 0) && 
				(ccw(bottomRight,bottomLeft,actorCenter) * ccw(bottomRight,bottomLeft,projectedPos)) < 0) {
			return true;
		} else {
			return false;
		}
		
	}

	public Entity[] getRayCasts(Environment env) {
		
		Entity[] rayCasts = new Entity[rayCastCount];
		
		Entity[] withinRange = env.getEntitiesWithinRange(Entity.copyFromIntArray(getPos()), (double)viewRadius);
		
		for(int ray=0; ray<rayCastCount;ray++) {
			double angle = Math.PI/2 - ray*Math.PI/rayCastCount + currentDirection*Math.PI/2;
			
			Entity closest = null;
			double closestDist = viewRadius + 1;
			
			for (int pTargetEntity=0; pTargetEntity<withinRange.length;pTargetEntity++) {
				if (!Arrays.equals(getPos(), withinRange[pTargetEntity].getPos()) && isIntersectingAngle(angle, Entity.copyFromIntArray(withinRange[pTargetEntity].getPos()))) {

					if (getPoint().distance(withinRange[pTargetEntity].getPoint()) < closestDist) {
						closest = withinRange[pTargetEntity];
						closestDist = getPoint().distance(withinRange[pTargetEntity].getPoint());
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
