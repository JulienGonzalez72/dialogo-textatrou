package main;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import main.controler.ControleurParam;
import main.view.Fenetre;

public class Parametres {

	public boolean oneHole = false;
	public Font police = ControleurParam.getFont(null, 0, Font.BOLD, Constants.DEFAULT_FONT_SIZE);
	public Color bgColor = Constants.BG_COLOR;
	public String title;
	public int sizeX;
	public int sizeY;
	public int sizeParamX;
	public int sizeParamY;
	public int firstPhrase = 1;
	public int timeToWaitToLetStudentRepeat;
	public int timeToShowWord;
	public boolean fixedField = true;
	public int panWidth, panHeight, panX, panY;
	public boolean highlight;
	public boolean replayPhrase;
	public Color rightColor = Color.green;
	public int maxPhraseByPage;

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
		prop.put("sizeParamX", String.valueOf(sizeParamX));
		prop.put("sizeParamY", String.valueOf(sizeParamY));
		prop.put("x", String.valueOf(panX));
		prop.put("y", String.valueOf(panY));
		prop.put("taillePolice", String.valueOf(police.getSize()));
		prop.put("typePolice", police.getFontName());
		prop.put("couleurFond", fromColorToString(bgColor));
		prop.put("couleurJuste", fromColorToString(rightColor));
		prop.put("tempsApparitionMot", String.valueOf(timeToShowWord));
		prop.put("tempsAttente", String.valueOf(timeToWaitToLetStudentRepeat));
		prop.put("fenetreFixe", String.valueOf(fixedField));
		prop.put("premierSegment", String.valueOf(firstPhrase));
		prop.put("oneHole", String.valueOf(oneHole));
		prop.put("surlignage", String.valueOf(highlight));
		prop.put("rejouerSon", String.valueOf(replayPhrase));
		prop.put("maxPhrase", String.valueOf(maxPhraseByPage));

		String fichier = "./ressources/preferences/preference_" + Constants.NAME_STUDENT + ".txt";
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
	 * Essaye de charger les param�tres, retourne les param�tres par d�faut sinon.
	 */
	public static Parametres load() {
		Parametres p = new Parametres();
		String fichier = "./ressources/preferences/preference_" + Constants.NAME_STUDENT + ".txt";
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

		p.sizeParamX = Integer.valueOf(pro.getProperty("sizeParamX"));
		p.sizeParamY = Integer.valueOf(pro.getProperty("sizeParamY"));
		p.timeToShowWord = Integer.valueOf(pro.getProperty("tempsApparitionMot"));
		p.police = ControleurParam.getFont(police, index, Font.BOLD, taillePolice);
		p.bgColor = fromStringToColor(pro.getProperty("couleurFond"));
		p.rightColor = fromStringToColor(pro.getProperty("couleurJuste"));
		p.timeToWaitToLetStudentRepeat = Integer.valueOf(pro.getProperty("tempsAttente"));
		p.panX = Integer.valueOf(pro.getProperty("x"));
		p.panY = Integer.valueOf(pro.getProperty("y"));
		p.panWidth = Integer.valueOf(pro.getProperty("w"));
		p.panHeight = Integer.valueOf(pro.getProperty("h"));
		p.firstPhrase = Integer.valueOf(pro.getProperty("premierSegment"));
		p.fixedField = Boolean.valueOf(pro.getProperty("fenetreFixe"));
		p.oneHole = Boolean.valueOf(pro.getProperty("oneHole"));
		p.highlight = Boolean.valueOf(pro.getProperty("surlignage"));
		p.replayPhrase = Boolean.valueOf(pro.getProperty("rejouerSon"));
		p.maxPhraseByPage = Integer.valueOf(pro.getProperty("maxPhrase"));
		return p;
	}

}
