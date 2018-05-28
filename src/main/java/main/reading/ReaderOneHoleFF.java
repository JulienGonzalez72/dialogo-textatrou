package main.reading;

import main.controler.ControlerText;

public class ReaderOneHoleFF extends ReaderThread {//extends ReaderHole{

	public ReaderOneHoleFF(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {

			controler.desactiverFenetreFixe();

			showPage(h);

			// on montre uniquement le trou actuel
			controler.showJustHole(h);
			// active la fenêtre de saisie avec le trou actuel
			controler.activateInputFenetreFixe(h);

			if (needToDead) {return;}

			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				if (needToDead) {return;}
				controler.doError();
			}

			if (needToDead) {return;}

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
	 * Montre la page du trou h
	 * Remplace tous les trous de la page par le bon mot
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
