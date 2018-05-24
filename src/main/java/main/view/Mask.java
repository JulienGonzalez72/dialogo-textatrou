package main.view;

import java.util.Comparator;

import javax.swing.*;


public class Mask extends JInternalFrame {

	public Object lock = new Object();
	public int start;
	public int end;
	public JTextField jtf;
	public int page;
	/**
	 * numéro du trou
	 */
	public int n;
	public int phrase;
	public String motCouvert;
	

	public Mask() {
	}

	public Mask(int start, int end, JTextField jtf) {
		this.start = start;
		this.end = end;
		this.jtf = jtf;
	}
	
	public void activate() {
		jtf.setEnabled(true);
		jtf.grabFocus();
	}
	
	public void desactivate() {
		jtf.setEnabled(false);
	}
	
	/**
	 * Retourne true si le mot rentré par l'utilisateur correspond bien au mot caché.
	 */
	public boolean correctWord() {
		return jtf.getText().equals(motCouvert);
	}

	public static class PositionComparator implements Comparator<Mask> {
		@Override
		public int compare(Mask m1, Mask m2) {
			return m1.start - m2.start;
		}
		
	}
	
	public String toString(){
		return motCouvert+" ( "+start+"/"+end+" )";
	}


}
