package main.controler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Constants;
import main.Parametres;
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
		if (jcb == panneau.fontSizeComboBox) {
			int taille = (Integer) jcb.getSelectedItem();
			Font font = new Font(panneau.fontFamilyComboBox.getFont().getFontName(), Constants.DEFAULT_FONT_STYLE,
					taille);
			if (fen.editorPane != null) {
				fen.editorPane.setFont(font);

				// redimentionnement de l'eventuelle fenetre fixe
				Panneau panneauExo = fen.fenetre.pan;
				if (panneauExo.panelFenetreFixe != null) {
					panneauExo.panelSud.setPreferredSize(new Dimension(panneauExo.fenetre.getWidth(),
							font.getSize() + panneauExo.progressBar.getHeight()));
					panneauExo.panelFenetreFixe
							.setPreferredSize(new Dimension(panneauExo.fenetre.getWidth(), font.getSize()));
				}

				fen.fenetre.pan.rebuildPages();
			}
		}
		if (jcb == panneau.fontFamilyComboBox) {
			String police = (String) jcb.getSelectedItem();
			Font font = new Font(police, Constants.DEFAULT_FONT_STYLE,
					(Integer) panneau.fontSizeComboBox.getSelectedItem());
			jcb.setFont(font.deriveFont((float) jcb.getFont().getSize()));
			if (fen.editorPane != null) {
				fen.editorPane.setFont(font);
				fen.fenetre.pan.rebuildPages();
			}
		}
		if (arg0.getSource() == panneau.hightlightCheckBox) {
			boolean selected = panneau.hightlightCheckBox.isSelected();
			panneau.rightColorComboBox.setEnabled(selected);
			panneau.couleurSurlignage.setEnabled(selected);
		}
		if (arg0.getSource() == panneau.valider) {
			panneau.savePreferences();
			fen.eMenuItem2.setEnabled(true);
			// mise a jour de la couleur de la barre de progression
			fen.fenetre.pan.progressBar.setForeground(Color.GREEN);
			if (verifierValiditeChamp()) {
				if (!fen.fenetre.isVisible()) {
					fen.lancerExercice();
				}
			}
		}
	}

	/**
	 * Retourne le font correspondant � :
	 * 
	 * @param :
	 *            la police
	 * @param :
	 *            l'index du font dans la liste des polices de la FenetreParametre
	 * @param :
	 *            le style
	 * @param :
	 *            la taille
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

	/**
	 * Retourne vrai si : - Aucune couleur n'est sélectionnée en double - Les champs
	 * saisies sont cohérents
	 */
	public boolean verifierValiditeChamp() {
		boolean valide = true;

		if (!couleursUniques()) {
			JOptionPane.showMessageDialog(panneau, "Les couleurs doivent �tre diff�rentes", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			valide = false;
		}

		int premierSegment = -1;
		try {
			premierSegment = Integer.valueOf((String) panneau.firstPhraseField.getText());
			if (!isValidPhrase(premierSegment)) {
				JOptionPane.showMessageDialog(panneau,
						"Entrez un segment inférieur ou égal à "
								+ (fen.fenetre.pan.textHandler.getPhrasesCount()),
						"Erreur", JOptionPane.ERROR_MESSAGE);
				premierSegment = 1;
				panneau.firstPhraseField.setText("1");
				valide = false;
			}
		} catch (Exception e) {
			panneau.firstPhraseField.setText("1");
			valide = false;
		}

		int maxPhraseByPage = -1;
		try {
			maxPhraseByPage = Integer.valueOf((String) panneau.maxPhraseByPage.getText());
			if (maxPhraseByPage < 1) {
				JOptionPane.showMessageDialog(panneau, "Entrez un nombre positif", "Erreur", JOptionPane.ERROR_MESSAGE);
				panneau.maxPhraseByPage.setText("100");
				valide = false;
			}
		} catch (Exception e) {
			panneau.maxPhraseByPage.setText("100");
			valide = false;
		}

		param.maxPhraseByPage = maxPhraseByPage;
		param.firstPhrase = premierSegment;
		return valide;
	}

	/**
	 * Retourne vrai si toutes les couleurs des param�tres sont uniques
	 */
	private boolean couleursUniques() {
		boolean r = true;
		List<Color> couleursUtilisées = new ArrayList<Color>();
		couleursUtilisées.add(param.bgColor);
		couleursUtilisées.add(param.rightColor);
		if (occurence(param.bgColor, couleursUtilisées) != 1 || occurence(param.rightColor, couleursUtilisées) != 1) {
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

	@Override
	public void stateChanged(ChangeEvent arg0) {

	}
	
	public boolean isValidPhrase(int n) {
		return n <= fen.fenetre.pan.textHandler.getPhrasesCount()
				&& n >= 1;
	}

}
