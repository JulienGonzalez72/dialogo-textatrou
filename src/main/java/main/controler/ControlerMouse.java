package main.controler;


import java.awt.Rectangle;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import main.Constants;
import main.model.TextHandler;
import main.view.Panneau;

public class ControlerMouse implements MouseListener {

	public static int nbErreurs;
	Panneau view;
	TextHandler handler;

	/**
	 * Icone de la souris lors d'une phase d'écoute
	 */
	public boolean clicking;

	public ControlerMouse(Panneau p, TextHandler handler) {
		view = p;
		this.handler = handler;
		nbErreurs = 0;
	}

	public void mousePressed(MouseEvent e) {
		/*
		int offset = view.textHandler.getAbsoluteOffset(view.getNumeroPremierSegmentAffiché(),
				view.editorPane.getCaretPosition());
		int start = handler.startWordPosition(offset);
		int end = handler.endWordPosition(offset);
		String motCourant = "";
		for (int i = start; i < end; i++) {
			motCourant += handler.getShowText().toCharArray()[i];
		}
		boolean motMasque = true;
		for (Character c : motCourant.toCharArray()) {
			if (c != '_') {
				motMasque = false;
				break;
			}
		}
		String temp = view.editorPane.getText().substring(end);
		int nbMotsMasqueApresCeluiClique = ControlerMouse.stringOccur(temp, " _");
		int numeroMotClique = nbMotsDansLaPage - nbMotsMasqueApresCeluiClique;
		if (numeroMotClique == (numeroCourant+1) && motMasque) {
			view.editorPane.surlignerPhrase(start, end, Constants.WRONG_PHRASE_COLOR);
			try {
				afficherFrame(start, end);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}*/
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
		clicking = false;
	}


}
