package main.controler;

import java.awt.event.*;

public class ControlerMask implements ActionListener, KeyListener {

	
	public boolean enter;
	
	public ControlerMask() {

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		enter = true;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
}
