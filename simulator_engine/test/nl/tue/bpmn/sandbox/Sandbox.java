package nl.tue.bpmn.sandbox;

import java.util.concurrent.TimeUnit;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.ProcessQueue;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;
import nl.tue.bpmn.parser.ConditionEvaluator;
import nl.tue.simulator_engine.core.Case;
import nl.tue.simulator_engine.core.Resource;
import nl.tue.simulator_engine.core.SimulatorModel;

public class Sandbox {

	public static void main(String[] args) throws BPMNParseException {
		
		//Parse the Signavio model
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/Correct.bpmn");
		BPMNModel model = parser.getParsedModel();
		
		//Create a simulator model:
		SimulatorModel sm = new SimulatorModel(null, "", true, true, model);
		Experiment experiment = new Experiment("Little's Law Experiment", TimeUnit.SECONDS, TimeUnit.MINUTES, null);
		sm.connectToExperiment(experiment);
		
		//Now you have access to all the things you need:
		
		//- the queue that stores cases queueing for an activity		
		ProcessQueue<Case> aq = sm.queueForActivity("Wash a plate");
		System.out.println("Queue found with name: " + aq.getName());
		
		//- the queue that stores resources of a particular type
		ProcessQueue<Resource> rq = sm.queueForResourceType("Washing");
		System.out.println("Queue found with name: " + rq.getName());
		ProcessQueue<Resource> rq2 = sm.queueForResourceType("Draining,Washing");
		System.out.println("Queue found with name: " + rq2.getName());
		
		//- a method to instantiate new cases
		ConditionEvaluator ce = sm.instantiateCase();
		//  you can use this to evaluate whether a particular condition holds for the case
		System.out.println("For the case that was generated, the condition is " + ce.evaluate("Amount < 100000"));
		
		//- a method to sample the distribution of the interarrival time
		System.out.println("Interarrival time sample " + sm.interarrivalTimeSample());
		
		//- methods to sample the distribution of the processing time of a particular activity
		System.out.println("Processing time sample " + sm.processingTimeSampleFor("Wash a plate"));
		
		//- you can also find stuff by the queue instead of the activity label
		System.out.println("Processing time sample " + sm.processingTimeSampleFor(aq));
		System.out.println("The activity belonging to the queue " + sm.activityLabelForQueue(aq));
		System.out.println("The resource type name belonging to the queue " + sm.resourceTypeNameForQueue(rq));		
	}

}
