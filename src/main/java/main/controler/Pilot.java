package main.controler;

import main.Constants;
import main.reading.AnticipatedThread;
import main.reading.GuidedThread;
import main.reading.HighlightThread;
import main.reading.ReadThread;
import main.reading.SegmentedThread;
import main.view.Panneau;

public class Pilot {

	/**
	 * Thread de lecture actif
	 */
	private ReadThread activeThread;
	private Panneau p;
	private ControlerText controler;
	/**
	 * Segment actuel
	 */
	private int phrase;
	
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
		p.param.stockerPreference();
		phrase = n;
		///désacive la taille et la police et le segment de départ
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		/// désactive les boutons de contrôle pour éviter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);
		//vire le surlignagerouge
		p.editorPane.enleverSurlignageRouge();
		
		/// empêche le redimensionnement de la fenêtre lors de la première lecture ///
		p.fenetre.setResizable(false);
		
		//met a jour la barre de progression
		updateBar();
		
		
		
		controler.showPage(controler.getPageOfPhrase(n));
		/// play du son correspondant au segment N ///
		controler.play(n);
		/// attente de la fin du temps de pause ///
		controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);

		System.out.println(0);
		if(p.textHandler.motsParSegment.get(n).isEmpty()) {
			System.out.println(1);
			doNext();
		} else {
			System.out.println(2);
			p.nextHole();
		}
	}
	
	private void updateBar() {
		p.progressBar.setValue(getCurrentPhraseIndex());
		p.progressBar.setString((getCurrentPhraseIndex() + 1) + "/" + (p.textHandler.getPhrasesCount() - 1));
	}
	
	/**
	 * Essaye de passer au segment suivant, passe à la page suivante
	 * si c'était le dernier segment de la page.
	 * Déclenche une erreur si on était au dernier segment du texte.
	 */
	public void doNext() {
		goTo(p.player.getCurrentPhraseIndex() + 1);
	}

	/**
	 * Essaye de passer au segment précédent. Déclenche une erreur si on était au premier segment du texte.
	 */
	public void doPrevious() {
		goTo(p.player.getCurrentPhraseIndex() - 1);
	}

	/**
	 * Essaye d'arrêter l'enregistrement en cours.
	 */
	public void doStop() {
		p.player.stop();
		if (activeThread != null) {
			activeThread.doStop();
		}
	}

	/**
	 * Essaye de reprendre l'enregistrement. Si il est déjà démarré, reprend depuis
	 * le début.
	 */
	public void doPlay() {
		goTo(p.player.getCurrentPhraseIndex());
	}

	/**
	 * Créé un processus associé à la lecture d'un seul segment dans le mode de
	 * lecture actuel.
	 */
	public ReadThread getReadThread(int n) {
		ReadThread t;
		switch (p.param.readMode) {
			case ANTICIPE:
				t = new AnticipatedThread(controler, n);
				break;
			case GUIDEE:
				t = new GuidedThread(controler, n);
				break;
			case SEGMENTE:
				t = new SegmentedThread(controler, n);
				break;
			case SUIVI:
				t = new HighlightThread(controler, n);
				break;
			default:
				t = null;
				break;
		}
		return t;
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
