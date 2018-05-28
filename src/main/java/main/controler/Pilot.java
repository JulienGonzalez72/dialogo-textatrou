package main.controler;

import main.Constants;
import main.reading.*;
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
	
	public void initialiseHole() {
		this.hole = 0;
	}

	/**
	 * Se place sur le trou de numero h et démarre le lecteur.
	 */
	public void goTo(int h) throws IllegalArgumentException {
		if (h < 0 || h >= p.textHandler.getHolesCount()) {
			throw new IllegalArgumentException("Numéro de trou invalide : " + h);
		}
		
		p.fenetre.setResizable(false);

		hole = h;
		/// désacive la taille et la police et le segment de départ
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		/// désactive les boutons de contrôle pour éviter le spam ///
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
		
		p.controlerGlobal.updateHG(hole);
	}
	
	/**
	 * Se place sur le segment n et démarre le lecteur.
	 */
	public void goToPhrase(int n) {
		if (activeThread != null) {
			activeThread.doStop();
		}
		activeThread = getReaderThread(n);
		activeThread.start();
	}
	
	/**
	 * Lecteur de segments sans trous, lance un lecteur de trous dès qu'il rencontre un segment avec au moins un trou.
	 */
	private class PhraseThread extends ReaderThread {
		private int n;
		public PhraseThread(int n) {
			super(p.controlerGlobal, -1);
			this.n = n;
		}
		public void run() {
			controler.removeAllMasks();
			while (!p.textHandler.hasHole(n) && !needToDead) {
				controler.readPhrase(n);
				n++;
			}
			int h = p.textHandler.getFirstHole(n);
			if (!needToDead) {
				goTo(h);
			}
		}
	}

	public ReaderThread getReaderThread(int h) {

		return p.param.oneHole ?

				p.param.fixedField ? new ReaderOneHoleFF(p.controlerGlobal, h)
						: new ReaderOneHoleUF(p.controlerGlobal, h)

				:

				p.param.fixedField ? new ReaderAllHoleFF(p.controlerGlobal, h)
						: new ReaderAllHoleUF(p.controlerGlobal, h);
	}

	/**
	 * Essaye de passer au trou suivant, passe à la page suivante si c'était le
	 * dernier trou de la page.
	 */
	public void doNext() {
		goTo(hole + 1);
	}

	/**
	 * Essaye de passer au trou précédent. Déclenche une erreur si on était au
	 * premier trou du texte.
	 */
	public void doPrevious() {
		goTo(hole - 1);
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
		goTo(hole);
	}
	
	public void nextPhrase() {
		goToPhrase(getCurrentPhraseIndex() + 1);
	}
	
	public void previousPhrase() {
		goToPhrase(getCurrentPhraseIndex() - 1);
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
