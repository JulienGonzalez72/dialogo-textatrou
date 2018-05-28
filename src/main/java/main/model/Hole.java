package main.model;

import java.util.ArrayList;
import java.util.List;

public class Hole {
	
	private enum State {HIDDEN, FILLED}
	
	private State state;
	private String word;
	
	/**
	 * Position de début dans le segment
	 */
	public int startOffset;
	
	public Hole(String word) {
		this.word = word;
		this.state = State.HIDDEN;
	}
	
	public void fill() {
		state = State.FILLED;
	}
	
	public void hide() {
		state = State.HIDDEN;
	}
	
	public boolean isHidden() {
		return state == State.HIDDEN;
	}
	
	private int holeLength() {
		return word.length();
		//return word.length() + 3 - word.length() % 3;
	}
	
	public String getHidedWord() {
		return word;
	}
	
	public int length() {
		return state == State.FILLED ? word.length() : holeLength();
	}
	
	/**
	 * Retourne la différence de longueur entre le trou caché et le mot.
	 */
	public int getShift() {
		return holeLength() - word.length();
	}
	
	@Override
	public String toString() {
		String str = "";
		switch (state) {
			case HIDDEN :
				for (int i = 0; i < holeLength(); i++) {
					str += '_';//'\u00a0';
				}
				break;
			case FILLED :
				str = word;
				break;
		}
		return str;
	}
	
	public static List<String> getHidedWords(List<Hole> holes) {
		List<String> words = new ArrayList<>();
		for (int i = 0; i < holes.size(); i++) {
			words.add(holes.get(i).getHidedWord());
		}
		return words;
	}
	
}
