package nl.tue.simulatorgui.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import nl.tue.simulatorgui.executor.EvaluationResult.ResultType;
import nl.tue.simulatorgui.core.Environment;

public class SimulatorScript {
	
	File file;
	String fileToLoad;

	public SimulatorScript(File file){
		this.file = file;		
	}
	
	public void load() throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		setFileToLoad(br.readLine());
		br.close();
		fr.close();
	}

	public void save() throws IOException {
		PrintWriter writer = new PrintWriter(file);
		writer.println(fileToLoad);
		writer.close();
	}
	
	public EvaluationResult execute() {
		//TODO: This is where the execution of the simulator should happen
		//The results can be sent to a browser window. The browser window can display any HTML that describes the result of the simulation.
		Environment.getMainController().newOrUpdatedBrowser(file.getName(), "Platte tekst.");
		return new EvaluationResult("The executor does not work yet.", ResultType.ERROR);
	}
	
	public String getFileToLoad(){
		return fileToLoad;
	}
	
	public void setFileToLoad(String fileToLoad){
		this.fileToLoad = fileToLoad;
	}
}
