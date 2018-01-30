package nl.tue.simulator_engine.core;

import java.util.ArrayList;

import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;
import desmoj.extensions.visualization2d.engine.model.List;
import nl.tue.bpmn.concepts.Arc;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.concepts.ResourceType;
import nl.tue.bpmn.concepts.Role;
import nl.tue.bpmn.concepts.Type;
import nl.tue.util.Util;

public class Resource extends SimProcess {
	static int identifier = 0;
	int myIdentifier;
	ResourceType myType;
	BPMNModel model;
	SimulatorModel simmodel;
	double previousCompletionTime;

	public Resource(Model owner, String name, boolean showInTrace, ResourceType type) {
		super(owner, name, showInTrace);
		myIdentifier = identifier++;
		myType = type;
		simmodel = (SimulatorModel) owner;
		model = simmodel.getBBPMNModel();
		previousCompletionTime = 0;
	}

	@Override
	public void lifeCycle() {
		ProcessQueue<Resource> myQueue = simmodel.queueForResourceType(myType.getName());
		while (true) {
			//Random(myQueue);
			MostTardy(myQueue);
		}
	}

	public void ResourceAllocation(Node n, ProcessQueue<Case> pc) {
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
		for (Arc i : n.getIncoming()) {
			i.setEnable(false);
		}
		for (Arc o : n.getOutgoing()) {
			o.setEnable(true);
		}
		ac.setHistory(n, this);
		ac.activate();
	}

	public void Random(ProcessQueue<Resource> myQueue) {
		boolean doneSomething = false;
		for (Role r : myType.getRoles()) {
			Node[] arrnode = r.getContainedNodes().toArray(new Node[r.getContainedNodes().size()]);
			Integer[] rand = Util.randomOrder(r.getContainedNodes().size());
			for (int j = 0; j < rand.length; j++) {
				int random = rand[j];
				Node n = arrnode[random];
				if (n.getType() == Type.Task) {
					ProcessQueue<Case> pc = simmodel.queueForActivity(n.getName());
					if (!pc.isEmpty()) {
						Case c = pc.first();
						if (n.getResourceDependency() != null && !(n.getResourceDependency().equals("NONE"))) {
							for (Node d : n.getActivityDependency()) {
								if (n.getResourceDependency().equals("CASE")) {
									if (!(c.getResourceOfNode(d) == null)) {
										if (!(d.equals(n)) && (this.equals(c.getResourceOfNode(d)))) {
											ResourceAllocation(n, pc);
											doneSomething = true;
											break;
										}
									} else {
										ResourceAllocation(n, pc);
										doneSomething = true;
										break;

									}
								} else if (n.getResourceDependency().equals("SOFD")) {
									if (!(c.getResourceOfNode(d) == null)) {
										if (!(d.equals(n)) && (!(this.equals(c.getResourceOfNode(d))))) {
											ResourceAllocation(n, pc);
											doneSomething = true;
											break;
										}
									} else {
										ResourceAllocation(n, pc);
										doneSomething = true;
										break;
									}
								}
							}
						} else if (n.getResourceDependency() == null || (n.getResourceDependency().equals("NONE"))) {
							// If there is no resource dependency process
							// the case is processed
							ResourceAllocation(n, pc);
							doneSomething = true;
						}
					}
				}
			}
			if (doneSomething) {
				break;
			}
		}
		if (!doneSomething) {
			myQueue.insert(this);
			passivate();
		}
	}

	public void MostTardy(ProcessQueue<Resource> myQueue) {
		boolean doneSomething = false;
		for (Role r : myType.getRoles()) {
			// Check for each node if it is allowed to process it
			Node[] arrnode = r.getContainedNodes().toArray(new Node[r.getContainedNodes().size()]);
			// Create arrnode of those nodes and iterate over these nodes to find the most
			// tardy one
			ArrayList<Node> alwnode = new ArrayList<Node>();
			for (int k = 0; k < arrnode.length; k++) {
				Node n = arrnode[k];
				if (n.getType() == Type.Task) {
					ProcessQueue<Case> pc = simmodel.queueForActivity(n.getName());
					if (!pc.isEmpty()) {
						Case c = pc.first();
						if (n.getResourceDependency() != null && !(n.getResourceDependency().equals("NONE"))) {
							for (Node d : n.getActivityDependency()) {
								if (n.getResourceDependency().equals("CASE")) {
									if (!(c.getResourceOfNode(d) == null)) {
										if (!(d.equals(n)) && (this.equals(c.getResourceOfNode(d)))) {
											alwnode.add(n);
											break;
										}
									} else {
										alwnode.add(n);
										break;

									}
								} else if (n.getResourceDependency().equals("SOFD")) {
									if (!(c.getResourceOfNode(d) == null)) {
										if (!(d.equals(n)) && (!(this.equals(c.getResourceOfNode(d))))) {
											alwnode.add(n);
											break;
										}
									} else {
										alwnode.add(n);
										break;
									}
								}
							}
						} else if (n.getResourceDependency() == null || (n.getResourceDependency().equals("NONE"))) {
							// If there is no resource dependency process
							// the case is processed
							alwnode.add(n);
						}
					}
				}
			}

			Node mtn = null;
			if (alwnode != null) {
				double cmax = 0.0;
				for (Node nd : alwnode) {
					if (nd.getType() == Type.Task) {
						ProcessQueue<Case> pqc = simmodel.queueForActivity(nd.getName());
						if (!pqc.isEmpty()) {
							if (mtn == null) {
								mtn = nd;
								cmax = pqc.first().startTime;
							} else {
								double nmax = pqc.first().startTime;
								if (nmax < cmax) {
									mtn = nd;
									cmax = nmax;
								}
							}
						}
					}
				}
				if(mtn != null) {
					ProcessQueue<Case> pq = simmodel.queueForActivity(mtn.getName());
					ResourceAllocation(mtn, pq);
					doneSomething = true;	
				}
			}
	
			if (doneSomething) {
				break;
			}

		}

		if (!doneSomething) {
			myQueue.insert(this);
			passivate();
		}
	}
}
