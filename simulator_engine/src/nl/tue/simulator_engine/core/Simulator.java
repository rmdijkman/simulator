package nl.tue.simulator_engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		return Simulator.runSimulator(filePath, duration, nrReplications, warmup, null);
	}
	
	public static String runSimulator(String filePath, long duration, long nrReplications, long warmup, ReplicationMonitor monitor) throws BPMNParseException {
		BPMNParser parser = new BPMNParser();
		parser.parse(filePath);
		BPMNModel model = parser.getParsedModel();

		String result = documentHead();
		
		List<Double> sojournTimes = new ArrayList<Double>();
		List<Double> processingTimes = new ArrayList<Double>();
		List<Double> waitingTimes = new ArrayList<Double>();
		
		Map<String,List<Double>> activityProcessingTimes = new HashMap<String,List<Double>>();
		Map<String,List<Double>> activityWaitingTimes = new HashMap<String,List<Double>>();
		Map<String,List<Double>> resourceUtilizationRates = new HashMap<String,List<Double>>();		

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

			double modelSojournTime = simmodel.meanSojournTime();
			double modelProcessingTime = simmodel.meanProcessingTime();
			double modelWaitingTime = modelSojournTime - modelProcessingTime;
			sojournTimes.add(modelSojournTime);
			processingTimes.add(modelProcessingTime);
			waitingTimes.add(modelWaitingTime);
			
			Map<String, Double> modelActivityProcessingTimes = simmodel.meanActivityProcessingTimes(); 
			for (Map.Entry<String, Double> mapt: modelActivityProcessingTimes.entrySet()){
				List<Double> modelProcessingTimes = activityProcessingTimes.get(mapt.getKey());
				if (modelProcessingTimes == null){
					modelProcessingTimes = new ArrayList<Double>();					
				}
				modelProcessingTimes.add(mapt.getValue());
				activityProcessingTimes.put(mapt.getKey(), modelProcessingTimes);
			}

			Map<String, Double> modelActivityWaitingTimes = simmodel.meanActivityWaitingTimes(); 
			for (Map.Entry<String, Double> mawt: modelActivityWaitingTimes.entrySet()){
				List<Double> modelWaitingTimes = activityWaitingTimes.get(mawt.getKey());
				if (modelWaitingTimes == null){
					modelWaitingTimes = new ArrayList<Double>();					
				}
				modelWaitingTimes.add(mawt.getValue());
				activityWaitingTimes.put(mawt.getKey(), modelWaitingTimes);
			}

			Map<String, Double> modelResourceIdleTimes = simmodel.meanResourceTypeIdleTimes(); 
			Map<String, Double> modelResourceProcessingTimes = simmodel.meanResourceTypeProcessingTimes(); 
			for (Map.Entry<String, Double> mrpt: modelResourceProcessingTimes.entrySet()){
				List<Double> mrptList = resourceUtilizationRates.get(mrpt.getKey());
				if (mrptList == null){
					mrptList = new ArrayList<Double>();					
				}
				Double idleTime = modelResourceIdleTimes.get(mrpt.getKey());
				idleTime = (idleTime == null)?0:idleTime;
				Double processingTime = mrpt.getValue();
				mrptList.add(processingTime/(processingTime+idleTime));
				resourceUtilizationRates.put(mrpt.getKey(), mrptList);
			}
			if (monitor != null){
				monitor.setCurrentReplication(a);
			}
		}		

		Double soj[] = Util.lowerMeanUpper(sojournTimes);
		Double pro[] = Util.lowerMeanUpper(processingTimes);
		Double wai[] = Util.lowerMeanUpper(waitingTimes);
		result += "<h2>Process details</h2>";
		result += tableHead(new String[]{"", "mean", "95% CI"});
		result += tableRow(new String[]{"sojourn time", Util.round(soj[1],2).toString(), Util.round(soj[0],2) + "-" + Util.round(soj[2],2)}); 
		result += tableRow(new String[]{"processing time", Util.round(pro[1],2).toString(), Util.round(pro[0],2) + "-" + Util.round(pro[2],2)}); 
		result += tableRow(new String[]{"waiting time", Util.round(wai[1],2).toString(), Util.round(wai[0],2) + "-" + Util.round(wai[2],2)}); 
		result += tableTail(); 

		result += "<h2>Activity details</h2>";
		result += tableHead(new String[]{"activity", "mean processing time", "95% CI", "mean waiting time", "95% CI"});			
		for (Map.Entry<String, List<Double>> apt: activityProcessingTimes.entrySet()){
			Double aPro[] = Util.lowerMeanUpper(apt.getValue());			
			Double aWai[] = {0.0, 0.0, 0.0};
			if (activityWaitingTimes.get(apt.getKey()) != null){
				aWai = Util.lowerMeanUpper(activityWaitingTimes.get(apt.getKey()));
			}
			result += tableRow(new String[]{apt.getKey(), Util.round(aPro[1],2).toString(), Util.round(aPro[0],2) + "-" + Util.round(aPro[2],2), Util.round(aWai[1],2).toString(), Util.round(aWai[0],2) + "-" + Util.round(aWai[2],2)});  
		}
		result += tableTail();

		result += "<h2>Resource details</h2>";
		result += tableHead(new String[]{"resource type", "mean utilization rate", "95% CI"});			
		for (Map.Entry<String, List<Double>> rur: resourceUtilizationRates.entrySet()){
			Double uti[] = Util.lowerMeanUpper(rur.getValue());			
			result += tableRow(new String[]{rur.getKey(), Util.round(uti[1],2).toString(), Util.round(uti[0],2) + "-" + Util.round(uti[2],2)});  
		}
		result += tableTail();
		
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
