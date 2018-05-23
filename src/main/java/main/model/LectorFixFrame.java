package main.model;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import main.Constants;
import main.controler.ControlerText;

public class LectorFixFrame extends Thread {

	ControlerText controler;
	public Object lock = new Object();
	public boolean notified;
	public boolean needToDead = false;
	public int numberHole;

	public LectorFixFrame(ControlerText controler,int numberHole) {
		this.controler = controler;
		this.numberHole = numberHole;
	}

	public void run() {
		
		Map<Integer,List<String>> wordByPhrases = controler.getWordByPhrases();
		Map<Integer,String> words = controler.getWords();
		int numberHole = words.size();
		
		//pour chaque trou
			//si le trou est le premier de son segment	
				//on montre uniquement les trous à partir du trou actuel et de cette page
				//lire le fichier audio correspondant à ce segment
				//attendre le temps de pause nécessaire	
			//colorier le trou actuel en bleu
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
		for (int h = 0; h < numberHole ; h++) {
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
			//active la fnêtre de saisie
			controler.activateInput(h);
		}
		
		
		controler.showReport();

	}
	
	public void doStop() {
		needToDead = true;
		interrupt();
	}

}
