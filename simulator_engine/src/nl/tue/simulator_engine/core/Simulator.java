package nl.tue.simulator_engine.core;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;
import nl.tue.util.Util;

public class Simulator {

	public static String runSimulator(String filePath, long duration, int nrReplications) throws BPMNParseException {
		BPMNParser parser = new BPMNParser();
		parser.parse(filePath);
		BPMNModel model = parser.getParsedModel();

		String result = "";

		for (int a = 0; a < nrReplications; a++) {
			SimulatorModel simmodel = new SimulatorModel(null, "", true, true, model);

			Experiment experiment = new Experiment("Experiment", TimeUnit.SECONDS, TimeUnit.MINUTES, null);
			experiment.setSeedGenerator(System.currentTimeMillis());
			simmodel.connectToExperiment(experiment);

			experiment.setShowProgressBar(false);
			experiment.stop(new TimeInstant(duration, TimeUnit.MINUTES));
			experiment.setSilent(true);

			experiment.start();
			experiment.report();
			experiment.finish();

			double meanSojournTime = simmodel.meanSojournTime();
			double meanProcessingTime = simmodel.meanProcessingTime();
			result += "Mean sojourn time: " + Util.round(meanSojournTime,2) + "\n"; 
			result += "Mean processing time: " + Util.round(meanProcessingTime,2) + "\n"; 
			result += "Mean waiting time: " + Util.round(meanSojournTime - meanProcessingTime,2) + "\n";

			Map<String, Double> meanActivityWaitingTimes = simmodel.meanActivityWaitingTimes(); 
			for (Map.Entry<String, Double> mapt: simmodel.meanActivityProcessingTimes().entrySet()){
				result += "Activity '" + mapt.getKey() + "' processing time " + Util.round(mapt.getValue(),2) + ", waiting time " + Util.round(meanActivityWaitingTimes.get(mapt.getKey()),2) + "\n";  
			}

			Map<String, Double> meanResourceTypeIdleTimes = simmodel.meanResourceTypeIdleTimes(); 
			for (Map.Entry<String, Double> mrtit: simmodel.meanResourceTypeProcessingTimes().entrySet()){
				double idleTime = meanResourceTypeIdleTimes.get(mrtit.getKey());
				double processingTime = mrtit.getValue();
				result += "Resource type '" + mrtit.getKey() + "' utilization rate " + Util.round(processingTime/(processingTime+idleTime),2) + "\n";  
			}
		}		

		return result;
	}

}
