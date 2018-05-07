package main.controler;

import java.awt.Color;
import java.awt.Cursor;
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
	 * Construit les pages à partir du segment de numero spécifié.
	 */
	public void buildPages(int startPhrase) {
		p.buildPages(startPhrase);
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
			/// ///
			else if (!p.getCursorName().equals(Constants.CURSOR_SPEAK)) {
				p.setCursor(Constants.CURSOR_LISTEN);
			}
		}
	}

	/**
	 * Retourne le nombre de segments total du texte.
	 */
	public int getPhrasesCount() {
		return p.textHandler.getPhrasesCount();
	}

	/**
	 * Retourne la durée en millisecondes de l'enregistrement qui correspond au
	 * segment de phrase indiqué.
	 */
	public long getPhraseDuration(int phrase) {
		p.player.load(phrase);
		return p.player.getDuration();
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
	 * Colorie le segment numero n en couleur c
	 */
	public void highlightPhrase(Color c, int n) {
		if (p.textHandler.getPhrase(n) != null) {
			int debutRelatifSegment = p.textHandler.getRelativeStartPhrasePosition(p.getNumeroPremierSegmentAffiché(),
					n);
			int finRelativeSegment = debutRelatifSegment + p.textHandler.getPhrase(n).length();
			p.editorPane.surlignerPhrase(debutRelatifSegment, finRelativeSegment, c);
		}
	}

	/**
	 * Supprime le surlignage qui se trouve sur le segment n. Ne fait rien si ce
	 * segment n'est pas surligné.
	 */
	public void removeHighlightPhrase(int n) {
		int debutRelatifSegment = p.textHandler.getRelativeStartPhrasePosition(p.getNumeroPremierSegmentAffiché(), n);
		int finRelativeSegment = debutRelatifSegment + p.textHandler.getPhrase(n).length();
		p.editorPane.removeHighlight(debutRelatifSegment, finRelativeSegment);
	}

	/**
	 * Arrête l'enregistrement courant et enlève tout le surlignage.
	 */
	public void stopAll() {
		p.player.stop();
		p.editorPane.désurlignerTout();
	}

	/**
	 * Charge un segment de phrase dans le lecteur sans le démarrer.<br>
	 * Pas nécessaire si on démarre le lecteur directement avec la méthode
	 * {@link #play}.
	 */
	public void loadSound(int phrase) {
		p.player.load(phrase);
	}

	/**
	 * Enlève tout le surlignage d'erreur.
	 */
	public void removeWrongHighlights() {
		p.editorPane.enleverSurlignageRouge();
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

	/**
	 * Surligne tout depuis le début de la page jusqu'au segment de phrase indiqué.
	 */
	public void highlightUntilPhrase(Color c, int n) {
		p.surlignerJusquaSegment(c, n);
	}


}
