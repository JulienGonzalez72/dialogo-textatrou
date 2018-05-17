package main.view;

import java.util.Comparator;

import javax.swing.*;


public class Mask extends JInternalFrame {

	public int start;
	public int end;
	public JTextField jtf;
	public int page;
	public int phrase;
	public String motCouvert;
	

	public Mask() {
	}

	public Mask(int start, int end, JTextField jtf) {
		this.start = start;
		this.end = end;
		this.jtf = jtf;
	}
	

	public static class PositionComparator implements Comparator<Mask> {
		@Override
		public int compare(Mask m1, Mask m2) {
			return m1.start - m2.start;
		}
		
	}


}
