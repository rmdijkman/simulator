package nl.tue.tm.is.graph;

import nl.tue.tm.is.graph.RPSTree.RPSTreeNode;
import nl.tue.tm.is.maths.MultiSet;

public class RPSTreeUtil {

	public static MultiSet<RPSTreeNode> getAllParentsForNodes(MultiSet<RPSTreeNode> nodes) {
		MultiSet<RPSTreeNode> parents = new MultiSet<RPSTreeNode>();
		
		for (RPSTreeNode n : nodes) {
			if (!(n.getParent() == null))
				if (!parents.contains(n.getParent()))
					parents.add(n.getParent());
		}
		return parents;
	}


	
}
