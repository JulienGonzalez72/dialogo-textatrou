package main.controler;

import main.reading.*;
import main.view.Panneau;

public class Pilot {

	/**
	 * Thread de lecture actif
	 */
	private PhraseThread activeThread;
	private Panneau p;
	public ControlerText controler;

	/**
	 * Segment actuel
	 */
	private int phrase;

	public Pilot(Panneau p) {
		this.p = p;
		controler = p.controlerGlobal;
	}

	public void initialisePhrase() {
		this.phrase = 0;
	}

	/**
	 * Se place sur le segment n et démarre le lecteur.
	 */
	public void goToPhrase(int n) {
		phrase = n;
		if (activeThread != null) {
			activeThread.doStop();
		}
		activeThread = new PhraseThread(controler, n);
		activeThread.onPhraseEnd.add(new Runnable() {
			public void run() {
				phrase = activeThread.n;
			}
		});
		activeThread.start();
	}

	/**
	 * Essaye d'arrêter l'enregistrement en cours.
	 */
	public void doStop() {
		p.player.stop();
	}

	/**
	 * Essaye de reprendre l'enregistrement. Si il est déjà démarré, reprend depuis
	 * le début.
	 */
	public void doPlay() {
		goToPhrase(phrase);
	}

	public void nextPhrase() {
		goToPhrase(phrase + 1);
	}

	public void previousPhrase() {
		goToPhrase(phrase - 1);
	}

	public int getCurrentPhraseIndex() {
		return phrase;
	}

	public boolean isPlaying() {
		return p.player.isPlaying();
	}

	public boolean hasPreviousPhrase() {
		return p.player.hasPreviousPhrase();
	}

	public boolean hasNextPhrase() {
		return p.player.hasNextPhrase();
	}

}
