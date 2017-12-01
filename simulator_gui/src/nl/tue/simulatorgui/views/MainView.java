package nl.tue.simulatorgui.views;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nl.tue.simulatorgui.core.Environment;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Toolkit;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.Box;

public class MainView extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private JSplitPane splitRoot2TopBottom;
	private JSplitPane splitTop2BrowserEditor;
	
	private JCheckBoxMenuItem chckbxmntmConsole;
	private JCheckBoxMenuItem chckbxmntmVariables;
	private JCheckBoxMenuItem chckbxmntmActive;
	private JCheckBoxMenuItem chckbxmntmEditor;
	
	private JDesktopPane desktopBrowserContainer;
	
	/**
	 * Create the frame.
	 */
	public MainView() {
		setTitle("Streaming Process data Analyzer");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainView.class.getResource("/nl/tue/resources/icons/2 (18).png")));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(0, 0, 1024, 768);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we){
				Environment.getMainController().closeProgram();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmStream = new JMenuItem("New Simulator");
		mntmStream.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment.getMainController().newStreamFile();
			}
		});
		mnFile.add(mntmStream);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		
		JMenuItem mntmOpen = new JMenuItem("Open File");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Environment.getMainController().loadFile();
			}
		});
		mnFile.add(mntmOpen);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmSaveAll = new JMenuItem("Save All");
		mnFile.add(mntmSaveAll);
		
		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mnFile.add(mntmClose);
		
		JMenuItem mntmCloseAll = new JMenuItem("Close All");
		mnFile.add(mntmCloseAll);
		
		JSeparator separator_3 = new JSeparator();
		mnFile.add(separator_3);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);
		
		Component horizontalStrut = Box.createHorizontalStrut(10);
		menuBar.add(horizontalStrut);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);		
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(10);
		menuBar.add(horizontalStrut_1);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);		
		
		chckbxmntmConsole = new JCheckBoxMenuItem("Console", false);
		chckbxmntmConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmConsole.getState()){
					Environment.getMainController().openConsole();
				}else{
					Environment.getMainController().closeConsole();
				}
			}
		});
		mnView.add(chckbxmntmConsole);

		chckbxmntmEditor = new JCheckBoxMenuItem("Editor", false);
		chckbxmntmEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmEditor.getState()){
					Environment.getMainController().openEditorWindow();
				}else{
					Environment.getMainController().closeEditorWindow();
				}
			}
		});
		mnView.add(chckbxmntmEditor);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		desktopBrowserContainer = new JDesktopPane();
		
		splitRoot2TopBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitTop2BrowserEditor = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitRoot2TopBottom.setTopComponent(splitTop2BrowserEditor);
		splitRoot2TopBottom.setDividerLocation(700);
		splitTop2BrowserEditor.setDividerLocation(500);
		
		splitTop2BrowserEditor.setLeftComponent(desktopBrowserContainer);
		
		contentPane.add(splitRoot2TopBottom, BorderLayout.CENTER);
	}
	
	public void setMenuConsoleSelected(boolean b) {
		chckbxmntmConsole.setState(b);
	}

	public void setMenuVariablesSelected(boolean b) {
		chckbxmntmVariables.setState(b);
	}

	public void setMenuActiveSelected(boolean b) {
		chckbxmntmActive.setState(b);
	}
	
	public void setMenuEditorSelected(boolean b) {
		chckbxmntmEditor.setState(b);
	}

	public void addWindow(Component gui){
		if (gui instanceof ConsoleView){
			splitRoot2TopBottom.setBottomComponent(gui);
			gui.validate();
		}else if (gui instanceof EditorContainerView){
			splitTop2BrowserEditor.setRightComponent(gui);
			gui.validate();
		}else if (gui instanceof BrowserView){
			desktopBrowserContainer.add(gui);
		}
	}

	public void removeWindow(Component gui){
		if (gui instanceof ConsoleView){
			splitRoot2TopBottom.remove(gui);
		}else if (gui instanceof EditorContainerView){
			splitTop2BrowserEditor.remove(gui);
		}
	}

	public int[] getDividerLocations(){
		int[] result = new int[2];
		result[0] = splitRoot2TopBottom.getDividerLocation();
		result[1] = splitTop2BrowserEditor.getDividerLocation();
		return result;
	}
	
	public void setDividerLocations(int[] dividerLocations){
		splitTop2BrowserEditor.setDividerLocation(dividerLocations[1]);
		splitRoot2TopBottom.setDividerLocation(dividerLocations[0]);
	}	
}
