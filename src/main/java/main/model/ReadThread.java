package main.model;

import java.util.ArrayList;
import java.util.List;

import main.controler.ControlerText;

public class ReadThread extends Thread {
	
	public boolean running = true;
	public int N;
	public ControlerText controler;
	public List<Runnable> onPhraseEnd = new ArrayList<>();
	
	public ReadThread(ControlerText controler, int N) {
		this.controler = controler;
		this.N = N;
	}
	
	public void run() {
		while (N < controler.getPhrasesCount() - 1 && running) {
			controler.removeAllHoles();
			controler.showPage(controler.getPageOfPhrase(N));
			controler.showHoles(controler.getPageOfPhrase(N));
			controler.play(N);
			controler.firstHole();
			while (controler.hasNextHole()) {
				boolean right = controler.waitForFill();
				if (right) {
					controler.validCurrentHole();
					controler.nextHole();
				}
				else {
					controler.blink();
				}
			}
			N++;
			for (Runnable r : onPhraseEnd) {
				r.run();
			}
		}
	}
	
	public void doStop() {
		interrupt();
		running = false;
	}
	
}
