package main.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.*;
import javax.swing.JTextPane;
import javax.swing.text.*;
import javax.swing.text.Highlighter.*;
import main.Constants;
import main.Parametres;

public class TextPane extends JTextPane {

	private static final long serialVersionUID = 1L;

	private List<Object> greenHighlightTags = new ArrayList<>();
	
	public Parametres param;

	public String textReel;

	public TextPane(Parametres param) {
		this.param = param;

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

	/**
	 * surligne tout de début à fin avec la couleur spécifiée
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
	 * desurligne tout
	 *
	 */
	public void désurlignerTout() {
		getHighlighter().removeAllHighlights();
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

	public Rectangle getTextBounds(String str) {
		return getFont().createGlyphVector(getFontMetrics(getFont()).getFontRenderContext(), str).getPixelBounds(null,
				0, 0);
	}

	public float getSpacingFactor() {
		FontMetrics fm = getFontMetrics(getFont());
		return (float) (1f + fm.getHeight() / getTextBounds("|").getHeight());
	}

	public void updateColors() throws BadLocationException {
		List<Object> newGreen = new ArrayList<>();
		for (Object object : greenHighlightTags) {
			Highlight g = (Highlight) object;
			object = getHighlighter().addHighlight(g.getStartOffset(), g.getEndOffset(),
					new DefaultHighlighter.DefaultHighlightPainter(param.rightColor));
			newGreen.add(object);
		}
		enleverSurlignageVert();
		greenHighlightTags = newGreen;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}
