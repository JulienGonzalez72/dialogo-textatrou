package main.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import main.*;

public class ControlPanel extends JPanel {

	private static int imageSize = Constants.SIZE_IMAGE_FRAME;
	private static Image previousIcon, playIcon, pauseIcon, nextIcon, repeatIcon;

	/**
	 * Panneau pour les contrôles de trou à trou
	 */
	private JPanel holePanel = new JPanel();
	private JButton hpreviousButton = new JButton();
	private JButton hplayButton = new JButton();
	private JButton hnextButton = new JButton();
	private JButton hrepeatButton = new JButton();
	private JTextField hgoToField = new JTextField();
	
	/**
	 * Panneau pour les contrôles de segment à segment
	 */
	private JPanel phrasePanel = new JPanel(); 
	private JButton previousButton = new JButton();
	private JButton playButton = new JButton();
	private JButton nextButton = new JButton();
	private JButton repeatButton = new JButton();
	private JTextField goToField = new JTextField();
	
	private Panneau pan;

	private boolean usable = true;

	static {
		loadImages();
	}

	public ControlPanel(Panneau pan, FenetreParametre fen, Parametres param) {
		this.pan = pan;

		holePanel.add(hpreviousButton);
		hpreviousButton.setIcon(new ImageIcon(previousIcon));
		hpreviousButton.setEnabled(false);
		hpreviousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doPrevious();
				updateButtons();
			}
		});

		holePanel.add(hplayButton);
		hplayButton.setIcon(new ImageIcon(playIcon));
		hplayButton.setEnabled(false);
		hplayButton.addActionListener(new ActionListener() {
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

		holePanel.add(hnextButton);
		hnextButton.setIcon(new ImageIcon(nextIcon));
		hnextButton.setEnabled(false);
		hnextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doNext();
				updateButtons();
			}
		});

		holePanel.add(hrepeatButton);
		hrepeatButton.setIcon(new ImageIcon(repeatIcon));
		hrepeatButton.setEnabled(false);
		hrepeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doPlay();
				updateButtons();
			}
		});

		JLabel goToLabel = new JLabel("Passer au trou :");
		goToLabel.setFont(goToLabel.getFont().deriveFont(Font.ITALIC));
		holePanel.add(goToLabel);
		holePanel.add(hgoToField);
		hgoToField.setPreferredSize(new Dimension(40, 20));
		hgoToField.setEnabled(false);
		hgoToField.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				int h;
				try {
					h = Integer.parseInt(hgoToField.getText()) - 1;
					pan.pilot.goTo(h);
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(null, "Numéro de trou incorrect : " + hgoToField.getText());
				}
				updateButtons();
			}
		});
		
		holePanel.setBorder(BorderFactory.createTitledBorder("Contrôle par trou"));
		//add(holePanel);
		
		phrasePanel.add(previousButton);
		previousButton.setIcon(new ImageIcon(previousIcon));
		previousButton.setEnabled(false);
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.previousPhrase();
				updateButtons();
			}
		});

		phrasePanel.add(playButton);
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

		phrasePanel.add(nextButton);
		nextButton.setIcon(new ImageIcon(nextIcon));
		nextButton.setEnabled(false);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.nextPhrase();
				updateButtons();
			}
		});

		phrasePanel.add(repeatButton);
		repeatButton.setIcon(new ImageIcon(repeatIcon));
		repeatButton.setEnabled(false);
		repeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pan.pilot.doPlay();
				updateButtons();
			}
		});

		goToLabel = new JLabel("Passer au segment :");
		goToLabel.setFont(goToLabel.getFont().deriveFont(Font.ITALIC));
		phrasePanel.add(goToLabel);
		phrasePanel.add(goToField);
		goToField.setPreferredSize(new Dimension(40, 20));
		goToField.setEnabled(false);
		goToField.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				int n;
				try {
					n = Integer.parseInt(goToField.getText()) - 1;
					pan.pilot.goToPhrase(n);
				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(null, "Numéro de segment incorrect : " + goToField.getText());
				}
				updateButtons();
			}
		});
		
		phrasePanel.setBorder(BorderFactory.createTitledBorder("Contrôle par segment"));
		add(phrasePanel);
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
			hpreviousButton.setEnabled(pan.pilot.hasPreviousHole());
			hplayButton.setEnabled(true);
			hplayButton.setIcon(new ImageIcon(pan.player.isPlaying() ? pauseIcon : playIcon));
			hnextButton.setEnabled(pan.pilot.hasNextHole());
			hrepeatButton.setEnabled(pan.player.isPlaying());
			hgoToField.setEnabled(true);
			
			previousButton.setEnabled(pan.pilot.hasPreviousHole());
			playButton.setEnabled(true);
			playButton.setIcon(new ImageIcon(pan.player.isPlaying() ? pauseIcon : playIcon));
			nextButton.setEnabled(pan.pilot.hasNextHole());
			repeatButton.setEnabled(pan.player.isPlaying());
			goToField.setEnabled(true);
		} else {
			hpreviousButton.setEnabled(false);
			hplayButton.setEnabled(false);
			hplayButton.setIcon(new ImageIcon(playIcon));
			hnextButton.setEnabled(false);
			hrepeatButton.setEnabled(false);
			hgoToField.setEnabled(false);

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
	 * Désactive tous les boutons de la fenêtre de contrôle puis les ré-active après
	 * le temps duration.
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
