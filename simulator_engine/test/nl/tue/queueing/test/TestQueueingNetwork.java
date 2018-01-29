package nl.tue.queueing.test;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParser;
import nl.tue.queueing.QueueingNetwork;

public class TestQueueingNetwork {

	@Test
	public void testChoice() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Choice.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 1.0, qn.probability("START", "A"));
		assertEquals((Double) 0.0, qn.probability("START", "B"));
		assertEquals((Double) 0.7, qn.probability("A", "B"));
		assertEquals((Double) 0.3, qn.probability("A", "END"));
	}

	@Test
	public void testRepeatLoop() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Repeat Loop.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 1.0, qn.probability("START", "A"));
		assertEquals((Double) 0.0, qn.probability("START", "B"));
		assertEquals((Double) 0.7, qn.probability("A", "B"));
		assertEquals((Double) 0.3, qn.probability("A", "A"));
	}

	@Test
	public void testWhileLoop() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/While Loop.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 0.3, qn.probability("START", "A"));
		assertEquals((Double) 0.7, qn.probability("START", "B"));
		assertEquals((Double) 0.7, qn.probability("A", "B"));
		assertEquals((Double) 0.3, qn.probability("A", "A"));
	}

	@Test
	public void testDoubleChoice() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Double Choice.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
		
		assertEquals((Double) 0.3, qn.probability("A", "END"));
		assertEquals((Double) 0.42, qn.probability("A", "B"));
		assertEquals((Double) 0.28, new Double(Math.round(qn.probability("A", "C")*100.0)/100.0)); //Difficult because of a double rounding error
	}
	
	@Test
	public void testDoubleChoicePaths() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Double Choice.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);

		assertEquals("START A CHOICE( END ; CHOICE( B END ; C END ) ) ", qn.executionPathsToString());
	}

	@Test
	public void testRepeatLoopPaths() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Repeat Loop.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
	
		assertEquals("START A LOOP( A ) B END ", qn.executionPathsToString());
	}

	@Test
	public void testWhileLoopPaths() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/While Loop.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
	
		assertEquals("START LOOP( A ) B END ", qn.executionPathsToString());
	}

	@Test
	public void testNestedLoopPaths() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Nested Loop.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
	
		assertEquals("START A B LOOP( B ) C LOOP( A B LOOP( B ) C ) END ", qn.executionPathsToString());
	}

	@Test
	public void testLoopNestedChoicePaths() throws Exception {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/queueing/Loop Nested Choice.bpmn");
		BPMNModel model = parser.getParsedModel();
		QueueingNetwork qn = new QueueingNetwork(model);
	
		assertEquals("START A CHOICE( B LOOP( A CHOICE( B ; C ) ) END ; C LOOP( A CHOICE( B ; C ) ) END ) ", qn.executionPathsToString());
	}
}
