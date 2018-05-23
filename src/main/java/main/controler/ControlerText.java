package main.controler;

import java.awt.Color;
import java.awt.Cursor;
import java.util.List;
//import java.util.List;
import java.util.Map;

import javax.swing.JInternalFrame;
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
	 * Affiche tous les trous correspondant � la page indiqu�e et � partir du trou
	 * pass� en param�tre
	 */
	public void showHolesInPage(int h) {
		// pour tous les trous
		for (int i = 0; i < getPhrasesCount(); i++) {
			// si ce trou est dans la meme page que h
			System.out.println(i+"/"+h);
			if (getPageOf(i) == getPageOf(h)) {
				// si ce trou est apr�s le trou h
				if (i > h) {
					// on affiche ce trou
					showHole(i);
				}
			}
		}
	}

	private void showHole(int h) {

		String bonMot = p.textHandler.mots.get(h);

		int start = -1;
		int end = -1;

		Mask masque = null;
		//fenetres pas fixes
		if (!p.param.fixedField) {
			for (Mask m : p.fenetreMasque) {
				if (m.isVisible()) {
					m.setVisible(false);
					masque = m;
					break;
				}
			}
			try {
				start = masque.start;
				end = masque.end;
			} catch (Exception e) {}
		//fenentre fixe
		} else {
			start = p.editorPane.getText().indexOf(" " + p.param.mysterCarac) + 1;
			end = -1;
			if (bonMot != null) {
				end = start + bonMot.length();
			}
		}

		try {
			p.afficherFrame(start, end, masque);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	private int getPageOf(int h) {
		int r = -1;
		// recuperer le segment du trou avec son numero
		int n = p.textHandler.getPhraseOf(h);
		// pour toutes les pages
		for (int i = 0; i < getPhrasesCount(); i++) {
			// si la page contient le segment
			if (p.segmentsEnFonctionDeLaPage.get(i+1).contains(n)) {
				r = i;
				break;
			}
		}
		return r;
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

	// public boolean hasNextHole() {
	// return p.currentHole < getHolesCount(p.pilot.getCurrentPhraseIndex());
	// }

	public boolean waitForFill() {
		// p.setCurrentHole(p.pilot.getCurrentPhraseIndex(), p.currentHole);
		while (true) {
			Thread.yield();
			// if (p.controlerMask.enter) {
			// p.controlerMask.enter = false;
			return true;
		}
	}
	// }

	public void validCurrentHole() {
		// p.validHole(p.pilot.getCurrentPhraseIndex(), p.currentHole);
	}

	public void removeAllHoles() {
		// p.removeAllHoles();
	}

	public void blink() {
		p.blink();
	}

}
