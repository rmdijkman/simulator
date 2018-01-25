package nl.tue.queueing;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParser;

public class TestQueueingComputation {

	@Test
	public void testExercise11Lambda() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 11.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 1.0, qn.lambda("Register Sales Order"));
		assertEquals((Double) 1.0, qn.lambda("Pre-process Sales Order"));
	}

	@Test
	public void testExercise12Lambda() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 12.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 1.0, qn.lambda("Register Sales Order"));
		assertEquals((Double) 0.9, qn.lambda("Pre-process Sales Order"));
	}

	@Test
	public void testExercise13Lambda() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 13.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 2.0, qn.lambda("Register Sales Order"));
		assertEquals((Double) 1.8, qn.lambda("Pre-process Sales Order"));
	}

	@Test
	public void testExercise14Lambda() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 14.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 2.0, round(qn.lambda("Handle Simple Appeal"),1));
		assertEquals((Double) 3.0, round(qn.lambda("Inform Citizen"),1));
		assertEquals((Double) 1.0, round(qn.lambda("Handle Complex Appeal"),1));
	}
	
	@Test
	public void testExercise11EBTask() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 11.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 0.33, round(qn.eB("Register Sales Order"),2));
		assertEquals((Double) 0.5, qn.eB("Pre-process Sales Order"));

		assertEquals((Double) round(2.0/9.0,2), round(qn.eB2("Register Sales Order"),2));
		assertEquals((Double) round(1.0/2.0,2), round(qn.eB2("Pre-process Sales Order"),2));
	}

	@Test
	public void testExercise12EBTask() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 12.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 0.5, qn.eB("Register Sales Order"));
		assertEquals((Double) 0.5, qn.eB("Pre-process Sales Order"));

		assertEquals((Double) round(1.0/2.0,2), round(qn.eB2("Register Sales Order"),2));
		assertEquals((Double) round(1.0/2.0,2), round(qn.eB2("Pre-process Sales Order"),2));
	}

	@Test
	public void testExercise13EBTask() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 13.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 0.5, qn.eB("Register Sales Order"));
		assertEquals((Double) 0.5, qn.eB("Pre-process Sales Order"));

		assertEquals((Double) round(1.0/2.0,2), round(qn.eB2("Register Sales Order"),2));
		assertEquals((Double) round(1.0/2.0,2), round(qn.eB2("Pre-process Sales Order"),2));
	}

	@Test
	public void testExercise14EBTask() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Exercise 14.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 0.375, round(qn.eB("Handle Simple Appeal"),3));
		assertEquals((Double) 0.125, round(qn.eB("Inform Citizen"),3));
		assertEquals((Double) 0.5, qn.eB("Handle Complex Appeal"));

		assertEquals((Double) round(9.0/32.0,2), round(qn.eB2("Handle Simple Appeal"),2));
		assertEquals((Double) round(1.0/32.0,2), round(qn.eB2("Inform Citizen"),2));
		assertEquals((Double) round(1.0/2.0,2), round(qn.eB2("Handle Complex Appeal"),2));
	}
	
	private Double round(double number, int digits) {
		return new Double(Math.round(number*Math.pow(10.0,digits))/Math.pow(10.0,digits));
	}
}
