package nl.tue.simulatorgui.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.tue.simulatorgui.core.Environment;
import nl.tue.simulatorgui.core.viewstate.ViewState;
import nl.tue.simulatorgui.core.viewstate.ViewStateSerializable;
import nl.tue.simulatorgui.views.EditorContainerView;

public class EditorContainerController implements ViewStateSerializable{	
	
	EditorContainerView view;
	
	Set<EditorController> editorWindows;
	
	public EditorContainerController(){
		view = new EditorContainerView(this);
		editorWindows = new HashSet<EditorController>();
	}
	
	public void closeWindow() {
		view.setVisible(false);
		Environment.getMainController().removeWindow(view);
	}
	
	public void openWindow() {
		Environment.getMainController().addWindow(view);
		view.setVisible(true);
	}

	public boolean closeEditors() {
		for (EditorController ec: editorWindows){
			if (!ec.saveOnExit()){
				return false;
			}
		}
		return true;
	}
	
	public EditorController getEditorController(String fileName){
		for (EditorController ec: editorWindows){
			if (ec.getFileName().equals(fileName)){
				return ec;
			}
		}
		return null;
	}

	public void loadSimulator() {
		EditorController ec = EditorController.load();
		if (ec != null){
			editorWindows.add(ec);
			String fileName = ec.getFileName();
			view.addEditor(fileName, ec);
		}
	}

	public void runSimulator() {
		EditorController ec = view.getSelectedEditor();
		if (ec == null) return;
		ec.runScript();
		String fileName = ec.getFileName();
		if (fileName != null){
			view.setEditorTitle(ec, fileName);
		}
	}

	public void saveSimulator() {
		EditorController ec = view.getSelectedEditor();
		if (ec == null) return;
		ec.save(false);
		String fileName = ec.getFileName();
		if (fileName != null){
			view.setEditorTitle(ec, fileName);
		}
	}

	public void close(EditorController ec) {
		if (ec.saveOnExit()){
			editorWindows.remove(ec);
			view.removeTab(ec);
		}
	}

	@Override
	public ViewState getState() {
		ViewState gs = new ViewState();
		List<ViewState> editors = new ArrayList<ViewState>();
		for (EditorController ec: editorWindows){
			ViewState ecState = ec.getState();
			if (ecState != null){
				editors.add(ecState);
			}
		}
		gs.putStateVar("EDITORS", editors);		
		return gs;
	}

	@Override
	public void restoreState(ViewState state) {
		@SuppressWarnings("unchecked")
		List<ViewState> editors = (ArrayList<ViewState>) state.getStateVar("EDITORS");
		for (ViewState editorState: editors){
			EditorController ec = new EditorController();
			ec.restoreState(editorState);
			if (ec.getFileName() != null){
				editorWindows.add(ec);
				view.addEditor(ec.getFileName(), ec);
			}
		}		
	}

	public void newSimulator() {
		EditorController ec = new EditorController();
		editorWindows.add(ec);
		view.addEditor("new simulator", ec);
	}

	public void editorSelected() {
		updateSavedState();
	}
	
	public void updateSavedState(){
		EditorController ec = view.getSelectedEditor();
		if (ec == null){
			view.setSaveEnabled(false);
		}else{
			view.setSaveEnabled(!ec.isSaved());
		}		
	}
}
