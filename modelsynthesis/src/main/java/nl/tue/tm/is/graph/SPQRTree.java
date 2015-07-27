package nl.tue.tm.is.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nl.tue.tm.is.maths.MultiSet;

import com.mallardsoft.tuple.Pair;

/**
 * The SPQR tree is a representation of a graph. As described by Hopcroft and Tarjan (1973).
 *  
 * J. Hopcroft and R.E. Tarjan. Dividing a graph into triconnected components. SIAM J. Comput. 2:135-158, 1973.
 * 
 * @author Remco Dijkman
 * 
 */
public class SPQRTree {

	public enum SPQRNodeType{
		snode, pnode, qnode, rnode, none;
	}
	
	public class SPQRTreeNode{
				
		private Vector<SPQRTreeNode> childNodes;
		private SPQRTreeNode parentNode;		
		private SPQRNodeType nodeType;
		
		private MultiSet<Pair<Integer,Integer>> skeletonEdges;
		private MultiSet<Pair<Integer,Integer>> persistentGraphEdges;
		private Pair<Integer,Integer> referenceEdge;
		private boolean referenceEdgeIsVirtual;
		
		public SPQRTreeNode(){
			persistentGraphEdges = new MultiSet<Pair<Integer,Integer>>();
			childNodes = new Vector<SPQRTreeNode>();
			skeletonEdges = new MultiSet<Pair<Integer,Integer>>();
			parentNode = null;
		}
		public SPQRTreeNode(Vector<SPQRTreeNode> children, MultiSet<Pair<Integer,Integer>> persistentGraphEdges, SPQRNodeType nodeType){
			setChildNodes(children);
			this.persistentGraphEdges = persistentGraphEdges;
			this.nodeType = nodeType;										
		}
		
		public void mergeIn(SPQRTreeNode tn2){
			addChildNodes(tn2.getChildNodes());
			persistentGraphEdges.addAll(tn2.getPersistentGraphEdges());
			nodeType = SPQRNodeType.none;
			skeletonEdges = null;
			referenceEdge = null;
		}
		public SPQRNodeType getNodeType() {
			return nodeType;
		}
		public void setNodeType(SPQRNodeType nodeType) {
			this.nodeType = nodeType;
		}
		public void setParentNode(SPQRTreeNode parentNode){
			this.parentNode = parentNode;
		}
		public SPQRTreeNode getParentNode(){
			return parentNode;
		}
		public MultiSet<Pair<Integer,Integer>> getPersistentGraphEdges(){
			return persistentGraphEdges;
		}		
		public MultiSet<Pair<Integer,Integer>> getSkeletonEdges() {
			return skeletonEdges;
		}
		public Vector<SPQRTreeNode> getChildNodes(){
			return childNodes;
		}
		public void addPersistentGraphEdge(Pair<Integer,Integer> e){
			persistentGraphEdges.add(e);
		}
		public void addPersistentGraphEdge(Integer n1, Integer n2){
			persistentGraphEdges.add(new Pair<Integer,Integer>(n1,n2));
		}
		public void addPersistentGraphEdges(MultiSet<Pair<Integer,Integer>> es){
			persistentGraphEdges.addAll(es);
		}
		public void removePersistentGraphEdge(Pair<Integer,Integer> e){
			persistentGraphEdges.remove((Object)e);
		}
		public void removePersistentGraphEdges(MultiSet<Pair<Integer,Integer>> es){
			for (Pair<Integer,Integer> e: es){
				persistentGraphEdges.remove((Object)e);
			}
		}
		public void addSkeletonEdge(Integer n1, Integer n2){
			skeletonEdges.add(new Pair<Integer,Integer>(n1,n2));
		}
		public void addSkeletonEdge(Pair<Integer,Integer> e){
			skeletonEdges.add(e);
		}
		public void addSkeletonEdges(MultiSet<Pair<Integer,Integer>> es){
			skeletonEdges.addAll(es);
		}
		public void addChildNode(SPQRTreeNode n){
			n.setParentNode(this);
			childNodes.add(n);
		}
		public SPQRTreeNode addChildNode(){
			SPQRTreeNode s = new SPQRTreeNode();
			s.setParentNode(this);
			childNodes.add(s);
			return s;
		}
		public void addChildNodes(Vector<SPQRTreeNode> ns){
			childNodes.addAll(ns);
			for (SPQRTreeNode n: ns){
				n.setParentNode(this);
			}
		}
		public void setChildNodes(Vector<SPQRTreeNode> ns){
			childNodes = ns;
			for (SPQRTreeNode n: ns){
				n.setParentNode(this);
			}
		}
		public Pair<Integer, Integer> getReferenceEdge() {
			return referenceEdge;
		}
		public void setReferenceEdge(Pair<Integer, Integer> referenceEdge) {
			this.referenceEdge = referenceEdge;
		}
		public void setReferenceEdge(Integer n1, Integer n2) {
			this.referenceEdge = new Pair<Integer,Integer>(n1,n2);
		}
		public boolean referenceEdgeIsVirtual() {
			return referenceEdgeIsVirtual;
		}
		public void referenceEdgeIsVirtual(boolean referenceEdgeIsVirtual) {
			this.referenceEdgeIsVirtual = referenceEdgeIsVirtual;
		}
		public String toString(){
			String result = "";
			result += "(";
			switch (getNodeType()){
			case snode:
				result += "S";
				break;
			case pnode:
				result += "P";
				break;
			case qnode:
				result += "Q";
				break;
			case rnode:
				result += "R";
				break;
			case none:
			}
			result += "):";
			result += getPersistentGraphEdges() + "-";
			result += getSkeletonEdges();
			if (getNodeType() != SPQRNodeType.qnode){
				result += "-" + getReferenceEdge();
				result += referenceEdgeIsVirtual()?"V":"R";
			}
			return result;
		}
	}
	
	protected SPQRTreeNode rootNode;

	private Map<Integer,Integer> nodeId2NodePointer;
	private Map<Integer,Integer> nodePointer2NodeId;
	private Map<SPQRTreeNode,Integer> treeNode2TreeNodePtr;
	
	/**
	 * Creates an SPQRTree.
	 * 
	 * @param g Create the tree for this graph.
	 */
	public SPQRTree(Graph g, Pair<Integer,Integer> atEdge){
		rootNode = null;
		
		nodeId2NodePointer = new HashMap<Integer,Integer>();
		nodePointer2NodeId = new HashMap<Integer,Integer>();
		treeNode2TreeNodePtr = new HashMap<SPQRTreeNode,Integer>();		

		int graphPointer = createGraph();
		for (Integer v: g.getVertices()){
			int nodePointer = addNode(graphPointer);
			nodeId2NodePointer.put(v, nodePointer);
			nodePointer2NodeId.put(nodePointer, v);
		}
		Integer atEdgePtr = null;
		for (Pair<Integer,Integer> e: g.getEdges()){
			int eptr = addEdge(graphPointer, nodeId2NodePointer.get(Pair.get1(e)), nodeId2NodePointer.get(Pair.get2(e)));
			if (e.equals(atEdge)){
				atEdgePtr = eptr;
			}
		}
		int spqrTreePointer = (atEdgePtr == null)?createSPQRTree(graphPointer):createSPQRTree(graphPointer,atEdgePtr);
		rootNode = new SPQRTreeNode();
		int rootNodePointer = spqrRootNode(spqrTreePointer); 
		addSPQRTreeNodeAndChildren(spqrTreePointer,rootNodePointer,rootNode);
		orderSNodes(rootNode);		
	}
	
	private void addSPQRTreeNodeAndChildren(int spqrTreePointer, int spqrTreeNodePointer, SPQRTreeNode toNode){
		treeNode2TreeNodePtr.put(toNode, spqrTreeNodePointer);
		switch(this.spqrNodeType(spqrTreePointer, spqrTreeNodePointer)){
		case 0:
			toNode.setNodeType(SPQRNodeType.snode);
			break;
		case 1:
			toNode.setNodeType(SPQRNodeType.pnode);
			break;
		case 2:
			toNode.setNodeType(SPQRNodeType.rnode);
			break;
		}
		int pgPointer = spqrPertinentGraph(spqrTreePointer,spqrTreeNodePointer);
		for (Integer ePtr: spqrPertinentEdges(pgPointer)){
			int orgNodes[] = this.spqrPertinentEdgeOriginalNodes(pgPointer, ePtr);
			toNode.addPersistentGraphEdge(nodePointer2NodeId.get(orgNodes[0]),nodePointer2NodeId.get(orgNodes[1]));
		}
		int sgPointer = spqrSkeleton(spqrTreePointer,spqrTreeNodePointer);
		for (Integer ePtr: spqrSkeletonEdges(sgPointer)){
			int orgNodes[] = this.spqrSkeletonEdgeOriginalNodes(sgPointer, ePtr);
			toNode.addSkeletonEdge(nodePointer2NodeId.get(orgNodes[0]),nodePointer2NodeId.get(orgNodes[1]));
			if (!spqrSkeletonEdgeIsVirtual(sgPointer, ePtr)){
				SPQRTreeNode childForRealEdge = toNode.addChildNode();
				childForRealEdge.setNodeType(SPQRNodeType.qnode);
				childForRealEdge.addSkeletonEdge(nodePointer2NodeId.get(orgNodes[0]),nodePointer2NodeId.get(orgNodes[1]));
				childForRealEdge.addPersistentGraphEdge(nodePointer2NodeId.get(orgNodes[0]),nodePointer2NodeId.get(orgNodes[1]));
			}
		}

		int sRefEdge = this.spqrPertinentSkeletonReferenceEdge(pgPointer);
		int orgNodes[] = this.spqrSkeletonEdgeOriginalNodes(sgPointer, sRefEdge);
		toNode.setReferenceEdge(nodePointer2NodeId.get(orgNodes[0]),nodePointer2NodeId.get(orgNodes[1]));
		toNode.referenceEdgeIsVirtual(spqrSkeletonEdgeIsVirtual(sgPointer, sRefEdge));

		int spqrParentPointer = 0;
		if (toNode.getParentNode() != null){
			spqrParentPointer = treeNode2TreeNodePtr.get(toNode.getParentNode());
		}
		for (int childPtr: spqrChildNodes(spqrTreePointer,spqrTreeNodePointer,spqrParentPointer)){
			SPQRTreeNode newChild = toNode.addChildNode();			
			addSPQRTreeNodeAndChildren(spqrTreePointer, childPtr, newChild);
		}
	}
	
	private void orderSNodes(SPQRTreeNode toOrder){
		//If the node if a sequence.
		if (toOrder.getNodeType() == SPQRNodeType.snode){
			Vector<SPQRTreeNode> reorderedChildren = new Vector<SPQRTreeNode>();
			
			//Remove the reference edge from the sequence.
			MultiSet<Pair<Integer,Integer>> sEdgesMinReference = new MultiSet<Pair<Integer,Integer>>(toOrder.getSkeletonEdges());
			sEdgesMinReference.remove(toOrder.getReferenceEdge());
			
			//The start of the sequence is one of the nodes of the reference edge.
			Integer startNode = Pair.get1(toOrder.getReferenceEdge());
			Integer endNode = nextNode(startNode,sEdgesMinReference);

			//Iterate over all edges in the sequence.
			do{
				//Find the child that contains both the start and the end node of the edgeToWorkWith.
				SPQRTreeNode containingChild = null;
				for (SPQRTreeNode c: toOrder.getChildNodes()){
					boolean containsN1 = false;
					boolean containsN2 = false;
					for (Pair<Integer,Integer> e: c.getPersistentGraphEdges()){
						containsN1 = containsN1 || (Pair.get1(e).equals(startNode)) || (Pair.get2(e).equals(startNode)); 
						containsN2 = containsN2 || (Pair.get1(e).equals(endNode)) || (Pair.get2(e).equals(endNode)); 
					}
					if (containsN1 && containsN2){
						containingChild = c;
					}
				}
				//That child is the next child in the sequence.
				reorderedChildren.add(containingChild);
				
				startNode = endNode;
				endNode = nextNode(endNode,sEdgesMinReference);
			}while (endNode != null);
			toOrder.setChildNodes(reorderedChildren);
		}
		for (SPQRTreeNode c: toOrder.getChildNodes()){
			orderSNodes(c);
		}
	}
	private Integer nextNode(Integer fromNode, MultiSet<Pair<Integer,Integer>> edgeSet){
		Pair<Integer,Integer> nextEdge = null;
		Integer nextNode = null;
		for (Pair<Integer,Integer> e: edgeSet){			
			if (Pair.get1(e).equals(fromNode)){
				nextEdge = e;
				nextNode = Pair.get2(e); 
			}else if (Pair.get2(e).equals(fromNode)){
				nextEdge = e;
				nextNode = Pair.get1(e); 
			}			
		}
		edgeSet.remove(nextEdge);
		return nextNode;
	}
	
	public String toString(){
		return toStringHelper(rootNode, 0);
	}
	
	private String toStringHelper(SPQRTreeNode tn, int depth){
		String result = "";
		for (int i = 0; i < depth; i++){
			result += "\t";
		}
		result += tn.toString();
		result += "\n";
		for (SPQRTreeNode c: tn.getChildNodes()){
			result += toStringHelper(c, depth+1);
		}
		return result;
	}
	
	/**
	 * Returns the root node of this SPQR tree.
	 * 
	 * @return Root node.
	 */
	public SPQRTreeNode getRootNode(){
		return rootNode;
	}
	
	/****************************************
	 * The native part
	 ****************************************/
	
	/**
	 * Creates a native graph, returns a pointer to that graph.
	 * 
	 * @return pointer to created graph.
	 */
	private native int createGraph();
	/**
	 * Adds a node to a graph, returns a pointer to that node.
	 * 
	 * @param toGraph pointer to graph to add node to.
	 * @return pointer to the created node.
	 */
	private native int addNode(int toGraph);
	/**
	 * Adds an edge to a graph.
	 * 
	 * @param toGraph pointer to graph to add edge to.
	 * @param srcNode pointer to node that is the source for the edge.
	 * @param tgtNode pointer to node that is the target for the edge.
	 */
	private native int addEdge(int toGraph, int srcNode, int tgtNode);
	
	/**
	 * Computes the SPQR tree for a graph.
	 * 
	 * @param forGraph pointer to graph to compute SPQR tree for. 
	 * @return pointer to the SPQR tree.
	 */
	private native int createSPQRTree(int forGraph);
	private native int createSPQRTree(int forGraph, int atEdge);
	/**
	 * Returns the root node of the SPQR Tree.
	 * 
	 * @param ofTree the pointer to the SPQR tree.
	 * @return pointer to the root node of the SPQR tree.
	 */
	private native int spqrRootNode(int ofTree);
	/**
	 * Returns the array of child nodes of this node in the SPQR tree.
	 * 
	 * @param ofTree pointer to the SPQR tree.
	 * @param ofNode pointer to the SPQR tree node for which to return the child nodes.
	 * @return array of pointers to child nodes.
	 */
	private native int[] spqrChildNodes(int ofTree, int ofNode, int parentNode);
	
	/**
	 * Returns a pointer to the pertinent graph of a node in an SPQR tree.  
	 *  
	 * @param ofTree pointer to the SPQR tree.
	 * @param ofNode pointer to the SPQR tree node for which to return the pertinent graph nodes.
	 * @return pointer to a pertinent graph.
	 */
	private native int spqrPertinentGraph(int ofTree, int ofNode);
	/**
	 * Returns the array of nodes of a pertinent graph.
	 * 
	 * @param pg pointer to a pertinent graph.
	 * @return array of pointers to nodes from the pertinent graph.
	 */	
	@SuppressWarnings("unused")
	private native int[] spqrPertinentNodes(int pg);
	/**
	 * Returns the array of edges of a pertinent graph.
	 * 
	 * @param pg pointer to a pertinent graph.
	 * @return an array of pointers to edges in the pertinent graph.
	 */
	private native int[] spqrPertinentEdges(int pg);	
	/**
	 * Returns the node from the original graph that corresponds to the given node in the given pertinent graph.
	 * 
	 * @param pg pointer to a pertinent graph.
	 * @param n pointer to a node in the pertinent graph.
	 * @return pointer to a node in the original graph.
	 */
	@SuppressWarnings("unused")
	private native int spqrPertinentOriginalNode(int pg, int n);
	/**
	 * Returns the array of nodes from the original graph that correspond to the nodes of the given edge.
	 * 
	 * @param pg pointer to a pertinent graph.
	 * @param e pointer to an edge in the pertinent graph.
	 * @return array[2] of pointers to nodes in the original graph: array[0] = source node, array[2] = target node.
	 */
	private native int[] spqrPertinentEdgeOriginalNodes(int pg, int e);
	
	@SuppressWarnings("unused")
	private native int spqrPertinentReferenceEdge(int pg);
	private native int spqrPertinentSkeletonReferenceEdge(int pg);
	/**
	 * Returns the skeleton graph for the given node in the given SPQR tree.
	 * 
	 * @param ofTree a pointer to an SPQR tree.
	 * @param ofNode a pointer to a node in the SPQR tree. 
	 * @return pointer to a skeleton graph.
	 */	
	private native int spqrSkeleton(int ofTree, int ofNode);	
	/**
	 * Returns the nodes in the skeleton graph.
	 * 
	 * @param sg pointer to a skeleton graph.
	 * @return array of pointers to nodes.
	 */
	@SuppressWarnings("unused")
	private native int[] spqrSkeletonNodes(int sg);
	/**
	 * Returns the array of edges of a skeleton graph.
	 * 
	 * @param sg pointer to a skeleton graph.
	 * @return an array of pointers to edges in the skeleton graph.
	 */
	private native int[] spqrSkeletonEdges(int sg);
	/**
	 * Returns the node from the original graph that corresponds to the given node in the given skeleton graph.
	 * 
	 * @param sg pointer to a skeleton graph.
	 * @param n pointer to a node in the skeleton graph.
	 * @return pointer to a node in the original graph.
	 */
	@SuppressWarnings("unused")
	private native int spqrSkeletonOriginalNode(int sg, int n);
	/**
	 * Returns the array of nodes from the original graph that correspond to the nodes of the given edge.
	 * 
	 * @param sg pointer to a skeleton graph.
	 * @param e pointer to an edge in the skeleton graph.
	 * @return array[2] of pointers to nodes in the original graph: array[0] = source node, array[2] = target node.
	 */
	private native int[] spqrSkeletonEdgeOriginalNodes(int sg, int e);
	/**
	 * Returns true if and only if the given edge from the given skeleton graph is a virtual edge.
	 * 
	 * @param sg pointer to a skeleton graph.
	 * @param e pointer to an edge in the skeleton graph.
	 * @return true if the edge is virtual, false if the edge is real.
	 */
	private native boolean spqrSkeletonEdgeIsVirtual(int sg, int e);
	
	/**
	 * Returns the type of the given node in the given SPQR tree. 
	 *  
	 * @param ofTree pointer to an SPQR tree.
	 * @param ofNode pointer to a node in the SPQR tree.
	 * @return 0 iff node is an S-Node, 1 iff node is a P-Node, 2 iff node is an R-Node.
	 */
	private native int spqrNodeType(int ofTree, int ofNode);
	
	//Load the native library for computing the SPQR tree
	static {
		System.loadLibrary("ogdf");
	}
}
