package nl.tue.simulator_engine.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
import nl.tue.bpmn.concepts.Type;
import nl.tue.bpmn.concepts.TypeGtw;
import nl.tue.bpmn.concepts.Arc;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Role;
import nl.tue.bpmn.parser.ConditionEvaluator;
import nl.tue.bpmn.concepts.BPMNModel;

public class Case extends SimProcess{
	static int  identifier = 0;
	int myIdentifier;
	BPMNModel model;
	SimulatorModel simmodel;
	Map<Node, Resource> history;
	
	double startTime;
	double totalProcessingTime;
	
	public Case(Model owner, String name, boolean showInTrace){
		super(owner, name, showInTrace);
		simmodel = (SimulatorModel) owner;
		myIdentifier = identifier++;
		history = new HashMap<Node,Resource>();
		model = simmodel.getBBPMNModel();
	}
	
	public void lifeCycle(){
		startTime = simmodel.presentTime().getTimeAsDouble();
		totalProcessingTime = 0;
		
		//Instantiate the case properties
		ConditionEvaluator ce = simmodel.instantiateCase();
				
		/*
		 * At the creation of each case all start events are enabled. Therefore
		 * all outgoing arcs from the start event are enabled.
		 */
		
		for(Node n: model.getNodes()){
			if(n.getType() == Type.Event && n.getIncoming().size() == 0){
				for(Arc s: n.getOutgoing()){
					s.setEnable(true);
				}
			}
		}
		
		boolean end = false;
		
		while(!end){
			
			/*
			 * Case terminates after an end event is reached, end event is
			 * reached when an incoming arc at the end event is enabled.
			 */
			
			for (Node n : model.getNodes()) {
				if (n.getType() == Type.Event && n.getOutgoing().size() == 0) {
					for (Arc e : n.getIncoming()) {
						if (e.getEnable() != null) {
							end = e.getEnable();
						}
					}
				}
			}
			
			/*
			 * Check all gateways if they are enabled. If a gateway is enabled
			 * all incoming arcs are disabled and the outgoing arcs are enabled
			 * based upon the type of gateway. Re-check all gateways if one gateway
			 * is enabled.
			 */
			
			boolean gtw = true;
			
			while(gtw){
				gtw = false;
				for(Node n : model.getNodes()){
					if(n.getType() == Type.Gateway){
						boolean s_enbl = false;	//single enabled
						boolean a_enbl = true;	//all enabled
						for(Arc i : n.getIncoming()){
							if(i.getEnable() && !s_enbl){
								s_enbl = true;
							}
							if(!i.getEnable() && a_enbl){
								a_enbl = false;
							}
						}
						if(n.getTypeGtw() == TypeGtw.ParJoin && a_enbl){
							enable_all(n);
							gtw = true;
						}else if(n.getTypeGtw() != TypeGtw.XSplit && s_enbl){
							enable_all(n);
							gtw = true;
						}else{
							for(Arc x : n.getOutgoing()){
								if(ce.evaluate(x.getCondition())){
									for(Arc i: n.getIncoming()){
										i.setEnable(false);
									}
									x.setEnable(true);
									gtw = true;
								}
							}
						}
					}
				}
			}
			
			/*
			 * After all the instant moves on the model due to the gateways the
			 * enabled tasks need to be handled by activation and allocation.
			 */
			
			for(Node t : model.getNodes()){
				if (t.getType() == Type.Task) {
					for (Arc i : t.getIncoming()) {
						if (i.getEnable() != null) {
							if (i.getEnable()) {
								set_res(t);
								break;
							}
						}
					}
				}
			}
		}
		
		simmodel.addSojournTime(startTime, simmodel.presentTime().getTimeAsDouble());
		simmodel.addProcessingTime(startTime, totalProcessingTime);
	}
	
	/**
	 * Method enables all outgoing arcs of a node and disables all incoming arcs
	 * @param n is the node for which this action is performed
	 */
	
	public void enable_all(Node n){
		for(Arc a : n.getIncoming()){
			a.setEnable(false);
		}
		for(Arc a : n.getOutgoing()){
			a.setEnable(true);
		}
	}
	
	/**
	 * Method puts case in the queue for node n, where a resource is selected if one is idle
	 * @param n is the node for which this action is performed
	 */
	public void set_res(Node n){
		//Select queue for the Node
		ProcessQueue<Case> q = simmodel.queueForActivity(n.getName());
		q.insert(this);
		//Disable all incoming arcs in the task
		for(Arc a : n.getIncoming()){
			a.setEnable(false);
		}
		//Obtain the role for the node n
		Role rs = null;
		for(Role r : model.getRoles()){
			for(Node c : r.getContainedNodes()){
				if(c.equals(n)){
					rs = r;
				}
			}
		}
		//Obtain the resource queues for the role for the node n
		Set <ProcessQueue<Resource>> qr = new HashSet<ProcessQueue<Resource>>();
		for(ResourceType rt : model.getResourceTypes()){
			for(Role gr : rt.getRoles()){
				if(gr.equals(rs)){
					qr.add(simmodel.queueForResourceType(rt.getName()));
					break;
				}
			}
		}
		//Activate the resource if there is one idle
		for(ProcessQueue<Resource> qrr : qr){
			if(!qrr.isEmpty()){
				Resource res = qrr.removeFirst();
				res.activate();
				break;
			}
		}
		passivate();
	}

	public Resource getResourceOfNode(Node n) {
		return history.get(n);
	}

	public void setHistory(Node n, Resource r) {
		history.put(n, r);
	}
	
	public void addProcessingTime(double processingTime){
		totalProcessingTime += processingTime;
	}
}
