package nl.tue.queueing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tue.bpmn.concepts.Arc;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Role;
import nl.tue.bpmn.concepts.Type;
import nl.tue.bpmn.concepts.TypeGtw;

public class QueueingNetwork {
	
	//Note that taskA below can also be the start event and taskB can also be an end event.
	private Map<String,Map<String,Double>> taskFlow; //the transitions (taskA, taskB, probability), identified by taskA
	private Map<String,Map<String,Double>> taskFlowR; //the transitions (taskA, taskB, probability), identified by taskB
	
	public QueueingNetwork(BPMNModel bpmnModel) throws Exception{
		testSyntaxConstraints(bpmnModel);
		
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
}
