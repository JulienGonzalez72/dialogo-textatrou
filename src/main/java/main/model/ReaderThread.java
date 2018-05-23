package main.model;

import main.controler.ControlerText;

public abstract class ReaderThread extends Thread {
	
	public boolean needToDead;
	public ControlerText controler;
	public int h;
	
	public ReaderThread(ControlerText controler, int h) {
		this.controler = controler;
		this.h = h;
	}
	
	public abstract void run();
	
	public void doStop() {
		needToDead = true;
		interrupt();
	}
	
}