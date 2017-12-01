package nl.tue.simulatorgui.views;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import nl.tue.simulatorgui.controllers.ConsoleController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class ConsoleView extends JPanel{
	private static final long serialVersionUID = 1L;
		
	ConsoleController controller;
	
	JTextPane textPane;	
	
	public ConsoleView(ConsoleController controller){
		this.controller = controller;
		
		this.setLayout(new BorderLayout());

		textPane = new JTextPane();
		textPane.setFont(new Font("Courier New", Font.PLAIN, 13));
		textPane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textPane);

		add(scrollPane, BorderLayout.CENTER);
	}
			
	public void printError(String error){
		appendTextToPane(error, Color.RED);
	}
	
	public void printMessage(String msg){
		appendTextToPane(msg, Color.BLUE);
	}
		
	public void printEntry(String entry){
		appendTextToPane(entry, Color.BLACK);		
	}
		
	private void appendTextToPane(String msg, Color c){
		textPane.setEditable(true);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        textPane.setCaretPosition(textPane.getDocument().getLength());
        textPane.setCharacterAttributes(aset, false);
        textPane.replaceSelection(msg);
        textPane.setCaretPosition(textPane.getDocument().getLength());
		textPane.setEditable(false);
    }
}