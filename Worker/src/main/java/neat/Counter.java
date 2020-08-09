package main.java.neat;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;

import main.java.neat.NodeGene.TYPE;

public class Counter {

	private int innovation = 0;
	
	private Map<ArrayList<Integer>, Integer> markers;
	
	private ArrayList<Genome[]> speciesTree;
	
	public int numOfSpecies = 0;
	
	private Random r;

	private String[] firstNames = {"Green", "Broken", "Huge", "Numbered", "Rigid", "Soft", "Blue", "Yellow", "Black", "White", "Purple", "North", "South", "East", "West", "Sticky", "Wide", "Slim"};
	private String[] lastNames = {"Arm", "Repair", "Draft", "Month", "Topic", "Series", "King", "Queen", "Disk", "Cookie", "Coffee", "Ground", "Mic", "Beer", "Pencil", "Dice", "Lamp", "Bottle", "Apple", "Orange", "Pear"};
	
	public Counter(Random r) {
		this.r = r;
		markers = new HashMap<ArrayList<Integer>, Integer>();
		speciesTree = new ArrayList<Genome[]>();
	}
	
	public synchronized int getInnovation(int inNode, int outNode) {
//		System.out.println(markers.containsKey(new ArrayList<Integer>(Arrays.asList(inNode,outNode))));
		if (markers.containsKey(new ArrayList<Integer>(Arrays.asList(inNode,outNode)))) {
			return markers.get(new ArrayList<Integer>(Arrays.asList(inNode,outNode)));
		} else {
			innovation++;
			markers.put(new ArrayList<Integer>(Arrays.asList(inNode,outNode)), innovation-1);
			return innovation-1;
		}

	}
	
	public synchronized Genome speciate(Genome newGenome, Genome oldGenome) {
		
		
		if (oldGenome != null && newGenome.calculateGeneticDistance(oldGenome) <= 0.1) {
			newGenome.speciesName = oldGenome.speciesName;
			return oldGenome;
		}
		
		float closestDist = 10000;
		Genome closestGenome = null;
		for (Genome[] genomePair : speciesTree) {
			float geneticDistance = newGenome.calculateGeneticDistance(genomePair[0]);
			if (geneticDistance < closestDist) {
				closestDist = geneticDistance;
				closestGenome = genomePair[0];
			}
		}
		
		if (closestDist > 0.1) {
			speciesTree.add(new Genome[] {newGenome, oldGenome});
//			newGenome.speciesName = firstNames[r.nextInt(firstNames.length)] + " " + firstNames[r.nextInt(firstNames.length)] + " " + lastNames[r.nextInt(lastNames.length)];
			newGenome.speciesName = numOfSpecies;
			numOfSpecies += 1;
			drawTree();
			
			return newGenome;
		} else {
//			drawTree();
			newGenome.speciesName = closestGenome.speciesName;
			return closestGenome;
		}
	}
	
	public void drawTree() {
		
		LinkedList<Genome> nextLayer = new LinkedList<Genome>();
		LinkedList<Genome> thisLayer = new LinkedList<Genome>();
		
		LinkedList<LinkedList<Genome>> levels = new LinkedList<LinkedList<Genome>>();
		
		nextLayer.add(speciesTree.get(0)[0]);
		
		int maxWidth = 0;
		
		while (!nextLayer.isEmpty()) {
			
			thisLayer.addAll(nextLayer);
			levels.add((LinkedList<Genome>)thisLayer.clone());
			if (thisLayer.size() > maxWidth) {
				maxWidth = thisLayer.size();
			}
			nextLayer.clear();
			
			while (!thisLayer.isEmpty()) {
				
				Genome parentGenome = thisLayer.pop();
				
				for (Genome[] childSpecies : speciesTree) {
					if (childSpecies[1] == parentGenome) {
						nextLayer.add(childSpecies[0]);
					}
				}
				
			}
		}
		
//		for (LinkedList<Genome> layer : levels) {
//			System.out.println(layer.toString());
//		}
//		
//		System.out.println("\n");
		
		int xMargin = 16;
		int yMargin = 64;
		int eachWidth = 160;
		int eachHeight = 24;
		
		BufferedImage image = new BufferedImage(xMargin + (xMargin + eachWidth) * maxWidth, yMargin + (yMargin + eachHeight) * levels.size(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)image.createGraphics();  // not sure on this line, but this seems more right
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(), image.getHeight()); // give the whole image a white background
		
		g.setColor(Color.black);
		int startY = yMargin;
		for (int row=0; row<levels.size(); row++) {
			int startX = xMargin;
			LinkedList<Genome> level = levels.get(row);
			for (int column=0; column<level.size(); column++) {
				g.drawRect(startX, startY, eachWidth, eachHeight);
				
				g.drawString(String.valueOf(level.get(column).speciesName), startX + 10, startY + (int)(eachHeight/2f));
				
				if (row > 0) {
					LinkedList<Genome> lastLevel = levels.get(row-1);
					for (Genome[] candidate : speciesTree) {
						if (level.get(column) == candidate[0]) {
							for (int p=0; p<lastLevel.size(); p++) {
								if (candidate[1] == lastLevel.get(p)) {
									g.drawLine(xMargin + (xMargin + eachWidth) * p + (int)(eachWidth/2f), startY-yMargin, startX + (int)(eachWidth/2f), startY);
								}
							}
						}
					}
				}
				startX += eachWidth + xMargin;
			}
			
			startY += eachHeight + yMargin;
		}
		
		
		
		
		try {
		  ImageIO.write(image, "jpg", new File("speciesTree.jpg"));
		}catch (IOException e) { 
		 e.printStackTrace();
		}
	}
	
}
