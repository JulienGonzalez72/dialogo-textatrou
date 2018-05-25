package main.controler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;
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
	 * 
	 * Affiche tous les trous correspondant à la page et à partir du trou indiquée.
	 * Désaffiche au préalable tous les trous.
	 */
	public void showHolesInPage(int h) {
		showHolesInPage(h,getPageOf(h));
	}
	
	/**
	 * 
	 * Affiche tous les trous correspondant à la page indiqué et à partir du trou indiquée.
	 * Désaffiche au préalable tous les trous.
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
		p.controlerMask.enter = false;
		while (true) {
			Thread.yield();
			if (p.controlerMask.enter) {
				return m != null && m.n == h ? m.correctWord() : true;
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
	 * Crée une fenêtre de saisie fixe qui attends le resultat du trou h
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
		if (m == null)
			return;
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
	
	/**
	 * Remplace le trou h par le bon mot.
	 */
	public void fillHole(int h) {
		p.textHandler.fillHole(h);
		p.updateText();
		p.replaceAllMask();
	}
	
	/**
	 * Cache le trou h.
	 */
	public void hideHole(int h) {		
		p.textHandler.hideHole(h);
		p.updateText();
		p.replaceAllMask();
	}
	
	private boolean isFilled(int h) {
		int startPhrase = p.segmentsEnFonctionDeLaPage.get(getPageOf(h)).get(0);
		
		int start = p.textHandler.getRelativeOffset(startPhrase, p.textHandler.getHoleStartOffset(h));
		
		return p.editorPane.getText().charAt(start) != '_';
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
	
}
