package nl.tue.queueing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.bpmn.concepts.Arc;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Role;
import nl.tue.bpmn.concepts.Type;
import nl.tue.bpmn.concepts.TypeGtw;

/**
 *
 * Pre-conditions:
 * - only structured loops are allowed, i.e.: loops with a single entry and a single exit //TODO: add a check for that
 * - no two tasks have the same name //TODO: add a check for that
 * - no parallelism (this can possibly be 'fixed' by using interleaving; note that this requires that the previous constraint is lifted)
 */
public class QueueingNetwork {

	private BPMNModel bpmnModel;
	private ExecutionNode executionTree;

	//The direct successor and predecessor matrix for the bpmnModel
	//Note that taskA below can also be the start event and taskB can also be an end event.
	private Map<String,Map<String,Double>> taskFlow; //the transitions (taskA, taskB, probability), identified by taskA
	private Map<String,Map<String,Double>> taskFlowR; //the transitions (taskA, taskB, probability), identified by taskB
	
	public QueueingNetwork(BPMNModel bpmnModel) throws Exception{
		this.bpmnModel = bpmnModel;
		
		//Test more detailed syntax constraints
		testSyntaxConstraints(bpmnModel);
		
		//Create the transition probability matrix
		taskFlow = new HashMap<String,Map<String,Double>>();
		taskFlowR = new HashMap<String,Map<String,Double>>();
		for (Node a: bpmnModel.getNodes()) {
			if ((a.getType() == Type.Task) || (a.getType() == Type.Event)) {
				for (Node b: bpmnModel.getNodes()) {
					if ((b.getType() == Type.Task) || (b.getType() == Type.Event)) {
						if (taskFlow.get(a.getName()) == null) {
							taskFlow.put(a.getName(), new HashMap<String,Double>());
						}
						if (taskFlowR.get(b.getName()) == null) {
							taskFlowR.put(b.getName(), new HashMap<String,Double>());
						}
						double probability = pathProbability(a, b, new HashSet<Node>());
						if ((taskFlow.get(a.getName()).get(b.getName()) != null) && (taskFlow.get(a.getName()).get(b.getName()) < probability) && !b.getName().equals("END")) {
							throw new Exception("ERROR: it should not be possible to have multiple paths - consisting only of control nodes - between two nodes.");
						}
						if ((taskFlow.get(a.getName()).get(b.getName()) == null) || (taskFlow.get(a.getName()).get(b.getName()) < probability)) { 
							taskFlow.get(a.getName()).put(b.getName(), probability);					
							taskFlowR.get(b.getName()).put(a.getName(), probability);
						}
					}				
				}
			}
		}
		
		//Compute execution tree
		executionTree = createExecutionTree(bpmnModel.nodeByName("START"), null, new ExecutionNode(null, false, false));
	}
	
	/**
	 * Returns the probability that, when a case has executed the node with label 'from',
	 * the next node to execute is the node with label 'to'.
	 * 
	 * @param from the label of the node to compute the probability from
	 * @param to the label of the node to compute the probability to
	 * @return a probability or null if either one of (the labels of) the nodes cannot be found
	 */
	public Double probability(String from, String to) {
		if (taskFlow.get(from) == null) {
			return null;
		} else {
			return taskFlow.get(from).get(to);
		}
	}

	private ExecutionNode createExecutionTree(Node fromNode, Node toNode, ExecutionNode subTree) {
		if ((fromNode.getType() == Type.Task) || (fromNode.getType() == Type.Event)){
			subTree.children.add(new ExecutionNode(fromNode, false, false));
		}
		
		//If we have reached the end of a loop: done creating execution path
		if (fromNode == toNode) {
			return subTree;
		}
		
		//If we have reached the end of the tree: done creating execution path
		if (fromNode.getOutgoing().isEmpty()) {
			return subTree;
		}
		
		//If the current node is an XOR-split: create a loop for each outgoing arc that starts a loop 
		List<Arc> nonLoops = new ArrayList<Arc>();
		for (Arc outgoing: fromNode.getOutgoing()) {
			if (startsLoop(outgoing)) {
				ExecutionNode loop = new ExecutionNode(fromNode, true, false);
				subTree.children.add(loop);
				createExecutionTree(outgoing.getTarget(), fromNode, loop);
			} else {
				nonLoops.add(outgoing);
			}
		}
		
		//Recurse for all non-loop children
		//If it is a choice
		if (nonLoops.size() > 1) {
			ExecutionNode newSubTree = new ExecutionNode(fromNode, false, true);
			subTree.children.add(newSubTree);
			for (Arc outgoing: nonLoops) {				
				newSubTree.children.add(createExecutionTree(outgoing.getTarget(), toNode, new ExecutionNode(null, false, false)));
			}	
			return newSubTree;
		}else {
			//If it is not a choice
			for (Arc outgoing: nonLoops) {
				createExecutionTree(outgoing.getTarget(), toNode, subTree);
			}
			return subTree;
		}
	}

	
	private String executionTreeToString(ExecutionNode tree) {
		String result = "";
		if (tree.isLoop) {
			result += "LOOP( ";
		}else if (tree.isChoice) {
			result += "CHOICE( ";
		}
		if ((tree.node != null) && (tree.node.getName().length() > 0)) {
			result += tree.node.getName();
			result += " ";
		}
		for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
			ExecutionNode child = children.next();
			result += executionTreeToString(child);
			if (children.hasNext() && (tree.isChoice)) {
				result += "; ";				
			}
		}
		if (tree.isLoop || tree.isChoice) {
			result += ") ";				
		}
		return result;
	}
	/**
	 * Returns a string representation of all possible execution paths through the model
	 * 
	 * @return a string representation of all possible execution paths through the model
	 */
	public String executionPathsToString() {
		return executionTreeToString(executionTree);
	}
	
	private boolean startsLoop(Arc a) {		
		//the source of the arc is an XOR-split		
		//the arc will always return on itself
		return ((a.getSource().getType() == Type.Gateway) && (a.getSource().getTypeGtw() == TypeGtw.XSplit) && alwaysReturnsOn(a,a,new HashSet<Arc>())); 
	}
	private boolean alwaysReturnsOn(Arc a, Arc on, Set<Arc> visited) {		
		if (a.getTarget().getOutgoing().contains(on)) {
			return true;
		}
		if (a.getTarget().getOutgoing().isEmpty()) {
			return false;
		}
		if (visited.contains(a)) {
			return true;
		}
		visited.add(a);
		boolean always = true;
		for (Arc b: a.getTarget().getOutgoing()) {
			always = alwaysReturnsOn(b, on, visited);
			if (!always) break;
		}
		return always;
	}
	
	private void testSyntaxConstraints(BPMNModel bpmnModel) throws Exception{		
		//There should be a 1-to-1 correspondence between roles and resource types 
		boolean resourceTypesEqualRoles = true;
		if (bpmnModel.getResourceTypes().size() != bpmnModel.getRoles().size()) {
			resourceTypesEqualRoles = false;
		}
		for (Role r: bpmnModel.getRoles()) {
			boolean roleFound = false;
			for (ResourceType rt: bpmnModel.getResourceTypes()) {
				if (r.getName().equals(rt.getName())) {
					roleFound = true;
					break;
				}
			}
			if (!roleFound) {
				resourceTypesEqualRoles = false;
				break;
			}
		}
		if (!resourceTypesEqualRoles) {
			throw new Exception("ERROR: queueing models can only be used in BPMN models that just use roles (no resource types).");
		}
		
		for (Node n: bpmnModel.getNodes()) {
			//There should be no intermediate events, 
			if ((n.getType() == Type.Event) && (n.getIncoming().size() > 0) && (n.getOutgoing().size() > 0)) {
				throw new Exception("ERROR: queueing models can only be used in BPMN models that do not have intermediate events.");				
			}
			//There should be no parallel gateways
			if ((n.getType() == Type.Gateway) && ((n.getTypeGtw() == TypeGtw.ParJoin) || (n.getTypeGtw() == TypeGtw.ParSplit))) {
				throw new Exception("ERROR: queueing models can only be used in BPMN models that do not have parallel gateways.");								
			}
		}		
	}
	
	private Double pathProbability(Node a, Node b, Set<Node> visitedGateways) throws Exception {
		for (Arc arc: a.getOutgoing()) {
			double transitionProbability = 1.0; //If there is no annotation on the arc, we assume a probability of 1.0
			if ((arc.getCondition() != null) && (arc.getCondition().endsWith("%"))) {
				transitionProbability *= Double.parseDouble(arc.getCondition().substring(0, arc.getCondition().length()-1))/100;
			}
			if (arc.getTarget().equals(b)) {
				return transitionProbability;
			}else if (visitedGateways.contains(arc.getTarget())) {
				//This is to prevent infinite loops
				throw new Exception("ERROR: queueing models cannot be used in BPMN models that have loops with only control nodes.");
			}else if (arc.getTarget().getType() == Type.Gateway) {
				if (a.getType() == Type.Gateway) {
					visitedGateways.add(a);
				}
				double followupProbability = pathProbability(arc.getTarget(), b, visitedGateways);
				if (followupProbability > 0.0) {
					return transitionProbability * followupProbability;
				}
			}
		}
		return 0.0;
	}

	class ExecutionNode {		
		List<ExecutionNode> children;
		Node node;
		boolean isLoop;
		boolean isChoice;
		
		public ExecutionNode(Node node, boolean isLoop, boolean isChoice) {
			this.children = new ArrayList<ExecutionNode>();
			this.node = node;
			this.isLoop = isLoop;
			this.isChoice = isChoice;
		}
	}
}