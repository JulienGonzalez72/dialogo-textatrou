package main.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import main.*;
import main.model.Lecteur;

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
				gotoMagiqueReculer(pan, pan.pilot.getCurrentPhraseIndex() - 1);
			}
		});

		add(playButton);
		playButton.setIcon(new ImageIcon(playIcon));
		playButton.setEnabled(false);
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gotoMagiqueReculer(pan, pan.pilot.getCurrentPhraseIndex());
			}
		});

		add(nextButton);
		nextButton.setIcon(new ImageIcon(nextIcon));
		nextButton.setEnabled(false);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				gotoMagiqueAvancer(pan, pan.pilot.getCurrentPhraseIndex() + 1);
			}
		});

		add(repeatButton);
		repeatButton.setIcon(new ImageIcon(repeatIcon));
		repeatButton.setEnabled(false);
		repeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				gotoMagiqueReculer(pan, pan.pilot.getCurrentPhraseIndex());					
			}
		});

		JLabel goToLabel = new JLabel("Passer au segment :");
		goToLabel.setFont(goToLabel.getFont().deriveFont(Font.ITALIC));
		add(goToLabel);
		add(goToField);
		goToField.setPreferredSize(new Dimension(40, 20));
		goToField.setEnabled(false);
		goToField.addActionListener((ActionEvent e) -> {
			gotoMagiqueAvancer(pan, -1);
		});
	}

	// appeler avec -1 si on lance le goto avec le gotoField
	private void gotoMagiqueAvancer(Panneau pan, int numeroSegment) {

		int n;
		try {

			if (pan.fenetre.isResizable()) {
				pan.pilot.doPlay();
				return;
			}
			if (numeroSegment == -1) {
				n = Integer.parseInt(goToField.getText()) - 1;
			} else {
				n = numeroSegment;
			}			
			
			// on met a jour le numeroCourant et le numero de segment;
			String s = null;
			while (s == null) {
				try {
					s = pan.textHandler.motsParSegment.get(n).get(0);
				} catch (Exception e2) {
					n++;
				}
			}
			
			for (int i = 0; i < pan.textHandler.mots.size(); i++) {
				if (pan.textHandler.mots.get(i) == s) {
					pan.numeroCourant = i;
					break;
				}
			}
			
			int page = -1;
			for (int i = 1; i <= pan.segmentsEnFonctionDeLaPage.size(); i++) {
				if (pan.segmentsEnFonctionDeLaPage.get(i).contains(n)) {
					page = i;
					System.out.println("Nouvelle page : "+page+" /  Page actuelle : "+pan.pageActuelle);
					break;
				}
			}
			
			// phrase = le numero de segment
			pan.pilot.phrase = n;
			
			// on réécrit tous les mots dépassés
			// et on enleve les fenetres
			for (Mask m : pan.fenetreMasque) {
				if (pan.getNumero(m) < pan.numeroCourant && m.page == pan.pageActuelle) {
					System.out.println(" ( " + pan.fenetreMasque.indexOf(m)+" ) Masque numero " + pan.getNumero(m) + " est rendu visible. Valeur : " + m.motCouvert);
					String temp = pan.editorPane.getText();
					String r = "";
					char[] tab = temp.toCharArray();
					int j = 0;
					for (int i = 0; i < temp.length(); i++) {
						if (i >= m.start && i < m.end) {
							r += m.motCouvert.charAt(j);
							j++;
						} else {
							r += tab[i];
						}
						pan.editorPane.setText(r);
					}
					m.setVisible(false);
				}
			}

			// ON CHANGE DE page SI BESOIN
			if (page != pan.pageActuelle) {
				System.out.println("Nouvelle page = " + page);
				pan.pilot.controler.showPage(pan.pilot.controler.getPageOfPhrase(n));
				// on réécrit tous les mots dépassés
				// et on enleve les fenetres
				for (Mask m : pan.fenetreMasque) {
					if (pan.getNumero(m) < pan.numeroCourant && m.page == page) {
						System.out.println(" ( "+pan.fenetreMasque.indexOf(m)+" ) Masque numero " + pan.getNumero(m) + " est rendu visible. Valeur : " + m.motCouvert);
						String temp = pan.editorPane.getText();
						String r = "";
						char[] tab = temp.toCharArray();
						int j = 0;
						for (int i = 0; i < temp.length(); i++) {
							if (i >= m.start && i < m.end) {
								r += m.motCouvert.charAt(j);
								j++;
							} else {
								r += tab[i];
							}
							pan.editorPane.setText(r);
						}
						m.setVisible(false);
					}
				}
			}

			

			// on replace les masques visibles
			pan.replaceAllMask();

			// on interrompt la lecture et on la relance en partant du nouveau segment
			pan.lecteur.needToDead = true;

			// on reprend le lecteur
			synchronized (pan.lecteur.lock) {
				pan.lecteur.lock.notify();
				pan.lecteur.notified = true;
			}

			pan.lecteur = new Lecteur(pan, n);
			pan.lecteur.start();

		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(null, "Numéro de segment incorrect : " + goToField.getText());
		}
		updateButtons();
	}

	private void gotoMagiqueReculer(Panneau pan, int numeroSegment) {

		int n;
		try {

			if (pan.fenetre.isResizable()) {
				pan.pilot.doPlay();
				return;
			}
			if (numeroSegment == -1) {
				n = Integer.parseInt(goToField.getText()) - 1;
			} else {
				n = numeroSegment;
			}
			
			int oldN = n;

			// on met a jour le numeroCourant et le numero de segment;
			String s = null;
			while (s == null) {
				if(n < 0) {
					n = oldN;
					break;
				}
				try {
					s = pan.textHandler.motsParSegment.get(n).get(0);
				} catch (Exception e2) {
					n--;
				}
			}

			for (int i = 0; i < pan.textHandler.mots.size(); i++) {
				if (pan.textHandler.mots.get(i) == s) {
					pan.numeroCourant = i;
					break;
				}
			}
	
			int page = -1;
			for (int i = 1; i <= pan.segmentsEnFonctionDeLaPage.size(); i++) {
				if (pan.segmentsEnFonctionDeLaPage.get(i).contains(n)) {
					page = i;
					System.out.println("Nouvelle page : "+page+" /  Page actuelle : "+pan.pageActuelle);
					break;
				}
			}
			
			// phrase = le numero de segment
			pan.pilot.phrase = n;
	
			for (Mask m : pan.fenetreMasque) {
				if (m.jtf.getBackground().equals(Color.CYAN)) {
					m.jtf.setBackground(Color.white);
				}
				if (pan.getNumero(m) >= pan.numeroCourant && m.page == pan.pageActuelle) {
					System.out.println(" ( "+pan.fenetreMasque.indexOf(m)+" ) Masque numero " + pan.getNumero(m) + " est rendu visible. Valeur : " + m.motCouvert);
					m.setVisible(true);
				}
			}

			// ON CHANGE DE page SI BESOIN
			if (page != pan.pageActuelle) {
				System.out.println("Nouvelle page = " + page);
				pan.pilot.controler.showPage(pan.pilot.controler.getPageOfPhrase(n));
				for (Mask m : pan.fenetreMasque) {
					if (pan.getNumero(m) >= pan.numeroCourant && m.page == page) {
						System.out.println(" ( "+pan.fenetreMasque.indexOf(m)+" ) Masque numero " + pan.getNumero(m) + " est rendu visible. Valeur : " + m.motCouvert);
						m.setVisible(true);
					} else if (m.page == page) {
						System.out.println(" ( "+pan.fenetreMasque.indexOf(m)+" ) Masque numero " + pan.getNumero(m) + " est rendu visible. Valeur : " + m.motCouvert);
						String temp = pan.editorPane.getText();
						String r = "";
						char[] tab = temp.toCharArray();
						int j = 0;
						for (int i = 0; i < temp.length(); i++) {
							if (i >= m.start && i < m.end) {
								r += m.motCouvert.charAt(j);
								j++;
							} else {
								r += tab[i];
							}
							pan.editorPane.setText(r);
						}
					}
				}
			}



			// on replace les masques visibles
			pan.replaceAllMask();

			// on interrompt la lecture et on la relance en partant du nouveau segment
			pan.lecteur.needToDead = true;

			// on reprend le lecteur
			synchronized (pan.lecteur.lock) {
				pan.lecteur.lock.notify();
				pan.lecteur.notified = true;
			}

			pan.lecteur = new Lecteur(pan, n);
			pan.lecteur.start();

		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(null, "Numéro de segment incorrect : " + goToField.getText());
		}
		updateButtons();
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
