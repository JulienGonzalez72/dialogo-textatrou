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

	public void initialiseExo() {
		if(activeThread != null) {
			activeThread.doStop();
		}
		this.phrase = 0;
		p.nbErreurs = 0;
		controler.removeAllMasks();
		p.textHandler.init();
	}

	/**
	 * Se place sur le segment n et démarre le lecteur.
	 */
	public void goToPhrase(int n) throws IllegalArgumentException {
		if(n > p.textHandler.getPhrasesCount() - 1 || n < 0) {
			throw new IllegalArgumentException("Numero de segment invalide");
		}
		
		if(p.param.fixedField) {
			controler.desactiverFenetreFixe();
		}
		
		p.fenetre.setResizable(false);
		p.fenetreParam.updateOptionsOnExoStart(false);
		
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
