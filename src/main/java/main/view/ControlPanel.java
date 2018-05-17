package main.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import com.alee.extended.dock.DockingPaneLayout;

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

				/*for (Mask m : pan.fenetreMasque) {
					if (m.jtf.getBackground().equals(Color.cyan)) {
						m.jtf.setBackground(Color.white);
					}
				}

				if (param.fixedField) {
					fenetreFixeFlechePrecedente(pan, param);
				} else {
					fenetreNonFixeFlechePrecedente(pan, param);
				}

				for (Mask m : pan.fenetreMasque) {
					if (m.isVisible()) {
						try {
							pan.replacerMasque(m);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
				}*/

				pan.pilot.doPrevious();
				updateButtons();
			}

			/*private void fenetreNonFixeFlechePrecedente(Panneau pan, Parametres param) {
				for (JInternalFrame f : pan.getAllFrames()) {
					f.dispose();
				}
				
				int indexMinimum = pan.fenetreMasque.indexOf(pan.fenetreMasque.get(pan.numeroCourant
						- pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex() - 1).size()));
				int indexMaximum = indexMinimum
						+ pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex() - 1).size();

				int max = pan.fenetreMasque.size();
				for (int k = 0; k < max; k++) {
					if (pan.fenetreMasque.indexOf(pan.fenetreMasque.get(k)) >= indexMinimum
							&& pan.fenetreMasque.indexOf(pan.fenetreMasque.get(k)) < indexMaximum) {

						String temp = "";
						for (int i = 0; i < pan.editorPane.getText().length(); i++) {
							if (i >= pan.fenetreMasque.get(k).start && i < pan.fenetreMasque.get(k).end) {
								temp += param.mysterCarac;
							} else {
								temp += pan.editorPane.getText().charAt(i);
							}
						}
						pan.editorPane.setText(temp);
					}

					try {
						pan.afficherFrameVide(pan.fenetreMasque.get(k).start, pan.fenetreMasque.get(k).end,
								pan.fenetreMasque.get(k).page, pan.fenetreMasque.get(k).motCouvert);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}

				}

				int reallyOldNumero = pan.numeroCourant;

				// on decremente le numeroCourant du nombre de mot a decouvrir
				pan.numeroCourant -= pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex() - 1)
						.size();

				pan.numeroCourant += pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex())
						.indexOf(pan.textHandler.mots.get(reallyOldNumero));
			}

			private void fenetreFixeFlechePrecedente(Panneau pan, Parametres param) {
				int reallyOldNumero = pan.numeroCourant;

				// on decremente le numeroCourant du nombre de mot a decouvrir
				String bonMot = pan.textHandler.mots.get(pan.numeroCourant -= pan.textHandler.motsParSegment
						.get(pan.pilot.getCurrentPhraseIndex() - 1).size());

				int oldNumero = pan.numeroCourant;

				for (Mask m : pan.fenetreMasque) {

					if (pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex() - 1).contains(bonMot)
							&& pan.fenetreMasque.indexOf(m) >= oldNumero) {
						m.setVisible(true);

						try {
							pan.afficherFrame(m.start, m.end, m);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}

						String temp = "";
						for (int i = 0; i < pan.editorPane.getText().length(); i++) {
							if (i >= m.start && i < m.end) {
								temp += param.mysterCarac;
							} else {
								temp += pan.editorPane.getText().charAt(i);
							}
						}
						pan.editorPane.setText(temp);

						pan.numeroCourant++;
						bonMot = pan.textHandler.mots.get(pan.numeroCourant);
					}

				}
				pan.numeroCourant = oldNumero - pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex())
						.indexOf(pan.textHandler.mots.get(reallyOldNumero));
			}*/
		});

		add(playButton);
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

		add(nextButton);
		nextButton.setIcon(new ImageIcon(nextIcon));
		nextButton.setEnabled(false);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/*if (pan.fenetre.isResizable()) {
					pan.pilot.doPlay();
					return;
				}

				if (param.fixedField) {
					String bonMot = pan.textHandler.mots.get(pan.numeroCourant);
					// pour tous les masques
					for (Mask m : pan.fenetreMasque) {
						// si le masque est visible
						if (m.isVisible()) {
							// si le segment actuel contient le mot actuel
							if (pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex())
									.contains(bonMot)) {
								// faire le traitement comme si on avait rentré le bon mot
								pan.saisieCorrecte(m, m.start, m.end, bonMot);
								// passer au bonMot suivant
								bonMot = pan.textHandler.mots.get(pan.numeroCourant);
							}
						}
					}
				} else {
					String bonMot = pan.textHandler.mots.get(pan.numeroCourant);
					int oldNumero = pan.numeroCourant;
					// pour tous les masques
					for (Mask m : pan.fenetreMasque) {
						// si le segment actuel contient le mot actuel
						if (pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex()).contains(bonMot)
								&& pan.fenetreMasque.indexOf(m) >= oldNumero) {
							// faire le traitement comme si on avait rentré le bon mot
							m.setVisible(false);
							pan.saisieCorrecte(m, m.start, m.end, bonMot);
							// passer au bonMot suivant
							bonMot = pan.textHandler.mots.get(pan.numeroCourant++);
						}

					}
					pan.numeroCourant = oldNumero
							+ pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex()).size()
							- pan.textHandler.motsParSegment.get(pan.pilot.getCurrentPhraseIndex())
									.indexOf(pan.textHandler.mots.get(oldNumero));
				}*/

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
				/*if ( n == 0) {
					pan.pilot.doPlay();
				}*/
				
				pan.pilot.goTo(n);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(null, "Numéro de segment incorrect : " + goToField.getText());
			}
			updateButtons();
		});

		// addMenu();
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
