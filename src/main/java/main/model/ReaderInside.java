package main.model;

import main.controler.ControlerText;

public class ReaderInside extends ReaderThread {
	
	public ReaderInside(ControlerText controler, int h) {
		super(controler, h);
	}
	
	public void run() {
		while (h < controler.getHolesCount()) {
			controler.removeAllHoles();
			int n = controler.getPhraseOf(h);
			controler.showPage(controler.getPageOfPhrase(n));
			controler.showHolesInPage(h);
			controler.play(n);
			while (controler.waitForFill()) {
				
			}
			h++;
		}
	}
	
}
