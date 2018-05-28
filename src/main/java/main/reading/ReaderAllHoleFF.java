package main.reading;

import java.awt.Color;
import main.controler.ControlerText;

public class ReaderAllHoleFF extends HoleThread {

	public ReaderAllHoleFF(ControlerText controler, int h) {
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
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			controler.readPhrase(controler.getPhraseOf(h));
		} else {
			/// valide tous les trous de la page avant le trou actuel ///
			for (int i = 0; i < h; i++) {
				if (controler.getPageOf(i) == controler.getPageOf(h)) {
					controler.fillHole(i);
				}
			}
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
		}

		if (needToDead) {
			return;
		}
		
		// colorier le trou actuel en bleu ou jaune si le fond est bleu
		controler.color(h, controler.getColorBackground() != Color.cyan ? Color.cyan : Color.YELLOW);
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

}
