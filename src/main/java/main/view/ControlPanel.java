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
	 * Panneau pour les contr�les de segment � segment
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
				} else {
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

		JLabel goToLabel = new JLabel("Passer au segment :");
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
	 * M�thode qui s'ex�cute lorsque les contr�les sont pr�ts � �tre effectifs.
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
	 * Actualise l'�tat de tous les composants de la fen�tre de contr�le.
	 */
	public void updateButtons() {
		if (usable) {
			previousButton.setEnabled(pan.pilot.hasPreviousPhrase());
			playButton.setEnabled(true);
			playButton.setIcon(new ImageIcon(pan.player.isPlaying() ? pauseIcon : playIcon));
			nextButton.setEnabled(pan.pilot.hasNextPhrase());
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
	 * D�sactive tous les boutons de la fen�tre de contr�le puis les r�-active apr�s
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
