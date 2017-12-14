package nl.tue.bpmn.sandbox;

import nl.tue.bpmn.parser.BPMNParseException;

public class SimulatorRun {

	public static void main(String[] args) throws BPMNParseException{
		String prt = nl.tue.simulator_engine.core.Simulator.runSimulator("./resources/tests/Sequential Duo.bpmn", 1440, 1, 1);
		System.out.println(prt);
	}
}
