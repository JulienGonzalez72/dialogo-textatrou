package main;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import main.controler.ControleurParam;
import main.view.Fenetre;

public class Parametres {

	public boolean oneHole = false;
	public Font police = ControleurParam.getFont(null, 0, Font.BOLD, Constants.DEFAULT_FONT_SIZE);
	public Color bgColor = Constants.BG_COLOR;
	public String titre;
	public int tailleX;
	public int tailleY;
	public int premierSegment = 1;
	public char mysterCarac = '_';
	public int tempsPauseEnPourcentageDuTempsDeLecture;
	public boolean fixedField = true;
	public int panWidth, panHeight, panX, panY;
	public boolean surlignage;
	public Color rightColor = Color.green;

	public Parametres() {

	}

	public static String fromColorToString(Color c) {
		return (c.getRed() + "/" + c.getGreen() + "/" + c.getBlue());
	}

	public static Color fromStringToColor(String s) {
		String[] temp = s.split("/");
		return new Color(Integer.valueOf(temp[0]), Integer.valueOf(temp[1]), Integer.valueOf(temp[2]));
	}

	public void stockerPreference() {
		Properties prop = new Properties();
		prop.put("w", String.valueOf(panWidth));
		prop.put("h", String.valueOf(panHeight));
		prop.put("x", String.valueOf(panX));
		prop.put("y", String.valueOf(panY));
		prop.put("taillePolice", String.valueOf(police.getSize()));
		prop.put("typePolice", police.getFontName());
		prop.put("couleurFond", fromColorToString(bgColor));
		prop.put("tempsAttente", String.valueOf(tempsPauseEnPourcentageDuTempsDeLecture));
		prop.put("fenetreFixe", String.valueOf(fixedField));
		//prop.put("premierSegment", String.valueOf(premierSegment));
		prop.put("oneHole", String.valueOf(oneHole));
		prop.put("surlignage", String.valueOf(surlignage));
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
	 * Applique les preferences de taille et position uniquement
	 */
	public void appliquerPreferenceTaillePosition(Fenetre fen) {
		fen.setBounds(panX, panY, panWidth, panHeight);
	}
	
	/**
	 * Essaye de charger les paramètres,
	 * retourne les paramètres par défaut sinon.
	 */
	public static Parametres load() {
		Parametres p = new Parametres();
		
		String fichier = "./ressources/preferences/preference_" + Constants.NOM_ELEVE + ".txt";
		InputStream ips = null;
		try {
			ips = new FileInputStream(fichier);
		} catch (Exception e) {
			return p;
		}
		Properties pro = new Properties();
		try {
			pro.load(ips);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int taillePolice = Integer.valueOf(pro.getProperty("taillePolice"));
		String police = pro.getProperty("typePolice");
		int index = 999;
		if (police.equals("OpenDyslexic") || police.equals("OpenDyslexic Bold")) {
			index = 0;
		}
		if (police.equals("Andika")) {
			index = 1;
		}
		if (police.equals("Lexia")) {
			index = 2;
		}
		p.police = ControleurParam.getFont(police, index, Font.BOLD, taillePolice);
		p.bgColor = fromStringToColor(pro.getProperty("couleurFond"));
		p.tempsPauseEnPourcentageDuTempsDeLecture = Integer.valueOf(pro.getProperty("tempsAttente"));
		p.panX = Integer.valueOf(pro.getProperty("x"));
		p.panY = Integer.valueOf(pro.getProperty("y"));
		p.panWidth = Integer.valueOf(pro.getProperty("w"));
		p.panHeight = Integer.valueOf(pro.getProperty("h"));
		//p.premierSegment = Integer.valueOf(pro.getProperty("premierSegment"));
		p.fixedField = Boolean.valueOf(pro.getProperty("fenetreFixe"));
		p.oneHole = Boolean.valueOf(pro.getProperty("oneHole"));
		p.surlignage = Boolean.valueOf(pro.getProperty("surlignage"));
		
		return p;
	}

}
