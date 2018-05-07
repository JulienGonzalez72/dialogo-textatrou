package main.reading;

import java.util.*;

import main.controler.ControlerText;
import main.model.Player;

public abstract class ReadThread extends Thread {
	
	/**
	 * Numero du segment courant 
	**/
	public int N;
	
	/**
	 * Actions a effectuer à la fin de la lecture du segment
	**/
	public List<Runnable> onPhraseEnd = new ArrayList<>();
	public boolean running = true;
	public ControlerText controler;
	public Player player;
	
	public ReadThread(ControlerText controler, int N) {
		this.controler = controler;
		this.N = N;
	}
	
	public void doStop() {
		interrupt();
		running = false;
	}
	
}
