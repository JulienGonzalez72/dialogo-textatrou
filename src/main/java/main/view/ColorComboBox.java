package main.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import main.Constants;

public class ColorComboBox extends JComboBox<String> {
	
	public ColorChangeListener colorListener;
	
	private final Map<String, Color> colors;
	private boolean canPerform = true;
	
	public ColorComboBox(Map<String, Color> colors, boolean showMoreColors) {
		this.colors = colors;
		
		setModel(new DefaultComboBoxModel<>(getColorNames()));
		setFont(new Font("OpenDyslexic", Font.PLAIN, 15));
		setRenderer(new ColorCellRenderer());
		
		if (showMoreColors) {
			addItem(Constants.SHOW_MORE_COLORS_TEXT);
		}
		
		updateBackground(stringToColor((String) getSelectedItem()));
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = stringToColor((String) getSelectedItem());
				if (c == null && canPerform) {
					ColorSelectionPanel csp = new ColorSelectionPanel();
					JOptionPane.showMessageDialog(null, csp, "Sélecteur de couleur", JOptionPane.QUESTION_MESSAGE);
					c = csp.getColor();
				}
				if (c != null) {
					updateBackground(c);
					if (colorListener != null) {
						colorListener.colorChanged(c);
					}
				}
			}
		});
	}
	
	public ColorComboBox(Map<String, Color> colors) {
		this(colors, false);
	}
	
	private void updateBackground(Color color) {
		setBackground(color);
		setForeground(getForeground(color));
	}
	
	private class ColorCellRenderer implements ListCellRenderer<Object> {
		private DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		private static final float NORMAL_FONT_SIZE = 14;
		private static final float SELECTED_FONT_SIZE = 20;
		public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			list.setBackground(stringToColor((String) value));
			list.setSelectionBackground(list.getBackground());
			list.setForeground(getForeground(list.getBackground()));
			list.setSelectionForeground(list.getForeground());
			list.setFont(list.getFont()
					.deriveFont(isSelected ? SELECTED_FONT_SIZE : NORMAL_FONT_SIZE)
					.deriveFont(isSelected ? Font.BOLD : Font.PLAIN));
			renderer.setHorizontalAlignment(SwingConstants.CENTER);
			renderer.setPreferredSize(new Dimension(0, Constants.COMBOBOX_CELL_HEIGHT));
			return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}
	
	private Color stringToColor(String name) {
		return colors.get(name);
	}
	
	private String[] getColorNames() {
		return colors.keySet().toArray(new String[0]);
	}
	
	public Color getSelectedColor() {
		return stringToColor((String) getSelectedItem());
	}
	
	public void selectColor(Color color) {
		canPerform = false;
		String s = colorToString(color);
		setSelectedItem(s != null ? s : Constants.SHOW_MORE_COLORS_TEXT);
		updateBackground(color);
		canPerform = true;
	}
	
	private String colorToString(Color color) {
		Set<String> keys = colors.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (stringToColor(key).equals(color)) {
				return key;
			}
		}
		return null;
	}
	
	private class ColorSelectionPanel extends JPanel {
		private ColorComboBox colors = new ColorComboBox(Constants.MORE_COLORS);
		public ColorSelectionPanel() {
			add(new JLabel("Sélectionnez une couleur :"));
			colors.setPreferredSize(new Dimension(150, 30));
			add(colors);
		}
		public Color getColor() {
			return colors.getSelectedColor();
		}
	}
	
	private static Color getForeground(Color bgColor) {
		float luminosity = bgColor.getRed() + bgColor.getGreen() * 1.5f + bgColor.getBlue();
		return luminosity < 382.5 ? Color.WHITE : Color.BLACK;
	}
	
	public interface ColorChangeListener {
		void colorChanged(Color newColor);
	}
	
}
