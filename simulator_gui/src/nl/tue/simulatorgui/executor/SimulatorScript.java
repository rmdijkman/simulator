package nl.tue.simulatorgui.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import nl.tue.simulatorgui.executor.EvaluationResult.ResultType;
import nl.tue.simulatorgui.views.SimulatorWithProgressDialog;
import nl.tue.simulatorgui.core.Environment;

public class SimulatorScript{
	
	File file;
	String fileToLoad;
	long simulationLength;
	long replications;
	long warmup;

	public SimulatorScript(File file){
		this.file = file;		
	}
	
	public void load() throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		setFileToLoad(br.readLine());
		setSimulationLength(Long.parseLong(br.readLine()));
		setReplications(Long.parseLong(br.readLine()));
		setWarmup(Long.parseLong(br.readLine()));
		br.close();
		fr.close();
	}

	public void save() throws IOException {
		PrintWriter writer = new PrintWriter(file);
		writer.println(fileToLoad);
		writer.println(Long.toString(simulationLength));
		writer.println(Long.toString(replications));
		writer.println(Long.toString(warmup));
		writer.close();
	}
	
	public EvaluationResult execute() {
		new SimulatorWithProgressDialog(fileToLoad, simulationLength, replications, warmup, this);
		return new EvaluationResult("", ResultType.UNDEFINED);
	}
	
	public void callBackResult(String result){
		Environment.getMainController().newOrUpdatedBrowser(file.getName(), result);		
	}
	
	public void callBackException(String exception){
		Environment.getMainController().printResult(new EvaluationResult(exception, ResultType.ERROR));
	}
	
	public String getFileToLoad(){
		return fileToLoad;
	}
	
	public void setFileToLoad(String fileToLoad){
		this.fileToLoad = fileToLoad;
	}

	public long getSimulationLength() {
		return simulationLength;
	}

	public void setSimulationLength(long simulationLength) {
		this.simulationLength = simulationLength;
	}

	public long getReplications() {
		return replications;
	}

	public void setReplications(long replications) {
		this.replications = replications;
	}

	public long getWarmup() {
		return warmup;
	}

	public void setWarmup(long warmup) {
		this.warmup = warmup;
	}
}
