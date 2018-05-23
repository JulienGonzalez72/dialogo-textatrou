package main.controler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import main.Constants;
import main.model.*;
import main.view.Mask;
import main.view.Panneau;

public class Pilot {

	/**
	 * Thread de lecture actif
	 */
	private LectorFixFrame activeThread;
	private Panneau p;
	public ControlerText controler;
	/**
	 * Segment actuel
	 */
	public int phrase;

	public Pilot(Panneau p) {
		this.p = p;
		controler = p.controlerGlobal;
	}

	/**
	 * Se place sur le segment de numero n et démarre le lecteur.
	 */
	public void goTo(int n) throws IllegalArgumentException {
		if (n < p.param.premierSegment - 1 || n >= p.textHandler.getPhrasesCount() - 1) {
			throw new IllegalArgumentException("Numéro de segment invalide : " + n);
		}

		// desactivation des autres fenetres fixes
		for (JInternalFrame j : p.getAllFrames()) {
			if (!(j instanceof Mask)) {
				j.dispose();
			}
		}

		phrase = n;
		/// désacive la taille et la police et le segment de départ
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		/// désactive les boutons de contrôle pour éviter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);

		/*boolean doitJouer = !p.fenetre.isResizable();
		/// empêche le redimensionnement de la fenêtre lors de la première lecture ///
		p.fenetre.setResizable(false);

		// met a jour la barre de progression
		updateBar();

		controler.showPage(controler.getPageOfPhrase(n));

		if (doitJouer) {
			/// play du son correspondant au segment N ///
			controler.play(n);
			/// attente de la fin du temps de pause ///
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);
		}*/
		
		updateBar();
		
		if (activeThread != null) {
			activeThread.doStop();
		}
		activeThread = (LectorFixFrame) getReaderThread(0);
		activeThread.start();
	}
	
	public void lancerLectorFixe() {
		activeThread = (LectorFixFrame) getReaderThread(0);
		activeThread.start();
	}
	
	public ReaderThread getReaderThread(int h) {
		return p.param.fixedField ? new LectorFixFrame(p.controlerGlobal, h) : new ReaderInside(p.controlerGlobal, h);
	}

	private void updateBar() {
		p.progressBar.setValue(getCurrentPhraseIndex());
		p.progressBar.setString((getCurrentPhraseIndex() + 1) + "/" + (p.textHandler.getPhrasesCount() - 1));
	}

	/**
	 * Essaye de passer au segment suivant, passe à la page suivante si c'était le
	 * dernier segment de la page. Affiche le compte rendu si on était au dernier
	 * segment du texte.
	 */
	public void doNext() {

		try {
			goTo(p.player.getCurrentPhraseIndex() + 1);
		} catch (IllegalArgumentException e) {
			p.afficherCompteRendu();
		}

	}

	/**
	 * Essaye de passer au segment précédent. Déclenche une erreur si on était au
	 * premier segment du texte.
	 */
	public void doPrevious() {
		goTo(p.player.getCurrentPhraseIndex() - 1);
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
		//showAllHoleInPages();

		goTo(p.player.getCurrentPhraseIndex());

		//si c'est la première fois qu'on appuie sur play
		if (p.player.getCurrentPhraseIndex() == p.param.premierSegment-1 && !p.lecteur.isAlive()) {
			
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
		}

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

}
