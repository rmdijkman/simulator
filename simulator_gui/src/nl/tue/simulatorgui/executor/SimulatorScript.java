package nl.tue.simulatorgui.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import nl.tue.simulatorgui.executor.EvaluationResult.ResultType;
import nl.tue.bpmn.parser.BPMNParseException;
import nl.tue.simulator_engine.core.Simulator;
import nl.tue.simulatorgui.core.Environment;

public class SimulatorScript {
	
	File file;
	String fileToLoad;
	long simulationLength;
	long replications;

	public SimulatorScript(File file){
		this.file = file;		
	}
	
	public void load() throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		setFileToLoad(br.readLine());
		setSimulationLength(Long.parseLong(br.readLine()));
		setReplications(Long.parseLong(br.readLine()));
		br.close();
		fr.close();
	}

	public void save() throws IOException {
		PrintWriter writer = new PrintWriter(file);
		writer.println(fileToLoad);
		writer.println(Long.toString(simulationLength));
		writer.println(Long.toString(replications));
		writer.close();
	}
	
	public EvaluationResult execute() {
		try {
			String result = Simulator.runSimulator(fileToLoad, simulationLength, replications);
			Environment.getMainController().newOrUpdatedBrowser(file.getName(), result);
		} catch (BPMNParseException e) {
			return new EvaluationResult("There was an error reading the BPMN file: " + e.getMessage(), ResultType.ERROR);
		}
		return new EvaluationResult("", ResultType.UNDEFINED);
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
}
