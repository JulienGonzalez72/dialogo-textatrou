package main.reading;

import main.controler.ControlerText;

public class ReaderOneHoleUF extends ReaderThread {
	
	public ReaderOneHoleUF(ControlerText controler, int h) {
		super(controler, h);
	}
	
	public void run() {
		while (h < controler.getHolesCount() - 1 && !needToDead) {
			/// affiche la page correspondante ///
			int page = controler.getPageOf(h);
			controler.showPage(page);
			/// remplace tous les trous de la page///
			for (int i = 0; i < controler.getPhrasesCount(); i++) {
				if (controler.getPageOf(i) == page) {
					controler.fillHole(i);
				}
			}
			/// affiche le trou actuel ///
			controler.showJustHole(h);
			/// lit tous les segments à lire jusqu'au trou actuel ///
			if (controler.isFirstInPhrase(h)) {
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
				controler.doError();
			}
			h++;
			/// appel des écouteurs de fin de trou ///
			for (Runnable r : onHoleEnd) {
				r.run();
			}
			/// si le trou était le dernier du segment lit tous les segments sans trou après ///
			if (controler.isLastInPhrase(h - 1)) {
				controler.fillHole(h - 1);
				for (int i = controler.getPhraseOf(h - 1) + 1; !controler.hasHole(i) && i < controler.getPhrasesCount() - 1 && !needToDead; i++) {
					controler.readPhrase(i);
				}
			}
		}
		/// à la fin, affiche le compte rendu ///
		if (h == controler.getHolesCount() - 1 && !needToDead) {
			//efface tous les trous
			controler.removeAllMasks();
			
			controler.showReport();
		}
	}
	
}
