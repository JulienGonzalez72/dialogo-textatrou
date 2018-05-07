package main.controler;

import java.awt.event.*;
import main.Constants;

public class ControlerKey implements KeyListener {

	private Pilot pilot;


	/**
	 * Moment du dernier clic
	 */
	private long lastClick;

	public ControlerKey(Pilot pilot) {
		this.pilot = pilot;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			/// recommence le segment ///
			if (e.getWhen() - lastClick > Constants.LEFT_DELAY) {
				pilot.goTo(pilot.getCurrentPhraseIndex());
			}
			/// retourne au segment pr�c�dent ///
			else if (pilot.hasPreviousPhrase()) {
				pilot.goTo(pilot.getCurrentPhraseIndex() - 1);
			}
			lastClick = e.getWhen();
		}

		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			/// pause ///
			if (pilot.isPlaying()) {
				pilot.doStop();
			}
			/// reprend le segment ///
			else {
				pilot.doPlay();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
