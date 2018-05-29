package main.view;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;

import main.Constants;
import main.Parametres;

public class Fenetre extends JFrame {

	public Panneau pan;

	public Fenetre(String titre, int tailleX, int tailleY, FenetreParametre fenetreParam, Parametres param) {
		setIconImage(getToolkit().getImage("icone.jpg"));
		try {
			pan = new Panneau(this, fenetreParam, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setContentPane(pan);
		
		setTitle(titre);
		setSize(tailleX, tailleY);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setMinimumSize(new Dimension(Constants.MIN_FRAME_WIDTH, Constants.MIN_FRAME_HEIGHT));
		//setUndecorated(true);
		
		addComponentListener(new ComponentAdapter() {
			private int lastWidth = getWidth(), lastHeight = getHeight();

			@Override
			public void componentResized(ComponentEvent e) {
				/// lors d'un redimensionnement, refait la mise en page ///
				if (isResizable() && pan.editorPane != null && pan.editorPane.getWidth() > 0
						&& (lastWidth != getWidth() || lastHeight != getHeight())) {
					pan.rebuildPages();
					
					/// corrige le rendimensionnement s'il empêche une mise en page correcte ///
					if (!pan.correctSize() && (getWidth() < lastWidth || getHeight() < lastHeight)) {
						setSize(getWidth() + 100, getHeight() + 100);
						pan.rebuildPages();
					}
					
					else {
						lastWidth = getWidth();
						lastHeight = getHeight();
					}
				}

				param.panWidth = Fenetre.this.getWidth();
				param.panHeight = Fenetre.this.getHeight();

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				param.panX = Fenetre.this.getX();
				param.panY = Fenetre.this.getY();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				param.firstPhrase = pan.pilot.getCurrentPhraseIndex() + 1;
				param.stockerPreference();
			}
		});
	}

	public void start() {
		setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pan.init();
			}
		});
	}

}
