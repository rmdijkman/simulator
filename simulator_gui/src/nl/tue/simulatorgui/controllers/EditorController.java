package nl.tue.simulatorgui.controllers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.tue.simulatorgui.core.Environment;
import nl.tue.simulatorgui.core.viewstate.ViewState;
import nl.tue.simulatorgui.core.viewstate.ViewStateSerializable;
import nl.tue.simulatorgui.executor.SimulatorScript;
import nl.tue.simulatorgui.views.EditorView;

public class EditorController implements ViewStateSerializable {

	String fileName;
	File file;
	boolean saved;

	EditorView view;
	
	public EditorController() {
		this.view = new EditorView(this);
		saved = false;
	}
	
	//Fills a simulator script with all values from the GUI
	private SimulatorScript scriptFromView(){
		SimulatorScript script = new SimulatorScript(file);
		script.setFileToLoad(view.getSelectedFile());
		script.setSimulationLength(view.getSimulationLength());
		script.setReplications(view.getReplications());
		script.setWarmup(view.getWarmup());
		return script;
	}

	public void runScript(){
		askToSaveBeforeExecution();
		if (saved){
			Environment.getMainController().printResult(scriptFromView().execute());
		}
	}
			
	public boolean save(boolean saveAs){
		if (saveAs || (this.fileName == null)){
			boolean selected = selectSaveFile();
			if (!selected){
				return false;
			}
		}
		try{
			scriptFromView().save();
		}catch (Exception e){
			file = null;
			fileName = null;
			Environment.getMainController().showMessageDialog("An error occurred while trying to save the file: " + e.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		saved = true;
		Environment.getEditorContainerController().updateSavedState();
		return true;
	}
	
	public static EditorController load(String fullPath){
		EditorController ec = new EditorController();
		ec.file = new File(fullPath);
		ec.fileName = ec.file.getName();
		try{
			SimulatorScript script = new SimulatorScript(ec.file);
			script.load();
			ec.view.setSelectedFile(script.getFileToLoad());
			ec.view.setSimulationLength(script.getSimulationLength());
			ec.view.setReplications(script.getReplications());
			ec.view.setWarmup(script.getWarmup());
		}catch (Exception e){
			Environment.getMainController().showMessageDialog("An error occurred while trying to load the file: " + e.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		ec.saved = true;
		Environment.getEditorContainerController().updateSavedState();
		return ec;		
	}
			
	public EditorView getGUI(){
		return view;
	}

	@Override
	public void restoreState(ViewState state) {
		EditorController ec = load((String) state.getStateVar("FILE"));
		if (ec != null){
			file = ec.file;
			fileName = ec.fileName;
			Environment.getEditorContainerController().updateSavedState();
			refreshFile();
			saved = true;
		}
	}

	public void fileChanged() {
		if (saved){
			saved = false;
			Environment.getEditorContainerController().updateSavedState();
		}
	}

	public void refreshFile() {
		try{
			SimulatorScript script = new SimulatorScript(file);
			script.load();
			view.setSelectedFile(script.getFileToLoad());
			view.setSimulationLength(script.getSimulationLength());
			view.setReplications(script.getReplications());
			view.setWarmup(script.getWarmup());
		}catch (Exception e){
			Environment.getMainController().showMessageDialog("An error occurred while trying to load the file: " + e.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public String getFileName(){
		return fileName;
	}

	public boolean isSaved(){
		return saved;
	}	

	/**
	 * Checks whether the file was saved. If not, asks the user whether he wants to save.
	 * If the user wants to save, saves the file.
	 * Returns true if the user does not want to save, or if the user wants to save and save was successful.
	 * Returns false if the user wants to cancel, or if the user wants to save and save was unsuccessful.
	 * 
	 * @return true, if and only if the program should continue with a hypothetical quit action.
	 */
	public boolean saveOnExit(){
		if (!saved){
			int answer = Environment.getMainController().showDialog("Your simulator has not been saved. Do you want to save now?", "Notification", JOptionPane.YES_NO_CANCEL_OPTION); 					
			if (answer == JOptionPane.YES_OPTION){
				return save(false);
			}else if (answer == JOptionPane.CANCEL_OPTION){
				return false;
			}
		}
		return true;
	}

	public boolean selectSaveFile(){
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Simulator (.sim)", "sim");
		fc.addChoosableFileFilter(filter);
		int returnVal = Environment.getMainController().showDialog(fc, "Save");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			file = fc.getSelectedFile();
			fileName = file.getName();
			if (!fileName.endsWith(".sim")){
				file = new File(fc.getSelectedFile().getAbsolutePath() + ".sim");
				fileName = file.getName();
			}
			Environment.getProperties().setLastFolder(file.getParent());
			return true;
		}
		return false;		
	}

	public static EditorController load(){
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Simulator (.sim)", "sim");
		fc.addChoosableFileFilter(filter);
		int returnVal = Environment.getMainController().showDialog(fc, "Load");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			Environment.getProperties().setLastFolder(fc.getSelectedFile().getParent());
			return load(fc.getSelectedFile().getPath());
		}
		return null;
	}
	
	@Override
	public ViewState getState() {
		if (file == null){
			return null;
		}else{
			ViewState gs = new ViewState();
			gs.putStateVar("FILE", file.getAbsolutePath());
			return gs;
		}
	}
	
	/**
	 * If the script that belongs to this controller is not saved, asks the user to save the script.
	 * If the user wants to save, opens the save dialog and handles saving.
	 */
	public void askToSaveBeforeExecution(){
		if (!saved){
			int answer = Environment.getMainController().showDialog("Your simulator must be saved before it can be executed. Do you want to save now?", "Notification", JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION){
				return;
			}
		}
		save(false);
	}	
}
