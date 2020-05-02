package entity;

import java.awt.Point;
import java.util.Properties;

import interpreter.Interpreter;

public class Actor extends Entity {
	
	private Resource heldResource;
	
//	private Tool equippedTool;
	
	private boolean state;

	public Actor(Properties properties, Point newPos) {
		super(properties, newPos);
		
	}

	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		damageEntity(1);
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
	}

	@Override
	public void onStep(Interpreter interpreter) {
		// TODO Auto-generated method stub
		if (!state) {
			interpreter.addToMoveQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
			
		} else {
			interpreter.addToActionQueue(getPos(), new int[] {getPos()[0],getPos()[1] + 1});
		}
		
		state = !state;
		
		if (isDestroyed()) {
			interpreter.addToConvertQueue(getPos());
		}
	}


	@Override
	public Entity getNewChild(int x, int y) {
		// TODO Auto-generated method stub
		return null;
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
//	public int getToolLevel() {
//		if (equippedTool != null) { 
//			return equippedTool.getToolLevel();
//		} else {
//			return 1;
//		}
//	}
//	
//	public void useTool() {
//		if (equippedTool != null) { 
//			equippedTool.useTool();
//			if (equippedTool.getUsesLeft() == 0) {
//				equippedTool = null;
//			}
//		}
//	}

}
