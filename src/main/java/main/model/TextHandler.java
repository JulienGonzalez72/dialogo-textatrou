package main.model;

import java.util.*;
import main.Constants;
import main.Parametres;

public class TextHandler {

	/**
	 * Texte formaté
	 */
	public String txt;
	
	/**
	 * Liste des segments associés à leurs numéros
	 */
	private Map<Integer, String> phrases;

	/**
	 * Liste des mots associés à leurs numéros de trous.
	 */
	public Map<Integer, String> mots;

	/**
	 * Liste des mots pour chaque segment.
	 */
	public Map<Integer, List<String>> motsParSegment;

	Parametres param;

	public TextHandler(String texteOriginal, Parametres param) {
		this.param = param;
		this.mots = new HashMap<Integer, String>();
		this.motsParSegment = new HashMap<>();

		remplirMots(texteOriginal);
		txt = format(texteOriginal);
		this.phrases = new HashMap<Integer, String>();
		for (String phrase : txt.split(Constants.PAUSE)) {
			phrases.put(phrases.size(), phrase);
		}
	}

	public boolean oneHoleEqualOneWord() {
		boolean r = true;
		for (String s : mots.values()) {
			if (s.contains(" ")) {
				r = false;
			}
		}
		return r;
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
			if (tab[i] == '/') {
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
			} else if (motCourant != "") {
				mots.put(numero, motCourant);
				listStrings.add(motCourant);
				motCourant = "";
				numero++;
			}
		}
	}

	private String format(String str) {
		String r = "";
		String temp = str.replace(" /", "/");
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
				r += '_';
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
				// r += '_';
				r += tab[i];
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
	 * Indique si la césure est placée au bon endroit.
	 */
	public boolean correctPause(int offset) {
		String b = getTextWithCutPauses(offset);
		return b.charAt(offset) == '/';
	}

	/**
	 * Retourne la position absolue du début du segment passé en paramètre.
	 */
	public int getPhraseOffset(int phrase) {
		return getPhrasesLength(0, phrase - 1);
	}

	/**
	 * Retourne l'indice du segment à la position indiquée.
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
	 * Enlève les césures du texte avec césures jusqu'à la position indiquée.
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
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * Retourne la position du début du segment d'indice <i>phrase</i>, relative au
	 * premier segment <i>startPhrase</i>.
	 */
	public int getRelativeStartPhrasePosition(int startPhrase, int phrase) {
		return getRelativeOffset(startPhrase, getPhraseOffset(phrase));
	}

	private static boolean isPunctuation(char c) {
		return c == ',' || c == '.' || c == ';' || c == ':' || c == '!' || c == '?';
	}

	/**
	 * Retourne la position du caractère dans le texte en entier en indiquant la
	 * position d'un caractère cliqué à partir d'un segment indiqué.
	 */
	public int getAbsoluteOffset(int startPhrase, int offset) {
		return getPhrasesLength(0, startPhrase - 1) + offset;
	}

	/**
	 * Retourne une liste des mots à trouver par segment.
	 */
	public List<String> getHidedWords(int phrase) {
		return motsParSegment.containsKey(phrase) ? motsParSegment.get(phrase) : new ArrayList<>();
	}

	public int getStartOffset(String expression, int phrase) {
		return getPhrasesLength(0, phrase - 1) + phrases.get(phrase).indexOf(expression);
	}

	public int getEndOffset(String expression, int phrase) {
		return getStartOffset(expression, phrase) + expression.length();
	}

	/**
	 * Retourne le nombre de trous que contient le segment <code>phrase</code>.
	 */
	public int getHolesCount(int phrase) {
		return motsParSegment.containsKey(phrase) ? motsParSegment.get(phrase).size() : 0;
	}
	
	public int getHolesCount(int startPhrase, int endPhrase) {
		int count = 0;
		for (int i = startPhrase; i <= endPhrase; i++) {
			count += getHolesCount(i);
		}
		return count;
	}
	
	/**
	 * Retourne le nombre de trous total du texte.
	 */
	public int getHolesCount() {
		return mots.size();
	}
	
	/**
	 * Retourne <code>true</code> si il y a au moins un autre trou après le trou indiqué dans le même segment.
	 */
	public boolean hasNextHoleInPhrase(int hole) {
		int p = getPhraseOf(hole);
		List<String> words = getHidedWords(p);
		int holeInPhrase = hole - getHolesCount(0, p - 1);
		return holeInPhrase < words.size() - 1;
	}
	
	/**
	 * Retourne <code>true</code> si il y a au moins un autre trou avant le trou indiqué dans le même segment.
	 */
	public boolean hasPreviousHoleInPhrase(int hole) {
		int p = getPhraseOf(hole);
		List<String> words = getHidedWords(p);
		int holeInPhrase = hole - getHolesCount(0, p - 1);
		return holeInPhrase > 0 && words.size() > 1;
	}
	
	/**
	 * Retourne le numéro de segment correspondant au trou indiqué.
	 */
	public int getPhraseOf(int hole) {
		int n = 0;
		for (int i = 0; i < getPhrasesCount(); i++) {
			n += getHolesCount(i);
			if (n > hole) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Ceci est l'opération inverse, elle permet d'obtenir la position par rapport
	 * au premier segment affiché avec la position du caractère dans tout le texte.
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
	
	/**
	 * Retourne la position de départ du trou indiqué.
	 */
	public int getHoleStartOffset(int hole) {
		int p = getPhraseOf(hole);
		int n = 0;
		for (int i = getPhraseOffset(p); i < length(); i++) {
			if (isHole(i)) {
				if (n == hole - getFirstHole(p)) {
					return i;
				}
				else {
					n++;
					while (isHole(++i));
				}
			}
		}
		return -1;
	}
	
	public int getHoleEndOffset(int hole) {
		return getHoleStartOffset(hole) + getHoleLength(hole);
	}
	
	public int length() {
		return getPhrasesLength(0, getPhrasesCount());
	}
	
	/**
	 * Retourne le numéro du premier trou à partir du segment indiqué.
	 */
	public int getFirstHole(int phrase) {
		for (int i = 0; i < getHolesCount(); i++) {
			if (phrase == getPhraseOf(i)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean isHole(int offset) {
		return getShowText().charAt(offset) == '_';
	}
	
	public int getHoleLength(int h) {
		return mots.get(h).length();
	}

	@Override
	public String toString() {
		return txt;
	}

	
}
