package main.controler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.text.BadLocationException;

import main.Constants;
import main.view.Mask;
import main.view.Panneau;

public class ControlerText {

	private Panneau p;

	/**
	 * Construit un contrôleur à partir du panneau correspondant.
	 */
	public ControlerText(Panneau p) {
		this.p = p;
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

	public boolean isLastInPhrase(int h) {
		return !p.textHandler.hasNextHoleInPhrase(h);
	}

	/**
	 * Affiche le compte rendu
	 */
	public void showReport() {
		p.afficherCompteRendu();
	}

	/**
	 * Affiche la page indiquée.
	 */
	public void showPage(int page) {
		p.showPage(page);
	}

	/**
	 * Joue un fichier .wav correspondant à un segment de phrase. On sortira de
	 * cette fonction lorsque le fichier .wav aura été totalement joué.
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
		return (long) (getCurrentPhraseDuration() * p.param.timeToWaitToLetStudentRepeat / 100.);
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
	 * 
	 * Affiche tous les trous correspondant à la page et à partir du trou indiquée.
	 * Désaffiche au préalable tous les trous.
	 */
	public void showHolesInPage(int h) {
		showHolesInPage(h, getPageOf(h));
	}

	/**
	 * 
	 * Affiche tous les trous correspondant à la page indiqué et à partir du trou
	 * indiquée. Désaffiche au préalable tous les trous.
	 */
	public void showHolesInPage(int h, int page) {
		// réinitialisation des trous
		removeAllMasks();
		// pour tous les trous
		for (int i = 0; i < p.textHandler.getHolesCount(); i++) {
			// si ce trou est dans la meme page que h
			if (getPageOf(i) == page) {
				// si ce trou est après le trou h ou est le trou h
				if (i >= h) {
					// on affiche ce trou
					showHole(i);
				}
			}
		}
	}

	private void showHole(int h) {
		/// on cache le trou avant de montrer la fenêtre ///
		hideHole(h);

		int startPhrase = p.segmentsEnFonctionDeLaPage.get(getPageOf(h)).get(0);

		int start = p.textHandler.getRelativeOffset(startPhrase, p.textHandler.getHoleStartOffset(h));
		int end = p.textHandler.getRelativeOffset(startPhrase, p.textHandler.getHoleEndOffset(h));

		try {
			p.afficherFrame(start, end, h);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
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

	/**
	 * Retourne le nombre de trous associés au segment n.
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

	public boolean waitForFill(int h) {
		Mask m = getMask(h);
		if (m == null)
			return true;
		m.activate();
		if(p.param.timeToShowWord == -1) {
			//m.setHint();
		} else {
			m.setHint(p.param.timeToShowWord*m.getNbCarac());
		}
		p.controlerMask.enter = false;
		while (true) {
			Thread.yield();
			if (p.controlerMask.enter) {
				return m != null && m.n == h ? m.correctWord() : true;
			}
		}
	}

	public boolean waitForFillFenetreFixe(int h) {

		Mask m = getFenetreFixe();
		m.activate();
		if(p.param.timeToShowWord == -1) {
			//m.setHint();
		} else {
			m.setHint(p.param.timeToShowWord*m.getNbCarac());
		}
		while (true) {

			if (getFenetreFixe() != m) {
				return true;
			}

			Thread.yield();
			if (p.controlerMask.enter) {
				p.controlerMask.enter = false;

				if (m.jtf.getText().equals(m.motCouvert)) {
					return true;
				} else {
					return false;
				}
			}
		}
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

	public Color getColorBackground() {
		return p.editorPane.getBackground();
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
	 * Crée une fenêtre de saisie fixe qui attends le resultat du trou h
	 * 
	 * @param h
	 * @return
	 */
	public void activateInputFenetreFixe(int h) {

		Mask frame = new Mask();
		((javax.swing.plaf.basic.BasicInternalFrameUI) frame.getUI()).setNorthPane(null);
		frame.setBorder(null);
		frame.setBounds(0, 0, p.panelFenetreFixe.getWidth(), p.panelFenetreFixe.getHeight());

		p.panelFenetreFixe.add(frame);

		Font f = new Font(p.editorPane.getFont().getFontName(), p.editorPane.getFont().getStyle(),
				p.editorPane.getFont().getSize() * 7 / 10);


		frame.initField(f, p.controlerMask);
		frame.n = h;
		frame.motCouvert = p.textHandler.getHidedWord(h);
		frame.toFront();
		frame.setVisible(true);
		frame.activate();

		p.fenetreFixe = frame;

	}

	public void doError() {
		blink();
		p.nbErreurs++;
		if(p.param.replayPhrase) {
			play(p.pilot.getCurrentPhraseIndex());
			doWait(getCurrentWaitTime(), Constants.CURSOR_LISTEN);
		}
	
	}

	public void desactiverFenetreFixe() {
		if (getFenetreFixe() != null) {
			getFenetreFixe().dispose();
		}
	}

	private Mask getFenetreFixe() {
		return p.fenetreFixe;
	}

	public void replaceMaskByWord(int h) {

		Mask m = getMask(h);
		if (m == null) {
			fillHole(h);
			return;
		}
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

		m.setVisible(false);

		p.replaceAllMask();

	}

	/**
	 * Remplace le trou h par le bon mot.
	 */
	public void fillHole(int h) {
		if (p.textHandler.isHidden(h)) {
			p.textHandler.fillHole(h);
			p.updateText();
			p.replaceAllMask();
		}
	}

	/**
	 * Cache le trou h.
	 */
	public void hideHole(int h) {		
		if (!p.textHandler.isHidden(h)) {
			p.textHandler.hideHole(h);
			p.updateText();
			p.replaceAllMask();
		}
	}

	/**
	 * Montre la page du segment i, lis le segment i et attends
	 * 
	 * @param i
	 */
	public void readPhrase(int i) {
		// on montre la page du segment
		showPage(getPageOfPhrase(i));
		// lire le fichier audio correspondant à ce segment
		play(i);
		// attendre le temps de pause nécessaire
		doWait(getCurrentWaitTime(), Constants.CURSOR_LISTEN);
	}

	/**
	 * Retourne <code>true</code> si le segment n contient au moins un trou.
	 */
	public boolean hasHole(int n) {
		return getHolesCount(n) > 0;
	}

	/**
	 * Desaffiche tous les trous puis montre uniquement le trou h
	 */
	public void showJustHole(int h) {
		removeAllMasks();
		showHole(h);
	}

	/**
	 * Surligne tout depuis le début de la page jusqu'au segment de phrase indiqué.
	 */
	public void highlightUntilPhrase(Color c, int n) {
		p.surlignerJusquaSegment(c, n);
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

	public void updateHG(int h) {
		p.updateHG(h);
	}

}
