package main;

import java.awt.Color;

public final class Constants {

	/**
	 * Nom des fichiers contenants les fonts pour dislexyques
	 */
	public static final String[] FONTS_NAMES = { "OpenDyslexic-Bold.otf", "AndBasR.ttf", "LEXIA___.otf" };

	/**
	 * Couleur pour indiquer l'erreur
	 */
	public static Color WRONG_COLOR = new Color(255, 40, 40);
	/**
	 * Couleur pour indiquer la r�ussite
	 */
	public static Color RIGHT_COLOR = Color.GREEN;
	/**
	 * Couleur pour corriger
	 */
	public static Color WRONG_PHRASE_COLOR = Color.CYAN;
	/**
	 * Couleur de fond du TextPane
	 */
	public static Color BG_COLOR = new Color(255, 255, 150);
	/**
	 * Nom de l'eleve courant
	 */
	public static String NOM_ELEVE = "Titouan";
	/**
	 * Largeur minimale de la fenetre principale
	 */
	public static int MIN_FENETRE_WIDTH = 800;
	/**
	 * Hauteur minimale de la fenetre principale
	 */
	public static int MIN_FENETRE_HEIGHT = 400;
	/**
	 * Taille des images dans le controleur du player
	 */
	public static final int tailleImageFrame = 40;

	/*
	 * Valeurs par d�faut des param�tres
	 */
	public static final int DEFAULT_FONT_SIZE = 12;
	public static final int MIN_WAIT_TIME_PERCENT = 0;
	public static final int MAX_WAIT_TIME_PERCENT = 300;
	public static final int DEFAULT_WAIT_TIME_PERCENT = 0;
	public static final int hauteurFenetreParam = 850;
	public static final int largeurFenetreParam = 500;
	public static final String titreFenetreParam = "Dialogo by roman and julien";
	public static final boolean LOAD_FIRST_PHRASE = false;

	/**
	 * Temps d'attente entre chaque page
	 */
	public static final long PAGE_WAIT_TIME = 1000;

	/**
	 * Caract�re qui correspond � une c�sure
	 */
	public static final String PAUSE = "/";

	public static final float TEXTPANE_MARGING = 20f;

	/**
	 * Vitesse de lecture (en fr�quence)
	 */
	public static final long PLAYER_INTERVAL = 20;

	/**
	 * Nom du texte de l'exercice courant
	 */
	public static final String TEXT_FILE_NAME = "Am�lie la sorci�re.txt";
	/**
	 * Nom de l'audio de l'exercice courant
	 */
	public static final String AUDIO_FILE_NAME = "T Am�lie la sorci�re";
	/**
	 * Indique si l'exercice courant � une consigne dans son texte
	 */
	public static final boolean HAS_INSTRUCTIONS = true;

	/**
	 * Intervalle de temps sans clic possible apr�s un appuie sur la touche gauche
	 */
	public static final long LEFT_DELAY = 400;
	
	/**
	 * Icone de la souris lors d'une phase d'�coute
	 */
	public static final String CURSOR_LISTEN = "ecouter.png";
	/**
	 * Icone de la souris lors d'une phase de prononciation
	 */
	public static final String CURSOR_SPEAK = "parler.png";
	
	/**
	 * Temps de chargement apr�s chaque pression sur un bouton de contr�le suivant/pr�c�dent.
	 */
	public static final long DISABLE_TIME = 200;

}
