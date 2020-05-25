package neat;

public class ConnectionGene {
	private int inNode;
	private int outNode;
	private float weight;
	private boolean expressed;
	private int innovation;
	private float stepSize;
	
	public ConnectionGene(int inNode, int outNode, float weight, float stepSize, boolean expressed, int innovation) {
		super();
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
		this.expressed = expressed;
		this.innovation = innovation;
		this.stepSize = stepSize;
	}

	public int getInNode() {
		return inNode;
	}

	public int getOutNode() {
		return outNode;
	}

	public float getWeight() {
		return weight;
	}

	public boolean isExpressed() {
		return expressed;
	}

	public int getInnovation() {
		return innovation;
	}
	
	public void disable() {
		expressed = false;
	}
	
	public void enable() {
		expressed = true;
	}
	
	public void setInnovation(int innov) {
		innovation = innov;
	}
	
	public void setWeight(float w) {
		weight = w;
	}
	
	public void setStepSize(float s) {
		stepSize = s;
	}
	public float getStepSize() {
		return stepSize;
	}

	
	public ConnectionGene copy() {
		return new ConnectionGene(inNode, outNode, weight, stepSize, expressed, innovation);
	}
	
	

}
