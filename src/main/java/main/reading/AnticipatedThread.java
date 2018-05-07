package main.reading;

import main.Constants;
import main.controler.ControlerText;

public class AnticipatedThread extends ReadThread {

	public AnticipatedThread(ControlerText controler, int N) {
		super(controler,N);
	}
	
	public void run() {
		while (N < controler.getPhrasesCount() - 1 && running) {
			/// réinitiliase l'état ///
			controler.stopAll();
			/// chargement du son ///
			controler.loadSound(N);
			/// affichage de la page correspondant au segment N ///
			controler.showPage(controler.getPageOfPhrase(N));
			/// surlignage du segment N ///
			controler.highlightPhrase(Constants.RIGHT_COLOR, N);
			/// attente de la fin du temps de pause ///
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);
			/// on arrête l'exécution si le thread est terminé ///
			if (!running) {
				return;
			}
			/// play du son correspondant au segment N ///
			controler.play(N);
			/// on arrête l'exécution si le thread est terminé ///
			if (!running) {
				return;
			}
			/// suppression du surlignage du segment de phrase N ///
			controler.removeHighlightPhrase(N);
			N++;
			/// appel des écouteurs de fin de segment ///
			for (Runnable r : onPhraseEnd) {
				r.run();
			} 
		}
	}

}
