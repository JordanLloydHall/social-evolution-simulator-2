package main.java.entity;

import java.awt.Point;
import java.util.Properties;
import java.util.Random;

import main.java.interpreter.Interpreter;

public abstract class Tool extends Edible {
	
	protected int toolUses;
	protected int maxToolUses;
	protected int toolLevel;

	/**
	 * Describes the behaviour of edibles that the actors can consume in order to increase their tool level.
	 * @author Jordan
	 * @since 1.0
	 */
	public Tool(Properties properties, Point newPos, Random r) {
		super(properties, newPos, r);
		
	}
	
	/**
	 * Whenever a tool is used by an actor, its uses are reduced by one.
	 */
	public void useTool() {
		toolUses -= 1;
	}
	
	/**
	 * Simple getter for the number of uses of the tool are left.
	 * @return remaining tool uses.
	 */
	public int getUsesLeft() {
		return toolUses;
	}
	
	/**
	 * Simple getter for the maximum number of uses of the tool.
	 * @return the maximum tool uses alloted to the tool type.
	 */
	public int getMaxUsesLeft() {
		return maxToolUses;
	}
	
	/**
	 * Simple getter for the level of the tool (how much damage it inflicts upon entities).
	 * @return the tool level of the tool type.
	 */
	public int getToolLevel() {
		return toolLevel;
	}
	
	@Override
	public void onAction(Interpreter interpreter, Actor actor) {
		/*
		 * When an actor acts upon a tool, it equips it and the tool no longer exists as an entity within the environment.
		 * If the actor already has a tool equipped, that tool is replaced by this one, destroying the other.
		 */
		actor.equipTool(this);
		interpreter.addToConvertQueue(getPos());
	}

}
