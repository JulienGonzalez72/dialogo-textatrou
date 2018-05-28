package main.reading;

import main.controler.ControlerText;

public class ReaderAllHoleUF extends HoleThread {

	public ReaderAllHoleUF(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {

		

		/// joue le son si c'est le premier trou du segment ///
		if (controler.isFirstInPhrase(h)) {
			controler.showPage(controler.getPageOf(h));
			/// valide tous les trous de la page avant le trou actuel ///
			for (int i = 0; i < h; i++) {
				if (controler.getPageOf(i) == controler.getPageOf(h)) {
					controler.fillHole(i);
				}
			}

			/// affiche tous et uniquement les trous de la page à partir du trou actuel ///
			controler.showHolesInPage(h);
			controler.readPhrase(controler.getPhraseOf(h));
		} else {
			/// valide tous les trous de la page avant le trou actuel ///
			for (int i = 0; i < h; i++) {
				if (controler.getPageOf(i) == controler.getPageOf(h)) {
					controler.fillHole(i);
				}
			}

			/// affiche tous et uniquement les trous de la page à partir du trou actuel ///
			controler.showHolesInPage(h);
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

		/// appel des écouteurs de fin de trou ///
		for (Runnable r : onHoleEnd) {
			r.run();
		}

	}

}
