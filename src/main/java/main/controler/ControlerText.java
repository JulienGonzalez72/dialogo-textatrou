package main.controler;

import java.awt.Cursor;
import java.util.List;

import main.Constants;
import main.view.Panneau;

public class ControlerText {

	public Panneau p;

	/**
	 * Construit un contrôleur à partir du panneau correspondant.
	 */
	public ControlerText(Panneau p) {
		this.p = p;
	}

	/**
	 * Affiche la page indiquée.
	 */
	public void showPage(int page) {
		p.showPage(page);
	}

	/**
	 * Joue un fichier .wav correspondant à un segment de phrase.
	 * On sortira de cette fonction lorsque le fichier .wav aura été totalement joué.
	 * METHODE DE TEST
	 */
	public void play(int phrase) {
		p.setCursor(Constants.CURSOR_LISTEN);
		p.player.play(phrase);
		while (true) {
			if (p.player.isPhraseFinished()) {
				p.setCursor(Cursor.getDefaultCursor());
				break;
			}
			/// fixe toujours le curseur d'écoute pendant toute la durée de l'enregistrement
			else if (!p.getCursorName().equals(Constants.CURSOR_LISTEN)) {
				p.setCursor(Constants.CURSOR_LISTEN);
			}
		}
	}

	/**
	 * Retourne la durée en millisecondes de l'enregistrement courant.
	 */
	public long getCurrentPhraseDuration() {
		return p.player.getDuration();
	}

	/**
	 * Retourne le temps d'attente en millisecondes correspondant à l'enregistrement
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
	 *            le type de curseur à définir pendant l'attente (peut être
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
	 * Affiche tous les trous correspondant à la page indiquée.
	 */
	public void showHoles(int page) {
		/*for (int i = 0; i < p.segmentsEnFonctionDeLaPage.get(page + 1).size(); i++) {
			int n = p.segmentsEnFonctionDeLaPage.get(page + 1).get(i);
			List<String> words = p.textHandler.getHidedWords(n);
			for (int j = 0; j < words.size(); j++) {
				String w = words.get(j);
				System.out.println(p.textHandler.getStartOffset(w, n) + ", " + p.textHandler.getEndOffset(w, n));
				//p.afficherFrameVide(p.textHandler.getStartOffset(w, n), p.textHandler.getEndOffset(w, n),
					//	getPageOfPhrase(n), w);
			}
		}*/
		p.showAllHoleInPage(page);
	}
	
	public void nextHole() {
		p.currentHole++;
	}
	
	/**
	 * Initialise le premier trou du segment.
	 */
	public void firstHole() {
		p.currentHole = 0;
	}
	
	/**
	 * Retourne le nombre de trous associés au segment n.
	 */
	public int getHolesCount(int n) {
		return p.textHandler.getHolesCount(n);
	}
	
	public boolean hasNextHole() {
		return p.currentHole < getHolesCount(p.pilot.getCurrentPhraseIndex());
	}
	
	public boolean waitForFill() {
		p.setCurrentHole(p.pilot.getCurrentPhraseIndex(), p.currentHole);
		while (true) {
			Thread.yield();
			if (p.controlerMask.enter) {
				p.controlerMask.enter = false;
				return true;
			}
		}
	}
	
	public void validCurrentHole() {
		p.validHole(p.pilot.getCurrentPhraseIndex(), p.currentHole);
	}
	
	public void removeAllHoles() {
		p.removeAllHoles();
	}
	
	public void blink() {
		p.blink();
	}


}
