package main.controler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
//import java.util.List;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import main.Constants;
import main.view.Mask;
import main.view.Panneau;

public class ControlerText {

	private Panneau p;

	/**
	 * Construit un contr�leur � partir du panneau correspondant.
	 */
	public ControlerText(Panneau p) {
		this.p = p;
	}

	/**
	 * Retourne la map des mots en fonction des segments
	 */
	public Map<Integer, List<String>> getWordByPhrases() {
		return p.textHandler.motsParSegment;
	}

	/**
	 * Retourne la map des mots en fonction de leur numero
	 */
	public Map<Integer, String> getWords() {
		return p.textHandler.mots;
	}

	/**
	 * Retourne true si le trou est le premier de son segment
	 * 
	 * @param h
	 *            : le numero de segment
	 */
	public boolean isFirstInPhrase(int h) {
		return !p.textHandler.hasPreviousHoleInPhrase(h);
	}

	/**
	 * Affiche le compte rendu
	 */
	public void showReport() {
		p.afficherCompteRendu();
	}

	/**
	 * Affiche la page indiqu�e.
	 */
	public void showPage(int page) {
		p.showPage(page);
	}

	/**
	 * Joue un fichier .wav correspondant � un segment de phrase. On sortira de
	 * cette fonction lorsque le fichier .wav aura �t� totalement jou�. METHODE DE
	 * TEST
	 */
	public void play(int phrase) {
		p.setCursor(Constants.CURSOR_LISTEN);
		p.player.play(phrase);
		while (true) {
			if (p.player.isPhraseFinished()) {
				p.setCursor(Cursor.getDefaultCursor());
				break;
			}
			/// fixe toujours le curseur d'�coute pendant toute la dur�e de l'enregistrement
			else if (!p.getCursorName().equals(Constants.CURSOR_LISTEN)) {
				p.setCursor(Constants.CURSOR_LISTEN);
			}
		}
	}

	/**
	 * Retourne la dur�e en millisecondes de l'enregistrement courant.
	 */
	public long getCurrentPhraseDuration() {
		return p.player.getDuration();
	}

	/**
	 * Retourne le temps d'attente en millisecondes correspondant � l'enregistrement
	 * courant.
	 */
	public long getCurrentWaitTime() {
		return (long) (getCurrentPhraseDuration() * p.param.tempsPauseEnPourcentageDuTempsDeLecture / 100.);
	}

	/**
	 * Mets en pause le thread courant pendant un certain temps.
	 * 
	 * @param time
	 *            le temps de pause, en millisecondes
	 * @param cursorName
	 *            le type de curseur � d�finir pendant l'attente (peut �tre
	 *            Constants.CURSOR_SPEAK ou Constants.CURSOR_LISTEN)
	 */
	public void doWait(long time, String cursorName) {
		try {
			p.setCursor(cursorName);
			Thread.sleep(time);
			p.setCursor(Cursor.getDefaultCursor());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Retourne la page qui contient le segment, ou -1 si le segment n'existe pas.
	 */
	public int getPageOfPhrase(int n) {
		int numeroPage = -1;
		for (Integer i : p.segmentsEnFonctionDeLaPage.keySet()) {
			if (p.segmentsEnFonctionDeLaPage.get(i).contains(n)) {
				numeroPage = i;
				break;
			}
		}
		return numeroPage;
	}

	public int getPhrasesCount() {
		return p.textHandler.getPhrasesCount();
	}

	/**
	 * 
	 * Affiche tous les trous correspondant � la page indiqu�e et � partir du trou
	 * pass� en param�tre. D�saffiche au pr�alable tous les trous.
	 */
	public void showHolesInPage(int h) {
		// d�saffice au pr�alable tous les trous
		for (JInternalFrame jif : p.getAllFrames()) {
			jif.dispose();
		}
		// reinitialisation de la liste des masques
		p.fenetreMasque = new ArrayList<>();
		// pour tous les trous
		for (int i = 0; i < p.textHandler.getHolesCount(); i++) {
			// si ce trou est dans la meme page que h
			if (getPageOf(i) == getPageOf(h)) {
				// si ce trou est apr�s le trou h ou est le trou h
				if (i >= h) {
					// on affiche ce trou
					showHole(i);
				}
			}
		}
	}

	private void showHole(int h) {
		
		int start = -1;
		int end = -1;

		// fenetres pas fixes
		if (!p.param.fixedField) {
<<<<<<< HEAD
			/*for (Mask m : p.fenetreMasque) {
				if (m.isVisible()) {
					m.setVisible(false);
					start = m.start;
					end = m.end;
					break;
				}
			}*/
=======
			start = p.textHandler.getHoleStartOffset(h);
			end = p.textHandler.getHoleEndOffset(h);
			try {
				p.afficherFrame(start, end, h);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c
		// fenetre fixe
		} else {
			String bonMot = p.textHandler.mots.get(h);
			int indexDeDebut = p.fenetreMasque.isEmpty() ? 0 : p.fenetreMasque.get(p.fenetreMasque.size()-1).end;
			start = p.editorPane.getText().substring(indexDeDebut).indexOf(" " + p.param.mysterCarac) +1+indexDeDebut;
			end = -1;
			if (bonMot != null) {
				end = start + bonMot.length();
			}
			try {
				p.afficherFrame(start, end,h);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
<<<<<<< HEAD
		start = p.textHandler.getHoleStartOffset(h);
		end = p.textHandler.getHoleEndOffset(h);
		try {
			p.afficherFrame(start, end, h);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
=======
>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c

	}

	/**
	 * 
	 * Retourne la page correspondant au trou h
	 * 
	 * @param h
	 * 
	 */
	public int getPageOf(int h) {
		return getPageOfPhrase(getPhraseOf(h));
	}

	/**
	 * 
	 */
	public int getPhraseOf(int h) {
		return p.textHandler.getPhraseOf(h);
	}

	public void nextHole() {
		// p.currentHole++;
	}

	/**
	 * Initialise le premier trou du segment.
	 */
	public void firstHole() {
		// p.currentHole = 0;
	}

	/**
	 * Retourne le nombre de trous associ�s au segment n.
	 */
	public int getHolesCount(int n) {
		return p.textHandler.getHolesCount(n);
	}

	/**
	 * Retourne le nombre de trous total dans le texte.
	 */
	public int getHolesCount() {
		return p.textHandler.getHolesCount();
	}

<<<<<<< HEAD
	// public boolean hasNextHole() {
	// return p.currentHole < getHolesCount(p.pilot.getCurrentPhraseIndex());
	// }
	
=======
>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c
	public boolean waitForFill(int h) {
		getMask(h).activate();
		p.controlerMask.enter = false;
		while (true) {
			Thread.yield();
			if (p.controlerMask.enter) {
				return true;
			}
		}
	}

	public boolean waitForFillFenetreFixe(int h) {
		while (true) {
			Thread.yield();
			if (p.controlerMask.enter) {
				p.controlerMask.enter = false;

				Mask m = getFenetreFixe();

				if (m.jtf.getText().equals(getMask(h).motCouvert)) {
					return true;
				} else {
					return false;
				}
			}
		}
<<<<<<< HEAD
	}

=======

	}

>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c
	public void validCurrentHole() {
		// p.validHole(p.pilot.getCurrentPhraseIndex(), p.currentHole);
	}

	public void removeAllMasks() {
		p.removeAllMasks();
	}

	public void blink() {
		p.blink();
	}

	/**
	 * 
	 * Colore le trou h en couleur c
	 * 
	 * @param h
	 *            : le numero du trou
	 * @param c
	 *            : la couleur de coloriage
	 */
	public void color(int h, Color c) {
		getMask(h).jtf.setBackground(c);
	}

	private Mask getMask(int h) {
		for (int i = 0; i < p.fenetreMasque.size(); i++) {
			if (p.fenetreMasque.get(i).n == h) {
				return p.fenetreMasque.get(i);
			}
		}
		return null;
	}

	/**
	 * 
	 * Cr�e une fen�tre de saisie fixe qui attends le resultat du trou h
	 * 
	 * @param h
	 * @return
	 */
	public Mask activateInputFenetreFixe(int h) {

		Mask frame = new Mask();
		((javax.swing.plaf.basic.BasicInternalFrameUI) frame.getUI()).setNorthPane(null);
		frame.setBorder(null);
		frame.setBounds(0, 0, p.panelFenetreFixe.getWidth(), p.panelFenetreFixe.getHeight());

		p.panelFenetreFixe.add(frame);

		JTextField jtf = new JTextField();
		Font f = new Font(p.editorPane.getFont().getFontName(), p.editorPane.getFont().getStyle(),
				p.editorPane.getFont().getSize() / 2);
		jtf.setFont(f);
		jtf.setHorizontalAlignment(JTextField.CENTER);
		jtf.addActionListener(p.controlerMask);

		frame.add(jtf);
		frame.jtf = jtf;
		frame.toFront();
		frame.setVisible(true);
		jtf.setEnabled(true);
		jtf.requestFocus();

		return frame;

	}

	public void doError() {
		blink();
		p.nbErreurs++;
	}

	public void desactiverFenetreFixe() {
		getFenetreFixe().dispose();

	}

	private Mask getFenetreFixe() {
		return p.getFenetreFixe();
	}

	public void replaceMaskByWord(int h) {
		Mask m = getMask(h);
		String temp = "";
		int j = 0;
		for (int i = 0; i < p.editorPane.getText().length(); i++) {
			if (i >= m.start && i < m.end) {
				temp += m.motCouvert.charAt(j);
				j++;
			} else {
				temp += p.editorPane.getText().charAt(i);
			}
		}

		p.editorPane.setText(temp);

		p.replaceAllMask();

	}

}
