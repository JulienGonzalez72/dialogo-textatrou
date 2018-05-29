package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class Constants {

	/**
	 * Nom des fichiers contenants les fonts pour dislexyques
	 */
	public static final String[] FONTS_NAMES = { "OpenDyslexic-Bold.otf", "AndBasR.ttf", "LEXIA___.otf" };
	
	/**
	 * Couleur de fond du TextPane
	 */
	public static Color BG_COLOR = new Color(255, 255, 150);
	
	/**
	 * Couleur de clignotement du fond lorsque l'utilisateur s'est trompé
	 */
	public static final Color ALERT_COLOR = new Color(255, 150, 150);
	
	/**
	 * Nom de l'eleve courant
	 */
	public static String NAME_STUDENT = "Titouan";
	/**
	 * Largeur minimale de la fenetre principale
	 */
	public static int MIN_FRAME_WIDTH = 750;
	/**
	 * Hauteur minimale de la fenetre principale
	 */
	public static int MIN_FRAME_HEIGHT = 450;
	/**
	 * Taille des images dans le controleur du player
	 */
	public static final int SIZE_IMAGE_FRAME = 40;

	/*
	 * Valeurs par défaut des paramètres
	 */
	public static final int DEFAULT_FONT_SIZE = 12;
	public static final int DEFAULT_FONT_STYLE = Font.BOLD;
	public static final int MIN_WAIT_TIME_PERCENT = 0;
	public static final int MAX_WAIT_TIME_PERCENT = 300;
	public static final int DEFAULT_WAIT_TIME_PERCENT = 0;
	public static final int PARAM_FRAME_HEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height*9/10;
	public static final int PARAM_FRAME_WIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width*4/11;
	public static final String PARAM_FRAME_TITLE = "Dialogo by roman and julien";
	public static final boolean LOAD_FIRST_PHRASE = false;

	/**
	 * Temps d'attente entre chaque page
	 */
	public static final long PAGE_WAIT_TIME = 1000;

	/**
	 * Caractère qui correspond à une césure
	 */
	public static final String PAUSE = "/";

	public static final float TEXTPANE_MARGING = 20f;

	/**
	 * Vitesse de lecture (en fréquence)
	 */
	public static final long PLAYER_INTERVAL = 20;

	/**
	 * Nom du texte de l'exercice courant
	 */
	public static final String TEXT_FILE_NAME = "Aha take on me.txt";
	/**
	 * Nom de l'audio de l'exercice courant
	 */
	public static final String AUDIO_FILE_NAME = "Aha take on me";
	/**
	 * Indique si l'exercice courant à une consigne dans son texte
	 */
	public static final boolean HAS_INSTRUCTIONS = false;

	/**
	 * Intervalle de temps sans clic possible après un appuie sur la touche gauche
	 */
	public static final long LEFT_DELAY = 400;
	
	/**
	 * Icone de la souris lors d'une phase d'écoute
	 */
	public static final String CURSOR_LISTEN = "ecouter.png";
	/**
	 * Icone de la souris lors d'une phase de prononciation
	 */
	public static final String CURSOR_SPEAK = "parler.png";
	
	/**
	 * Temps de chargement après chaque pression sur un bouton de contrôle suivant/précédent.
	 */
	public static final long DISABLE_TIME = 200;
	
	/**
	 * Hauteur des cellules des listes déroulantes de la fenêtre des paramètres.
	 */
	public static final int COMBOBOX_CELL_HEIGHT = 30;
	
	/**
	 * Couleurs disponibles pour l'utilisateur.
	 */
	public static final Map<String, Color> COLORS = new HashMap<>();
	static {
		COLORS.put("Blanc", Color.WHITE);
		COLORS.put("Bleu", new Color(50, 50, 255));
		COLORS.put("Cyan", Color.CYAN);
		COLORS.put("Jaune", new Color(255, 255, 150));
		COLORS.put("Orange", Color.ORANGE);
		COLORS.put("Rose", Color.PINK);
		COLORS.put("Rouge", new Color(255, 40, 40));
		COLORS.put("Vert", Color.GREEN);
	}
	
	public static final Map<String, Color> MORE_COLORS = new TreeMap<>();
	static {
		MORE_COLORS.put("Abricot", new Color(230, 126, 48));
		MORE_COLORS.put("Azur", new Color(0, 127, 255));
		MORE_COLORS.put("Beurre", new Color(240, 227, 107));
		MORE_COLORS.put("Bleu canard", new Color(4, 139, 154));
		MORE_COLORS.put("Bleu ciel", new Color(119, 181, 254));
		MORE_COLORS.put("Bleu givré", new Color(128, 208, 208));
		MORE_COLORS.put("Bleu marine", new Color(3, 34, 76));
		MORE_COLORS.put("Caramel", new Color(126, 51, 0));
		MORE_COLORS.put("Carotte", new Color(244, 102, 27));
		MORE_COLORS.put("Citrouille", new Color(223, 109, 20));
		MORE_COLORS.put("Coquille d'oeuf", new Color(253, 233, 224));
		MORE_COLORS.put("Corail", new Color(231, 62, 1));
		MORE_COLORS.put("Émeraude", new Color(1, 215, 88));
		MORE_COLORS.put("Fraise", new Color(191, 48, 48));
		MORE_COLORS.put("Framboise", new Color(199, 44, 72));
		MORE_COLORS.put("Fuchsia", new Color(253, 63, 146));
		MORE_COLORS.put("Indigo", new Color(121, 28, 248));
		MORE_COLORS.put("Ivoire", new Color(255, 255, 212));
		MORE_COLORS.put("Jaune citron", new Color(247, 255, 60));
		MORE_COLORS.put("Kaki", new Color(148, 129, 43));
		MORE_COLORS.put("Marron", new Color(88, 41, 0));
		MORE_COLORS.put("Olive", new Color(112, 141, 35));
		MORE_COLORS.put("Or", new Color(255, 215, 0));
		MORE_COLORS.put("Pistache", new Color(190, 245, 116));
		MORE_COLORS.put("Pourpre", new Color(147, 112, 219));
		MORE_COLORS.put("Rubis", new Color(224, 17, 95));
		MORE_COLORS.put("Saumon", new Color(248, 142, 85));
		MORE_COLORS.put("Tomate", new Color(222, 41, 22));
		MORE_COLORS.put("Vert pomme", new Color(52, 201, 36));
		MORE_COLORS.put("Violet", new Color(127, 0, 255));
	}
	
	public static final String SHOW_MORE_COLORS_TEXT = "Autre couleur...";
	
	public static final Color HINT_COLOR = new Color(100, 100, 100);
	
	public static final String[] FONT_FAMILIES = {"OpenDyslexic", "Andika", "Lexia", "Arial", "Times New Roman"};
	
	public static final Integer[] FONT_SIZES = {12, 16, 18, 20, 22, 24, 30, 36, 42};

	/**
	 *  Temps d'apparition des mots a decouvrir tel que Temps = DEFAULT_TIME_APPARITION_BY_CARAC * N, avec N le nombre de caractère dans le mot actuel
	 */
	public static final int DEFAULT_TIME_APPARITION_BY_CARAC = 0;

	public static final int MAX_APPARITION_TIME = 1200;

}
