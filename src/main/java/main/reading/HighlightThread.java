package main.reading;

import main.Constants;
import main.controler.ControlerText;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class HighlightThread extends ReadThread {

	public HighlightThread(ControlerText controler, int N) {
		super(controler, N);
	}

	/**
	 * mémorisation des surlignages
	 */
	private static Map<Integer, Color> coloriage = new HashMap<>();

	/**
	 * dernière phrase traitée
	 */
	private static int lastN = 0;

	public void run() {
		while (N < controler.getPhrasesCount() - 1 && running) {
			/// affichage de la page correspondant au segment N ///
			controler.showPage(controler.getPageOfPhrase(N));
			// si on a avancé dans le texte
			if (lastN < N) {
				// on surligne depuis le départ jusqu'à l'arrivée
				for (int i = lastN; i < N; i++) {
					// si le segment n'a pas de couleur retenue
					if (!coloriage.containsKey(i)) {
						// on le colorie
						if (pageActuelleContient(i)) {
							controler.highlightPhrase(Constants.RIGHT_COLOR, i);
						}
						// et on stocke sa couleur
						coloriage.put(i, Constants.RIGHT_COLOR);
					} else {
						// on le colorie avc la couleur stockée
						if (pageActuelleContient(i)) {
							controler.highlightPhrase(coloriage.get(i), i);
						}
					}
				}
				// si on a reculé dans le texte
			} else if (lastN > N) {
				// on désurligne depuis le départ jusqu'à l'arrivée et on déstocke la couleur
				for (int i = lastN; i > N; i--) {
					controler.removeHighlightPhrase(i);
					coloriage.remove(i - 1);
				}
			}
			// si changement de page il y a récupération des anciens surlignages
			if (!pageActuelleContient(lastN) && lastN > N) {
				for (int i = 0; i < coloriage.keySet().size(); i++) {
					// sauf celui de a phrase actuelle
					if (pageActuelleContient(i) && i != N) {
						controler.highlightPhrase(coloriage.get(i), i);
					}
				}
			}
			// mise a jour du segment précédent
			lastN = N;
			/// play du son correspondant au segment N ///
			controler.play(N);
			/// attente de la fin du temps de pause ///
			controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_SPEAK);
			// restauration du nombre d'essais
			int nbTry = controler.p.param.mysterCarac;
			// tant que on a pas fait le bon clic
			boolean doOne = true;
			while (!controler.waitForClick(N)) {
				// décrémentation du nombre d'essais restants
				nbTry--;
				if (nbTry <= 0) {
					/// on arrête l'exécution si le thread est terminé ///
					if (!running) {
						return;
					}
					if (doOne) {
						// incrémentation des erreurs de segments
						controler.incrementerErreurSegment();
						doOne = false;
					}
					// surligner phrase avec correction
					controler.highlightPhrase(Constants.WRONG_PHRASE_COLOR, N);
					// stockage coloriage
					coloriage.put(N, Constants.WRONG_PHRASE_COLOR);
					// rejouer son
					controler.play(N);
				} else {
					if (controler.p.param.rejouerSon) {
						controler.play(N);
					}
				}
			}
			// si on a plus d'essais restants
			if (nbTry == controler.p.param.mysterCarac) {
				/// on arrête l'exécution si le thread est terminé ///
				if (!running) {
					return;
				}
				// surligange du segment
				controler.highlightPhrase(Constants.RIGHT_COLOR, N);
				// stockage du coloriage du segment
				coloriage.put(N, Constants.RIGHT_COLOR);
			}
			// enlever surlignage
			controler.removeWrongHighlights();
			/// on arrête l'exécution si le thread est terminé ///
			if (!running) {
				return;
			}
			N++;
			/// appel des écouteurs de fin de segment ///
			for (Runnable r : onPhraseEnd) {
				r.run();
			}
		}
	}

	private boolean pageActuelleContient(int segment) {
		return controler.p.segmentsEnFonctionDeLaPage.get(controler.p.pageActuelle).contains(segment);
	}

}
