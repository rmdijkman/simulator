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
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.DistributionEvaluator;
import nl.tue.util.Matrix;
import nl.tue.util.QueueingFormulas;

/**
 *
 * Pre-conditions:
 * - only structured loops are allowed, i.e.: loops with a single entry and a single exit //TODO: add a check for that
 * - no two tasks have the same name //TODO: add a check for that (or lift this restriction, which should be possible)
 * - no parallelism (this can possibly be 'fixed' by using interleaving; note that this requires that the previous constraint is lifted)
 */
public class QueueingNetwork {

	private BPMNModel bpmnModel;
	
	private List<String> tasks;
	private String startEvent;
	private double lambda;
	private Map<String,Double> eBTask; //the expected processing time of each task
	private Map<String,Double> eB2Task; //the expected processing time of each task
	private Map<String,Double> lambdaTask; //the lambda of each task
	private Map<String,Double> lambdaRole; //the lambda of each role
	private Map<String,Double> eBRole; //the expected processing time of each task
	private Map<String,Double> eB2Role; //the expected processing time of each task
	private Map<String,Double> rhoRole; //the expected processing time of each task
	private Map<String,Double> eWRole; //the expected processing time of each task
	private double eS;
	private double eB;
	private double eW;
	
	private Map<String,String> task2Role;
	
	private ExecutionNode executionTree;

	//The direct successor and predecessor matrix for the bpmnModel
	//elementA can be a task or start event, elementB can be a task or end event.
	private Map<String,Map<String,Double>> taskFlow; //the transitions (elementA, elementB, probability), identified by elementA
	private Map<String,Map<String,Double>> taskFlowR; //the transitions (elementA, elementB, probability), identified by elementB
	
	public QueueingNetwork(BPMNModel bpmnModel) throws BPMNParseException{
		this.bpmnModel = bpmnModel;
		
		//Test more detailed syntax constraints
		testSyntaxConstraints(bpmnModel);
		
		task2Role = new HashMap<String,String>();
		for (Role r: bpmnModel.getRoles()) {
			for (Node n: r.getContainedNodes()) {
				if (n.getType() == Type.Task) {
					task2Role.put(n.getName(), r.getName());
				}
			}
		}
		
		//Create the adjacency matrix 
		createAdjacencyMatrix(bpmnModel);
		
		//Compute execution tree
		executionTree = createExecutionTree(bpmnModel.nodeByName(startEvent), null, new ExecutionNode(null, false, false, 1.0));
		
		//Solve the queueing network
		solve();
	}
	
	private void solve() throws BPMNParseException {
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
			bArray[rowPos][0] = - probability(startEvent, task) * lambda;
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
		lambdaRole = new HashMap<String,Double>();
		for (Role r: bpmnModel.getRoles()) {
			double lambda_r = 0.0;
			for (Node n: r.getContainedNodes()) {
				if (n.getType() == Type.Task) {
					lambda_r += lambdaTask.get(n.getName());
				}
			}
			lambdaRole.put(r.getName(), lambda_r);
		}
		
		//Step 3. Calculate E(B_role) and E(B^2_role) as the weighted average of E(B_task) and E(B^2_task) for task \in tasks_role
		eBRole = new HashMap<String,Double>();
		eB2Role = new HashMap<String,Double>();
		for (Role r: bpmnModel.getRoles()) {
			double lambda_r = lambdaRole.get(r.getName());
			double eB_r = 0.0;
			double eB2_r = 0.0;
			for (Node n: r.getContainedNodes()) {
				if (n.getType() == Type.Task) {
					eB_r += (lambdaTask.get(n.getName())/lambda_r) * eBTask.get(n.getName());
					eB2_r += (lambdaTask.get(n.getName())/lambda_r) * eB2Task.get(n.getName());
				}
			}
			eBRole.put(r.getName(), eB_r);
			eB2Role.put(r.getName(), eB2_r);
		}		
		
		//Step 4. We can now calculate \rho_role, E(W_role)
		rhoRole = new HashMap<String,Double>();
		eWRole = new HashMap<String,Double>();
		for (ResourceType rt: bpmnModel.getResourceTypes()) {			
			String role = rt.getName(); //Note that we can only do this, because we checked in the syntax constraints that roles = resourcetypes
			double c = rt.getNumber();
			double rho = QueueingFormulas.rho(lambdaRole.get(role), 1.0/eBRole.get(role), c);
			if (rho >= 1.0) {
				throw new BPMNParseException("Role '" + role + "' has a utilization rate " + rho + " greater than or equal to 1.0.");
			}
			rhoRole.put(role, rho);
			eWRole.put(role, QueueingFormulas.EWMMc(eBRole.get(role), eB2Role.get(role), rho, c));			
		}		
		
		//Step 5. Calculate E(S), E(W), E(B) for the entire process as follows
		//        assume that we have a block structured model
		//		  E(S) of a choice between block A and block B = probability_{block A} * E(S_{block A}) + probability_{block B} * E(S_{block B})
		//		  E(S) of a sequence of block A and block B = E(S_{block A}) + E(S_{block B})
		//		  E(S) of a loop of block A = 
		//		  E(S) of a trivial block that contains a single queue A = E(S_A)
		//        E(W) and E(B) can be calculated analogously
		eS = executionTreeToES(executionTree);
		eB = executionTreeToEB(executionTree);
		eW = executionTreeToEW(executionTree);
	}
	
	public double rho(String role) {
		return rhoRole.get(role);
	}
	
	public double eS() {
		return eS;
	}

	public double eB() {
		return eB;
	}

	public double eW() {
		return eW;
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
	
	public Double eW(String task) {
		return eWRole.get(task2Role.get(task));
	}

	public Double eB2(String task) {
		return eB2Task.get(task);
	}

	private void createAdjacencyMatrix(BPMNModel bpmnModel) throws BPMNParseException {
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
					throw new BPMNParseException("ERROR: currently only exponential distributions are allowed for processing times. Task '" + a.getName() + "' does not have an exponential processing time.");
				}
				eBTask.put(a.getName(), 1.0/ept);
				eB2Task.put(a.getName(), 2.0/Math.pow(ept, 2.0));
			}else if ((a.getType() == Type.Event) && (a.getIncoming().isEmpty())){
				startEvent = a.getName();
				Double iad = DistributionEvaluator.getLambda(a.getInterArrivalTimeDistribution());				
				if (iad == null) {
					throw new BPMNParseException("ERROR: currently only exponential distributions are allowed for interarrival times.");
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
							throw new BPMNParseException("ERROR: it should not be possible to have multiple paths - consisting only of control nodes - between two nodes.");
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
			subTree.children.add(new ExecutionNode(fromNode, false, false, 1.0));
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
				ExecutionNode loopSubTree = new ExecutionNode(fromNode, true, false, transitionProbability(outgoing));
				subTree.children.add(loopSubTree);
				loopSubTree.children.add(createExecutionTree(outgoing.getTarget(), fromNode, new ExecutionNode(null, false, false, 1.0)));
			} else {
				nonLoops.add(outgoing);
			}
		}
		
		//Recurse for all non-loop children
		//If it is a choice
		if (nonLoops.size() > 1) {
			ExecutionNode choiceSubTree = new ExecutionNode(fromNode, false, true, 1.0);
			subTree.children.add(choiceSubTree);
			for (Arc outgoing: nonLoops) {
				//TODO This must be changed for the situation of a choice/loop start that have the same XOR-split as a starting point. 
				//In that case, the probabilities of the choice part must be normalized: let 'a' be the loop path and 'b','c' be the choice paths, 
				//then probability 'b' must be normalized as probability 'b'/(probability 'b' + probability 'c')
				//TODO It is even better to refactor this into using a RPST
				choiceSubTree.children.add(createExecutionTree(outgoing.getTarget(), toNode, new ExecutionNode(null, false, false, transitionProbability(outgoing))));
			}
			return choiceSubTree;
		}else {
			//If it is not a choice
			for (Arc outgoing: nonLoops) {
				createExecutionTree(outgoing.getTarget(), toNode, subTree);
			}
			return subTree;
		}
	}
	
	private double executionTreeToES(ExecutionNode tree) {		
		double result = 0.0;
		if ((tree.node != null) && (tree.node.getType() == Type.Task)){
			result += eBTask.get(tree.node.getName());
			result += eWRole.get(task2Role.get(tree.node.getName()));
		}
		if (tree.isChoice) {
			for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
				ExecutionNode child = children.next();
				result += child.probability * executionTreeToES(child);
			}
		} else if (tree.isLoop) {
			ExecutionNode child = tree.children.iterator().next(); //By construction there is one child
			result += (tree.probability/(1.0 - tree.probability)) * executionTreeToES(child);
		} else {
			for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
				ExecutionNode child = children.next();
				result += executionTreeToES(child);
			}			
		}
		return result;
	}

	private double executionTreeToEW(ExecutionNode tree) {		
		double result = 0.0;
		if ((tree.node != null) && (tree.node.getType() == Type.Task)){
			result += eWRole.get(task2Role.get(tree.node.getName()));
		}
		if (tree.isChoice) {
			for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
				ExecutionNode child = children.next();
				result += child.probability * executionTreeToEW(child);
			}
		} else if (tree.isLoop) {
			ExecutionNode child = tree.children.iterator().next(); //By construction there is one child
			result += (tree.probability/(1.0 - tree.probability)) * executionTreeToEW(child);
		} else {
			for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
				ExecutionNode child = children.next();
				result += executionTreeToEW(child);
			}			
		}
		return result;
	}

	private double executionTreeToEB(ExecutionNode tree) {		
		double result = 0.0;
		if ((tree.node != null) && (tree.node.getType() == Type.Task)){
			result += eBTask.get(tree.node.getName());
		}
		if (tree.isChoice) {
			for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
				ExecutionNode child = children.next();
				result += child.probability * executionTreeToEB(child);
			}
		} else if (tree.isLoop) {
			ExecutionNode child = tree.children.iterator().next(); //By construction there is one child
			result += (tree.probability/(1.0 - tree.probability)) * executionTreeToEB(child);
		} else {
			for (Iterator<ExecutionNode> children = tree.children.iterator(); children.hasNext(); ) {
				ExecutionNode child = children.next();
				result += executionTreeToEB(child);
			}			
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
	
	private void testSyntaxConstraints(BPMNModel bpmnModel) throws BPMNParseException{		
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
			throw new BPMNParseException("ERROR: queueing models can only be used in BPMN models that just use roles (no resource types).");
		}		
		for (Node n: bpmnModel.getNodes()) {
			//There should be no intermediate events, 
			if ((n.getType() == Type.Event) && (n.getIncoming().size() > 0) && (n.getOutgoing().size() > 0)) {
				throw new BPMNParseException("ERROR: queueing models can only be used in BPMN models that do not have intermediate events.");				
			}
			//There should be no parallel gateways
			if ((n.getType() == Type.Gateway) && ((n.getTypeGtw() == TypeGtw.ParJoin) || (n.getTypeGtw() == TypeGtw.ParSplit))) {
				throw new BPMNParseException("ERROR: queueing models can only be used in BPMN models that do not have parallel gateways.");								
			}
		}
		for (Arc a: bpmnModel.getArcs()) {
			if ((a.getCondition() != null) && (a.getCondition().length() != 0) && !a.getCondition().endsWith("%")) {
				throw new BPMNParseException("ERROR: queueing models can only be used in BPMN models with probabilities on arcs (no advanced conditions).");												
			}
		}
	}
	
	private Double transitionProbability(Arc arc) {
		double transitionProbability = 1.0; //If there is no annotation on the arc, we assume a probability of 1.0
		if ((arc.getCondition() != null) && (arc.getCondition().endsWith("%"))) {
			transitionProbability *= Double.parseDouble(arc.getCondition().substring(0, arc.getCondition().length()-1))/100;
		}
		return transitionProbability;
	}
	
	private Double pathProbability(Node a, Node b, Set<Node> visitedGateways) throws BPMNParseException {
		for (Arc arc: a.getOutgoing()) {
			double transitionProbability = transitionProbability(arc);
			if (arc.getTarget().equals(b)) {
				return transitionProbability;
			}else if (visitedGateways.contains(arc.getTarget())) {
				//This is to prevent infinite loops
				throw new BPMNParseException("ERROR: queueing models cannot be used in BPMN models that have loops with only control nodes.");
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
		double probability;
		
		public ExecutionNode(Node node, boolean isLoop, boolean isChoice, double probability) {
			this.children = new ArrayList<ExecutionNode>();
			this.node = node;
			this.isLoop = isLoop;
			this.isChoice = isChoice;
			this.probability = probability;
		}
	}
}