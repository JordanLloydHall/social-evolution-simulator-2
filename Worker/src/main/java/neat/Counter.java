package main.java.neat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Counter {

	private int innovation = 0;
	
	private Map<ArrayList<Integer>, Integer> markers;
	
	public Counter() {
		markers = new HashMap<ArrayList<Integer>, Integer>();
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
	
}
