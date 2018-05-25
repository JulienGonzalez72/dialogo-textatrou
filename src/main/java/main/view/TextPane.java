package main.view;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JTextPane;
import javax.swing.text.*;

import main.Constants;
import main.Parametres;

public class TextPane extends JTextPane {

	private static final long serialVersionUID = 1L;
	
	public TextPane(Parametres param) {
		setFont(param.police);
		setSelectionColor(new Color(0, 0, 0, 0));

		/// mets les marges sur les côtés ///
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attrs, 1);
		StyleConstants.setSpaceAbove(attrs, Constants.TEXTPANE_MARGING);
		StyleConstants.setLeftIndent(attrs, Constants.TEXTPANE_MARGING);
		StyleConstants.setRightIndent(attrs, Constants.TEXTPANE_MARGING);
		getStyledDocument().setParagraphAttributes(0, 0, attrs, false);
	}




	/*@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}*/

}
