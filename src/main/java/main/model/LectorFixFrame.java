package main.model;

import java.awt.Color;

import main.Constants;
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

>>>>>>> 9f56689b3a0b9ae93eb4be7adcfd759ba894133f
>>>>>>> f20b17abadb3fde9dbb394c5329e78798de7f597
		// pour chaque trou
		for (int h = this.h; h < controler.getHolesCount(); h++) {
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
<<<<<<< HEAD
				// Pour tous les segments après le précédent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler
						.getPhraseOf(h); i++) {
=======
<<<<<<< HEAD
				// Pour tous les segments après le précédent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					controler.readPhrase(i);
=======
				// Pour tous les segments après le précédent trou, jusqu'au segment de ce trou comprise t
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
>>>>>>> f20b17abadb3fde9dbb394c5329e78798de7f597
					// on montre la page du segment
					controler.showPage(controler.getPageOfPhrase(i));
					// on montre uniquement les trous à partir du trou actuel et de cette page
					controler.showHolesInPage(h, controler.getPageOfPhrase(i));
					// lire le fichier audio correspondant à ce segment
					controler.play(i);
					// attendre le temps de pause nécessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
>>>>>>> 9f56689b3a0b9ae93eb4be7adcfd759ba894133f
				}
			}
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
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

		// lire les phrases qui restent : pour tous les segments après le dernier trou
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1) + 1; i != controler.getPhrasesCount()
				- 1; i++) {
			// on montre la page du segment
			controler.showPage(controler.getPageOfPhrase(i));
			// lire le fichier audio correspondant à ce segment
			controler.play(i);
			// attendre le temps de pause nécessaire
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
		}
		// afficher le compte rendu
		controler.showReport();

	}

}
