package main.model;

import java.awt.Color;

import main.Constants;
import main.controler.ControlerText;

public class LectorFixFrame extends ReaderThread {
	
	//TODO
	//travailler par segment
	//fenetre initialise : visualiser avec le texte initial possible
	//fenetre a remplir que sur le segment en cours
	//surlignage possible
	//ajouter la possibilité de choisir le temps d'apparition du mot en fonction du nombre de caractères 
	//flash = 15 ms*caractère, 40,80,600, illimité = jusqu'à la saisie du premier caractère
	
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
						return;
					}
						
					
					// lire le fichier audio correspondant à ce segment
					controler.play(i);
					
					if(needToDead) {
						return;
					}
					
					// attendre le temps de pause nécessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
					
					if(needToDead) {
						return;
					}
				}
			}
			
			if(needToDead) {
				return;
			}
			
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// colorier le trou actuel en bleu
			controler.color(h, controler.getColorBackground() != Color.cyan ? Color.cyan : Color.YELLOW);
			// active la fenêtre de saisie avec le trou actuel
			controler.activateInputFenetreFixe(h);
			
			if(needToDead) {
				return;
			}
			
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				// clignotement et compter une erreur
				controler.doError();
			}
			
			if(needToDead) {
				return;
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
				return;
			}
			
			// lire le fichier audio correspondant à ce segment
			controler.play(i);
			
			if(needToDead) {
				return;
			}
			
			// attendre le temps de pause nécessaire
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
			
			if(needToDead) {
				return;
			}
		}
		// afficher le compte rendu
		controler.showReport();

	}

}
