package nl.tue.bpmn.sandbox;

import nl.tue.bpmn.parser.BPMNParseException;

public class SimulatorRun {

	public static void main(String[] args) throws BPMNParseException{
		String prt = nl.tue.simulator_engine.core.Simulator.runSimulator("./resources/tests/Correct.bpmn", 1440, 1, 100);
		System.out.println(prt);
	}
}
