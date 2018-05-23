package main.model;

import main.controler.ControlerText;

public class ReaderInside extends ReaderThread {
	
	public ReaderInside(ControlerText controler, int h) {
		super(controler, h);
	}
	
	public void run() {
		while (h < controler.getHolesCount()) {
			controler.removeAllMasks();
			int n = controler.getPhraseOf(h);
			controler.showPage(controler.getPageOfPhrase(n));
			controler.showHolesInPage(h);
			if (controler.isFirstInPhrase(h)) {
				for (int i = h > 0 ? controler.getPhraseOf(h - 1) + 1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h) && !needToDead; i++) {
					controler.readPhrase(i);
				}
			}
			controler.replaceMaskByWord(h);
			while (!controler.waitForFill(h)) {
				controler.doError();
			}
			h++;
		}
		
		for (int i = controler.getPhraseOf(controler.getHolesCount() - 1 + 1); i != controler.getPhrasesCount() - 1; i++) {
			controler.readPhrase(i);
		}
		
		controler.showReport();
	}
	
}
