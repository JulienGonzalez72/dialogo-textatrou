package main.view;
import javax.swing.*;


public class Mask extends JInternalFrame {

	public int start;
	public int end;
	public JTextField jtf;
	public int page;
	public String motCouvert;
	

	public Mask() {
	}

	public Mask(int start, int end, JTextField jtf) {
		this.start = start;
		this.end = end;
		this.jtf = jtf;
	}
	


}
