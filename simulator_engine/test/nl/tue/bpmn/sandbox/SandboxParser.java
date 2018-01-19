package nl.tue.bpmn.sandbox;

import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;

public class SandboxParser {

	public static void main(String[] args) throws BPMNParseException {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/Simplified Specification Test.bpmn");
		BPMNModel model = parser.getParsedModel();
	}

}
