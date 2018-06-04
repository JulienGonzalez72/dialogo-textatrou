package main.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class NumberField extends JTextField {
	
	private JButton upButton;
	private JButton downButton;
	
	private int min, max;

	public NumberField() {
		setFont(new Font("OpenDyslexic", Font.PLAIN, 15));
		setHorizontalAlignment(SwingConstants.CENTER);
		setLayout(new BorderLayout());
		
		ArrowControler controler = new ArrowControler();
		addKeyListener(controler);
		addFocusListener(controler);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(2, 1));
		buttonsPanel.setPreferredSize(new Dimension(45, 0));
		
		upButton = new JButton("\u02C4");
		upButton.addActionListener(controler);
		buttonsPanel.add(upButton);
		
		downButton = new JButton("\u02C5");
		downButton.addActionListener(controler);
		buttonsPanel.add(downButton);
		
		add(buttonsPanel, BorderLayout.EAST);
	}
	
	private class ArrowControler implements ActionListener, KeyListener, FocusListener {
		private String oldText = String.valueOf(min);
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == upButton && (!isBounded() || getValue() < max)) {
				setValue(getValue() + 1);
			}
			else if (e.getSource() == downButton && (!isBounded() || getValue() > min)) {
				setValue(getValue() - 1);
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e) {
			if (validText()) {
				oldText = getText();
			}
			if (e.getKeyCode() == KeyEvent.VK_UP && (!isBounded() || getValue() < max)) {
				setValue(getValue() + 1);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN && (!isBounded() || getValue() > min)) {
				setValue(getValue() - 1);
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if (!validText() && !getText().isEmpty()) {
				setText(oldText);
			}
			else if (isBounded() && getValue() > max) {
				setValue(max);
			}
			else if (isBounded() && getValue() < min) {
				setValue(min);
			}
			oldText = getText();
		}
		@Override
		public void focusGained(FocusEvent e) {
		}
		@Override
		public void focusLost(FocusEvent e) {
			if (getText().isEmpty()) {
				setValue(min);
			}
		}
	}
	
	public void setValue(int value) {
		setText(String.valueOf(value));
	}
	
	public int getValue() {
		try {
			int v = Integer.parseInt(getText());
			return v;
		} catch (NumberFormatException e) {
			return min;
		}
	}
	
	public boolean validText() {
		try {
			Integer.parseInt(getText());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public void setBounds(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	private boolean isBounded() {
		return max > min;
	}
	
}
