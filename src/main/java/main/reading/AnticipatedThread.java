package main.reading;

import main.Constants;
import main.controler.ControlerText;

public class AnticipatedThread extends ReadThread {

	public AnticipatedThread(ControlerText controler, int N) {
		super(controler,N);
	}
	
	public void run() {
		while (N < controler.getPhrasesCount() - 1 && running) {
			/// r�initiliase l'�tat ///
			controler.stopAll();
			/// chargement du son ///
			controler.loadSound(N);
			/// affichage de la page correspondant au segment N ///
			controler.showPage(controler.getPageOfPhrase(N));
			/// surlignage du segment N ///
			controler.highlightPhrase(Constants.RIGHT_COLOR, N);
			/// attente de la fin du temps de pause ///
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);
			/// on arr�te l'ex�cution si le thread est termin� ///
			if (!running) {
				return;
			}
			/// play du son correspondant au segment N ///
			controler.play(N);
			/// on arr�te l'ex�cution si le thread est termin� ///
			if (!running) {
				return;
			}
			/// suppression du surlignage du segment de phrase N ///
			controler.removeHighlightPhrase(N);
			N++;
			/// appel des �couteurs de fin de segment ///
			for (Runnable r : onPhraseEnd) {
				r.run();
			} 
		}
	}

}
