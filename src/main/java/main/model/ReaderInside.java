package main.model;

import main.controler.ControlerText;

public class ReaderInside extends ReaderThread {
	
	public ReaderInside(ControlerText controler, int h) {
		super(controler, h);
	}
	
	public void run() {
		while (h < controler.getHolesCount() && !needToDead) {
			int n = controler.getPhraseOf(h);
			/// affiche la page orrespondante ///
			int page = controler.getPageOfPhrase(n);
			controler.showPage(page);
			/// valide tous les trous de la page avant le trou actuel ///
			for (int i = 0; i < h; i++) {
				if (controler.getPageOf(i) == page) {
					controler.fillHole(i);
				}
			}
			/// affiche tous les trous de la page à partir du trou actuel ///
			controler.showHolesInPage(h);
			/// lit tous les segments à lire jusqu'au trou actuel ///
			if (controler.isFirstInPhrase(h)) {
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h) && !needToDead; i++) {
					controler.readPhrase(i);
				}
			}
			if (needToDead) {
				return;
			}
			/// attends une saisie de l'utilisateur ///
			while (!controler.waitForFill(h)) {
				controler.doError();
			}
			h++;
			/// appel des écouteurs de fin de trou ///
			for (Runnable r : onHoleEnd) {
				r.run();
			}
		}
		/// lit les segments restants après le dernier trou ///
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1 + 1); i != controler.getPhrasesCount() - 1 && !needToDead; i++) {
			controler.readPhrase(i);
		}
		/// à la fin, affiche le compte rendu ///
		if (h == controler.getHolesCount() - 1) {
			controler.showReport();
		}
	}
	
}
