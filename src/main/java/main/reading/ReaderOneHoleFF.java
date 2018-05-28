package main.reading;

import main.controler.ControlerText;

public class ReaderOneHoleFF extends HoleThread {

	public ReaderOneHoleFF(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {

		controler.desactiverFenetreFixe();

		

		/// joue le son si c'est le premier trou du segment ///
		if (controler.isFirstInPhrase(h)) {		
			controler.showPage(controler.getPageOf(h));
			replaceHoleOfPage(controler.getPageOf(h));
			controler.showJustHole(h);
			controler.readPhrase(controler.getPhraseOf(h));
		} else {
			replaceHoleOfPage(controler.getPageOf(h));
			controler.showJustHole(h);
		}

		if (needToDead) {
			return;
		}
		
		// active la fenêtre de saisie avec le trou actuel
		controler.activateInputFenetreFixe(h);

		// tant que la saisie n'est pas juste
		while (!controler.waitForFillFenetreFixe(h)) {
			if (needToDead) {
				return;
			}
			controler.doError(h);
		}

		if (needToDead) {
			return;
		}

		// désactiver la fenêtre de saisie
		controler.desactiverFenetreFixe();
		// remplacer le trou par le mot correspondant et replacer tous les masques
		controler.replaceMaskByWord(h);

		/// appel des écouteurs de fin de trou ///
		for (Runnable r : onHoleEnd) {
			r.run();
		}

	}

	/**
	 * Remplace tous les trous de la page par le bon mot
	 * 
	 * @param h
	 */
	public void replaceHoleOfPage(int p) {
		for (int i = 0; i < controler.getHolesCount(); i++) {
			if (controler.getPageOf(i) == p) {
				controler.replaceMaskByWord(i);
			}
		}
	}

}
