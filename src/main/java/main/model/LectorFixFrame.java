package main.model;

import java.awt.Color;
import java.util.Random;

import main.Constants;
import main.controler.ControlerText;

public class LectorFixFrame extends ReaderThread {

	public Object lock = new Object();

	public LectorFixFrame(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {

		controler.desactiverFenetreFixe();
		controler.showPage(controler.getPageOf(h));
		for (int i = 0; i < h; i++) {
			if(controler.getPageOf(i) == controler.getPageOf(h)) {
				controler.replaceMaskByWord(i);
			}
		}
		
		// pour chaque trou
		while (h < controler.getHolesCount()) {	
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
				// Pour tous les segments après le précédent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler
						.getPhraseOf(h); i++) {
					// on montre la page du segment
					controler.showPage(controler.getPageOfPhrase(i));
					// on montre uniquement les trous à partir du trou actuel et de cette page
					controler.showHolesInPage(h, controler.getPageOfPhrase(i));
					
					if(needToDead) {
						System.out.println("MORT DU THREAD "+getName());return;
					}
						
					
					// lire le fichier audio correspondant à ce segment
					controler.play(i);
					
					if(needToDead) {
						System.out.println("MORT DU THREAD "+getName());return;
					}
					
					// attendre le temps de pause nécessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
					
					if(needToDead) {
						System.out.println("MORT DU THREAD "+getName());return;
					}
				}
			}
			
			if(needToDead) {
				System.out.println("MORT DU THREAD "+getName());return;
			}
			
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// colorier le trou actuel en bleu
			controler.color(h, Color.cyan);
			// active la fenêtre de saisie avec le trou actuel
			controler.activateInputFenetreFixe(h);
			
			if(needToDead) {
				System.out.println("MORT DU THREAD "+getName());return;
			}
			
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				// clignotement et compter une erreur
				controler.doError();
			}
			
			if(needToDead) {
				System.out.println("MORT DU THREAD "+getName());return;
			}
			
			// désactiver la fenêtre de saisie
			controler.desactiverFenetreFixe();
			// remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
			//on passe au trou suivant
			h++;
			/// appel des écouteurs de fin de trou ///
			for (Runnable r : onHoleEnd) {
				r.run();
			}
		}

		// on enlève le dernier trou
		controler.removeAllMasks();
		
		// lire les phrases qui restent : pour tous les segments après le dernier trou
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1) + 1; i != controler.getPhrasesCount()- 1; i++) {
			// on montre la page du segment
			controler.showPage(controler.getPageOfPhrase(i));
			
			if(needToDead) {
				System.out.println("MORT DU THREAD "+getName());return;
			}
			
			// lire le fichier audio correspondant à ce segment
			controler.play(i);
			
			if(needToDead) {
				System.out.println("MORT DU THREAD "+getName());return;
			}
			
			// attendre le temps de pause nécessaire
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
			
			if(needToDead) {
				System.out.println("MORT DU THREAD "+getName());return;
			}
		}
		// afficher le compte rendu
		controler.showReport();

	}

}
