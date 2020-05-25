package neat;

public class NodeGene {
	public enum TYPE {
		INPUT,
		OUTPUT,
		HIDDEN;
	}
	
	private TYPE type;
	private int id;
	private float output;
	private float lastOutput;
	private double level;
	
	public NodeGene(TYPE type, int id, double level) {
		super();
		this.type = type;
		this.id = id;
		this.level = level;
		output = 0;
		lastOutput = 0;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public int getId() {
		return id;
	}
	
	public void setOutput(float val) {
		output = val;
	}
	
	public float getOutput() {
		return output;
	}
	
	public void setLastOutput(float val) {
		lastOutput = val;
	}
	
	public float getLastOutput() {
		return lastOutput;
	}
	
	public double getLevel() {
		return level;
	}
	
	public void setLevel(double l) {
		level = l;
	}
}
