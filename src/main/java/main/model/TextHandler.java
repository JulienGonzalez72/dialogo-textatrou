package main.model;

import java.util.*;
import main.Constants;
import main.Parametres;

public class TextHandler {

	/**
	 * Texte d'origine
	 */
	private String originText;
	
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
	private Map<Integer, Hole> holes;

	/**
	 * Liste des mots pour chaque segment.
	 */
	public Map<Integer, List<Hole>> motsParSegment;
	
	/**
	 * Pour chaque mot, true si il est en texte plein ou false si il est caché.
	 */
	private Map<Integer, Boolean> filledWords;

	Parametres param;

	public TextHandler(String texteOriginal, Parametres param) {
		this.originText = texteOriginal;
		this.param = param;
		this.holes = new HashMap<Integer, Hole>();
		this.motsParSegment = new HashMap<>();
		this.filledWords = new HashMap<>();
		this.phrases = new HashMap<Integer, String>();

		remplirMots(texteOriginal);
		updateText();
	}

	public boolean oneHoleEqualOneWord() {
		boolean r = true;
		for (Hole h : holes.values()) {
			if (h.getHidedWord().contains(" ")) {
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
		int offset = 0;
		String motCourant = "";
		List<Hole> listStrings = new ArrayList<>();
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] == '/') {
				motsParSegment.put(numeroSegmentCourant, listStrings);
				listStrings = new ArrayList<>();
				numeroSegmentCourant++;
				offset = 0;
			}
			else if (tab[i] == '[') {
				dansCrochet = true;
				i++;
			} else if (tab[i] == ']') {
				dansCrochet = false;
			}
			else {
				offset++;
			}
			if (dansCrochet) {
				motCourant += tab[i];
			} else if (motCourant != "") {
				Hole h = new Hole(motCourant);
				h.startOffset = offset - motCourant.length() + 1;
				offset += h.getShift() + 1;
				holes.put(numero, h);
				filledWords.put(numero, false);
				listStrings.add(h);
				motCourant = "";
				numero++;
			}
		}
	}
	
	private void updateText() {
		String r = "";
		String temp = originText.replace(" /", "/");
		char[] tab = temp.toCharArray();
		boolean dansCrochet = false;
		int tailleTrou = 0;
		int numeroTrou = 0;
		
		for (int i = 0; i < tab.length; i++) {
			if (tab[i] == '[') {
				dansCrochet = true;
				i++;
			} else if (tab[i] == ']') {
				/*for (int j = 0; j < holeLength(tailleTrou); j++) {
					r += '_';
				}*/
				r += holes.get(numeroTrou);
				dansCrochet = false;
				tailleTrou = 0;
				numeroTrou++;
				i++;
			}
			if (dansCrochet) {
				tailleTrou++;
			} else {
				r += tab[i];
			}
		}
		txt = r;
		
		phrases.clear();
		for (String phrase : txt.split(Constants.PAUSE)) {
			phrases.put(phrases.size(), phrase);
		}
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
		if (offset >= length())
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

	/**
	 * Retourne la position du début du segment d'indice <i>phrase</i>, relative au
	 * premier segment <i>startPhrase</i>.
	 */
	public int getRelativeStartPhrasePosition(int startPhrase, int phrase) {
		return getRelativeOffset(startPhrase, getPhraseOffset(phrase));
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
		return motsParSegment.containsKey(phrase) ? Hole.getHidedWords(motsParSegment.get(phrase)) : new ArrayList<>();
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
		return holes.size();
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
		return getPhrasesLength(0, getPhraseOf(hole) - 1) + holes.get(hole).startOffset;
	}
	
	public int getHoleEndOffset(int hole) {
		return getHoleStartOffset(hole) + holes.get(hole).length();
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
	
	/**
	 * Retourne le mot associé au trou indiqué.
	 */
	public String getHidedWord(int hole) {
		return holes.get(hole).getHidedWord();
	}
	
	public boolean isHole(int offset) {
		return getShowText().charAt(offset) == '_';
	}
	
	public int getHidedWordLength(int h) {
		return holes.get(h).length();
	}
	
	/**
	 * Remplace le trou h par le mot qui lui correspond.
	 */
	public void fillHole(int hole) {
		Hole h = holes.get(hole);
		
		if (h.isHidden()) {
			h.fill();
			
			/// décale les trous du même segment ///
			for (int i = hole + 1; i < getHolesCount() && getPhraseOf(i) == getPhraseOf(hole); i++) {
				holes.get(i).startOffset -= h.getShift();
			}
			
			updateText();
		}
	}
	
	
	public void hideHole(int hole) {
		Hole h = holes.get(hole);
		
		if (!h.isHidden()) {
			h.hide();
			
			/// décale les trous du même segment ///
			for (int i = hole + 1; i < getHolesCount() && getPhraseOf(i) == getPhraseOf(hole); i++) {
				holes.get(i).startOffset += h.getShift();
			}
			
			updateText();
		}
	}
	
	@Override
	public String toString() {
		return txt;
	}
	
}
