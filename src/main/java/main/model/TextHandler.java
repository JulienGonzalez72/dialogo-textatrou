package main.model;

import java.util.*;

import main.Constants;
import main.Parametres;

public class TextHandler {

	/**
	 * Texte format�s
	 */
	public String txt;
	/**
	 * Liste des segments associ�s � leurs num�ros
	 */
	private Map<Integer, String> phrases;
	
	public Map<Integer,String> mots;
	
	public Map<Integer,List<String>> motsParSegment;
	
	Parametres param;

	public TextHandler(String texteOriginal, Parametres param) {
		this.param = param;
		this.mots = new HashMap<Integer,String>();
		this.motsParSegment = new HashMap<>();
		remplirMots(texteOriginal);
		txt = format(texteOriginal);
		this.phrases = new HashMap<Integer, String>();
		for (String phrase : txt.split(Constants.PAUSE)) {
			phrases.put(phrases.size(), phrase);
		}
	}

	private void remplirMots(String s) {
		int numeroSegmentCourant = 0;
		String temp = s.replaceAll(" /", "/");
		char[] tab = temp.toCharArray();
		boolean dansCrochet = false;
		int numero = 0;
		String motCourant = "";
		List<String> listStrings = new ArrayList<>();
		for (int i = 0; i < tab.length; i++) {
			if ( tab[i] == '/') {		
				motsParSegment.put(numeroSegmentCourant, listStrings);
				listStrings = new ArrayList<>();
				numeroSegmentCourant++;
			}
			if (tab[i] == '[') {
				dansCrochet = true;
				i++;
			} else if (tab[i] == ']') {
				dansCrochet = false;
			}
			if (dansCrochet) {
				motCourant += tab[i];
			} else if ( motCourant != ""){
				mots.put(numero, motCourant);
				listStrings.add(motCourant);
				motCourant ="";
				numero++;
			}
		}	
		System.out.println(mots.toString());
		System.out.println(motsParSegment.toString());
	}

	private String format(String str) {
		String r = "";
		String temp = str.replace(" /", "/").replaceAll("]/", "] /");
		char[] tab = temp.toCharArray();
		boolean dansCrochet = false;
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] == '[') {
				dansCrochet = true;
				i++;
			} else if (tab[i] == ']') {
				dansCrochet = false;
				i++;
			}
			if (dansCrochet) {
				r += param.mysterCarac;
			} else {
				r += tab[i];
			}
		}
		return r;
	}


	public String getShowText() {
		String r = "";
		String temp = txt.replace(Constants.PAUSE, "");
		char[] tab = temp.toCharArray();
		boolean dansCrochet = false;
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] == '[') {
				dansCrochet = true;
				i++;
			} else if (tab[i] == ']') {
				dansCrochet = false;
				i++;
			}
			if (dansCrochet) {
				r += param.mysterCarac;
			} else {
				r += tab[i];
			}
		}
		return r;
	}

	public String[] getPhrases(int start, int end) {
		List<String> list = new ArrayList<String>();
		Iterator<Integer> keys = phrases.keySet().iterator();
		while (keys.hasNext()) {
			int key = keys.next();
			if (key >= start && key <= end) {
				list.add(phrases.get(key));
			}
		}
		return list.toArray(new String[0]);
	}

	public String[] getPhrases() {
		return getPhrases(0, getPhrasesCount());
	}

	public String getPhrase(int index) {
		return phrases.get(index);
	}

	/**
	 * Nombre de segments total
	 */
	public int getPhrasesCount() {
		return phrases.size();
	}

	/**
	 * Indique si la c�sure est plac�e au bon endroit.
	 */
	public boolean correctPause(int offset) {
		String b = getTextWithCutPauses(offset);
		return b.charAt(offset) == '/';
	}

	/**
	 * Retourne l'indice de la pause � la position indiqu�e.
	 */
	public int getPauseIndex(int offset) {
		if (!correctPause(offset))
			return -1;
		int index = 0;
		for (int i = 0; i < offset; i++) {
			if (txt.charAt(i) == '/') {
				index++;
			}
		}
		return index;
	}

	/**
	 * Retourne la position absolue du segment indiqu� en param�tre.
	 */
	public int getPauseOffset(int phrase) {
		return getPhrasesLength(0, phrase);
	}

	/**
	 * Retourne la position absolue du d�but du segment pass� en param�tre.
	 */
	public int getPhraseOffset(int phrase) {
		return getPhrasesLength(0, phrase - 1);
	}

	/**
	 * Retourne l'indice du segment � la position indiqu�e.
	 */
	public int getPhraseIndex(int offset) {
		if (offset >= getShowText().length())
			return -1;
		int index = 0;
		for (int i = 0; i < offset; i++) {
			if (txt.charAt(i) == '/') {
				index++;
				offset += Constants.PAUSE.length();
			}
		}
		return index;
	}

	/**
	 * Enl�ve les c�sures du texte avec c�sures jusqu'� la position indiqu�e.
	 */
	private String getTextWithCutPauses(int endOffset) {
		StringBuilder b = new StringBuilder(txt);
		for (int i = 0; i < b.length(); i++) {
			if (i >= endOffset) {
				break;
			}
			/// supprime le slash ///
			if (b.charAt(i) == '/') {
				b.deleteCharAt(i);
			}
		}
		return b.toString();
	}

	public int endWordPosition(int offset) {
		for (int i = offset; i < getShowText().length(); i++) {
			if (Character.isWhitespace(getShowText().charAt(i)) || isPunctuation(getShowText().charAt(i))) {
				return i;
			}
		}
		return -1;
	}

	public int startWordPosition(int offset) {
		for (int i = Math.min(getShowText().length() - 1, offset); i >= 0; i--) {
			if (Character.isWhitespace(getShowText().charAt(i)) || isPunctuation(getShowText().charAt(i))) {
				return i+1;
			}
		}
		return -1;
	}

	/**
	 * Retourne la position du d�but du segment d'indice <i>phrase</i>, relative au
	 * premier segment <i>startPhrase</i>.
	 */
	public int getRelativeStartPhrasePosition(int startPhrase, int phrase) {
		return getRelativeOffset(startPhrase, getPhraseOffset(phrase));
	}

	private static boolean isPunctuation(char c) {
		return c == ',' || c == '.' || c == ';' || c == ':' || c == '!' || c == '?';
	}

	/**
	 * Indique si le mot sur lequel a cliqu� l'utilisateur correspond bien � une
	 * c�sure.
	 */
	public boolean wordPause(int offset) {
		int err = 0;
		String txt = getShowText();
		for (int i = offset; i < txt.length(); i++) {
			if (correctPause(i)) {
				return true;
			}
			/// �vite le probl�me de la ponctuation avec des espaces avant ///
			if (i < txt.length() - 2 && isPunctuation(txt.charAt(i + 1))) {
				err--;
			}
			if (Character.isWhitespace(txt.charAt(i)) || isPunctuation(txt.charAt(i))) {
				err++;
				if (err >= 2) {
					return false;
				}
			}
			if (Character.isAlphabetic(txt.charAt(i)) && err == 1) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Retourne la position du caract�re dans le texte en entier en indiquant la
	 * position d'un caract�re cliqu� � partir d'un segment indiqu�.
	 */
	public int getAbsoluteOffset(int startPhrase, int offset) {
		return getPhrasesLength(0, startPhrase - 1) + offset;
	}

	/**
	 * Ceci est l'op�ration inverse, elle permet d'obtenir la position par rapport
	 * au premier segment affich� avec la position du caract�re dans tout le texte.
	 */
	public int getRelativeOffset(int startPhrase, int offset) {
		return offset - getPhrasesLength(0, startPhrase - 1);
	}

	public int getPhrasesLength(int startPhrase, int endPhrase) {
		int length = 0;
		for (String phrase : getPhrases(startPhrase, endPhrase)) {
			length += phrase.length();
		}
		return length;
	}

}