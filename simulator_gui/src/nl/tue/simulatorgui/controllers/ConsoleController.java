package nl.tue.simulatorgui.controllers;

import nl.tue.simulatorgui.core.Environment;
import nl.tue.simulatorgui.views.ConsoleView;
import nl.tue.simulatorgui.executor.EvaluationResult;
import nl.tue.simulatorgui.executor.EvaluationResult.ResultType;

public class ConsoleController{

	ConsoleView view;
	
	public ConsoleController() {
		this.view = new ConsoleView(this);
	}	

	public void printEntry(String entry) {
		view.printEntry(entry);
	}

	public void openWindow() {
		Environment.getMainController().addWindow(view);
		view.setVisible(true);
	}
	
	public void closeWindow() {
		view.setVisible(false);
		Environment.getMainController().removeWindow(view);
	}
	
	public void log(String text){
		view.printError("\n" + text + "\n");
	}

	public void printError(String error) {
		view.printEntry("\n");
		view.printError(error);
		view.printEntry("\n");
	}

	public void printMessage(String msg) {
		view.printEntry("\n");
		view.printMessage(msg);
		view.printEntry("\n");
	}

	public void printResult(EvaluationResult er) {
		if (er.getType() == ResultType.RESULT){
			view.printMessage(er.getResult());
			view.printEntry("\n");
		}else if (er.getType() == ResultType.ERROR){
			view.printError(er.getResult());
			view.printEntry("\n");
		}
	}
}
