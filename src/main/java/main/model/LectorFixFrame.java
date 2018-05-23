package main.model;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import main.Constants;
import main.controler.ControlerText;

public class LectorFixFrame extends ReaderThread {

	public Object lock = new Object();
	public boolean notified;
<<<<<<< HEAD
	public int numberHole;

	public LectorFixFrame(ControlerText controler, int n) {
		super(controler, n);
=======
	public boolean needToDead = false;
	public int firstHole;

	public LectorFixFrame(ControlerText controler,int firstHole) {
		this.controler = controler;
		this.firstHole = firstHole;
>>>>>>> 9fa16605021141354e45cf5e3f55f7a521ab2e8d
	}

	public void run() {
		

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
<<<<<<< HEAD
		for (int h = 0; h < controler.getHolesCount() ; h++) {
=======
		for (int h = firstHole; h < numberHole ; h++) {
>>>>>>> 9fa16605021141354e45cf5e3f55f7a521ab2e8d
			//si le trou est le premier de son segment
			if(controler.isFirstInPhrase(h)) {
				//on montre uniquement les trous à partir du trou actuel et de cette page
				controler.showHolesInPage(h);
				//lire le fichier audio correspondant à ce segment
				controler.play(controler.getPageOf(h));
				//attendre le temps de pause nécessaire	
				controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
			}
			//colorier le trou actuel en bleu
			controler.color(h,Color.cyan);
			//active la fenêtre de saisie
			controler.activateInput(h);
			while(!controler.waitForFill(h)) {
				System.out.println("salut");
			}
			System.out.println("NE DOIT PAS SE LIRE");
		}
		
		
		controler.showReport();

	}
	
	public void doStop() {
		needToDead = true;
		interrupt();
	}

}
