package main.controler;

import main.Constants;
import main.model.*;
import main.view.Panneau;

public class Pilot {

	/**
	 * Thread de lecture actif
	 */
	private ReaderThread activeThread;
	private Panneau p;
	public ControlerText controler;
	/**
	 * Trou actuel
	 */
	private int hole;

	public Pilot(Panneau p) {
		this.p = p;
		controler = p.controlerGlobal;
	}

	/**
	 * Se place sur le trou de numero h et d�marre le lecteur.
	 */
	public void goTo(int h) throws IllegalArgumentException {
		if (h < 0 || h >= p.textHandler.getHolesCount() - 1) {
			throw new IllegalArgumentException("Num�ro de trou invalide : " + h);
		}

		hole = h;
		/// d�sacive la taille et la police et le segment de d�part
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		/// d�sactive les boutons de contr�le pour �viter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);
		
		updateBar();
		
		if (activeThread != null) {
			activeThread.doStop();
		}
		activeThread = getReaderThread(h);
		activeThread.onHoleEnd.add(new Runnable() {
			public void run() {
				hole = activeThread.h;
				updateBar();
			}
		});
		activeThread.start();
	}
	
	public ReaderThread getReaderThread(int h) {
		return p.param.fixedField ? new LectorFixFrame(p.controlerGlobal, h) : new ReaderInside(p.controlerGlobal, h);
	}

	private void updateBar() {
		p.progressBar.setValue(getCurrentHoleIndex());
		p.progressBar.setString((getCurrentHoleIndex() + 1) + "/" + (p.textHandler.getHolesCount() - 1));
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
		//showAllHoleInPages();

		goTo(hole);

		//si c'est la premi�re fois qu'on appuie sur play
		/*if (p.player.getCurrentPhraseIndex() == p.param.premierSegment-1 && !p.lecteur.isAlive()) {
			
			//on recupere le numero courant
			int numeroCourant = 0;
			for (int i =0; i < p.textHandler.motsParSegment.size();i++) {
				if ( i == p.param.premierSegment-1) {
					break;
				}
				numeroCourant += p.textHandler.motsParSegment.get(i).size();	
			}	
			p.numeroCourant =  numeroCourant;
			p.lecteur.start();
		}*/

	}

	public int getCurrentPhraseIndex() {
		return p.player.getCurrentPhraseIndex();
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
	
}
