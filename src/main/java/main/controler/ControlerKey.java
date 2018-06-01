package main.controler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
				pilot.doPlay();
			}
			/// retourne au segment pr�c�dent ///
			else if (pilot.hasPreviousPhrase()) {
				pilot.previousPhrase();
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
