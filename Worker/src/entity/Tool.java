package entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import interpreter.Interpreter;

public abstract class Tool extends Resource {
	
	protected int toolUses;
	protected int maxToolUses;
	protected int toolLevel;

	public Tool(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		
	}

	@Override
	public Entity getNewChild(int x, int y) {
		return null;
	}

	@Override
	public Entity getNewConvert() {
		return null;
	}
	
	public void useTool() {
		toolUses -= 1;
	}
	
	public int getUsesLeft() {
		return toolUses;
	}
	
	public int getMaxUsesLeft() {
		return maxToolUses;
	}
	
	public int getToolLevel() {
		return toolLevel;
	}
	
	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		actor.equipTool(this);
		interpreter.addToConvertQueue(getPos());
	}

}
