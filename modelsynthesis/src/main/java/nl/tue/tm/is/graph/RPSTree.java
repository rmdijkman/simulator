package nl.tue.tm.is.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mallardsoft.tuple.Pair;

import nl.tue.tm.is.maths.MultiSet;

public class RPSTree {

	/**
	 * RPSTreeNode is a node in the Refined Process Structure Tree (RPST)
	 */
	public class RPSTreeNode{
		private RPSTreeNode parent;
		private MultiSet<RPSTreeNode> children;
		private MultiSet<Pair<Integer,Integer>> edges;
		private Set<Integer> nodes;		
			
		public RPSTreeNode() {
			this.children = new MultiSet<RPSTreeNode>();
			this.edges = new MultiSet<Pair<Integer,Integer>>();
		}
		public RPSTreeNode(MultiSet<RPSTreeNode> children, MultiSet<Pair<Integer, Integer>> edges) {
			this.children = children;
			this.edges = edges;
		}
		
		/**
		 * @return returns the direct children of this node in the RPST
		 */
		public MultiSet<RPSTreeNode> getChildren() {
			return children;
		}
		/**
		 * Returns the children of this node recursively, i.e. the direct children, the direct children of the children, ...
		 * 
		 * @return returns the children of this node recursively
		 */
		public MultiSet<RPSTreeNode> getChildrenRecursive() {
			MultiSet<RPSTreeNode> result = new MultiSet<RPSTreeNode>();
			result.addAll(children);
			for (RPSTreeNode c: children){
				result.addAll(c.getChildrenRecursive());
			}
			return result;
		}
		/**
		 * @return returns those children (recursively) that do not have any children themselves. 
		 */
		public MultiSet<RPSTreeNode> getLeafs() {
			MultiSet<RPSTreeNode> result = new MultiSet<RPSTreeNode>();
			if (children.isEmpty()){
				result.add(this);
			}
			for (RPSTreeNode c: children){
				result.addAll(c.getLeafs());
			}
			return result;
		}
		/**
		 * Sets the children of this node to the given multiset of children. 
		 * 
		 * @param children
		 */
		public void setChildren(MultiSet<RPSTreeNode> children) {
			this.children = children;
		}
		/**
		 * Adds a child to the set of children, returns the added child.
		 * 
		 * @return the child that is added.
		 */
		public RPSTreeNode addChild(){
			RPSTreeNode newChild = new RPSTreeNode();
			children.add(newChild);
			return newChild;
		}
		/**
		 * Adds the given child to the set of children.
		 * 
		 * @param newChild
		 */
		public void addChild(RPSTreeNode newChild){
			children.add(newChild);
		}
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Returns the multiset of edges that this node represents.
		 * 
		 * @return the multiset of edges that this node represents.
		 */
		public MultiSet<Pair<Integer, Integer>> getEdges() {
			return edges;
		}
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Sets the multiset of edges that this node represents.
		 * 
		 * @param edges the multiset of edges that this node must represent.
		 */
		public void setEdges(MultiSet<Pair<Integer, Integer>> edges) {
			this.edges = edges;
		}
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Adds edges to that set.
		 * 
		 * @param es the multiset of edges to add to the set of edges that this node represents.
		 */
		public void addEdges(MultiSet<Pair<Integer, Integer>> es) {
			this.edges.addAll(es);
		}
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Adds an edge to that set.
		 * 
		 * @param e the edge to add to the set of edges that this node represents.
		 */
		public void addEdge(Pair<Integer, Integer> e){
			edges.add(e);
		}
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Adds an edge to that set by providing the start node and the end node of that edge.
		 * 
		 * @param n1 the start node of the edge that must be added.
		 * @param n2 the end node of the edge that must be added.
		 */
		public Pair<Integer, Integer> addEdge(Integer n1, Integer n2){
			Pair<Integer, Integer> e = new Pair<Integer, Integer>(n1,n2);
			edges.add(e);
			return e;
		}
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Returns the set of nodes, from the original graph, that correspond to these edges. 
		 */
		public Set<Integer> getNodes(){
			if (nodes != null){
				return nodes;
			}
			
			nodes = new HashSet<Integer>();
			for (Pair<Integer,Integer> e: edges){
				nodes.add(Pair.get1(e));
				nodes.add(Pair.get2(e));				
			}
			return nodes;
		}
		
		/**
		 * Each node in the RPST corresponds to a multiset of edges in the original graph.
		 * Returns the set of nodes, from the original graph, that correspond to the edges of
		 *   this node in the RPST as well as its children (recursively)
		 */
		public Set<Integer> getNodesRecursive(){
			Set<Integer> result = new HashSet<Integer>(getNodes());
			
			for (RPSTreeNode c: getChildren()){
				result.addAll(c.getNodesRecursive());
			}
			
			return result;
		}
		
		public String toString(){
			return edges.toString();
		}
		/**
		 * @return true if and only if the direct children of this node and the multiset of edges
		 * that this node represents are the same as that of the given node.
		 */
		public boolean equals(Object o){
			if (o instanceof RPSTreeNode){
				RPSTreeNode r = (RPSTreeNode)o;
				return r.getEdges().equals(edges) && r.getChildren().equals(children);
			}else{
				return false;
			}
		}
		
		/**
		 * Set the parent tree node of this node.
		 * 
		 * @param parent
		 */
		public void setParent(RPSTreeNode parent) {
			this.parent = parent;
		}
		
		/**
		 * Get the parent tree node of this node.
		 * Will be null for the root node.
		 * 
		 * @return the parent node of this node
		 */
		public RPSTreeNode getParent() {
			return parent;
		}
		
	}
	
	private RPSTreeNode rootNode;
	
	private Map<RPSTreeNode, Integer> depthCache = new HashMap<RPSTreeNode, Integer>();

	/**
	 * Constructs a new RPST
	 */
	public RPSTree() {
		rootNode = new RPSTreeNode();
	}
	/**
	 * Constructs a new RPST with the given node as its root.
	 */
	public RPSTree(RPSTreeNode rootNode) {
		this.rootNode = rootNode;
	}
	/**
	 * Returns the depth of the given node in this RPST. The root is at depth == 0. 
	 */
	public int getDepth(RPSTreeNode ofNode){
		if (depthCache.containsKey(ofNode))
			return depthCache.get(ofNode);
		
		depthCache.put(ofNode, getDepthHelper(ofNode, rootNode, 0));
		return depthCache.get(ofNode);
	}	
	private int getDepthHelper(RPSTreeNode ofNode, RPSTreeNode currNode, int currdepth){
		if (ofNode.equals(currNode)){
			return currdepth;
		}else{
			for (RPSTreeNode c: currNode.getChildren()){
				int cdepth = getDepthHelper(ofNode, c, currdepth + 1);
				if (cdepth != -1){
					return cdepth;
				}
			}
			return -1;
		}
	}	
	/**
	 * Returns the root node. 
	 */
	public RPSTreeNode getRootNode() {
		return rootNode;
	}
	/**
	 * Sets the root node.
	 */
	public void setRootNode(RPSTreeNode rootNode) {
		this.rootNode = rootNode;
	}
	
	public String toString(){
		return toStringHelper(rootNode, 0);
	}	
	private String toStringHelper(RPSTreeNode tn, int depth){
		String result = "";
		for (int i = 0; i < depth; i++){
			result += "\t";
		}
		result += tn.toString();
		result += "\n";
		for (RPSTreeNode c: tn.getChildren()){
			result += toStringHelper(c, depth+1);
		}
		return result;
	}
	
	public boolean equals(Object o){
		if (o instanceof RPSTree){
			return ((RPSTree) o).getRootNode().equals(rootNode);
		}else{
			return false;
		}
	}
}
