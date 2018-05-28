package main.reading;

import main.controler.ControlerText;

public class ReaderOneHoleUF extends HoleThread {

	public ReaderOneHoleUF(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {

		int page = controler.getPageOf(h);

		/// affiche uniquement le trou actuel ///
		controler.showJustHole(h);

		if (needToDead) {
			return;
		}

		/// attends une saisie de l'utilisateur ///
		while (!controler.waitForFill(h)) {
			if (needToDead) {
				return;
			}
			controler.doError();
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
	 * Montre la page du trou h Remplace tous les trous de la page par le bon mot
	 * 
	 * @param h
	 */
	public void showPage(int h) {
		controler.showPage(controler.getPageOf(h));
		for (int i = 0; i < controler.getPhrasesCount(); i++) {
			if (controler.getPageOf(i) == controler.getPageOf(h)) {
				controler.replaceMaskByWord(i);
			}
		}
	}

}
