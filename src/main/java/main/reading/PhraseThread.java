package main.reading;

public class PhraseThread extends ReaderThread {
	
	private int n;
	
	public PhraseThread(int n) {
		super(null, -1);
		this.n = n;
	}
	
	public void run() {
		while (!controler.hasHole(n)) {
			controler.readPhrase(n);
			n++;
		}
		//int h = p.textHandler.getFirstHole(n);
		//goTo(h);
	}
	
}
