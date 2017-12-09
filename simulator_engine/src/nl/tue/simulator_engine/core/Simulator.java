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

	public static String runSimulator(String filePath, long duration, long nrReplications, long warmup) throws BPMNParseException {
		BPMNParser parser = new BPMNParser();
		parser.parse(filePath);
		BPMNModel model = parser.getParsedModel();

		String result = documentHead();

		for (int a = 0; a < nrReplications; a++) {
			SimulatorModel simmodel = new SimulatorModel(null, "", true, true, model, warmup);

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
			
			result += "<h2>Process details</h2>";
			result += tableHead(new String[]{"", "mean"});
			result += tableRow(new String[]{"sojourn time", Util.round(meanSojournTime,2).toString()}); 
			result += tableRow(new String[]{"processing time", Util.round(meanProcessingTime,2).toString()}); 
			result += tableRow(new String[]{"waiting time", Util.round(meanSojournTime - meanProcessingTime,2).toString()});
			result += tableTail(); 

			result += "<h2>Activity details</h2>";
			result += tableHead(new String[]{"activity", "mean processing time", "mean waiting time"});			
			Map<String, Double> meanActivityWaitingTimes = simmodel.meanActivityWaitingTimes(); 
			for (Map.Entry<String, Double> mapt: simmodel.meanActivityProcessingTimes().entrySet()){
				result += tableRow(new String[]{mapt.getKey(), Util.round(mapt.getValue(),2).toString(), Util.round(meanActivityWaitingTimes.get(mapt.getKey()),2).toString()});  
			}
			result += tableTail();

			result += "<h2>Resource details</h2>";
			result += tableHead(new String[]{"resource type", "mean utilization rate"});			
			Map<String, Double> meanResourceTypeIdleTimes = simmodel.meanResourceTypeIdleTimes(); 
			for (Map.Entry<String, Double> mrtit: simmodel.meanResourceTypeProcessingTimes().entrySet()){
				Double idleTime = meanResourceTypeIdleTimes.get(mrtit.getKey());
				idleTime = (idleTime == null)?0:idleTime; 
				double processingTime = mrtit.getValue();
				result += tableRow(new String[]{mrtit.getKey(), Util.round(processingTime/(processingTime+idleTime),2).toString()});  
			}
			result += tableTail();
			
		}		

		return result + documentTail();
	}
	
	public static String documentHead(){
		String result = "<!DOCTYPE html>"
				+ "<html lang=\"en\">"
				+ ""
				+ "<head>"
				+ "<title>Bootstrap Example</title>"
				+ "<meta charset=\"utf-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>"
				+ "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>"
				+ "</head>"
				+ ""
				+ "<body>"
				+ "<div class=\"container\">";
		return result;
	}
	
	public static String documentTail(){
		String result = "</div>"
				+ "</body>"
				+ "</html>";
		return result;
	}
	
	public static String tableHead(String[] columnNames){
		String result = "<table class=\"table\">"
				+ "<thead>"
				+ "<tr>";
		for (String columnName: columnNames){
			result += "<th>" + columnName + "</th>";
		}
		result += "</tr>"
				+ "</thead>"
				+ "<tbody>";
		return result;
	}
	
	public static String tableRow(String[] cellValues){
		String result = "<tr>";
		for (String cellValue: cellValues){
			result += "<td>" + cellValue + "</td>";
		}
		result += "</tr>";
		return result;
	}
	
	public static String tableTail(){
		String result = "</tbody>"
				+ "</table>";
		return result;	
	}
}
