package main.reading;

import main.controler.ControlerText;

public class ReaderOneHoleUF extends HoleThread {

	public ReaderOneHoleUF(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {
		replaceHoleOfPage(controler.getPageOf(h));

		/// affiche uniquement le trou actuel ///
		controler.showJustHole(h);

		/// joue le son si c'est le premier trou du segment ///
		if (controler.isFirstInPhrase(h)) {
			controler.showPage(controler.getPageOf(h));
			controler.readPhrase(controler.getPhraseOf(h));
		}

		if (needToDead) {
			return;
		}

		/// attends une saisie de l'utilisateur ///
		while (!controler.waitForFill(h)) {
			if (needToDead) {
				return;
			}
			controler.doError(h);
		}

		if (needToDead) {
			return;
		}

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
	 * @param page
	 */
	public void replaceHoleOfPage(int page) {
		for (int i = 0; i < controler.getHolesCount(); i++) {
			if (controler.getPageOf(i) == page) {
				controler.replaceMaskByWord(i);
			}
		}
	}

}
