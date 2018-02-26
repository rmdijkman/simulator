package nl.tue.simulatorgui.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import nl.tue.simulatorgui.controllers.BrowserController;
import nl.tue.simulatorgui.core.Environment;

public class BrowserView extends JInternalFrame{
	private static final long serialVersionUID = 1L;
	
	BrowserController controller;
	
    private final JFXPanel jfxPanel;
    private WebEngine engine;
	
    public BrowserView(BrowserController controller){
		super("View", true, true, true, false);
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosing(InternalFrameEvent e){
				Environment.getMainController().closeGraph(controller);
			}
		});
		setBounds(0, 0, 775, 600);
		setResizable(true);
		BasicInternalFrameUI ui = (BasicInternalFrameUI) getUI();
		Container north = (Container) ui.getNorthPane();
		north.remove(0);
		north.validate();
		north.repaint();

		this.controller = controller;
		
		jfxPanel = new JFXPanel();

		getContentPane().add(jfxPanel, BorderLayout.CENTER);
	}
	
	public void loadContent(String content){
        Platform.runLater(new Runnable() {
            @Override 
            public void run() {
        		try {
        			setMaximum(true);
        		} catch (PropertyVetoException e) {}
                WebView view = new WebView();
                engine = view.getEngine();
                jfxPanel.setScene(new Scene(view));
            	engine.loadContent(content);
            }
        });
	}		
}