package main.controler;

import javax.swing.text.BadLocationException;

import main.Constants;
import main.view.Panneau;

public class Pilot {

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
	 * Se place sur le segment de numero n et d�marre le lecteur.
	 */
	public void goTo(int n) throws IllegalArgumentException {
		if (n < p.param.premierSegment - 1 || n >= p.textHandler.getPhrasesCount() - 1) {
			throw new IllegalArgumentException("Num�ro de segment invalide : " + n);
		}
		
		p.param.stockerPreference();
		phrase = n;
		///d�sacive la taille et la police et le segment de d�part
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		p.fenetreParam.pan.champMysterCarac.setEditable(false);
		/// d�sactive les boutons de contr�le pour �viter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);

		/// emp�che le redimensionnement de la fen�tre lors de la premi�re lecture ///
		p.fenetre.setResizable(false);

		// met a jour la barre de progression
		updateBar();

		controler.showPage(controler.getPageOfPhrase(n));
		
		/// play du son correspondant au segment N ///
		controler.play(n);
		/// attente de la fin du temps de pause ///
		controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);

		if (p.textHandler.motsParSegment.get(n).isEmpty()) {
			doNext();
		} else {
			nextHole();
		}
	}

	private void updateBar() {
		p.progressBar.setValue(getCurrentPhraseIndex());
		p.progressBar.setString((getCurrentPhraseIndex() + 1) + "/" + (p.textHandler.getPhrasesCount() - 1));
	}

	/**
	 * Essaye de passer au segment suivant, passe � la page suivante si c'�tait le
	 * dernier segment de la page. D�clenche une erreur si on �tait au dernier
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
	 * Essaye de passer au segment pr�c�dent. D�clenche une erreur si on �tait au
	 * premier segment du texte.
	 */
	public void doPrevious() {
		goTo(p.player.getCurrentPhraseIndex() - 1);
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
		goTo(p.player.getCurrentPhraseIndex());
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
	
	public void nextHole() {
		int offset = p.textHandler.getAbsoluteOffset(p.getNumeroPremierSegmentAffich�(),p.editorPane.getText().indexOf(" _")+1);
		int start2 = p.textHandler.startWordPosition(offset);
		int end2 = p.textHandler.endWordPosition(offset);
		if ( start2 > end2) {
			System.out.println("start2 > end2"); 
			return;
		}
		try {
			p.afficherFrame(start2,end2);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
