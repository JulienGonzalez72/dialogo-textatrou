package main.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import main.Constants;
import main.Parametres;


public class Player {

	private TextHandler text;
	private int currentPhrase;
	private boolean playing, blocked;

	private Timer timer;
	private PlayTask playTask;
	private WaitTask waitTask;

	private Clip clip;
	private long lastPosition;

	/**
	 * Si un temps de pause s'effectue après l'enregistrement (valeur par défaut =
	 * <code>false</code>).
	 */
	public boolean waitAfter = false;

	/**
	 * Ecouteurs qui s'enclenchent lorsque un segment a finis d'être prononcé.
	 */
	public List<Runnable> onPhraseEnd = new ArrayList<>();
	public List<Runnable> onNextPhrase = new ArrayList<>();
	public List<Runnable> onPreviousPhrase = new ArrayList<>();
	/**
	 * Ecouteurs qui s'enclenchent lorsque l'enregistrement est lancé.
	 */
	public List<Runnable> onPlay = new ArrayList<>();
	/**
	 * Ecouteurs qui s'enclenchent lorsque le temps de pause de
	 * l'enregistrement se termine.
	 */
	public List<Runnable> onBlockEnd = new ArrayList<>();
	/**
	 * Ecouteurs qui se déclenchent lorsque l'utilisateur est mis en attente pour répéter.
	 */
	public List<Runnable> onWait = new ArrayList<>();
	
	private Parametres param;

	public Player(TextHandler textHandler, Parametres param) {
		text = textHandler;
		this.param = param;
	}

	/**
	 * Charge l'enregistrement correspondant à un segment précis.
	 */
	public void load(int phrase) {
		currentPhrase = phrase;
		try {
			clip = AudioSystem.getClip();
			clip.open(getAudioStream(Constants.AUDIO_FILE_NAME, phrase));
			clip.setMicrosecondPosition(lastPosition);
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Joue un segment de phrase.
	 */
	public void play(int phrase) {
		stop();
		lastPosition = 0;
		currentPhrase = phrase;
		play();
	}

	/**
	 * Démarre la lecture (n'a aucun effet si la lecture est déjà démarrée).
	 */
	public void play() {
		if (playing) {
			return;
		}
		load(currentPhrase);
		clip.start();
		timer = new Timer();
		playTask = new PlayTask();
		timer.scheduleAtFixedRate(playTask, 0, 20);
		playing = true;
		for (Runnable r : onPlay) {
			r.run();
		}
	}

	private class PlayTask extends TimerTask {
		public void run() {
			/// fin de la phrase ///
			if (isPhraseFinished()) {
				stop();
				lastPosition = 0;
				for (Runnable r : onPhraseEnd) {
					r.run();
				}
				if (waitAfter) {
					doWait();
				}
			}
		}
	}

	/**
	 * Marque un temps de pause. Ne fait rien si la pause est en cours.
	 */
	public void doWait() {
		if (blocked)
			return;
		if (clip == null) {
			load(currentPhrase);
		}
		blocked = true;
		waitTask = new WaitTask();
		if (playing) {
			timer.cancel();
		}
		timer = new Timer();
		timer.scheduleAtFixedRate(waitTask, 0, 20);
		for (Runnable r : onWait) {
			r.run();
		}
	}

	private class WaitTask extends TimerTask {
		private long time;

		public void run() {
			time += 20;

			/// fin du blocage ///
			if (blocked && time > clip.getMicrosecondLength() / 1000
					* param.tempsPauseEnPourcentageDuTempsDeLecture / 100.) {
				blocked = false;
				cancel();
				for (Runnable r : onBlockEnd) {
					r.run();
				}
			}
		}
	}

	/**
	 * Arrête la lecture (n'a aucun effet si elle n'est pas en cours).
	 */
	public void stop() {
		if (clip != null) {
			clip.stop();
		}
		if (playTask != null) {
			playTask.cancel();
			playTask = null;
		}
		playing = false;
	}

	/**
	 * Mets en pause l'enregistrement.
	 */
	public void pause() {
		stop();
		lastPosition = clip.getMicrosecondPosition();
	}

	/**
	 * Indique si le segment a finis d'être prononcé.
	 */
	public boolean isPhraseFinished() {
		return clip != null ? clip.getFramePosition() == clip.getFrameLength() : false;
	}

	/**
	 * Indique si le lecteur est en train de prononcer le segment.
	 */
	public boolean isPlaying() {
		return playing;
	}

	/**
	 * Indique
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * Retourne la phrase courante du lecteur, qu'elle soit finie d'être prononcée
	 * ou non.
	 */
	public String getCurrentPhrase() {
		return text.getPhrase(currentPhrase);
	}

	/**
	 * Retourne l'indice du segment actuel.
	 */
	public int getCurrentPhraseIndex() {
		return currentPhrase;
	}

	/**
	 * Passe au segment suivant et démarre le lecteur.
	 */
	public void nextPhrase() {
		for (Runnable r : onNextPhrase) {
			r.run();
		}
		currentPhrase++;
		repeat();
	}

	/**
	 * Retourne true si il reste au moins un segment à lire.
	 */
	public boolean hasNextPhrase() {
		return currentPhrase < text.getPhrasesCount() - 2;
	}

	/**
	 * Retourne au segment prédédent et démarre le lecteur.
	 */
	public void previousPhrase() {
		for (Runnable r : onPreviousPhrase) {
			r.run();
		}
		currentPhrase--;
		repeat();
	}

	/**
	 * Retourne true si il y a au moins un segment avant le segment actuel.
	 */
	public boolean hasPreviousPhrase() {
		return currentPhrase > param.premierSegment - 1;
	}

	/**
	 * Recommence la phrase.
	 */
	public void repeat() {
		lastPosition = 0;
		stop();
		play();
	}

	/**
	 * Se place directement à un segment donné sans démarrer le lecteur.
	 */
	public void goTo(int index) {
		lastPosition = 0;
		stop();
		currentPhrase = index;
	}
	
	/**
	 * Retourne la durée en millisecondes de l'enregistrement courant.
	 */
	public long getDuration() {
		return clip != null ? clip.getMicrosecondLength() / 1000 : 0;
	}

	private static AudioInputStream getAudioStream(String fileName, int n) {
		try {
			String num = format(Constants.HAS_INSTRUCTIONS ? n + 2 : n + 1);
			return AudioSystem.getAudioInputStream(
					new File("ressources/sounds/" + fileName + "/" + fileName + "(" + num + ").wav"));
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String format(int n) {
		String str = String.valueOf(n);
		for (int i = str.length(); i < 3; i++) {
			str = "0" + str;
		}
		return str;
	}

	public void setCurrentPhrase(int currentPhrase) {
		this.currentPhrase = currentPhrase;
	}

}
