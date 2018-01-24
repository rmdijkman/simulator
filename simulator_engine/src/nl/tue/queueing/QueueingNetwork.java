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
import nl.tue.bpmn.parser.DistributionEvaluator;
import nl.tue.util.Matrix;

/**
 *
 * Pre-conditions:
 * - only structured loops are allowed, i.e.: loops with a single entry and a single exit //TODO: add a check for that
 * - no two tasks have the same name //TODO: add a check for that (or lift this restriction, which should be possible)
 * - no parallelism (this can possibly be 'fixed' by using interleaving; note that this requires that the previous constraint is lifted)
 */
public class QueueingNetwork {

	private List<String> tasks;
	private double lambda;
	private Map<String,Double> eBTask; //the expected processing time of each task
	private Map<String,Double> eB2Task; //the expected processing time of each task
	private Map<String,Double> lambdaTask; //the lambda of each task
	
	private ExecutionNode executionTree;

	//The direct successor and predecessor matrix for the bpmnModel
	//elementA can be a task or start event, elementB can be a task or end event.
	private Map<String,Map<String,Double>> taskFlow; //the transitions (elementA, elementB, probability), identified by elementA
	private Map<String,Map<String,Double>> taskFlowR; //the transitions (elementA, elementB, probability), identified by elementB
	
	public QueueingNetwork(BPMNModel bpmnModel) throws Exception{		
		//Test more detailed syntax constraints
		testSyntaxConstraints(bpmnModel);
		
		//Create the adjacency matrix 
		createAdjacencyMatrix(bpmnModel);
		
		//Compute execution tree
		executionTree = createExecutionTree(bpmnModel.nodeByName("START"), null, new ExecutionNode(null, false, false));
		
		//Solve the queueing network
		solve();
	}
	
	private void solve() {
		//Consider one queue per resource type/role, let role denote that queue and let tasks_role denote the tasks in each role
		//Step 1. Calculate lambda_task for each task as follows:
		//        let A, B, ... be tasks, let START be the start event, and let probability_(x,y) be the probability of transitioning from x to y.  
		//		  this leads to a system of linear equations that we can solve, we have the equations:
		//        for each task t: \lambda_t = \lambda*probability_(START,t) + \lambda_A*probability_(A,t) + \lambda_B*probability_(B,t) + ...
		double aArray[][] = new double[tasks.size()][];
		double bArray[][] = new double[tasks.size()][];
		int rowPos = 0;
		for (String task: tasks) {
			double taskVector[] = new double[tasks.size()];
			int colPos = 0;
			for (String preceedingTask: tasks) {
				taskVector[colPos] = probability(preceedingTask, task) - (preceedingTask.equals(task)?1.0:0.0);
				colPos++;
			}
			aArray[rowPos] = taskVector;
			bArray[rowPos] = new double[1];
			bArray[rowPos][0] = - probability("START", task) * lambda;
			rowPos++;
		}
		Matrix aMatrix = new Matrix(aArray);
		Matrix bMatrix = new Matrix(bArray);
		Matrix xMatrix = aMatrix.solve(bMatrix); 
		
		lambdaTask = new HashMap<String,Double>();
		for (int i = 0; i < tasks.size(); i++) {
			lambdaTask.put(tasks.get(i), xMatrix.get(i, 0));
		}
		
		//Step 2. Calculate lambda_role as the sum of lambda_task for task \in tasks_role
		//Step 3. Calculate E(B_role) and E(B^2_role) as the weighted average of E(B_task) and E(B^2_task) for task \in tasks_role
		//Step 4. We can now calculate \rho_role, E(R_role), \Pi_W_role, E(W_role)
		//Step 5. Calculate E(S), E(W), E(B) for the entire process as follows:
	}
		
	/**
	 * Returns the interarrival rate of the process as a whole (i.e. the start event)
	 * 
	 * @return the interarrival rate
	 */
	public double lambda() {
		return lambda;
	}
	
	/**
	 * Returns the interarrival rate of the task with the given label or null if it is not specified 
	 * 
	 * @param task the label of a task
	 * @return an interarrival rate
	 */
	public Double lambda(String task) {
		return lambdaTask.get(task);
	}
	
	public Double eB(String task) {
		return eBTask.get(task);
	}

	public Double eB2(String task) {
		return eB2Task.get(task);
	}

	private void createAdjacencyMatrix(BPMNModel bpmnModel) throws Exception {
		tasks = new ArrayList<String>();
		eBTask = new HashMap<String,Double>();
		eB2Task = new HashMap<String,Double>();
		taskFlow = new HashMap<String,Map<String,Double>>();
		taskFlowR = new HashMap<String,Map<String,Double>>();
		
		//Create the transition probability matrix
		for (Node a: bpmnModel.getNodes()) {
			if (a.getType() == Type.Task) {
				tasks.add(a.getName());
				Double ept = DistributionEvaluator.getLambda(a.getProcessingTimeDistribution());
				if (ept == null) {
					throw new Exception("ERROR: currently only exponential distributions are allowed for processing times. Task '" + a.getName() + "' does not have an exponential processing time.");
				}
				eBTask.put(a.getName(), 1.0/ept);
				eB2Task.put(a.getName(), 1.0/Math.pow(ept, 2.0));
			}else if ((a.getType() == Type.Event) && (a.getIncoming().isEmpty())){
				Double iad = DistributionEvaluator.getLambda(a.getInterArrivalTimeDistribution());				
				if (iad == null) {
					throw new Exception("ERROR: currently only exponential distributions are allowed for interarrival times.");
				}
				lambda = iad;
			}
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