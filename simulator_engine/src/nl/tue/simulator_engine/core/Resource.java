package nl.tue.simulator_engine.core;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;
import nl.tue.bpmn.concepts.Arc;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Role;
import nl.tue.bpmn.concepts.Type;

public class Resource extends SimProcess{
	static int identifier = 0;
	int myIdentifier;
	ResourceType myType;
	BPMNModel model;
	SimulatorModel simmodel;
	double previousCompletionTime;
		
	public Resource(Model owner, String name, boolean showInTrace, ResourceType type){
		super(owner, name, showInTrace);
		myIdentifier = identifier++;
		myType = type;
		simmodel = (SimulatorModel) owner;
		model = simmodel.getBBPMNModel();
		previousCompletionTime = 0;
	}
	
	public void lifeCycle() {
		ProcessQueue<Resource> myQueue = simmodel.queueForResourceType(myType.getName());
		while (true) {
			for(Role r : myType.getRoles()){
				for (Node n : r.getContainedNodes()) {
					if (n.getType() == Type.Task) {
						ProcessQueue<Case> pc = simmodel.queueForActivity(n.getName());
						if (!pc.isEmpty()) {
							Case c = pc.first();
							if (n.getResourceDependency() != null && !(n.getResourceDependency() == "NONE")) {
								for (Node d : n.getActivityDependency()) {
									if (n.getResourceDependency() == "CASE") {
										if (!(d.equals(n)) && (this.equals(c.getResourceOfNode(d))
												|| c.getResourceOfNode(d).equals(null))) {
											ResourceAllocation(n, pc);
										}
									} else if (n.getResourceDependency() == "SOFD") {
										if (!(d.equals(n)) && (!((this.equals(c.getResourceOfNode(d))))
												|| c.getResourceOfNode(d).equals(null))) {
											ResourceAllocation(n, pc);
										}
									}
								}
							} else {
								// If there is no resource dependency process
								// the case is processed
								ResourceAllocation(n, pc);
							}
						}
					}
				}
			}
			myQueue.insert(this);
			passivate();
		}
	}
	
	public void ResourceAllocation(Node n, ProcessQueue<Case> pc){
		Case ac = pc.removeFirst();
		TimeSpan pt = new TimeSpan(simmodel.processingTimeSampleFor(pc));		
		double activityStartTime = simmodel.presentTime().getTimeAsDouble();
		simmodel.addResourceTypeIdleTime(myType.getName(), previousCompletionTime, activityStartTime);
		hold(pt);
		double activityCompletionTime = simmodel.presentTime().getTimeAsDouble();
		ac.addProcessingTime(activityCompletionTime - activityStartTime);
		simmodel.addActivityProcessingTime(n.getName(), activityStartTime, activityCompletionTime);
		simmodel.addResourceTypeProcessingTime(myType.getName(), activityStartTime, activityCompletionTime);
		previousCompletionTime = activityCompletionTime;
		for(Arc i : n.getIncoming()){
			i.setEnable(false);
		}
		for(Arc o : n.getOutgoing()){
			o.setEnable(true);
		}
		ac.setHistory(n, this);
		ac.activate();
	}
}



