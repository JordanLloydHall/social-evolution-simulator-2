package main.java.neat;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import main.java.neat.NodeGene.TYPE;

public class Genome {
	protected Map<Integer,ConnectionGene> connections;
	protected Map<Integer,NodeGene> nodes;
	
	private Counter counter;
	
	private int numInputs;
	private int numOutputs;
	
	private float connectionMutationProb;
	private float connectionMutationStepSize;
	private float nodeMutationProb;
	private float nodeMutationStepSize;
	
		
	public Genome(Counter counter) {
		super();
		nodes = new HashMap<Integer, NodeGene>();
		connections = new HashMap<Integer, ConnectionGene>();
		this.counter = counter;
		
		numInputs = 0;
		numOutputs = 0;
		
		connectionMutationProb = 0.05f;
		connectionMutationStepSize = 0.1f;
		nodeMutationProb = 0.03f;
		nodeMutationStepSize = 0.1f;
	}
	
	public int addNode(TYPE type, double level) {
		NodeGene newNode = new NodeGene(type, nodes.size(), level);
		nodes.put(newNode.getId(), newNode);
		
		if (type == TYPE.INPUT) {
			numInputs += 1;
		} else if (type == TYPE.OUTPUT) {
			numOutputs += 1;
		}
		return newNode.getId();
	}
	
	public int addNode(TYPE type, double level, int nodeNumber) {
		NodeGene newNode = new NodeGene(type, nodeNumber, level);
		nodes.put(newNode.getId(), newNode);
		
		if (type == TYPE.INPUT) {
			numInputs += 1;
		} else if (type == TYPE.OUTPUT) {
			numOutputs += 1;
		}
		return newNode.getId();
	}
	
	public void addNewConnection(ConnectionGene newConn) {
		int innov = counter.getInnovation(newConn.getInNode(), newConn.getOutNode());
		
		if (!connections.containsKey(innov)) {
			newConn.setInnovation(innov);
			connections.put(innov, newConn);
		}

	}
	
	public void addConnectionMutation(Random r) {
		
		Object[] genes = nodes.values().toArray();
		int node1r = r.nextInt(genes.length);
		NodeGene node1 = (NodeGene)genes[node1r];
		int node2r = r.nextInt(genes.length);
		NodeGene node2 = (NodeGene)genes[node2r];
		
		if (node1 == null || node2 == null) {
			boolean a = true;
		}
		
		while (connections.containsKey(counter.getInnovation(node1.getId(), node2.getId()))) {
			node1r = r.nextInt(genes.length);
			node1 = (NodeGene)genes[node1r];
			node2r = r.nextInt(genes.length);
			node2 = (NodeGene)genes[node2r];
			if (node1 == null || node2 == null) {
				boolean a = true;
			}
		}
		
		float stepSize = 0.1f;
		float weight = (float) (r.nextGaussian());
		ConnectionGene newConnection = new ConnectionGene(node1.getId(), node2.getId(), weight, stepSize, true, -1);
		addNewConnection(newConnection);
	}
	
	public void addNodeMutation(Random r) {
		
		Object[] genes = connections.values().toArray();
		ConnectionGene oldCon = (ConnectionGene)genes[r.nextInt(genes.length)];

		while (!oldCon.isExpressed()) {
			oldCon = (ConnectionGene)genes[r.nextInt(genes.length)];
		}
		
		
		NodeGene inNode = nodes.get(oldCon.getInNode());
		NodeGene outNode = nodes.get(oldCon.getOutNode());
		
		oldCon.disable();
		double newLevel = (inNode.getLevel() + outNode.getLevel()) / 2f;
		int newGeneId = addNode(TYPE.HIDDEN, newLevel);
		ConnectionGene newInCon = new ConnectionGene(inNode.getId(), newGeneId, 1f, 0.1f, true, -1);
		ConnectionGene newOutCon = new ConnectionGene(newGeneId, outNode.getId(), oldCon.getWeight(), oldCon.getStepSize(), true, -1);
		addNewConnection(newInCon);
		addNewConnection(newOutCon);
		
		
	}
	
	public void visualize(Random r, String fileName) {
		BufferedImage image = new BufferedImage(4096, 2048, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)image.createGraphics();  // not sure on this line, but this seems more right
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(), image.getHeight()); // give the whole image a white background
		
		int nodeRadius = 15;
		int nodeSpacing = 38;
		
		// Get number of INPUT nodes and OUTPUT nodes
		int numInputNodes = 1;
		int numOutputNodes = 1;
		
		Map<Integer,int[]> locations = new HashMap<Integer, int[]>();
		
		int numSpaces = (image.getHeight()-4*(nodeRadius+nodeSpacing))/(nodeRadius*2+nodeSpacing);
		
		g.setColor(Color.blue);
		for (NodeGene node : nodes.values()) {
			if (node.getType() == TYPE.INPUT) {
				locations.put(node.getId(), new int[] {nodeSpacing*numInputNodes + nodeRadius*2*(numInputNodes-1), image.getHeight()-nodeSpacing*2-nodeRadius});
				numInputNodes += 1;
			} else if (node.getType() == TYPE.OUTPUT) {
				locations.put(node.getId(), new int[] {nodeSpacing*numOutputNodes + nodeRadius*2*(numOutputNodes-1), nodeSpacing*2-nodeRadius});
				numOutputNodes += 1;
			} else if (node.getType() == TYPE.HIDDEN) {
				boolean foundPlace = false;
				for (int xloc=0; xloc<numSpaces; xloc++) {
					int x = xloc*(nodeRadius*2+nodeSpacing);
					int y = image.getHeight()-(int)(node.getLevel()*numSpaces*(nodeRadius*2+nodeSpacing))-2*nodeRadius-2*nodeSpacing;
					foundPlace = true;
					for (int[] loc : locations.values()) {
						if ((loc[0] == x && loc[1] == y)) {
							foundPlace = false;
							break;
						}
					}
					
					if (foundPlace) {
						locations.put(node.getId(), new int[] {x, y});
						break;
					}	
				}
				
				if (!foundPlace) {
					int x = 0*(nodeRadius*2+nodeSpacing);
					int y = image.getHeight()-(int)(node.getLevel()*numSpaces*(nodeRadius*2+nodeSpacing))-2*nodeRadius-2*nodeSpacing;
					locations.put(node.getId(), new int[] {x, y});
				}

			}
			
		}
		
		for (ConnectionGene conn : connections.values()) {
			int x1 = locations.get(conn.getInNode())[0] + nodeRadius;
			int y1 = locations.get(conn.getInNode())[1];
			int x2 = locations.get(conn.getOutNode())[0] + nodeRadius;
			int y2 = locations.get(conn.getOutNode())[1] + 2*nodeRadius;
			
			if (conn.isExpressed()) {
				if (conn.getWeight() >= 0.05) {
					g.setStroke(new BasicStroke(Math.round(conn.getWeight())));
					g.setColor(Color.green);
					g.drawLine(x1, y1, x2, y2);
				} else if (conn.getWeight() < -0.05) {
					g.setStroke(new BasicStroke(Math.round(Math.abs(conn.getWeight()))));
					g.setColor(Color.red);
					g.drawLine(x1, y1, x2, y2);
				}
			}
			
			
			
		}
		
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.black);
		for (NodeGene node : nodes.values()) {
			
			int x = locations.get(node.getId())[0];
			int y = locations.get(node.getId())[1];

			g.drawOval(x, y, nodeRadius*2, nodeRadius*2);
			numInputNodes += 1;

			x = locations.get(node.getId())[0] + nodeRadius;
			y = locations.get(node.getId())[1] + nodeRadius;
			g.drawString(String.valueOf(node.getId()), x-10, y+5);
			
		}
		
		
		try {
		  ImageIO.write(image, "jpg", new File(fileName+".jpg"));
		}catch (IOException e) { 
		 e.printStackTrace();
		}
		
	}
	
	public Genome crossOverAndMutate(Genome otherGenome,Random r) {
		Genome newGenome = new Genome(counter);
		
		int n = 0;
		for (ConnectionGene conn : connections.values()) {
			if (conn.isExpressed()) {
				n += 1;
			}
		}
		double tau = 1/Math.sqrt(2*Math.sqrt(n));
		double tauPrime = 1/Math.sqrt(2*n);
		
		for (NodeGene node : nodes.values()) {
			if (node.getType() == TYPE.INPUT || node.getType() == TYPE.OUTPUT) {
				newGenome.addNode(node.getType(), node.getLevel());
			}
		}
		

		for (ConnectionGene conn : connections.values()) {
			boolean notPresentInOtherGenome = false;
			boolean isDisabledInOneGenome = false;
			ConnectionGene newConn = conn.copy();
			
			if (!conn.isExpressed()) {
				isDisabledInOneGenome = true;
			}
			
			if (otherGenome.connections.containsKey(conn.getInnovation())) {
				newConn.setStepSize((newConn.getStepSize() + otherGenome.connections.get(conn.getInnovation()).getStepSize())/2f);
				newConn.setWeight((newConn.getWeight() + otherGenome.connections.get(conn.getInnovation()).getWeight())/2f);
				if (!otherGenome.connections.get(conn.getInnovation()).isExpressed()) {
					isDisabledInOneGenome = true;
				}
			} else {
				notPresentInOtherGenome = true;
			}
			
			if (isDisabledInOneGenome) {
				if (r.nextFloat() < 0.95) {
					newConn.disable();
				} else {
					newConn.enable();
				}
			}
			
			if (newConn.isExpressed()) {
				float stepSizePrime = (float) (newConn.getStepSize() * Math.exp(tauPrime*r.nextGaussian() + tau*r.nextGaussian()));
				if (stepSizePrime < 0.01) {
					stepSizePrime = 0.01f;
				}
				
				float weightDelta = (float) (stepSizePrime*r.nextGaussian());
				
				if (Math.abs(weightDelta) > 3) {
					weightDelta = Math.copySign(3, weightDelta);
				}
				float weightPrime = newConn.getWeight() + weightDelta;
				
				newConn.setWeight(weightPrime);
				newConn.setStepSize(stepSizePrime);
			}
			
			if (!notPresentInOtherGenome || r.nextFloat() < 0.5) {
				if (!newGenome.nodes.containsKey(newConn.getInNode())) {
					newGenome.addNode(nodes.get(newConn.getInNode()).getType(), nodes.get(newConn.getInNode()).getLevel(), newConn.getInNode());
				}
				
				if (!newGenome.nodes.containsKey(newConn.getOutNode())) {
					newGenome.addNode(nodes.get(newConn.getOutNode()).getType(), nodes.get(newConn.getOutNode()).getLevel(), newConn.getOutNode());
				}
				
				newGenome.connections.put(newConn.getInnovation(), newConn);
				
			}
			
		}
		
		for (ConnectionGene conn : otherGenome.connections.values()) {
			
			if (!connections.containsKey(conn.getInnovation())) {
				if (r.nextFloat() < 0.5) {
					ConnectionGene newConn = conn.copy();
					
					if (!conn.isExpressed()) {
						if (r.nextFloat() < 0.95) {
							newConn.enable();
						} else {
							newConn.disable();
						}
					}
					
					if (newConn.isExpressed()) {
						float stepSizePrime = (float) (newConn.getStepSize() * Math.exp(tauPrime*r.nextGaussian() + tau*r.nextGaussian()));
						if (stepSizePrime < 0.01) {
							stepSizePrime = 0.01f;
						}
						
						float weightDelta = (float) (stepSizePrime*r.nextGaussian());
						
						if (Math.abs(weightDelta) > 3) {
							weightDelta = Math.copySign(3, weightDelta);
						}
						float weightPrime = newConn.getWeight() + weightDelta;
						
						newConn.setWeight(weightPrime);
						newConn.setStepSize(stepSizePrime);
					}
					
					if (!newGenome.nodes.containsKey(newConn.getInNode())) {
						newGenome.addNode(otherGenome.nodes.get(newConn.getInNode()).getType(), otherGenome.nodes.get(newConn.getInNode()).getLevel(), newConn.getInNode());
					}
					
					if (!newGenome.nodes.containsKey(newConn.getOutNode())) {
						newGenome.addNode(otherGenome.nodes.get(newConn.getOutNode()).getType(), otherGenome.nodes.get(newConn.getOutNode()).getLevel(), newConn.getOutNode());
					}
					
					newGenome.connections.put(newConn.getInnovation(), newConn);
				}
			}
			
		}

		// Mutate and crossover the connection mutation probability and step size.
		float connectionStepSizePrime = (connectionMutationStepSize + otherGenome.connectionMutationStepSize) / 2f;
		connectionStepSizePrime *= Math.exp(tauPrime*r.nextGaussian() + tau*r.nextGaussian());
		if (connectionStepSizePrime < 0.01) connectionStepSizePrime = 0.01f;
		
		float connectionMutationProbPrime = (connectionMutationProb + otherGenome.connectionMutationProb) / 2f;
		connectionMutationProbPrime += connectionStepSizePrime*r.nextGaussian();
		if(connectionMutationProbPrime < 0.05) connectionMutationProbPrime = 0.05f;
		
		newGenome.connectionMutationStepSize = connectionStepSizePrime;
		newGenome.connectionMutationProb = connectionMutationProbPrime;
		
		
		// Mutate and crossover the node mutation probability and step size.
		float nodeStepSizePrime = (nodeMutationStepSize + otherGenome.nodeMutationStepSize) / 2f;
		nodeStepSizePrime *= Math.exp(tauPrime*r.nextGaussian() + tau*r.nextGaussian());
		if (nodeStepSizePrime < 0.01) nodeStepSizePrime = 0.01f;
		
		float nodeMutationProbPrime = (nodeMutationProb + otherGenome.nodeMutationProb) / 2f;
		nodeMutationProbPrime += nodeStepSizePrime*r.nextGaussian();
		if(nodeMutationProbPrime < 0.03) nodeMutationProbPrime = 0.03f;
		
		newGenome.nodeMutationStepSize = nodeStepSizePrime;
		newGenome.nodeMutationProb = nodeMutationProbPrime;
		
		
		if (r.nextDouble() < newGenome.nodeMutationProb) {
			newGenome.addNodeMutation(r);
		}
		if (r.nextDouble() < newGenome.connectionMutationProb) {
			newGenome.addConnectionMutation(r);
		}
		
		return newGenome;
	}
	
	public int getNumberOfConnections() {
		return connections.size();
	}
	
	public int getNumberOfNodes() {
		return nodes.size();
	}
	
	public float[] feedForward(float[] inputs) {
		assert inputs.length == numInputs;
		float[] outputs = new float[numOutputs];
		for (int timeStep=0; timeStep<10; timeStep++) {
			for (ConnectionGene conn : connections.values()) {
				if (conn.isExpressed()) {
					NodeGene inNode = nodes.get(conn.getInNode());
					NodeGene outNode = nodes.get(conn.getOutNode());
					outNode.setOutput(outNode.getOutput() + inNode.getLastOutput() * conn.getWeight());
				}
			}
			
			for (NodeGene node : nodes.values()) {
				if (node.getType() == TYPE.INPUT) {
					node.setOutput(node.getOutput() + inputs[node.getId()]);
					node.setLastOutput(Math.max(0,node.getOutput()));
				} else if (node.getType() == TYPE.OUTPUT) {
					node.setLastOutput(node.getOutput());
					outputs[node.getId()-(numInputs)] = node.getOutput();
				} else {
					node.setLastOutput(Math.max(0,node.getOutput()));
				}
				node.setOutput(0f);
			}	
		}	
		return outputs;
		
	}
	
	public float calculateGeneticDistance(Genome otherGenome) {
		float distance = 0;
		float conjointDisjointCoeff = 2;
		float weightCoeff = 1;
		int numberOfExcessAndDisjointGenes = 0;
		int genesInCommon = 0;
		float weightDifference = 0;
		
		for (ConnectionGene conn : connections.values()) {
			if (otherGenome.connections.containsKey(conn.getInnovation())) {
				weightDifference += Math.abs(conn.getWeight() - otherGenome.connections.get(conn.getInnovation()).getWeight());
				genesInCommon += 1;
			}
		}
		
		float averageWeightDifference = weightDifference / (float)genesInCommon;
		
		numberOfExcessAndDisjointGenes = connections.size() - genesInCommon + otherGenome.connections.size() - genesInCommon;
		
		distance = conjointDisjointCoeff * numberOfExcessAndDisjointGenes + weightCoeff * averageWeightDifference;
		
		return distance;
	}
	
	public static float[] calculateSoftmax(float[] arr) {
		
		float[] e_x = new float[arr.length];
		float e_x_sum = 0;
		float maxX = arr[0];
		
		for (int i=1; i<arr.length; i++) {
			if (arr[i] > maxX) {
				maxX = arr[i];
			}
		}
		
		
		for (int i=0; i<arr.length; i++) {
			e_x[i] = (float) Math.exp(arr[i] - maxX);
			e_x_sum += e_x[i];
		}
		
		for (int i=0; i<arr.length; i++) {
			e_x[i] /= e_x_sum;
		}
		
		
		return e_x;
	}
}