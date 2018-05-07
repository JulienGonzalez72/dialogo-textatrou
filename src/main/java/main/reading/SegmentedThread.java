package main.reading;

import main.Constants;
import main.controler.ControlerText;

public class SegmentedThread extends ReadThread {

	public SegmentedThread(ControlerText controler, int N) {
		super(controler, N);
	}

	public void run() {
		while (N < controler.getPhrasesCount() - 1 && running) {
			/// affichage de la page correspondant au segment N ///
			controler.showPage(controler.getPageOfPhrase(N));
			/// play du son correspondant au segment N ///
			controler.play(N);
			/// attente de la fin du temps de pause ///
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);
			// enlever surlignage
			controler.removeHighlightPhrase(N);
			controler.removeWrongHighlights();
			/// appel des écouteurs de fin de segment ///
			for (Runnable r : onPhraseEnd) {
				r.run();
			}
		}

	}

}
