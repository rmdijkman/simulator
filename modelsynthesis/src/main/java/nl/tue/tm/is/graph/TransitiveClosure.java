package nl.tue.tm.is.graph;

import java.util.List;

import com.mallardsoft.tuple.Pair;


/**
 * Calculates the transitive closure for a given graph.
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
	
	private Graph g;
	
	private boolean[][] matrix;
	
	private List<Integer> vertices;
	
	public TransitiveClosure(Graph g) {
		this.g = g;
		this.matrix = null;
		this.vertices.addAll(this.g.getVertices());
	}
	
	/**
	 * Checks whether the given node is part of a flow relation
	 * cycle.
	 * 
	 * @param node
	 * @return true, if the node is contained in a a flow relation cycle. false, otherwise.
	 */
	public boolean isInLoop(Integer node) {
		if (matrix == null)
			calculateMatrix();
		int index = this.vertices.indexOf(node);
		return matrix[index][index];
	}

	protected void calculateMatrix() {
		this.matrix = new boolean[this.vertices.size()][this.vertices.size()];
		
		// setup relationships
		for (Pair<Integer, Integer> p: this.g.getEdges()) {
			int source = this.vertices.indexOf(Pair.get1(p));
			int target = this.vertices.indexOf(Pair.get2(p));
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
	public boolean isPath(Integer node1, Integer node2) {
		if (matrix == null)
			calculateMatrix();
		int i = this.vertices.indexOf(node1);
		int j = this.vertices.indexOf(node2);
		return matrix[i][j];
	}
	
}
