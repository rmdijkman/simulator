package nl.tue.tm.is.ptnet;

import java.util.List;


/**
 * Calculates the transitive closure of the flow relation
 * for a given PTNet.
 * 
 * Use the method isInLoop to get the information whether a 
 * node of the net is part of a cycle of the flow relation.
 * 
 * Use the method isPath to get the information whether there
 * is a path from one node to another node.
 * 
 * @author gero.decker,matthias.weidlich
 *
 */
public class TransitiveClosure {
	
	private PTNet pn;
	
	private boolean[][] matrix;
	
	private List<Node> nodes;
	
	public TransitiveClosure(PTNet pn) {
		this.pn = pn;
		this.matrix = null;
		this.nodes.addAll(this.pn.nodes());
	}
	
	/**
	 * Checks whether the given node is part of a flow relation
	 * cycle.
	 * 
	 * @param node
	 * @return true, if the node is contained in a a flow relation cycle. false, otherwise.
	 */
	public boolean isInLoop(Node node) {
		if (matrix == null)
			calculateMatrix();
		int index = this.nodes.indexOf(node);
		return matrix[index][index];
	}

	protected void calculateMatrix() {
		this.matrix = new boolean[this.nodes.size()][this.nodes.size()];
		
		// setup relationships
		for (Arc f: this.pn.arcs()) {
			int source = this.nodes.indexOf(f.getSource());
			int target = this.nodes.indexOf(f.getTarget());
			matrix[source][target] = true;
		}
		
		// compute transitive closure
	      for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				// In Warshall's original paper, the inner-most loop is
				// guarded by the boolean value in [row][k] --- omitting
				// the loop on false and removing the "&" in the evaluation.
				if (matrix[row][k])
					for (int col = 0; col < matrix.length; col++)
						matrix[row][col] = matrix[row][col] | matrix[k][col];
			}
		}
	}

	public String toString(){
		if (matrix == null)
			calculateMatrix();
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------\n");
		sb.append("Transitive Closure\n");
		sb.append("------------------------------------------\n");
		for (int k = 0; k < matrix.length; k++) {
			for (int row = 0; row < matrix.length; row++) {
				sb.append(matrix[row][k] + " , ");
			}
			sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		return sb.toString();
	}

	/**
	 * Checks whether there is a flow relation path from node1 to node2.
	 * 
	 * @param node1
	 * @param node2
	 * @return true, if there is a path from node1 to node2. false, otherwise.
	 */
	public boolean isPath(Node node1, Node node2) {
		if (matrix == null)
			calculateMatrix();
		int i = this.nodes.indexOf(node1);
		int j = this.nodes.indexOf(node2);
		return matrix[i][j];
	}

}
