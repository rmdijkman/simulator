package nl.tue.tm.is.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache for RPSTrees that have been calculated for graphs.
 */
public class RPSTreeCache {
	
	private static Map<Graph,RPSTree> trees = new HashMap<Graph, RPSTree>();
	
	public static RPSTree getRPSTree(Graph g) {
		if (!trees.containsKey(g))
			trees.put(g, new RPSTreeComputer(g).compute());
		
		return trees.get(g);
	}

}
