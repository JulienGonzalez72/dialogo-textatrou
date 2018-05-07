package main.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import main.Constants;
import main.Parametres;

public class ControlPanel extends JPanel {

	private static int imageSize = Constants.tailleImageFrame;
	private static Image previousIcon, playIcon, pauseIcon, nextIcon, repeatIcon;

	private JButton previousButton = new JButton();
	private JButton playButton = new JButton();
	private JButton nextButton = new JButton();
	private JButton repeatButton = new JButton();
	public JTextField goToField = new JTextField();
	private Panneau pan;

	private boolean usable = true;

	static {
		loadImages();
	}

	public ControlPanel(Panneau pan, FenetreParametre fen, Parametres param) {

		this.pan = pan;
		
		add(previousButton);
		previousButton.setIcon(new ImageIcon(previousIcon));
		previousButton.setEnabled(false);
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doPrevious();
				updateButtons();
			}
		});
		
		add(playButton);
		playButton.setIcon(new ImageIcon(playIcon));
		playButton.setEnabled(false);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pan.player.isPlaying()) {
					pan.pilot.doStop();
				}
				else {
					pan.pilot.doPlay();
				}
				updateButtons();
			}
		});
		
		add(nextButton);
		nextButton.setIcon(new ImageIcon(nextIcon));
		nextButton.setEnabled(false);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doNext();
				updateButtons();
			}
		});
		
		add(repeatButton);
		repeatButton.setIcon(new ImageIcon(repeatIcon));
		repeatButton.setEnabled(false);
		repeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doPlay();
				updateButtons();
			}
		});
		
		JLabel goToLabel = new JLabel("Passer au segment :");
		goToLabel.setFont(goToLabel.getFont().deriveFont(Font.ITALIC));
		add(goToLabel);
		add(goToField);
		goToField.setPreferredSize(new Dimension(40, 20));
		goToField.setEnabled(false);
		goToField.addActionListener((ActionEvent e) -> {
			int n;
			try {
				n = Integer.parseInt(goToField.getText()) - 1;
				pan.pilot.goTo(n);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Numéro de segment incorrect : " + goToField.getText());
			}
			updateButtons();
		});

		//addMenu();
	}
	
	/**
	 * Méthode qui s'exécute lorsque les contrôles sont prêts à être effectifs.
	 */
	public void init() {
		Runnable update = () -> {
			updateButtons();
		};
		pan.player.onPhraseEnd.add(update);
		pan.player.onBlockEnd.add(update);
		pan.player.onPlay.add(update);
		enableAll();
	}

	/**
	 * Actualise l'état de tous les composants de la fenêtre de contrôle.
	 */
	public void updateButtons() {
		if (usable) {
			previousButton.setEnabled(pan.player.hasPreviousPhrase());
			playButton.setEnabled(true);
			playButton.setIcon(new ImageIcon(pan.player.isPlaying() ? pauseIcon : playIcon));
			nextButton.setEnabled(pan.player.hasNextPhrase());
			repeatButton.setEnabled(pan.player.isPlaying());
			goToField.setEnabled(true);
		} else {
			previousButton.setEnabled(false);
			playButton.setEnabled(false);
			playButton.setIcon(new ImageIcon(playIcon));
			nextButton.setEnabled(false);
			repeatButton.setEnabled(false);
			goToField.setEnabled(false);
		}
	}

	public void disableAll() {
		usable = false;
		updateButtons();
	}
	
	/**
	 * Désactive tous les boutons de la fenêtre de contrôle puis les ré-active après le temps duration.
	 */
	public void disableAll(long duration) {
		disableAll();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				enableAll();
			}
		});
	}

	public void enableAll() {
		usable = true;
		updateButtons();
	}

	private static void loadImages() {
		previousIcon = getIcon("previous_icon.png");
		playIcon = getIcon("play_icon.png");
		pauseIcon = getIcon("pause_icon.png");
		nextIcon = getIcon("next_icon.png");
		repeatIcon = getIcon("repeat_icon.png");
	}

	private static Image getIcon(String imageName) {
		try {
			return ImageIO.read(new File("ressources/images/" + imageName)).getScaledInstance(imageSize, imageSize,
					Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
