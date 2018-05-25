package main.controler;

import java.awt.Color;
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
	 * Se place sur le trou de numero h et démarre le lecteur.
	 */
	public void goTo(int h) throws IllegalArgumentException {
		if (h < 0 || h >= p.textHandler.getHolesCount()) {
			throw new IllegalArgumentException("Numéro de trou invalide : " + h);
		}

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
				controler.updateHG(hole+1);
				hole = activeThread.h;
				p.updateBar(hole);
			}
		});
		activeThread.start();
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

	public boolean hasPreviousHole() {
		return hole > 0;
	}

	public boolean hasNextHole() {
		return hole < p.textHandler.getHolesCount() - 1;
	}

}
