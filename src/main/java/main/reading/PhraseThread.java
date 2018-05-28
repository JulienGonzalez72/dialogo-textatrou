package main.reading;

import java.util.ArrayList;
import java.util.List;

import main.controler.ControlerText;

public class PhraseThread extends ReaderThread {
	
	public int n;
	private HoleThread activeThread;
	/**
	 * Liste des écouteurs de fin de segment.
	 */
	public List<Runnable> onPhraseEnd = new ArrayList<>();
	
	public PhraseThread(ControlerText controler, int n) {
		super(controler);
		this.n = n;
	}
	
	public void run() {
		while (n < controler.getPhrasesCount() - 1 && !needToDead) {
			/// joue le segment ///
			controler.readPhrase(n);
			/// traite tous les trous du segment un par un ///
			for (int h = 0; h < controler.getHolesCount(n) && !needToDead; h++) {
				activeThread = controler.getHoleThread(controler.getFirstHole(n) + h);
				activeThread.start();
				try {
					activeThread.join();
				} catch (InterruptedException e) {
				}
			}
			n++;
			/// appel les écouteurs de fin de segment ///
			for (Runnable r : onPhraseEnd) {
				r.run();
			}
		}
		/// affiche le compte rendu ///
		if (n == controler.getPhrasesCount() - 1) {
			controler.showReport();
		}
	}
	
	@Override
	public void doStop() {
		if (activeThread != null) {
			activeThread.doStop();
		}
		super.doStop();
	}
	
}
