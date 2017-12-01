package nl.tue.simulatorgui.views;

import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.tue.simulatorgui.controllers.EditorController;
import nl.tue.simulatorgui.core.Environment;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class EditorView extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	EditorController controller;
	private JTextField txtFile;
	private String selectedFile;
	
	public EditorView(EditorController controller){
		setPreferredSize(new Dimension(660, 510));
		setMinimumSize(new Dimension(660, 510));
		this.controller = controller;
		setLayout(null);
		
		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(30, 54, 61, 16);
		add(lblFile);
		
		txtFile = new JTextField();
		txtFile.setEnabled(false);
		txtFile.setEditable(false);
		txtFile.setBounds(70, 48, 286, 28);
		add(txtFile);
		txtFile.setColumns(10);
		
		JButton btnFile = new JButton("...");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.fileChanged();
				if (selectFile()){
					controller.refreshFile();
				}
			}
		});
		btnFile.setBounds(368, 48, 32, 28);
		add(btnFile);
	}
	
	public EditorController getController() {
		return controller;
	}
	
	private boolean selectFile(){
		final JFileChooser fc = new JFileChooser();
		String lastFolder = Environment.getProperties().getLastFolder();
		if (lastFolder != null){
			fc.setCurrentDirectory(new File(lastFolder));
		}
		fc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BPMN 2.0 XML (.bpmn)", "bpmn");
		fc.addChoosableFileFilter(filter);
		int returnVal = Environment.getMainController().showDialog(fc, "Open");
		if (returnVal == JFileChooser.APPROVE_OPTION){
			selectedFile = fc.getSelectedFile().getAbsolutePath();
			txtFile.setText(fc.getSelectedFile().getName());
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.fileChanged();
	}

	public String getSelectedFile() {
		return selectedFile;
	}
	
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}
}
