package main.reading;

import java.util.ArrayList;
import java.util.List;

import main.controler.ControlerText;

public abstract class ReaderThread extends Thread {
	
	public boolean needToDead;
	public ControlerText controler;
	/**
	 * Ecouteurs qui s'appellent à la fin de chaque trou (lorsque l'utilisateur a bien rempli son mot).
	 */
	public List<Runnable> onHoleEnd = new ArrayList<>();
	
	public ReaderThread(ControlerText controler) {
		this.controler = controler;
	}
	
	public abstract void run();
	
	public void doStop() {
		needToDead = true;
		interrupt();
	}
	
}