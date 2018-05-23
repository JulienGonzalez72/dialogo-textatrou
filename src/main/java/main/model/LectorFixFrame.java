package main.model;

import java.awt.Color;

import main.controler.ControlerText;

public class LectorFixFrame extends ReaderThread {

	public Object lock = new Object();
	public boolean notified;

	public LectorFixFrame(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {
<<<<<<< HEAD

=======
<<<<<<< HEAD
=======
		
>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c
>>>>>>> df7a7eea6cc11cfa4fedb74001e0915dbcc37763
		// pour chaque trou
		for (int h = this.h; h < controler.getHolesCount(); h++) {
			// on montre uniquement les trous � partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
<<<<<<< HEAD
				// Pour tous les segments apr�s le pr�c�dent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					controler.readPhrase(i);
=======
<<<<<<< HEAD
				// on montre uniquement les trous � partir du trou actuel et de cette page
				controler.showHolesInPage(h);
				// lire le fichier audio correspondant � ce segment
				controler.play(controler.getPageOf(h));
				// attendre le temps de pause n�cessaire
				controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
=======
>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c
				//Pour tous les segments apr�s le pr�c�dent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h-1)+1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					//on montre la page du segment
					controler.showPage(controler.getPageOfPhrase(i));
					// lire le fichier audio correspondant � ce segment
					controler.play(i);
					// attendre le temps de pause n�cessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
>>>>>>> df7a7eea6cc11cfa4fedb74001e0915dbcc37763
				}
			}
			// colorier le trou actuel en bleu
			controler.color(h, Color.cyan);
			// active la fen�tre de saisie avec le trou actuel
<<<<<<< HEAD
			controler.activateInputFenetreFixe(h);
			/*while (!controler.waitForFill()) {
				System.out.println("FAUX");
			}*/
=======
>>>>>>> 3d6261c7358b1a9657c9e8c6a1727a3c089ece9c
			controler.activateInputFenetreFixe(h);
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				// clignotement et compter une erreur
				controler.doError();
			}
			// d�sactiver la fen�tre de saisie
			controler.desactiverFenetreFixe();
			// remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
		}

		// on enl�ve le dernier trou
		controler.removeAllMasks();

		// lire les phrases qui restent
		// Pour tous les segments apr�s le dernier trou
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1) + 1; i != controler.getPhrasesCount()- 1; i++) {
			controler.readPhrase(i);
		}
		// afficher le compte rendu
		controler.showReport();

	}

}
