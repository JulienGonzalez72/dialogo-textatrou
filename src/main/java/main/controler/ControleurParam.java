package main.controler;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.Constants;
import main.Parametres;
import main.reading.ReadMode;
import main.view.FenetreParametre;
import main.view.Panneau;

public class ControleurParam implements ActionListener, ChangeListener {

	FenetreParametre.PanneauParam panneau;
	FenetreParametre fen;
	Parametres param;

	public ControleurParam(FenetreParametre fen, FenetreParametre.PanneauParam p) {
		this.panneau = p;
		this.fen = fen;
		param = fen.param;
	}

	public void actionPerformed(ActionEvent arg0) {
		JComboBox<?> jcb = null;
		if (arg0.getSource() instanceof JComboBox) {
			jcb = (JComboBox<?>) arg0.getSource();
		}
		if (jcb == panneau.colorComboBox || jcb == panneau.wrongColorComboBox || jcb == panneau.rightColorComboBox
				|| panneau.correctionColorComboBox == jcb) {
			String s = (String) jcb.getSelectedItem();
			Color color = FenetreParametre.stringToColor(s);
			((JComboBox<?>) jcb).setBackground(color);
			if (jcb == panneau.wrongColorComboBox) {
				Constants.WRONG_COLOR = color;
			}
			if (jcb == panneau.correctionColorComboBox) {
				Constants.WRONG_PHRASE_COLOR = color;
			}
			if (jcb == panneau.rightColorComboBox) {
				Constants.RIGHT_COLOR = color;
			}
			if (jcb == panneau.colorComboBox) {
				if (fen.editorPane != null) {
					fen.editorPane.setBackground(color);
				}
				param.couleurFond = color;
			}
			panneau.grabFocus();
		}
		if (jcb == panneau.fontSizeComboBox) {
			int taille = Integer.valueOf((String) jcb.getSelectedItem());
			param.taillePolice = taille;
			param.police = param.police.deriveFont((float) taille);
			panneau.fontSizeComboBox.setFont(new Font(param.police.getFontName(), param.police.getStyle(),
					Math.min(20, param.police.getSize())));
			if (fen.editorPane != null) {
				fen.editorPane.setFont(param.police);
				fen.fenetre.pan.rebuildPages();
			}
		}
		if (jcb == panneau.fontFamilyComboBox) {
			String police = (String) jcb.getSelectedItem();
			param.police = getFont(police, jcb.getSelectedIndex(), Font.BOLD, param.taillePolice);
			panneau.fontFamilyComboBox.setFont(new Font(param.police.getFontName(), param.police.getStyle(),
					Math.min(20, param.police.getSize())));
			if (fen.editorPane != null) {
				fen.editorPane.setFont(param.police);
			}
		}
		if (arg0.getSource() == panneau.highlightMode) {
			if (((JRadioButton) arg0.getSource()).isSelected()) {
				param.readMode = ReadMode.SUIVI;
				try {
					fen.pan.chargerPreferences();
				} catch (NumberFormatException | IOException e) {}
			}
		}
		if (arg0.getSource() == panneau.guidedMode) {
			if (panneau.guidedMode.isSelected()) {
				param.readMode = ReadMode.GUIDEE;
				try {
					fen.pan.chargerPreferences();
				} catch (NumberFormatException | IOException e) {}
			}
		}
		if (arg0.getSource() == panneau.segmentedMode) {
			if (panneau.segmentedMode.isSelected()) {
				param.readMode = ReadMode.SEGMENTE;
				try {
					fen.pan.chargerPreferences();
				} catch (NumberFormatException | IOException e) {}
			}
		}
		if (arg0.getSource() == panneau.anticipatedMode) {
			if (panneau.anticipatedMode.isSelected()) {
				param.readMode = ReadMode.ANTICIPE;
				try {
					fen.pan.chargerPreferences();
				} catch (NumberFormatException | IOException e) {}
			}
		}
		if (arg0.getSource() == panneau.rejouerSon) {
			param.rejouerSon = panneau.rejouerSon.isSelected();
		}
		if (arg0.getSource() == panneau.champMysterCarac) {
			param.mysterCarac = panneau.champMysterCarac.getText().toCharArray()[0];
		}
		if (arg0.getSource() == panneau.valider) {
			fen.eMenuItem2.setEnabled(true);
			// mise a jour de la couleur de la barre de progression
			fen.fenetre.pan.progressBar.setForeground(Constants.RIGHT_COLOR);
			if (verifierValiditeChamp()) {
				try {
					param.mysterCarac = panneau.champMysterCarac.getText().toCharArray()[0];
				} catch (Exception e) {
					param.mysterCarac = '_';
					panneau.champMysterCarac.setText("_");
				}
				try {
					param.premierSegment = Math.max(0, Integer.valueOf(panneau.segmentDeDepart.getText()));
				} catch (Exception e) {
					param.premierSegment = 0;
					panneau.segmentDeDepart.setText("0");
				}
				param.rejouerSon = panneau.rejouerSon.isSelected();
				fen.lancerExercice();
			}
		}
	}

	/**
	 * Retourne le font correspondant à :
	 * 
	 * @param1 : la police
	 * @param2 : l'index du font dans la liste des polices de la FenetreParametre
	 * @param3 : le style
	 * @param4 : la taille
	 */
	public static Font getFont(String police, int selectedIndex, int style, int size) {
		try {
			Font font;
			if (selectedIndex < Constants.FONTS_NAMES.length && selectedIndex >= 0) {
				font = Font
						.createFont(Font.TRUETYPE_FONT,
								new File("ressources/fonts/" + Constants.FONTS_NAMES[selectedIndex]))
						.deriveFont(style).deriveFont((float) size);
			} else {
				font = new Font(police, style, size);
			}
			return font;
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == panneau.sliderAttente) {
			param.tempsPauseEnPourcentageDuTempsDeLecture = panneau.sliderAttente.getValue();
		}
	}

	/**
	 * Retourne vrai si : - Aucune couleur n'est sélectionnée en double - Les champs
	 * saisies sont cohérents
	 */
	public boolean verifierValiditeChamp() {
		boolean valide = true;

		if (!couleursUniques()) {
			JOptionPane.showMessageDialog(panneau, "Les couleurs doivent être différentes", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			valide = false;
		}

		// premier segment
		int premierSegment = -1;
		try {
			premierSegment = Integer.valueOf((String) panneau.segmentDeDepart.getText());
			if (premierSegment + 2 > ((Panneau) fen.fenetre.getContentPane()).textHandler.getPhrasesCount()
					|| premierSegment < 1) {
				JOptionPane.showMessageDialog(panneau,
						"Entrez un segment inférieur à "
								+ (((Panneau) fen.fenetre.getContentPane()).textHandler.getPhrasesCount() - 1),
						"Erreur", JOptionPane.ERROR_MESSAGE);
				premierSegment = 1;
				panneau.segmentDeDepart.setText("1");
				valide = false;
			}
		} catch (Exception e) {
			panneau.segmentDeDepart.setText("1");
			valide = false;
		}
		param.premierSegment = premierSegment;

		try {
			if (panneau.champMysterCarac.getText().length() > 1) {
				JOptionPane.showMessageDialog(panneau, "Entrez un seul caractere",
						"Erreur", JOptionPane.ERROR_MESSAGE);
				panneau.champMysterCarac.setText("_");
				valide = false;
			}
		} catch (Exception e) {
			panneau.champMysterCarac.setText("_");
			param.mysterCarac = '_';
			valide = false;
		}
		return valide;
	}

	/**
	 * Retourne vrai si toutes les couleurs des paramètres sont uniques
	 */
	private boolean couleursUniques() {
		boolean r = true;
		List<Color> couleursUtilisées = new ArrayList<Color>();
		couleursUtilisées.add(Constants.RIGHT_COLOR);
		couleursUtilisées.add(Constants.WRONG_COLOR);
		couleursUtilisées.add(Constants.WRONG_PHRASE_COLOR);
		couleursUtilisées.add(param.couleurFond);
		if (occurence(Constants.RIGHT_COLOR, couleursUtilisées) != 1) {
			r = false;
		}
		if (occurence(Constants.WRONG_COLOR, couleursUtilisées) != 1) {
			r = false;
		}
		if (occurence(Constants.WRONG_PHRASE_COLOR, couleursUtilisées) != 1) {
			r = false;
		}
		if (occurence(param.couleurFond, couleursUtilisées) != 1) {
			r = false;
		}
		return r;
	}

	/**
	 * Retourne le nombre d'apparition de la couleur dans la FenetreParametre
	 */
	private int occurence(Color c, List<Color> liste) {
		int r = 0;
		for (Color c2 : liste) {
			if (c.equals(c2)) {
				r++;
			}
		}
		return r;
	}

}
