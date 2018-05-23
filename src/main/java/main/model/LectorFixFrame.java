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

		// pour chaque trou
		for (int h = this.h; h < controler.getHolesCount(); h++) {
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
				// Pour tous les segments après le précédent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					controler.readPhrase(i);
				}
			}
			// colorier le trou actuel en bleu
			controler.color(h, Color.cyan);
			// active la fenêtre de saisie avec le trou actuel
			controler.activateInputFenetreFixe(h);
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				// clignotement et compter une erreur
				controler.doError();
			}
			// désactiver la fenêtre de saisie
			controler.desactiverFenetreFixe();
			// remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
		}

		// on enlève le dernier trou
		controler.removeAllMasks();

		// lire les phrases qui restent
		// Pour tous les segments après le dernier trou
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1) + 1; i != controler.getPhrasesCount()- 1; i++) {
			controler.readPhrase(i);
		}
		// afficher le compte rendu
		controler.showReport();

	}

}
