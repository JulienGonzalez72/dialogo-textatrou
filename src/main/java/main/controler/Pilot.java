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
	 * Trou actuel
	 */
	private int hole;
	/**
	 * Segment actuel
	 */
	private int phrase;

	public Pilot(Panneau p) {
		this.p = p;
		controler = p.controlerGlobal;
	}
	
	public void initialiseHole() {
		this.hole = 0;
	}

	/**
	 * Se place sur le trou de numero h et d�marre le lecteur.
	 */
	public void goTo(int h) throws IllegalArgumentException {
		if (h < 0 || h >= p.textHandler.getHolesCount()) {
			throw new IllegalArgumentException("Num�ro de trou invalide : " + h);
		}

		
		/*p.fenetre.setResizable(false);

		hole = h;
		/// d�sacive la taille et la police et le segment de d�part
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		/// d�sactive les boutons de contr�le pour �viter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);

		p.updateBar(hole);

		if (activeThread != null) {
			activeThread.doStop();
		}
		activeThread = getReaderThread(h);
		activeThread.onHoleEnd.add(new Runnable() {
			public void run() {
				controler.updateHG(hole + 1);
				hole = activeThread.h;
				p.updateBar(hole);
			}
		});
		activeThread.start();
		
		p.controlerGlobal.updateHG(hole);*/
	}
	
	/**
	 * Se place sur le segment n et d�marre le lecteur.
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
	 * Essaye de passer au trou suivant, passe � la page suivante si c'�tait le
	 * dernier trou de la page.
	 */
	public void doNext() {
		goTo(hole + 1);
	}

	/**
	 * Essaye de passer au trou pr�c�dent. D�clenche une erreur si on �tait au
	 * premier trou du texte.
	 */
	public void doPrevious() {
		goTo(hole - 1);
	}

	/**
	 * Essaye d'arr�ter l'enregistrement en cours.
	 */
	public void doStop() {
		p.player.stop();
	}

	/**
	 * Essaye de reprendre l'enregistrement. Si il est d�j� d�marr�, reprend depuis
	 * le d�but.
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

	public int getCurrentHoleIndex() {
		return hole;
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

	public boolean hasPreviousHole() {
		return hole > 0;
	}

	public boolean hasNextHole() {
		return hole < p.textHandler.getHolesCount() - 1;
	}

}
