package main.reading;

import main.controler.ControlerText;

public abstract class HoleThread extends ReaderThread {
	
	public int h;
	
	public HoleThread(ControlerText controler, int h) {
		super(controler);
		this.h = h;
	}
	
}
