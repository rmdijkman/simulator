package nl.tue.simulator_engine.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.TimeInstant;
import nl.tue.bpmn.concepts.BPMNModel;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.bpmn.parser.BPMNParser;
import nl.tue.queueing.QueueingNetwork;
import nl.tue.util.Util;

public class Simulator {

	public static String runSimulator(String filePath, long duration, long nrReplications, long warmup) throws BPMNParseException {
		return Simulator.runSimulator(filePath, duration, nrReplications, warmup, false, null);
	}
	
	public static String runSimulator(String filePath, long duration, long nrReplications, long warmup, boolean queueing, ReplicationMonitor monitor) throws BPMNParseException {
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
		QueueingNetwork qn = null;
		if (queueing) {
			qn = new QueueingNetwork(model);
		}

		Double soj[] = Util.lowerMeanUpper(sojournTimes);
		Double pro[] = Util.lowerMeanUpper(processingTimes);
		Double wai[] = Util.lowerMeanUpper(waitingTimes);
		result += "<h2>Process details</h2>";
		result += tableHead(new String[]{"", (qn==null)?null:"expected", "mean", "95% CI"});
		result += tableRow(new String[]{"sojourn time", (qn==null)?null:Util.round((qn==null)?null:qn.eS(),2).toString(), Util.round(soj[1],2).toString(), Util.round(soj[0],2) + "-" + Util.round(soj[2],2)}); 
		result += tableRow(new String[]{"processing time", (qn==null)?null:Util.round((qn==null)?null:qn.eB(),2).toString(), Util.round(pro[1],2).toString(), Util.round(pro[0],2) + "-" + Util.round(pro[2],2)}); 
		result += tableRow(new String[]{"waiting time", (qn==null)?null:Util.round((qn==null)?null:qn.eW(),2).toString(), Util.round(wai[1],2).toString(), Util.round(wai[0],2) + "-" + Util.round(wai[2],2)}); 
		result += tableTail();
		
		result += pieChart(Util.round(wai[1],2),Util.round(pro[1],2));

		result += "<h2>Activity details</h2>";
		
		result += tableHead(new String[]{"activity", (qn==null)?null:"expected processing time", "mean processing time", "95% CI"});
		String activityNames[] = new String[activityProcessingTimes.size()];
		double processingValues[] = new double[activityProcessingTimes.size()];
		double processingErrors[] = new double[activityProcessingTimes.size()];
		int activityIndex = 0;
		for (Map.Entry<String, List<Double>> apt: activityProcessingTimes.entrySet()){
			Double aPro[] = Util.lowerMeanUpper(apt.getValue());			
			result += tableRow(new String[]{apt.getKey(), (qn==null)?null:Util.round(qn.eB(apt.getKey()),2).toString(), Util.round(aPro[1],2).toString(), Util.round(aPro[0],2) + "-" + Util.round(aPro[2],2)});
			activityNames[activityIndex] = apt.getKey().replaceAll("[^a-zA-Z ]", "");
			processingValues[activityIndex] = Util.round(aPro[1],2);
			processingErrors[activityIndex] = Util.round(aPro[1]-aPro[0],2);
			activityIndex++;
		}
		result += tableTail();
		result += barChart("processingGraph", "processing time", activityNames, processingValues, processingErrors);

		result += tableHead(new String[]{"activity", (qn==null)?null:"expected waiting time", "mean waiting time", "95% CI"});
		double waitingValues[] = new double[activityWaitingTimes.size()];
		double waitingErrors[] = new double[activityWaitingTimes.size()];
		activityIndex = 0;
		for (Map.Entry<String, List<Double>> awa: activityWaitingTimes.entrySet()){
			Double aWai[] = Util.lowerMeanUpper(awa.getValue());			
			result += tableRow(new String[]{awa.getKey(), (qn==null)?null:Util.round(qn.eW(awa.getKey()),2).toString(), Util.round(aWai[1],2).toString(), Util.round(aWai[0],2) + "-" + Util.round(aWai[2],2)});
			waitingValues[activityIndex] = Util.round(aWai[1],2);
			waitingErrors[activityIndex] = Util.round(aWai[1]-aWai[0],2);
			activityIndex++;
		}
		result += tableTail();
		result += barChart("waitingGraph", "waiting time", activityNames, waitingValues, waitingErrors);

		result += "<h2>Resource details</h2>";
		result += tableHead(new String[]{"resource type", (qn==null)?null:"expected utilization rate", "mean utilization rate", "95% CI"});			
		String resourceNames[] = new String[resourceUtilizationRates.size()];
		double rValues[] = new double[resourceUtilizationRates.size()];
		double rErrors[] = new double[resourceUtilizationRates.size()];
		int resourceIndex = 0;
		for (Map.Entry<String, List<Double>> rur: resourceUtilizationRates.entrySet()){
			Double uti[] = Util.lowerMeanUpper(rur.getValue());			
			result += tableRow(new String[]{rur.getKey(), (qn==null)?null:Util.round(qn.rho(rur.getKey()),2).toString(), Util.round(uti[1],2).toString(), Util.round(uti[0],2) + "-" + Util.round(uti[2],2)});
			resourceNames[resourceIndex] = rur.getKey();
			rValues[resourceIndex] = Util.round(uti[1],2);
			rErrors[resourceIndex] = Util.round(uti[1]-uti[0],2); 
			resourceIndex++;
		}
		result += tableTail();
		result += barChart("utilizationGraph", "utilization rate", resourceNames, rValues, rErrors);
		
		return result + documentTail();
	}
	
	private static String pieChart(double waitingTime, double processingTime){
		String result = "";
		result += "<div id=\"timegraph\" style=\"width: 600px; height: 300px;\"></div>";

		result += "<script>" +
				"trace1 = {" +
				"labels: ['waiting time', 'processing time']," + 
				"marker: {line: {color: 'transparent'}}," + 
				"type: 'pie'," +
				"textinfo: 'value'," +
				"values: ["+waitingTime+","+processingTime+"]" +
				"};" +
				"data = [trace1];" +
				"layout = {hovermode: 'closest',margin: {r: 10,t: 25,b: 40,l: 60},showlegend: true};" +
				"var configuration = {displayModeBar: false, displaylogo: false, showTips: true};" +
				"Plotly.newPlot('timegraph', data, layout, configuration);";

		result += "</script>";
				
		return result;

	}
	
	private static String barChart(String uniqueId, String yAxisLabel, String labels[], double values[], double errors[]) {
		String result = "";
		result += "<div id=\""+uniqueId+"\" style=\"width: 100%; height: 500px;\"></div>";

		result += "<script>" +
				"var trace1 = {" +
				"x: "+toString(labels)+"," +
				"y: "+Arrays.toString(values)+"," +
				"name: 'Control'," +
				"error_y: {" +
				"type: 'data'," +
				"array: "+Arrays.toString(errors)+"," +
				"visible: true" +
				"}," +
				"type: 'bar'" +
				"};" +
				"var data = [trace1];" +
				"var layout = {barmode: 'group', yaxis: {title: '"+yAxisLabel+"'}};" +
				"var configuration = {displayModeBar: false, displaylogo: false, showTips: true};" +
				"Plotly.newPlot('"+uniqueId+"', data, layout, configuration);" +
				"</script>";
		
		return result;
	}
	
	public static String toString(String strings[]){
		String result = "[";
		for (int i = 0; i < strings.length; i++){
			result += "'" + strings[i] + "'";
			if (i < strings.length - 1){
				result += ",";
			}
		}
		return result + "]";
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
				+ "<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>"
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
			if (columnName != null) {
				result += "<th>" + columnName + "</th>";
			}
		}
		result += "</tr>"
				+ "</thead>"
				+ "<tbody>";
		return result;
	}
	
	public static String tableRow(String[] cellValues){
		String result = "<tr>";
		for (String cellValue: cellValues){
			if (cellValue != null) {
				result += "<td>" + cellValue + "</td>";
			}
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
