package nl.tue.bpmn.sandbox;

import nl.tue.bpmn.parser.BPMNParseException;

public class SimulatorRun {

	public static void main(String[] args) throws BPMNParseException{
		String prt = nl.tue.simulator_engine.core.Simulator.runSimulator("./resources/tests/Simplified Specification Test.bpmn", 1440, 1, 0);
		System.out.println(prt);
	}
}
