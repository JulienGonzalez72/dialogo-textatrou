package main.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import main.Constants;
import main.Parametres;

public class TextPane extends JTextPane {

	private static final long serialVersionUID = 1L;

	private List<Object> greenHighlightTags = new ArrayList<>();

	public Parametres param;

	public String textReel;

	public int lastPhraseToHG = -1;

	public TextPane(Parametres param) {
		this.param = param;

		setFont(param.police);
		setSelectionColor(new Color(0, 0, 0, 0));

		/// mets les marges sur les cétés ///
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attrs, 1);
		StyleConstants.setSpaceAbove(attrs, Constants.TEXTPANE_MARGING);
		StyleConstants.setLeftIndent(attrs, Constants.TEXTPANE_MARGING);
		StyleConstants.setRightIndent(attrs, Constants.TEXTPANE_MARGING);
		getStyledDocument().setParagraphAttributes(0, 0, attrs, false);
	}

	/**
	 * surligne tout de début é fin avec la couleur spécifiée
	 *
	 */
	public void surlignerPhrase(int debut, int fin, Color couleur) {

		if (fin <= debut)
			return;
		try {
			Object tag = getHighlighter().addHighlight(debut, fin,
					new DefaultHighlighter.DefaultHighlightPainter(couleur));
			if (couleur.equals(param.rightColor)) {
				greenHighlightTags.add(tag);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	public void enleverSurlignageVert() {
		for (int i = 0; i < greenHighlightTags.size(); i++) {
			getHighlighter().removeHighlight(greenHighlightTags.get(i));
		}
		greenHighlightTags.clear();
	}



	/**
	 * Enlève tout le surlignage présent entre les bornes start et end.
	 */
	public void removeHighlight(int start, int end) {
		Highlight[] hl = getHighlighter().getHighlights();
		for (int i = 0; i < hl.length; i++) {
			if ((hl[i].getStartOffset() >= start && hl[i].getEndOffset() <= end)
					|| (hl[i].getEndOffset() >= start && hl[i].getEndOffset() <= end)) {
				getHighlighter().removeHighlight(hl[i]);
			}
		}
	}
	
}
