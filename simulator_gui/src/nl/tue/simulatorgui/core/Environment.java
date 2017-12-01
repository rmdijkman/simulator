package nl.tue.simulatorgui.core;

import nl.tue.simulatorgui.controllers.ConsoleController;
import nl.tue.simulatorgui.controllers.EditorContainerController;
import nl.tue.simulatorgui.controllers.MainController;

public class Environment {

	public static final transient String STATE_FILE = ".spastate.ser"; 

	private static MainController mainController;
	private static ConsoleController consoleController;
	private static EditorContainerController editorContoller;
	private static Properties properties;
		
	public static MainController getMainController(){
		if (mainController == null){
			mainController = new MainController();
		}
		return mainController;
	}

	public static ConsoleController getConsoleController(){
		if (consoleController == null){
			consoleController = new ConsoleController();
		}
		return consoleController;
	}
	
	public static EditorContainerController getEditorContainerController() {
		if (editorContoller == null){
			editorContoller = new EditorContainerController();
		}
		return editorContoller;
	}
	
	public static Properties getProperties(){
		if (properties == null){
			properties = new Properties();
		}
		return properties;
	}

	public static boolean isMac(){
		return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
	}
}
