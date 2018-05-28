package main.reading;

import main.controler.ControlerText;

public class PhraseThread extends ReaderThread {
	
	public int n;
	private HoleThread activeThread;
	
	public PhraseThread(ControlerText controler, int n) {
		super(controler);
		this.n = n;
	}
	
	public void run() {
		while (n < controler.getPhrasesCount() - 1) {
			/// joue le segment ///
			controler.readPhrase(n);
			/// traite tous les trous du segment un par un ///
			for (int h = 0; h < controler.getHolesCount(n); h++) {
				activeThread = controler.getHoleThread(h);
				activeThread.start();
				try {
					activeThread.join();
				} catch (InterruptedException e) {
				}
			}
		}
		/// affiche le compte rendu ///
		controler.showReport();
	}
	
	@Override
	public void doStop() {
		if (activeThread != null) {
			activeThread.doStop();
		}
		super.doStop();
	}
	
}
