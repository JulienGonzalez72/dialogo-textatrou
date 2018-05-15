package main.controler;

import java.awt.Color;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
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
	 * Se place sur le segment de numero n et démarre le lecteur.
	 */
	public void goTo(int n) throws IllegalArgumentException {
		if (n < p.param.premierSegment - 1 || n >= p.textHandler.getPhrasesCount() - 1) {
			throw new IllegalArgumentException("Numéro de segment invalide : " + n);
		}

		p.param.stockerPreference();
		phrase = n;
		/// désacive la taille et la police et le segment de départ
		p.fenetreParam.pan.fontFamilyComboBox.setEnabled(false);
		p.fenetreParam.pan.fontSizeComboBox.setEnabled(false);
		p.fenetreParam.pan.segmentDeDepart.setEnabled(false);
		p.fenetreParam.pan.champMysterCarac.setEditable(false);
		/// désactive les boutons de contrôle pour éviter le spam ///
		p.controlPanel.disableAll(Constants.DISABLE_TIME);

		/// empêche le redimensionnement de la fenêtre lors de la première lecture ///
		p.fenetre.setResizable(false);

		// met a jour la barre de progression
		updateBar();

		controler.showPage(controler.getPageOfPhrase(n));

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

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
		});

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
		showAllHoleInPages(p.pageActuelle);
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

	public void showHole(int n) {

		JInternalFrame masque = null;

		// desactivation de la prochaine fenetre de masque
		if (!p.param.fixedField) {
			for (JInternalFrame f : p.fenetreMasque) {
				if (f.isVisible()) {
					f.setVisible(false);
					masque = f;
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

		if (start2 < end2) {
			//si la fenetre est fixe on indique quel mot on doit remplir
			if ( p.param.fixedField) {
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
		} else {
			doNext();
		}
	}

	public void nextHole() {
		showHole(p.numeroCourant);
	}

	public void showAllHoleInPages(int pageActuelle) {
		String text = p.editorPane.getText();
		int oldIndex = 0;
		// pour tous les mots à trouver
		for (int i = 0; i < p.textHandler.mots.size(); i++) {

			String bonMot = p.textHandler.mots.get(i);
			List<Integer> numerosSegments = p.segmentsEnFonctionDeLaPage.get(p.pageActuelle);
			// pour tous les segments de la page actuelle
			for (Integer integer : numerosSegments) {
				// si le segment contient des mots a trouver
				if (p.textHandler.motsParSegment.get(integer) != null) {
					// pour chacun de ces mots
					for (String s : p.textHandler.motsParSegment.get(integer)) {
						// si ce mot est egale a un bon mot
						if (s.equals(bonMot)) {
							int start2 = text.indexOf(" " + p.param.mysterCarac, oldIndex) + 1;
							int end2 = start2 + bonMot.length();
							oldIndex = end2;
							try {
								p.afficherFrameVide(start2, end2);
							} catch (BadLocationException e) {
								e.printStackTrace();
							}

						}
					}
				}
			}

		}
	}

}
