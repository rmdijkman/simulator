package nl.tue.simulatorgui.controllers;

import nl.tue.simulatorgui.core.Environment;
import nl.tue.simulatorgui.views.BrowserView;

public class BrowserController {

	BrowserView view;
	String fileName;
	
	public BrowserController(){
		this.view = new BrowserView(this);
		Environment.getMainController().addWindow(view);
	}
	
	public void loadContent(String content){
		view.loadContent(content);
	}

	public void closeWindow() {
		view.setVisible(false);
	}
	
	public void openWindow() {
		view.setVisible(true);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		view.setTitle("View - " + fileName);
	}
	
	public String getFileName(){
		return fileName;
	}

	public boolean isWindowOpen() {
		return view.isVisible();
	}	
}
