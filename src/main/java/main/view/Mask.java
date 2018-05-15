package main.view;

import javax.swing.JInternalFrame;
import javax.swing.JTextField;

public class Mask extends JInternalFrame{
	
	public int start;
	public int end;
	public JTextField jtf;
	
	public Mask() {
		
	}
	
	public Mask(int start, int end,JTextField jtf) {
		this.start= start;
		this.end = end;
		this.jtf =jtf;
	}

}
