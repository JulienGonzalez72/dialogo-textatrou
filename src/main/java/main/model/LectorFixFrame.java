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
	//ajouter la possibilit� de choisir le temps d'apparition du mot en fonction du nombre de caract�res 
	//flash = 15 ms*caract�re, 40,80,600, illimit� = jusqu'� la saisie du premier caract�re
	
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
				// Pour tous les segments apr�s le pr�c�dent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler
						.getPhraseOf(h); i++) {
					// on montre la page du segment
					controler.showPage(controler.getPageOfPhrase(i));
					// on montre uniquement les trous � partir du trou actuel et de cette page
					controler.showHolesInPage(h, controler.getPageOfPhrase(i));
					
					if(needToDead) {
						return;
					}
						
					
					// lire le fichier audio correspondant � ce segment
					controler.play(i);
					
					if(needToDead) {
						return;
					}
					
					// attendre le temps de pause n�cessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
					
					if(needToDead) {
						return;
					}
				}
			}
			
			if(needToDead) {
				return;
			}
			
			// on montre uniquement les trous � partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// colorier le trou actuel en bleu
			controler.color(h, controler.getColorBackground() != Color.cyan ? Color.cyan : Color.YELLOW);
			// active la fen�tre de saisie avec le trou actuel
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
			
			// d�sactiver la fen�tre de saisie
			controler.desactiverFenetreFixe();
			// remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
			//on passe au trou suivant
			h++;
			/// appel des �couteurs de fin de trou ///
			for (Runnable r : onHoleEnd) {
				r.run();
			}
		}

		// on enl�ve le dernier trou
		controler.removeAllMasks();
		
		// lire les phrases qui restent : pour tous les segments apr�s le dernier trou
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1) + 1; i != controler.getPhrasesCount()- 1; i++) {
			// on montre la page du segment
			controler.showPage(controler.getPageOfPhrase(i));
			
			if(needToDead) {
				return;
			}
			
			// lire le fichier audio correspondant � ce segment
			controler.play(i);
			
			if(needToDead) {
				return;
			}
			
			// attendre le temps de pause n�cessaire
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
			
			if(needToDead) {
				return;
			}
		}
		// afficher le compte rendu
		controler.showReport();

	}

}
