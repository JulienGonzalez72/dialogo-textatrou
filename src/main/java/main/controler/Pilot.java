package main.controler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import main.Constants;
import main.view.Mask;
import main.view.Panneau;

public class Pilot {

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
	 * Se place sur le segment de numero n et d�marre le lecteur.
	 */
	public void goTo(int n) throws IllegalArgumentException {
		if (n < p.param.premierSegment - 1 || n >= p.textHandler.getPhrasesCount() - 1) {
			throw new IllegalArgumentException("Num�ro de segment invalide : " + n);
		}

		// desactivation des autres fenetres fixes
		for (JInternalFrame j : p.getAllFrames()) {
			if (!(j instanceof Mask)) {
				j.dispose();
			}
		}

		p.param.stockerPreference();
		phrase = n;
		/// d�sacive la taille et la police et le segment de d�part
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		/// d�sactive les boutons de contr�le pour �viter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);

		boolean doitJouer = !p.fenetre.isResizable();
		/// emp�che le redimensionnement de la fen�tre lors de la premi�re lecture ///
		p.fenetre.setResizable(false);

		// met a jour la barre de progression
		updateBar();

		controler.showPage(controler.getPageOfPhrase(n));

		if (doitJouer) {
			/// play du son correspondant au segment N ///
			controler.play(n);
			/// attente de la fin du temps de pause ///
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);
		}

	}

	private void updateBar() {
		p.progressBar.setValue(getCurrentPhraseIndex());
		p.progressBar.setString((getCurrentPhraseIndex() + 1) + "/" + (p.textHandler.getPhrasesCount() - 1));
	}

	/**
	 * Essaye de passer au segment suivant, passe � la page suivante si c'�tait le
	 * dernier segment de la page. Affiche le compte rendu si on �tait au dernier
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
		showAllHoleInPages();

		goTo(p.player.getCurrentPhraseIndex());

		//si c'est la premi�re fois qu'on appuie sur play
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
			//mise a jour du lecteur en fonction du segment de depart
			p.lecteur.segmentDeDepart = p.param.premierSegment-1;	
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

	public void showHole(int n) {

		Mask masque = null;

		// desactivation de la prochaine fenetre de masque
		if (!p.param.fixedField) {
			for (Mask m : p.fenetreMasque) {
				if(m.isVisible()) {
					m.setVisible(false);
					masque = m;
					break;
				}
			}
		}

		String bonMot = p.textHandler.mots.get(n);

		int start2 = p.editorPane.getText().indexOf(" " + p.param.mysterCarac) + 1;
		int end2 = -1;
		if (bonMot != null) {
			end2 = start2 + bonMot.length();
		}
		if (!p.param.fixedField) {
			try {
				start2 = masque.start;
				end2 = masque.end;
			} catch (Exception e) {
			}
		}

		if (start2 < end2) {
			// si la fenetre est fixe on indique quel mot on doit remplir
			if (p.param.fixedField) {
				for (JInternalFrame f : p.fenetreMasque) {
					if (f.isVisible()) {
						p.fenetreMasque.get(p.fenetreMasque.indexOf(f)).jtf.setBackground(Color.cyan);
						break;
					}
				}
			}
			try {
				p.afficherFrame(start2, end2, masque);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	public void nextHole() {
		showHole(p.numeroCourant);
	}

	// affiche les trous de la page courante
	public void showAllHoleInPages() {

		Map<Integer, List<String>> tempMotsParSegment = new HashMap<>();
		int indice = 0;
		for (List<String> s : p.textHandler.motsParSegment.values()) {
			List<String> ll = new ArrayList<>();
			for (String string : s) {
				ll.add(string);
			}
			tempMotsParSegment.put(indice, ll);
			indice++;
		}

		String text = p.editorPane.getText();
		int oldIndex = 0;
		// pour tous les mots � trouver
		for (int i = 0; i < p.textHandler.mots.size(); i++) {

			String bonMot = p.textHandler.mots.get(i);

			List<Integer> numerosSegments = p.segmentsEnFonctionDeLaPage.get(p.pageActuelle);
			// pour tous les segments de la page actuelle
			for (Integer integer : numerosSegments) {
				// si le segment contient des mots a trouver
				if (!tempMotsParSegment.get(integer).isEmpty()) {
					// pour chacun de ces mots
					for (int j = 0; j < tempMotsParSegment.get(integer).size(); j++) {
						// si ce mot est egale a un bon mot
						if (tempMotsParSegment.get(integer).get(j).equals(bonMot)) {
							tempMotsParSegment.get(integer).set(j, "$unmotquinexistepas$");
							int start2 = text.indexOf(" " + p.param.mysterCarac, oldIndex) + 1;
							int end2 = start2 + bonMot.length();
							oldIndex = end2;
							try {
								p.afficherFrameVide(start2, end2, p.pageActuelle, bonMot);
							} catch (BadLocationException e) {
								e.printStackTrace();
							}

						}
					}

				}
			}
			p.replaceAllMask();
		}
	}

}
