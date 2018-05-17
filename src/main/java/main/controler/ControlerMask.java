package main.controler;

import java.awt.event.*;

import main.view.Panneau;

public class ControlerMask implements ActionListener, KeyListener {

	private Panneau p;
	
	public boolean enter;
	
	public ControlerMask(Panneau p) {
		this.p = p;
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
