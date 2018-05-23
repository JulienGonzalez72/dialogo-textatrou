package main.model;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import main.Constants;
import main.controler.ControlerText;

public class LectorFixFrame extends ReaderThread {

	public Object lock = new Object();
	public boolean notified;
	public int numberHole;

	public LectorFixFrame(ControlerText controler, int n) {
		super(controler, n);
	}

	public void run() {
		

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
		
		//pour chaque trou
		for (int h = 0; h < controler.getHolesCount() ; h++) {
			//si le trou est le premier de son segment
			if(controler.isFirstInPhrase(h)) {
				//on montre uniquement les trous � partir du trou actuel et de cette page
				controler.showHolesInPage(h);
				//lire le fichier audio correspondant � ce segment
				controler.play(controler.getPageOf(h));
				//attendre le temps de pause n�cessaire	
				controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
			}
			//colorier le trou actuel en bleu
			controler.color(h,Color.cyan);
			//active la fen�tre de saisie
			controler.activateInput(h);
			while(!controler.waitForFill()) {
				System.out.println("salut");
			}
		}
		
		
		controler.showReport();

	}
	
	public void doStop() {
		needToDead = true;
		interrupt();
	}

}
