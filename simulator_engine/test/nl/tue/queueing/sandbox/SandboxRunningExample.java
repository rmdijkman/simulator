package nl.tue.queueing.sandbox;

import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;
import nl.tue.queueing.QueueingNetwork;

public class SandboxRunningExample {

	public static void main(String args[]) throws BPMNParseException {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Running Example.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		System.out.println(qn.eS());
		System.out.println(qn.eB());
		System.out.println(qn.eW());
	}
	
}
