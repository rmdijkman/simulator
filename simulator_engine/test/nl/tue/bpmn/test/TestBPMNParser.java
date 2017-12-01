package nl.tue.bpmn.test;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.concepts.Node;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;

public class TestBPMNParser {

	@Test
	public void testDocumentation() throws BPMNParseException {
		//Parse the Signavio model
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/Correct.bpmn");
		BPMNModel model = parser.getParsedModel();
		assertEquals("amount: N(100000, 10000) ; risklevel: [{high, 10%}, {medium, 20%}, {low, 70%}]}]", model.getInformationAttributes());
		boolean startEventFound = false;
		boolean taskFound = false;
		for (Node n: model.getNodes()){
			if (n.getIncoming().isEmpty()){
				assertEquals("exp(5)", n.getInterArrivalTimeDistribution());
				startEventFound = true;
			}
			if (n.getName().equals("Wash a plate")){
				assertEquals("exp(10)", n.getProcessingTimeDistribution());
				taskFound = true;
			}
		}
		assertTrue(startEventFound);
		assertTrue(taskFound);
	}

}
