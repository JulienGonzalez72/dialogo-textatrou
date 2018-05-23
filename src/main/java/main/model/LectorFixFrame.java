package main.model;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import main.Constants;
import main.controler.ControlerText;
import main.view.Mask;

public class LectorFixFrame extends ReaderThread {

	public Object lock = new Object();
	public boolean notified;

	public LectorFixFrame(ControlerText controler, int h) {
		super(controler, h);
	}

	public void run() {

		// pour chaque trou
		for (int h = this.h; h < controler.getHolesCount(); h++) {
			//on montre la page du trou
			controler.showPage(controler.getPageOf(h));
			// on montre uniquement les trous � partir du trou actuel et de cette page
			controler.showHolesInPage(h);
			// si le trou est le premier de son segment
			if (controler.isFirstInPhrase(h)) {
				//Pour tous les segments apr�s le pr�c�dent trou, jusqu'au segment de ce trou compris
				for (int i = h > 0 ? controler.getPhraseOf(h-1)+1 : controler.getPhraseOf(h); i <= controler.getPhraseOf(h); i++) {
					// lire le fichier audio correspondant � ce segment
					controler.play(i);
					// attendre le temps de pause n�cessaire
					controler.doWait(controler.getCurrentWaitTime(), Constants.CURSOR_LISTEN);
				}
				
			}
			// colorier le trou actuel en bleu
			controler.color(h, Color.cyan);
			// active la fen�tre de saisie avec le trou actuel
			controler.activateInputFenetreFixe(h);
			// tant que la saisie n'est pas juste
			while (!controler.waitForFillFenetreFixe(h)) {
				System.out.println("FAUX");
				//clignotement et compter une erreur
				controler.doError();
			
			}
			//d�sactiver la fen�tre de saisie
			controler.desactiverFenetreFixe();
			//remplacer le trou par le mot correspondant et replacer tous les masques
			controler.replaceMaskByWord(h);
		}

		controler.showReport();

	}

	public void doStop() {
		needToDead = true;
		interrupt();
	}

}
