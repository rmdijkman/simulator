package nl.tue.simulatorgui.controllers;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import nl.tue.simulatorgui.core.Environment;
import nl.tue.simulatorgui.core.Main;
import nl.tue.simulatorgui.core.viewstate.ViewState;
import nl.tue.simulatorgui.core.viewstate.ViewStateSerializable;
import nl.tue.simulatorgui.views.MainView;
import nl.tue.simulatorgui.executor.EvaluationResult;

public class MainController implements ViewStateSerializable{
	
	private MainView view;
	private Map<String, BrowserController> graphWindows;
	
	private boolean consoleVisible;
	private boolean editorVisible;
	
	public MainController() {
		this.view = new MainView();
	}

	public void makeVisible(){
		view.setVisible(true);
		graphWindows = new HashMap<String, BrowserController>();
	}
		
	public void openConsole() {
		ConsoleController cc = Environment.getConsoleController();
		cc.openWindow();
		view.setMenuConsoleSelected(true);
		consoleVisible = true;
	}

	public void closeConsole() {
		ConsoleController cc = Environment.getConsoleController();
		cc.closeWindow();
		view.setMenuConsoleSelected(false);
		consoleVisible = false;
	}

	public void printEntry(String entry) {
		Environment.getConsoleController().printEntry(entry);
	}

	public void newOrUpdatedBrowser(String fileName, String script) {
		BrowserController bc = graphWindows.get(fileName);
		if (bc == null){
			bc = new BrowserController();
			bc.setFileName(fileName);
			graphWindows.put(fileName, bc);
		}
		if (!bc.isWindowOpen()){
			bc.openWindow();
		}
		bc.loadContent(script);
	}	
	
	public void closeGraph(BrowserController gc){
		gc.closeWindow();
	}
	
	public void loadFile() {
		Environment.getEditorContainerController().loadSimulator();
	}

	public void closeProgram() {
		boolean closeCanContinue = Environment.getEditorContainerController().closeEditors();
		if (closeCanContinue){
			Main.saveState();
		
			System.exit(0);
		}
	}

	public ViewState getState(){
		ViewState gs = new ViewState();
		gs.putStateVar("BOUNDS", view.getBounds());
		gs.putStateVar("DIVIDER_LOCATIONS", view.getDividerLocations());
		gs.putStateVar("WINDOW_STATE", view.getExtendedState());		
		gs.putStateVar("CONSOLE_VISIBLE", consoleVisible);
		gs.putStateVar("EDITOR_VISIBLE", editorVisible);
		gs.putStateVar("EDITOR", Environment.getEditorContainerController().getState());
		return gs;
	}
	
	public void restoreState(ViewState state){
		view.setBounds((Rectangle) state.getStateVar("BOUNDS"));
		view.setExtendedState((int) state.getStateVar("WINDOW_STATE"));
		if ((Boolean) state.getStateVar("CONSOLE_VISIBLE")){
			openConsole();
		}
		Environment.getEditorContainerController().restoreState((ViewState) state.getStateVar("EDITOR")); 
		if ((Boolean) state.getStateVar("EDITOR_VISIBLE")){
			openEditorWindow();
		}
		view.setDividerLocations((int[]) state.getStateVar("DIVIDER_LOCATIONS"));
	}

	public void closeEditorWindow() {
		EditorContainerController ecc = Environment.getEditorContainerController();
		ecc.closeWindow();
		view.setMenuEditorSelected(false);
		editorVisible = false;
	}

	public void openEditorWindow() {
		EditorContainerController ecc = Environment.getEditorContainerController();
		ecc.openWindow();
		view.setMenuEditorSelected(true);
		editorVisible = true;
	}

	public int showDialog(JFileChooser fc, String approveButtonText) {		
		return fc.showDialog(view, approveButtonText);
	}

	public int showDialog(String text, String title, int yesNoCancelOption) {
		return JOptionPane.showConfirmDialog(view, text, title, yesNoCancelOption);
	}

	public void newStreamFile() {
		Environment.getEditorContainerController().newSimulator();
	}

	public void showMessageDialog(String text, String title, int messageType) {
		JOptionPane.showMessageDialog(view, text, title, messageType);		
	}

	public BrowserController getGraphWindow(String fileName) {
		return graphWindows.get(fileName);
	}

	public void addWindow(Component gui) {
		view.addWindow(gui);
		view.setDividerLocations(view.getDividerLocations());
	}

	public void removeWindow(Component gui) {
		view.removeWindow(gui);
	}
	
	public void printResult(EvaluationResult er) {
		Environment.getConsoleController().printResult(er);		
	}
}