package main.model;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import main.Constants;
import main.controler.ControlerText;
import main.view.Mask;

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
			//activer la fenêtre de saisie
			//attendre une saisie
			//tant que la saisie n'est pas juste
				//compabiliser une erreur
				//clignoter
				//attendre une saisie
			//désactiver la fenêtre de saisie
			//remplacer le trou par le mot correspondant	
			//replacer tous les trous
			//passer au trou suivant
		
		//pour chaque trou
		for (int h = this.h; h < controler.getHolesCount() ; h++) {
			//si le trou est le premier de son segment
			if(controler.isFirstInPhrase(h)) {
				//on montre uniquement les trous à partir du trou actuel et de cette page
=======
		// activer la fenêtre de saisie
		// attendre une saisie
		// tant que la saisie n'est pas juste
		// compabiliser une erreur
		// clignoter
		// attendre une saisie
		// désactiver la fenêtre de saisie
		// remplacer le trou par le mot correspondant
		// replacer tous les trous
		// passer au trou suivant

>>>>>>> d1e14c697f813926ddef73c3888a4470d0fbded0
		// pour chaque trou
		for (int h = this.h; h < controler.getHolesCount(); h++) {
			//on montre la page du trou
			controler.showPage(controler.getPageOf(h));
			// on montre uniquement les trous à partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
<<<<<<< HEAD
				//Pour tous les segments après le précédent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h-1)+1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					// lire le fichier audio correspondant à ce segment
					controler.play(i);
					// attendre le temps de pause nécessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
				}
				
=======
				// on montre uniquement les trous à partir du trou actuel et de cette page
>>>>>>> 30f00871bb49bfad71e94aaf7a4c474cfe0b6772
				controler.showHolesInPage(h);
				// lire le fichier audio correspondant à ce segment
				controler.play(controler.getPageOf(h));
				// attendre le temps de pause nécessaire
				controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
>>>>>>> d1e14c697f813926ddef73c3888a4470d0fbded0
			}
			// colorier le trou actuel en bleu
			controler.color(h, Color.cyan);
			// active la fenêtre de saisie avec le trou actuel
<<<<<<< HEAD
			controler.activateInputFenetreFixe(h);
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				System.out.println("FAUX");
				//clignotement et compter une erreur
				controler.doError();
			
=======
			controler.activateInput(h);
<<<<<<< HEAD
			while(!controler.waitForFill()) {
				System.out.println("salut");
=======

			while (!controler.waitForFill()) {
				System.out.println("FAUX");
>>>>>>> 30f00871bb49bfad71e94aaf7a4c474cfe0b6772
>>>>>>> d1e14c697f813926ddef73c3888a4470d0fbded0
			}
			//désactiver la fenêtre de saisie
			controler.desactiverFenetreFixe();
			//remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
		}

		controler.showReport();

	}

	public void doStop() {
		needToDead = true;
		interrupt();
	}

}
