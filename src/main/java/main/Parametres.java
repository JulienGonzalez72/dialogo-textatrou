package main;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import main.controler.ControleurParam;
import main.view.*;

public class Parametres {

	public Font police = ControleurParam.getFont(null, 0, Font.BOLD, Constants.DEFAULT_FONT_SIZE);
	public int taillePolice = Constants.DEFAULT_FONT_SIZE;
	public Color couleurFond = Constants.BG_COLOR;
	public String titre;
	public int tailleX;
	public int tailleY;
	public int premierSegment;
	public char mysterCarac;
	public int tempsPauseEnPourcentageDuTempsDeLecture;
	public boolean rejouerSon = true;
	public int panWidth, panHeight, panX, panY;

	public Parametres() {

	}

	public String fromColorToString(Color c) {
		return (c.getRed() + "/" + c.getGreen() + "/" + c.getBlue());
	}

	public Color fromStringToColor(String s) {
		String[] temp = s.split("/");
		return new Color(Integer.valueOf(temp[0]), Integer.valueOf(temp[1]), Integer.valueOf(temp[2]));
	}

	public void stockerPreference() {
		Properties prop = new Properties();
		prop.put("w", String.valueOf(panWidth));
		prop.put("h", String.valueOf(panHeight));
		prop.put("x", String.valueOf(panX));
		prop.put("y", String.valueOf(panY));
		prop.put("taillePolice", String.valueOf(taillePolice));
		prop.put("typePolice", police.getFontName());
		prop.put("couleurFond", fromColorToString(couleurFond));
		prop.put("tempsAttente", String.valueOf(tempsPauseEnPourcentageDuTempsDeLecture));
		prop.put("rejouerSon", String.valueOf(rejouerSon));
		String fichier = "./ressources/preferences/preference_" + Constants.NOM_ELEVE + ".txt";
		OutputStream ops = null;
		try {
			ops = new FileOutputStream(fichier);
		} catch (Exception e) {

		}
		try {
			prop.store(ops, "Sauvegarde");
			ops.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Applique toutes les preferences sauf la position et les dimentions de pan
	 */
	public void appliquerPreference(FenetreParametre fen, Panneau pan) {
		String fichier = "./ressources/preferences/preference_" + Constants.NOM_ELEVE + ".txt";
		InputStream ips = null;
		try {
			ips = new FileInputStream(fichier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Properties pro = new Properties();
		try {
			pro.load(ips);
		} catch (IOException e) {
			e.printStackTrace();
		}

		taillePolice = Integer.valueOf(pro.getProperty("taillePolice"));
		String p = pro.getProperty("typePolice");
		int index = 999;
		if (p.equals("OpenDyslexic") || p.equals("OpenDyslexic Bold")) {
			index = 0;
		}
		if (p.equals("Andika")) {
			index = 1;
		}
		if (p.equals("Lexia")) {
			index = 2;
		}
		police = ControleurParam.getFont(p, index, Font.BOLD, taillePolice);
		fen.pan.colorComboBox.setBackground(couleurFond = fromStringToColor(pro.getProperty("couleurFond")));
		pan.editorPane.setFont(police);
		pan.editorPane.setBackground(couleurFond);
		tempsPauseEnPourcentageDuTempsDeLecture = Integer.valueOf(pro.getProperty("tempsAttente"));
		fen.pan.waitSlider.setValue(tempsPauseEnPourcentageDuTempsDeLecture);
	}

	/**
	 * Applique les preferences de taille et position uniquement
	 */
	public void appliquerPreferenceTaillePosition(FenetreParametre fenetreParam, Fenetre fen) {
		String fichier = "./ressources/preferences/preference_" + Constants.NOM_ELEVE + "_.txt";
		InputStream ips = null;
		try {
			ips = new FileInputStream(fichier);
			Properties pro = new Properties();
			try {
				pro.load(ips);
			} catch (IOException e) {
				e.printStackTrace();
			}
			fen.setBounds(Integer.valueOf(pro.getProperty("x")), Integer.valueOf(pro.getProperty("y")),
					Integer.valueOf(pro.getProperty("w")), Integer.valueOf(pro.getProperty("h")));
		} catch (Exception e) {}
	}

	/**
	 * Retourne le Rectangle définissant la fentre de l'exercice
	 */
	public Rectangle getTaillePositionExercice() {
		return new Rectangle(panX, panY, panWidth, panHeight);
	}

}
