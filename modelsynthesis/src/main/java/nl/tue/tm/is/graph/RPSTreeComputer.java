package nl.tue.tm.is.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import nl.tue.tm.is.graph.RPSTree.RPSTreeNode;
import nl.tue.tm.is.graph.SPQRTree.SPQRNodeType;
import nl.tue.tm.is.graph.SPQRTree.SPQRTreeNode;
import nl.tue.tm.is.maths.MultiSet;

import com.mallardsoft.tuple.Pair;

/**
 * Implements the computation of the refined process structure tree, as described by Vanhatalo, Voelzer and Koehler (2008)
 * 
 * J. Vanhatalo, H. Voelzer, and J. Koehler. The Refined Process Structure Tree. In: Proceedings of BPM 2008, Milan, Italy, 2008.
 * 
 * @author Remco Dijkman
 *
 */
public class RPSTreeComputer{

	private boolean returnWarnings = true;
	
	private Map<SPQRTreeNode, Boolean> isFragment;
	private Map<SPQRTreeNode, Vector<SPQRTreeNode>> toBeMergedList;
	private MultiSet<Pair<Integer,Integer>> edgesAdded;
	private Set<Integer> nodesAdded;
	private Pair<Integer,Integer> referenceEdge;
	
	private SPQRTree spqrTree;
	private Graph originalGraph;
	
	private Integer realEntry = Integer.MIN_VALUE, realExit = Integer.MIN_VALUE;
			
	public RPSTreeComputer(Graph g) {
		originalGraph = g;
		edgesAdded = new MultiSet<Pair<Integer,Integer>>(); //The reference edge is NOT a member of this set.
		nodesAdded = new HashSet<Integer>();
		referenceEdge = null; //This edge will NOT be a member of edgesAdded.
		isFragment = new HashMap<SPQRTreeNode, Boolean>();
		toBeMergedList = new HashMap<SPQRTreeNode, Vector<SPQRTreeNode>>();
	}
	
	public RPSTree compute(){
		preProcessGraph(); //Make the graph a TTG and add the return edge.
		
		step1(); //Compute the SPQR tree
		step2(spqrTree.getRootNode()); //Determine whether each component is a fragment, add the results to isFragment. 
		step3(spqrTree.getRootNode()); //Restructure fragments into canonical fragments.
		step4(spqrTree.getRootNode()); //Remove non-fragments from the tree. In the paper this is part of step 3. For simplicity, we implement it as a separate step. (Although this is less efficient.)
		
		postProcessGraph(); //Remove the nodes and edges introduced during pre-processing.
		
		RPSTree result = new RPSTree();		
		createRPSTree(spqrTree.getRootNode(), result.getRootNode(), null);
		return result;
	}
	
	/**
	 * Used to set the 'real' entry and the 'real' exist of the process. 
	 * If there are other entries and exits an additional edge is added from those
	 * to the main flow, such that they appear as loops and not as entries or exits.  
	 * 
	 * @param entryNode
	 * @param exitNode
	 */
	public void setSESE(Integer entryNode, Integer exitNode){
		realEntry = entryNode;
		realExit = exitNode;
	}
	
	/**
	 * Builds up the RPSTree from the SPQRTree recursively.
	 * 
	 * @param spqrTreeNode
	 * @param rpsTreeNode
	 * @param parent
	 */
	private void createRPSTree(SPQRTreeNode spqrTreeNode, RPSTreeNode rpsTreeNode, RPSTreeNode parent){
		rpsTreeNode.setEdges(spqrTreeNode.getPersistentGraphEdges());
		rpsTreeNode.setParent(parent);
		for (SPQRTreeNode c: spqrTreeNode.getChildNodes()){
			RPSTreeNode newChild = rpsTreeNode.addChild();
			createRPSTree(c,newChild,rpsTreeNode);
		}
	}

	/**
	 *  If |source nodes| > 1 then creates a single source node u with edges from u to each n \in source nodes.
	 *  If |sink nodes| > 1 then creates a single sink node v with edges from each n \in sink nodes to v.
	 *  Adds an edge from the single source to the single sink.
	 *  Stores the created edges and nodes, so they can be removed again later.  
	 */
	private void preProcessGraph(){
		Set<Integer> sourceNodes = originalGraph.sourceNodes();
		Integer singleSource, singleSink;
		if (sourceNodes.size() > 1){
			if (returnWarnings){
				System.err.println("WARNING: Graph has multiple source nodes: " + originalGraph.getLabels(sourceNodes) + ". This will be fixed by pre-processing.");
			}
			if (realEntry == Integer.MIN_VALUE){
				singleSource = originalGraph.addVertex("u");
				nodesAdded.add(singleSource);
				for (Integer orgSource: sourceNodes){
					edgesAdded.add(originalGraph.addEdge(singleSource, orgSource));
				}
			}else{
				singleSource = realEntry;
				for (Integer orgSource: sourceNodes){
					if (orgSource != singleSource){
						if (returnWarnings){
							System.err.print("INFO: Rerouting \"" + originalGraph.getLabel(orgSource) + "\" to ");
						}
						Integer mainFlowNode = orgSource;
						while ((originalGraph.outgoingEdges(mainFlowNode).size() == 1) && (originalGraph.incomingEdges(mainFlowNode).size() <= 1)){
							mainFlowNode = Pair.get2(originalGraph.outgoingEdges(mainFlowNode).iterator().next());
						}
						if (returnWarnings){
							System.err.println("\"" + originalGraph.getLabel(mainFlowNode) + "\"");
						}
						edgesAdded.add(originalGraph.addEdge(mainFlowNode, orgSource));
					}
				}				
			}
		}else{
			singleSource = sourceNodes.iterator().next();
		}
		Set<Integer> sinkNodes = originalGraph.sinkNodes();
		if (sinkNodes.size() > 1){
			if (returnWarnings){
				System.err.println("WARNING: Graph has multiple sink nodes: " + originalGraph.getLabels(sinkNodes) + ". This will be fixed by pre-processing.");
			}
			if (realExit == Integer.MIN_VALUE){
				singleSink = originalGraph.addVertex("v");
				nodesAdded.add(singleSink);
				for (Integer orgSink: sinkNodes){
					edgesAdded.add(originalGraph.addEdge(orgSink, singleSink));
				}
			}else{
				singleSink = realExit;
				for (Integer orgSink: sinkNodes){
					if (orgSink != singleSink){
						if (returnWarnings){
							System.err.print("INFO: Rerouting \"" + originalGraph.getLabel(orgSink) + "\" to ");
						}
						Integer mainFlowNode = orgSink;
						while ((originalGraph.incomingEdges(mainFlowNode).size() == 1) && (originalGraph.outgoingEdges(mainFlowNode).size() <= 1)){
							mainFlowNode = Pair.get1(originalGraph.incomingEdges(mainFlowNode).iterator().next());
						}
						if (returnWarnings){
							System.err.println("\"" + originalGraph.getLabel(mainFlowNode) + "\"");
						}
						edgesAdded.add(originalGraph.addEdge(orgSink, mainFlowNode));
					}
				}								
			}
		}else{
			singleSink = sinkNodes.iterator().next();
		}
		referenceEdge = originalGraph.addEdge(singleSink,singleSource);
		Integer cutVertex = originalGraph.isBiconnected(); 
		while (cutVertex != -1){
			if (returnWarnings){
				System.err.println("WARNING: The graph is not bi-connected. Cut vertex: \"" + originalGraph.getLabel(cutVertex) + "\". This will be fixed by pre-processing.");
			}
			originalGraph.splitVertex(cutVertex);
			cutVertex = originalGraph.isBiconnected();
		}
	}
	
	private void postProcessGraph(){
		originalGraph.removeEdges(edgesAdded);
		originalGraph.removeVertices(nodesAdded);
		postProcessTree(spqrTree.getRootNode());
	}
	private void postProcessTree(SPQRTreeNode tn){
		tn.removePersistentGraphEdges(edgesAdded);
		Vector<SPQRTreeNode> childrenToRemove = new Vector<SPQRTreeNode>();
		for (SPQRTreeNode c: tn.getChildNodes()){
			postProcessTree(c);
			if (c.getPersistentGraphEdges().isEmpty()){
				childrenToRemove.add(c);
			}
		}
		tn.getChildNodes().removeAll(childrenToRemove);
	}
	
	private void step1(){
		spqrTree = new SPQRTree(originalGraph, referenceEdge);
		spqrTree.getRootNode().removePersistentGraphEdge(referenceEdge);
		SPQRTreeNode qNodeForReferenceEdge = null;
		for (SPQRTreeNode c: spqrTree.getRootNode().getChildNodes()){
			if (c.getPersistentGraphEdges().contains(referenceEdge)){
				qNodeForReferenceEdge = c;
				break;
			}
		}
		spqrTree.getRootNode().getChildNodes().remove(qNodeForReferenceEdge);
		originalGraph.removeEdge(referenceEdge);
	}
	
	private void step2(SPQRTreeNode tn){
		if (tn.getNodeType() == SPQRNodeType.qnode){
			isFragment.put(tn, true);
		}else{
			tn.removePersistentGraphEdge(tn.getReferenceEdge());
			isFragment.put(tn, originalGraph.isFragment(tn.getPersistentGraphEdges()));
		}		
		for (SPQRTreeNode c: tn.getChildNodes()){
			step2(c);
		}		
	}
	
	private void step3(SPQRTreeNode tn){
		for (SPQRTreeNode c: tn.getChildNodes()){
			step3(c);
		}				
		if (tn.getNodeType() == SPQRNodeType.snode){
			//Step 3a: merge consecutive children that are not fragments
			for (int i = 0; i < tn.getChildNodes().size(); i++){
				if (!isFragment.get(tn.getChildNodes().get(i))){
					MultiSet<Pair<Integer,Integer>> mergedEdges = new MultiSet<Pair<Integer,Integer>>(tn.getChildNodes().get(i).getPersistentGraphEdges());
					Vector<SPQRTreeNode> nodesToMergeIn = new Vector<SPQRTreeNode>();
					boolean fragmentFound = false;
					boolean mergedFragmentFound = false;
					for (int j = i+1; (j < tn.getChildNodes().size()) && !fragmentFound && !mergedFragmentFound; j++){
						fragmentFound = isFragment.get(tn.getChildNodes().get(j));
						if (!fragmentFound){
							mergedEdges.addAll(tn.getChildNodes().get(j).getPersistentGraphEdges());
							nodesToMergeIn.add(tn.getChildNodes().get(j));
							mergedFragmentFound = originalGraph.isFragment(mergedEdges);
						}
					}
					if (mergedFragmentFound){
						for (SPQRTreeNode n: nodesToMergeIn){
							tn.getChildNodes().get(i).mergeIn(n);
							tn.getChildNodes().remove(n);
						}
						isFragment.put(tn.getChildNodes().get(i), true);
					}
				}
			}
			//Step 3b: create a maximal sequence
			if (!isFragment.get(tn)){
				for (int i = 0; i < tn.getChildNodes().size(); i++){
					if (isFragment.get(tn.getChildNodes().get(i))){
						MultiSet<Pair<Integer,Integer>> mergedEdges = new MultiSet<Pair<Integer,Integer>>(tn.getChildNodes().get(i).getPersistentGraphEdges());
						Vector<SPQRTreeNode> childrenInMaxSequence = new Vector<SPQRTreeNode>();
						childrenInMaxSequence.add(tn.getChildNodes().get(i));
						boolean nonFragmentFound = false;
						for (int j = i+1; (j < tn.getChildNodes().size()) && !nonFragmentFound; j++){
							nonFragmentFound = !isFragment.get(tn.getChildNodes().get(j));
							if (!nonFragmentFound){
								mergedEdges.addAll(tn.getChildNodes().get(j).getPersistentGraphEdges());
								childrenInMaxSequence.add(tn.getChildNodes().get(j));
							}
						}
						if (childrenInMaxSequence.size() >= 2){
							SPQRTreeNode newChild = spqrTree.new SPQRTreeNode(childrenInMaxSequence,mergedEdges,SPQRNodeType.snode);
							tn.getChildNodes().add(i, newChild);
							tn.getChildNodes().removeAll(childrenInMaxSequence);
							isFragment.put(newChild, true);
						}
					}
				}				
			}
		}
		if (tn.getNodeType() == SPQRNodeType.pnode){
			//step 3c: create maximal pure, maximal semi-pure and (optionally) maximal directed bond fragments
			MultiSet<Pair<Integer,Integer>> mpbuv = null; 
			MultiSet<Pair<Integer,Integer>> mspbuv = null; 
			MultiSet<Pair<Integer,Integer>> mpbvu = null; 
			MultiSet<Pair<Integer,Integer>> mspbvu = null; 
			MultiSet<Pair<Integer,Integer>> mdb = null;
			MultiSet<MultiSet<Pair<Integer,Integer>>> sepClasses = originalGraph.separationClasses(tn.getPersistentGraphEdges());
			if (isFragment.get(tn)){
				Integer entry = originalGraph.entryNodes(tn.getPersistentGraphEdges()).iterator().next();
				Integer exit = originalGraph.exitNodes(tn.getPersistentGraphEdges()).iterator().next();
				mpbuv = originalGraph.maximalPureBond(sepClasses, entry, exit); 
				mspbuv = originalGraph.maximalSemiPureBond(sepClasses, entry, exit); 
				mdb = originalGraph.maximalDirectedBond(sepClasses, entry, exit);
				if ((mspbuv != null) && (mdb != null) && (mspbuv.containsAll(mdb))){
					mdb = null; //maximal directed bond is not proper
				}
				if ((mpbuv != null) && (mspbuv != null) && (mpbuv.containsAll(mspbuv))){
					mspbuv = null; //maximal semi pure bond is not proper
				}
			}else{
				Vector<Integer> boundaryNodes = new Vector<Integer>(originalGraph.boundaryNodes(tn.getPersistentGraphEdges()));
				Integer U = 0, V = 0;
				if (boundaryNodes.size() == 1){
					U = boundaryNodes.get(0);
				}else if (boundaryNodes.size() == 2){
					U = boundaryNodes.get(0);
					V = boundaryNodes.get(1);
				}else{
					System.err.println("ERROR: Boundary node count is " + boundaryNodes.size() + " for subgraph: {");
					for (Pair<Integer,Integer> e: tn.getPersistentGraphEdges()){
						System.err.println("(\"" + originalGraph.getLabel(Pair.get1(e)) + "\",\"" + originalGraph.getLabel(Pair.get2(e)) + "\")");
					}
					System.err.println("}");
					System.exit(-1);
				}
				mpbuv = originalGraph.maximalPureBond(sepClasses, U, V); 
				mspbuv = originalGraph.maximalSemiPureBond(sepClasses, U, V);
				if (U != V){
					mpbvu = originalGraph.maximalPureBond(sepClasses, V, U); 
					mspbvu = originalGraph.maximalSemiPureBond(sepClasses, V, U);
				}
				if ((mpbuv != null) && (mspbuv != null) && (mpbuv.containsAll(mspbuv))){
					mspbuv = null; //maximal semi pure bond is not proper
				}
				if ((mpbvu != null) && (mspbvu != null) && (mpbvu.containsAll(mspbvu))){
					mspbvu = null; //maximal semi pure bond is not proper
				}				
			}
			if (mpbuv != null){
				if (!mpbuv.containsAll(tn.getPersistentGraphEdges())){
					moveChildrenThatContainPEToNewBondChild(tn,mpbuv);
				}
			}
			if (mpbvu != null){
				if (!mpbvu.containsAll(tn.getPersistentGraphEdges())){
					moveChildrenThatContainPEToNewBondChild(tn,mpbvu);
				}
			}
			if (mspbuv != null){
				if (!mspbuv.containsAll(tn.getPersistentGraphEdges())){
					moveChildrenThatContainPEToNewBondChild(tn,mspbuv);
				}
			}
			if (mspbvu != null){
				if (!mspbvu.containsAll(tn.getPersistentGraphEdges())){
					moveChildrenThatContainPEToNewBondChild(tn,mspbvu);
				}
			}
			if (mdb != null){
				if (!mdb.containsAll(tn.getPersistentGraphEdges())){
					moveChildrenThatContainPEToNewBondChild(tn,mdb);
				}
			}
		}
	}
	private void moveChildrenThatContainPEToNewBondChild(SPQRTreeNode ofNode, MultiSet<Pair<Integer,Integer>> persistentGraphEdges){
		Vector<SPQRTreeNode> childrenToMove = childrenContainedIn(ofNode, persistentGraphEdges);
		SPQRTreeNode newChild = spqrTree.new SPQRTreeNode(childrenToMove,persistentGraphEdges,SPQRNodeType.pnode);
		isFragment.put(newChild, true);
		ofNode.getChildNodes().removeAll(childrenToMove);
		ofNode.addChildNode(newChild);
	}

	private void step4(SPQRTreeNode tn){
		toBeMergedList.put(tn, new Vector<SPQRTreeNode>());
		for (SPQRTreeNode c: tn.getChildNodes()){
			step4(c);
		}
		for (SPQRTreeNode toBeMerged: toBeMergedList.get(tn)){
			tn.addChildNodes(toBeMerged.getChildNodes());
			tn.getChildNodes().remove(toBeMerged);
		}		
		if (!isFragment.get(tn)){
			toBeMergedList.get(tn.getParentNode()).add(tn);
		}
	}
	
	/**
	 * Returns the children of the given node, of which the persistent graph edges are all 
	 * contained in the given persistent graph edges.
	 * 
	 * @param childrenOf Node to check children of.
	 * @param persistentGraphEdges Persistent graph edges in which the children's edges should be contained.
	 * @return A vector of children of the given node. 
	 */
	private Vector<SPQRTreeNode> childrenContainedIn(SPQRTreeNode childrenOf, MultiSet<Pair<Integer,Integer>> persistentGraphEdges){
		Vector<SPQRTreeNode> result = new Vector<SPQRTreeNode>();
		
		for (SPQRTreeNode c: childrenOf.getChildNodes()){
			if (persistentGraphEdges.containsAll(c.getPersistentGraphEdges())){
				result.add(c);
			}
		}
		
		return result;
	}
}
