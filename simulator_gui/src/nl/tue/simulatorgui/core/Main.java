package nl.tue.simulatorgui.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import javafx.embed.swing.JFXPanel;
import nl.tue.simulatorgui.core.viewstate.ViewState;

public class Main {
	
	public static void saveState(){
		Environment.getProperties().saveProperties();
		String stateFile = System.getProperty("user.home") + "/" + Environment.STATE_FILE;
		try{
			FileOutputStream fileOut = new FileOutputStream(stateFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(Environment.getMainController().getState());
			out.close();
			fileOut.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void restoreState(){
		Environment.getProperties().loadProperties();
		try{
			String stateFile = System.getProperty("user.home") + "/" + Environment.STATE_FILE;
			File f = new File(stateFile);
			if (f.exists() && !f.isDirectory()){
				FileInputStream fileIn = new FileInputStream(stateFile);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				ViewState mcs = (ViewState) in.readObject();
				Environment.getMainController().restoreState(mcs);
				in.close();
				fileIn.close();
			}else{
				Environment.getMainController().defaultState();
			}
		}catch (Exception e){
			e.printStackTrace();			
		}
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void enableOSXQuitStrategy() {
        try {            
			Class application = Class.forName("com.apple.eawt.Application");
            Method getApplication = application.getMethod("getApplication");
            Object instance = getApplication.invoke(application);
            Class strategy = Class.forName("com.apple.eawt.QuitStrategy");
            Enum closeAllWindows = Enum.valueOf(strategy, "CLOSE_ALL_WINDOWS");
            Method method = application.getMethod("setQuitStrategy", strategy);
            method.invoke(instance, closeAllWindows);
        } catch (Exception exp) {
            exp.printStackTrace(System.err);
        }
    }
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JFXPanel(); //IMPORTANT! Initialize JavaFX here, to prevent deadlocks further on.
				try {
				    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				        if ("Nimbus".equals(info.getName())) {
				            UIManager.setLookAndFeel(info.getClassName());
				            break;
				        }
				    }
				} catch (Exception e) {
				}
				try {
					//System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS"); //To close the window on Mac
					if (System.getProperty("os.name").startsWith("Mac OS X")) {
			            enableOSXQuitStrategy();
			        }
					Environment.getMainController().makeVisible();
					restoreState();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
