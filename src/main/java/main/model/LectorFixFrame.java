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
			//activer la fen�tre de saisie
			//attendre une saisie
			//tant que la saisie n'est pas juste
				//compabiliser une erreur
				//clignoter
				//attendre une saisie
			//d�sactiver la fen�tre de saisie
			//remplacer le trou par le mot correspondant	
			//replacer tous les trous
			//passer au trou suivant
=======
>>>>>>> bced4c5e6b2a86fbac1d47293ae35214c55fcc86
		
		// pour chaque trou
		for (int h = this.h; h < controler.getHolesCount(); h++) {
			// on montre uniquement les trous � partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
<<<<<<< HEAD
				// on montre uniquement les trous � partir du trou actuel et de cette page
				controler.showHolesInPage(h);
				// lire le fichier audio correspondant � ce segment
				controler.play(controler.getPageOf(h));
				// attendre le temps de pause n�cessaire
				controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
=======
				//Pour tous les segments apr�s le pr�c�dent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h-1)+1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					//on montre la page du segment
					controler.showPage(controler.getPageOfPhrase(i));
					// lire le fichier audio correspondant � ce segment
					controler.play(i);
					// attendre le temps de pause n�cessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
				}
				
>>>>>>> bced4c5e6b2a86fbac1d47293ae35214c55fcc86
			}
			// colorier le trou actuel en bleu
			controler.color(h, Color.cyan);
			// active la fen�tre de saisie avec le trou actuel
<<<<<<< HEAD
			controler.activateInput(h);
			/*while (!controler.waitForFill()) {
				System.out.println("FAUX");
			}*/


=======
			controler.activateInputFenetreFixe(h);
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				//clignotement et compter une erreur
				controler.doError();
			
			}
			//d�sactiver la fen�tre de saisie
			controler.desactiverFenetreFixe();
			//remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
		}
		
		// on enl�ve le dernier trou
		controler.showHolesInPage(controler.getHolesCount());
		
		//lire les phrases qui restent
		//Pour tous les segments apr�s le dernier trou
		for (int i = controler.getPhraseOf(controler.getHolesCount()-1)+1;controler.getPageOfPhrase(i) != controler.getPhrasesCount()-1; i++) {
			//on montre la page du segment
			controler.showPage(controler.getPageOfPhrase(i));
			// lire le fichier audio correspondant � ce segment
			controler.play(i);
			// attendre le temps de pause n�cessaire
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
>>>>>>> bced4c5e6b2a86fbac1d47293ae35214c55fcc86
		}
		controler.showReport();

	}


}
