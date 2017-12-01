package nl.tue.simulatorgui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.tue.simulatorgui.controllers.EditorContainerController;
import nl.tue.simulatorgui.controllers.EditorController;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class EditorContainerView extends JPanel{
	private static final long serialVersionUID = 1L;

	EditorContainerController controller;
	
	JButton btnSave;
	JTabbedPane tabbedPane;
	
	public EditorContainerView(EditorContainerController controller){
		this.controller = controller;
		
		this.setLayout(new BorderLayout());
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setFocusable(false);
		add(toolBar, BorderLayout.NORTH);
				
		btnSave = new JButton("");
		btnSave.setToolTipText("Save");
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.saveSimulator();
			}
		});
		btnSave.setFocusable(false);
		btnSave.setIcon(new ImageIcon(EditorView.class.getResource("/nl/tue/resources/icons/2 (30).png")));
		toolBar.add(btnSave);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		toolBar.add(horizontalStrut);		
		
		JButton btnRun = new JButton("");
		btnRun.setToolTipText("Run once");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.runSimulator();
			}
		});
		btnRun.setFocusable(false);
		btnRun.setIcon(new ImageIcon(EditorView.class.getResource("/nl/tue/resources/icons/3 (24).png")));
		toolBar.add(btnRun);
				
		tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);
		tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
        		controller.editorSelected();
	        }
	    });
		
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	public void addEditor(String label, EditorController editor){
		tabbedPane.addTab(label, (Component) editor.getGUI());
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount()-1, createTabLabel(label));
		tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
	}
	
	private JPanel createTabLabel(String label){
		JPanel pnlTab = new JPanel(new GridBagLayout());
		pnlTab.setOpaque(false);
		JLabel lblTitle = new JLabel(label);
		JLabel btnClose = new JLabel("   x");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;

		pnlTab.add(lblTitle, gbc);

		gbc.gridx++;
		gbc.weightx = 0;
		pnlTab.add(btnClose, gbc);
		
		btnClose.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e) {
				btnClose.setForeground(Color.WHITE);
			}			
			public void mouseExited(MouseEvent e) {
				btnClose.setForeground(Color.BLACK);
			}
			public void mousePressed(MouseEvent e) {
				EditorController ec = getEditorForCloseButton((JLabel) e.getComponent());
				controller.close(ec);	
			}
		});
		
		return pnlTab;
	}
	
	public EditorController getSelectedEditor(){
		if (tabbedPane.getSelectedIndex() == -1){
			return null;
		}else{
			EditorView ge = (EditorView) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
			return ge.getController();
		}
	}
	
	public EditorController getEditorForCloseButton(JLabel btnClose){
		JPanel tabComponent = (JPanel) btnClose.getParent();
		for (int i = 0; i < tabbedPane.getTabCount(); i++){
			if (tabbedPane.getTabComponentAt(i).equals(tabComponent)){
				return ((EditorView) tabbedPane.getComponentAt(i)).getController();
			}
		}		
		return null;
	}

	public void setEditorTitle(EditorController ec, String title) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++){
			if (tabbedPane.getComponentAt(i).equals(ec.getGUI())){
				tabbedPane.setTitleAt(i, title);
				tabbedPane.setTabComponentAt(i, createTabLabel(title));
				break;
			}
		}
	}

	public void removeTab(EditorController ec) {
		int tabIndex = -1;
		for (int i = 0; i < tabbedPane.getTabCount(); i++){
			if (tabbedPane.getComponentAt(i).equals(ec.getGUI())){
				tabIndex = i;
				break;
			}
		}
		tabbedPane.remove(tabIndex);
	}

	public void setSaveEnabled(boolean saveEnabled) {
		btnSave.setEnabled(saveEnabled);
	}
}
