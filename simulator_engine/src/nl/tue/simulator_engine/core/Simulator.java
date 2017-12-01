package nl.tue.simulator_engine.core;

import java.util.concurrent.TimeUnit;
import desmoj.core.report.Message;
import desmoj.core.report.MessageReceiver;
import desmoj.core.report.Reporter;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;


public class Simulator implements MessageReceiver {

	int nrMessages = 0;
	boolean hasErrors = false;
	static int nrrep = 10;
	static double za2 = 1.96;
	
	static BPMNModel model;
	static SimulatorModel simmodel;
	static Experiment experiment;
	static Simulator dataCollector;
	static int duration;

	public static String runSimulator() throws BPMNParseException {
		BPMNParser parser = new BPMNParser();
		parser.parse("./resources/tests/Correct.bpmn");
		model = parser.getParsedModel();
		//model.actDepBySet();
		
		for (int a = 0; a < nrrep; a++) {
			simmodel = new SimulatorModel(null, "", true, true, model);
			dataCollector = new Simulator();
			experiment = new Experiment("Experiment", TimeUnit.SECONDS, TimeUnit.MINUTES, null);
			simmodel.connectToExperiment(experiment);
			duration = 1440;

			experiment.setShowProgressBar(false);
			experiment.stop(new TimeInstant(duration, TimeUnit.MINUTES));
			
			experiment.start();
			experiment.report();
			experiment.finish();

		}
		//TODO Fix Return
		return "TODO";
	}
		@Override
		public void receive(Message m) {
			nrMessages ++;
		}

		@Override
		public void receive(Reporter r) {
		}
}
