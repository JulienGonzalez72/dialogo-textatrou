package main.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import main.Constants;
import main.Parametres;
import main.controler.ControleurParam;

public class FenetreParametre extends JFrame {

	public Fenetre fenetre;
	public Parametres param;
	public TextPane editorPane;
	public ControlPanel controlPanel;
	public PanneauParam pan;

	public FenetreParametre(String titre, int tailleX, int tailleY) {
		param = new Parametres();
		setIconImage(getToolkit().getImage("icone.jpg"));
		param.police = ControleurParam.getFont(null, 0, Font.BOLD, Constants.DEFAULT_FONT_SIZE);
		param.taillePolice = Constants.DEFAULT_FONT_SIZE;
		param.couleurFond = Constants.BG_COLOR;
		editorPane = null;
		param.titre = titre;
		param.tailleX = tailleX;
		param.tailleY = tailleY;
		param.mysterCarac = '_';
		setTitle(titre);
		setSize(tailleX, tailleY);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		pan = null;
		try {
			pan = new PanneauParam(this);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		fenetre = new Fenetre(param.titre, param.tailleX * 2, param.tailleY, this, param);
		controlPanel = new ControlPanel(fenetre.pan, this, param);

		JTabbedPane generalTab = new JTabbedPane();
		generalTab.addTab("Paramètres", pan);
		generalTab.addTab("Contrôle", controlPanel);
		setContentPane(generalTab);
		addMenu();
		setVisible(true);

	}

	public class PanneauParam extends JPanel {

		private static final long serialVersionUID = 1L;

		public JPanel panelModes;
		public JComboBox<Object> fontFamilyComboBox;
		public JComboBox<Object> fontSizeComboBox;
		public JComboBox<Object> colorComboBox;
		public JComboBox<Object> rightColorComboBox;
		public JComboBox<Object> wrongColorComboBox;
		public JComboBox<Object> correctionColorComboBox;
		public JTextField segmentDeDepart;
		public JTextField champMysterCarac;
		public JButton valider;
		public JCheckBox rejouerSon;
		public JSlider sliderAttente;
		public final Object[] fontFamilies;
		public final Object[] fontSizes;
		public FenetreParametre fen;

		public PanneauParam(FenetreParametre fen) throws NumberFormatException, IOException {
			this.fen = fen;
			setLayout(new BorderLayout());
			JLabel titre = fastLabel("Choisissez vos parametres");
			titre.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
			add(titre, BorderLayout.NORTH);

			valider = fastButton("Valider les parametres", new Font("OpenDyslexic", Font.BOLD, 18), Color.green);
			JLabel police = fastLabel("Police : ");
			JLabel taillePolice = fastLabel("Taille de la police : ");
			JLabel couleurDeFond = fastLabel("Couleur de fond : ");
			JLabel couleurJuste = fastLabel("Couleur pour \"juste\" : ");
			JLabel couleurFausse = fastLabel("Couleur pour \"faux\" : ");
			JLabel couleurCorrection = fastLabel("Couleur de correction : ");
			JLabel segments = fastLabel("Segment de départ ");
			JLabel attente = fastLabel("Temps d'attente en % du temps de lecture");

			fontFamilies = new Object[] { "OpenDyslexic", "Andika", "Lexia", "Arial", "Times New Roman" };
			fontSizes = new Object[] { "12", "16", "18", "20", "22", "24", "30", "36", "42" };

			ControleurParam controleur = new ControleurParam(fen, this);
			valider.addActionListener(controleur);

			fontFamilyComboBox = new JComboBox<Object>(fontFamilies);
			fontFamilyComboBox.setRenderer(new ListCellRenderer<Object>() {
				private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

				public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					list.setFont(
							ControleurParam.getFont((String) value, index, Font.BOLD, Constants.DEFAULT_FONT_SIZE));
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
			});
			fontFamilyComboBox.setFont(ControleurParam.getFont((String) fontFamilyComboBox.getSelectedItem(), 0,
					Font.BOLD, Constants.DEFAULT_FONT_SIZE));
			fontFamilyComboBox.addActionListener(controleur);

			fontSizeComboBox = new JComboBox<Object>(fontSizes);
			fontSizeComboBox.setRenderer(new ListCellRenderer<Object>() {
				private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

				public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					list.setFont(new Font(Font.DIALOG, Font.BOLD, Integer.parseInt((String) value)));
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
			});
			fontSizeComboBox.addActionListener(controleur);
			fontSizeComboBox.setFont(new Font("OpenDyslexic", Font.PLAIN, 15));

			colorComboBox = fastComboBox(controleur, getColorNames());
			colorComboBox.setRenderer(new ColorCellRenderer());
			rightColorComboBox = fastComboBox(controleur, getColorNames());
			rightColorComboBox.setRenderer(new ColorCellRenderer());
			wrongColorComboBox = fastComboBox(controleur, getColorNames());
			wrongColorComboBox.setRenderer(new ColorCellRenderer());
			correctionColorComboBox = fastComboBox(controleur, getColorNames());
			correctionColorComboBox.setRenderer(new ColorCellRenderer());

			segmentDeDepart = fastTextField(String.valueOf(param.premierSegment),
					new Font("OpenDyslexic", Font.PLAIN, 15), "1");
			segmentDeDepart.addActionListener(controleur);

			JLabel mysterCarac = fastLabel("Caractere à trouver");
			champMysterCarac = fastTextField("", new Font("OpenDyslexic", Font.PLAIN, 15), "_");
			champMysterCarac.addActionListener(controleur);

			JPanel midPanel = new JPanel(new GridLayout(10, 2));

			midPanel.add(police);
			midPanel.add(taillePolice);
			fastCentering(fontFamilyComboBox, midPanel, "   ");
			fastCentering(fontSizeComboBox, midPanel, "   ");

			midPanel.add(segments);
			midPanel.add(mysterCarac);
			fastCentering(segmentDeDepart, midPanel, "   ");
			fastCentering(champMysterCarac, midPanel, "   ");

			midPanel.add(couleurDeFond);
			midPanel.add(couleurJuste);
			fastCentering(colorComboBox, midPanel, "   ");
			fastCentering(rightColorComboBox, midPanel, "   ");

			midPanel.add(couleurFausse);
			midPanel.add(couleurCorrection);
			fastCentering(wrongColorComboBox, midPanel, "   ");
			fastCentering(correctionColorComboBox, midPanel, "   ");

			LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
			JComboBox<Object> lfBox = fastComboBox(controleur, new Object[0]);
			for (int i = 0; i < lfs.length; i++) {
				lfBox.addItem(lfs[i].getName());
				if (UIManager.getLookAndFeel().getName().equals(lfs[i].getName())) {
					lfBox.setSelectedIndex(i);
				}
			}
			lfBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					try {
						UIManager.setLookAndFeel(lfs[lfBox.getSelectedIndex()].getClassName());
						SwingUtilities.updateComponentTreeUI(FenetreParametre.this);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException ex) {
						ex.printStackTrace();
					}
				}
			});
			midPanel.add(fastLabel("Look And Feel"));
			midPanel.add(new JLabel());
			fastCentering(lfBox, midPanel, "   ");

			sliderAttente = new JSlider();
			sliderAttente.setMaximum(Constants.MAX_WAIT_TIME_PERCENT);
			sliderAttente.setMinimum(Constants.MIN_WAIT_TIME_PERCENT);
			sliderAttente.setValue(Constants.DEFAULT_WAIT_TIME_PERCENT);
			sliderAttente.setPaintTicks(true);
			sliderAttente.setPaintLabels(true);
			sliderAttente.setMinorTickSpacing(10);
			sliderAttente.setMajorTickSpacing(50);
			sliderAttente.addChangeListener(controleur);

			JPanel panelSud = new JPanel(new GridLayout(8, 1));
			panelSud.add(new JLabel());
			rejouerSon = fastCheckBox("Rejouer les phrases si erreur", controleur);
			rejouerSon.setSelected(true);
			JPanel temp = new JPanel();
			temp.add(rejouerSon);
			panelSud.add(temp);

			panelSud.add(new JLabel());
			panelSud.add(new JLabel());
			panelSud.add(add(attente));
			panelSud.add(sliderAttente);
			panelSud.add(new JLabel());
			panelSud.add(valider);

			add(midPanel, BorderLayout.CENTER);
			add(panelSud, BorderLayout.SOUTH);

			chargerPreferences();

		}

		public void chargerPreferences() throws NumberFormatException, IOException {
			String fichier = "./ressources/preferences/preference_" + Constants.NOM_ELEVE + ".txt";
			InputStream ips = null;
			try {
				ips = new FileInputStream(fichier);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Properties pro = new Properties();
			try {
				pro.load(ips);
			} catch (IOException e) {
				e.printStackTrace();
			}

			int t = Integer.valueOf(pro.getProperty("taillePolice"));
			if (t == 12) {
				fontSizeComboBox.setSelectedItem(fontSizes[0]);
			}
			if (t == 16) {
				fontSizeComboBox.setSelectedItem(fontSizes[1]);
			}
			if (t == 18) {
				fontSizeComboBox.setSelectedItem(fontSizes[2]);
			}
			if (t == 20) {
				fontSizeComboBox.setSelectedItem(fontSizes[3]);
			}
			if (t == 22) {
				fontSizeComboBox.setSelectedItem(fontSizes[4]);
			}
			if (t == 24) {
				fontSizeComboBox.setSelectedItem(fontSizes[5]);
			}
			if (t == 30) {
				fontSizeComboBox.setSelectedItem(fontSizes[6]);
			}
			if (t == 36) {
				fontSizeComboBox.setSelectedItem(fontSizes[7]);
			}
			if (t == 42) {
				fontSizeComboBox.setSelectedItem(fontSizes[8]);
			}

			int index = -1;
			String p = pro.getProperty("typePolice");
			if (p.equals("OpenDyslexic") || p.equals("OpenDyslexic Bold")) {
				index = 0;
			}
			if (p.equals("Andika") || p.equals("Andika Basic")) {
				index = 1;
			}
			if (p.equals("Lexia")) {
				index = 2;
			}
			if (p.equals("Arial") || p.equals("Arial Gras")) {
				index = 3;
			}
			if (p.equals("Times New Roman") || p.equals("Times New Roman Gras")) {
				index = 4;
			}
			fontFamilyComboBox.setSelectedItem(fontFamilies[index]);

			appliquerCouleur(param.fromStringToColor(pro.getProperty("couleurFond")), colorComboBox);
			appliquerCouleur(param.fromStringToColor(pro.getProperty("couleurBonne")), rightColorComboBox);
			appliquerCouleur(param.fromStringToColor(pro.getProperty("couleurFausse")), wrongColorComboBox);
			appliquerCouleur(param.fromStringToColor(pro.getProperty("couleurCorrection")), correctionColorComboBox);

			int temp = Integer.valueOf(pro.getProperty("tempsAttente"));
			param.tempsPauseEnPourcentageDuTempsDeLecture = temp;
			sliderAttente.setValue(temp);

			rejouerSon.setSelected(Boolean.valueOf(pro.getProperty("rejouerSon")));
		}

		private void appliquerCouleur(Color color, JComboBox<Object> listeCouleurs) {
			listeCouleurs.setSelectedItem(colorToString(color));
		}

		private class ColorCellRenderer implements ListCellRenderer<Object> {
			private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

			public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				list.setBackground(stringToColor((String) value));
				list.setFixedCellHeight(Constants.COMBOBOX_CELL_HEIGHT);
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		}

		public void fermer() {
			fen.setVisible(false);
		}

		final Font defaultFont = new Font("OpenDyslexic", Font.ITALIC, 16);

		public JLabel fastLabel(String nom, Font font) {
			JLabel r = new JLabel(nom);
			r.setFont(font);
			r.setHorizontalAlignment(JLabel.CENTER);
			return r;
		}

		public JLabel fastLabel(String nom) {
			JLabel r = new JLabel(nom);
			r.setFont(defaultFont);
			r.setHorizontalAlignment(JLabel.CENTER);
			return r;
		}

		public JComboBox<Object> fastComboBox(ControleurParam controleur, Object[] elements) {
			JComboBox<Object> r = new JComboBox<Object>(elements);
			/*try {
				((JLabel) r.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
			} catch (Exception e) {
<<<<<<< HEAD
				// TODO: handle exception
			}*/
=======
			}
>>>>>>> f1247f1e93be56de0e8f353c06f3eb0a22afe99f
			r.addActionListener(controleur);
			r.setBackground(Constants.BG_COLOR);
			r.setFont(new Font("OpenDyslexic", Font.PLAIN, 15));
			return r;
		}

		private JCheckBox fastCheckBox(String nom, ControleurParam controleur) {
			JCheckBox r = new JCheckBox(nom);
			r.setFont(new Font("OpenDyslexic", Font.ITALIC, 15));
			r.addActionListener(controleur);
			r.setVerticalTextPosition(JRadioButton.TOP);
			r.setHorizontalTextPosition(JRadioButton.CENTER);
			return r;
		}

		public JTextField fastTextField(String nom, Font font, String texteParDefaut) {
			JTextField r = new JTextField(nom);
			r.setHorizontalAlignment(JButton.CENTER);
			r.setFont(font);
			r.setText(texteParDefaut);
			return r;
		}

		public JButton fastButton(String nom, Font font, Color color) {
			JButton r = new JButton(nom);
			r.setHorizontalAlignment(JButton.CENTER);
			r.setBackground(color);
			r.setFont(font);
			return r;
		}

		/**
		 * Centre le composant c dans le panneau p
		 * 
		 * @param marge
		 *            = taille de la marge
		 */
		private void fastCentering(Component c, JPanel p, String marge) {
			JPanel temp = new JPanel(new BorderLayout());
			temp.add(new JLabel(marge), BorderLayout.WEST);
			temp.add(c, BorderLayout.CENTER);
			temp.add(new JLabel(marge), BorderLayout.EAST);
			p.add(temp);
		}

	}

	public void lancerExercice() {
		Panneau.premierSegment = param.premierSegment;
		Panneau.defautNBEssaisParSegment = param.mysterCarac;
		fenetre.start();
	}

	public JMenuItem eMenuItem2;

	private void addMenu() {
		JMenuBar menubar = new JMenuBar();

		JMenu file = new JMenu("Options");

		JMenuItem eMenuItem = new JMenuItem("Quitter");
		eMenuItem.setToolTipText("Quitter l'application");
		eMenuItem.setMnemonic(KeyEvent.VK_Q);
		eMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		eMenuItem.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		eMenuItem2 = new JMenuItem("Arrêter l'exercice");
		eMenuItem2.setMnemonic(KeyEvent.VK_R);
		eMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		eMenuItem2.addActionListener((ActionEvent event) -> {
			/// réactive la taille et la police et le segment de départ
			pan.fontFamilyComboBox.setEnabled(true);
			pan.fontSizeComboBox.setEnabled(true);
			pan.segmentDeDepart.setEnabled(true);
			pan.fen.fenetre.setResizable(true);
			stopExercice();
		});
		file.add(eMenuItem2);
		eMenuItem2.setEnabled(false);
		file.add(eMenuItem);
		menubar.add(file);
		setJMenuBar(menubar);
	}

	public void stopExercice() {
		fenetre.pan.nbErreurs = 0;
		fenetre.pan.numeroCourant = 0;
		param.appliquerPreference(this, fenetre.pan);
		eMenuItem2.setEnabled(false);
		fenetre.setVisible(false);
		controlPanel.disableAll();
		fenetre.pan.pilot.doStop();
	}

	public static Color stringToColor(String name) {
		/*
		 * if (name.equalsIgnoreCase("blanc")) return Color.WHITE; if
		 * (name.equalsIgnoreCase("bleu")) return Color.BLUE; if
		 * (name.equalsIgnoreCase("cyan")) return Color.CYAN; if
		 * (name.equalsIgnoreCase("jaune")) return Color.YELLOW; if
		 * (name.equalsIgnoreCase("orange")) return Color.ORANGE; if
		 * (name.equalsIgnoreCase("rose")) return Color.PINK; if
		 * (name.equalsIgnoreCase("rouge")) return Color.RED; if
		 * (name.equalsIgnoreCase("vert")) return Color.GREEN;
		 */
		return Constants.COLORS.get(name);
	}

	public static String colorToString(Color color) {
		Set<String> keys = Constants.COLORS.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (stringToColor(key).equals(color)) {
				return key;
			}
		}
		return null;
	}

	public static String[] getColorNames() {
		return Constants.COLORS.keySet().toArray(new String[0]);
	}

}
