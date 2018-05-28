package main.view;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;

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
		param = Parametres.load();
		setIconImage(getToolkit().getImage("icone.jpg"));
		editorPane = null;
		setTitle(titre);
		if(param.sizeParamX > 0) {
			setSize(param.sizeParamX, param.sizeParamY);
		} else {
			setSize(tailleX, tailleY);
		}
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		pan = null;
		try {
			pan = new PanneauParam(this);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		fenetre = new Fenetre(param.title, param.panWidth, param.panHeight, this, param);
		fenetre.setLocation(param.panX, param.panY);
		controlPanel = new ControlPanel(fenetre.pan, this, param);

		JTabbedPane generalTab = new JTabbedPane();
		generalTab.addTab("Paramètres", pan);
		generalTab.addTab("Contrôle", controlPanel);
		setContentPane(generalTab);
		addMenu();
		setVisible(true);
		
		getRootPane().addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				param.sizeParamX = FenetreParametre.this.getWidth();
				param.sizeParamY = FenetreParametre.this.getHeight();
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

	}

	public class PanneauParam extends JPanel {

		private static final long serialVersionUID = 1L;

		public JComboBox<Object> fontFamilyComboBox;
		public JComboBox<Integer> fontSizeComboBox;
		public ColorComboBox bgColorComboBox;
		public ColorComboBox rightColorComboBox;
		public JTextField segmentDeDepart;
		public JButton valider;
		public JCheckBox replayPhrase;
		public JCheckBox fixedField;
		public JCheckBox oneHole;
		public JCheckBox surlignage;
		public JSlider waitSlider;
		public JSlider timeToShowWord;
		public final Object[] fontFamilies;
		public FenetreParametre fen;

		@SuppressWarnings("unchecked")
		public PanneauParam(FenetreParametre fen) throws NumberFormatException, IOException {
			this.fen = fen;
			setLayout(new BorderLayout());
			JLabel titre = fastLabel("Choisissez vos parametres");
			titre.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
			add(titre, BorderLayout.NORTH);
			
			ControleurParam controleur = new ControleurParam(fen, this);


			replayPhrase = fastCheckBox("Rejouer les phrases si erreur", controleur);
			valider = fastButton("Valider les parametres", new Font("OpenDyslexic", Font.BOLD, 18), Color.green);
			valider.addActionListener(controleur);
			JLabel police = fastLabel("Police : ");
			JLabel couleurSurlignage = fastLabel("Couleur de surlignage : ");
			JLabel taillePolice = fastLabel("Taille de la police : ");
			JLabel couleurDeFond = fastLabel("Couleur de fond : ");
			JLabel segments = fastLabel("Segment de départ : ");
			JLabel attente = fastLabel("Temps d'attente en % du temps de lecture");
			JLabel labelWordApparition = fastLabel("Temps d'apparition des mots par caracteres ( en ms )");

			fontFamilies = new Object[] { "OpenDyslexic", "Andika", "Lexia", "Arial", "Times New Roman" };
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

			fontSizeComboBox = new JComboBox<Integer>(Constants.FONT_SIZES);
			fontSizeComboBox.setRenderer(new ListCellRenderer<Object>() {
				private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

				public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					list.setFont(new Font(Font.DIALOG, Font.BOLD, (Integer) value));
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
			});
			fontSizeComboBox.addActionListener(controleur);
			fontSizeComboBox.setFont(new Font("OpenDyslexic", Font.PLAIN, 15));

			bgColorComboBox = new ColorComboBox(Constants.COLORS, true);
			bgColorComboBox.colorListener = new ColorComboBox.ColorChangeListener() {
				@Override
				public void colorChanged(Color newColor) {
					if (fenetre != null && fenetre.pan.editorPane != null) {
						fenetre.pan.editorPane.setBackground(newColor);
					}
					grabFocus();
				}
			};
			
			rightColorComboBox = new ColorComboBox(Constants.COLORS, true);
			rightColorComboBox.colorListener = new ColorComboBox.ColorChangeListener() {
				@Override
				public void colorChanged(Color newColor) {
					if (fenetre != null && fenetre.pan.editorPane != null) {
						param.rightColor = rightColorComboBox.getBackground();
						fenetre.pan.updateHG(fenetre.pan.pilot.getCurrentHoleIndex());
					}
					grabFocus();
				}
			};

		

			segmentDeDepart = fastTextField("1", new Font("OpenDyslexic", Font.PLAIN, 15), "1");
			segmentDeDepart.setEnabled(false);
			segments.setEnabled(false);
			segmentDeDepart.addActionListener(controleur);

			JPanel midPanel = new JPanel(new GridLayout(1, 2));
			JPanel midRightPanel = new JPanel(new GridLayout(7, 1));
			JPanel midLeftPanel = new JPanel(new GridLayout(7, 1));
			midPanel.add(midRightPanel);
			midPanel.add(midLeftPanel);

			// MID RIGHT
			midRightPanel.add(police);
			fastCentering(fontFamilyComboBox, midRightPanel, "   ");
			midRightPanel.add(taillePolice);
			fastCentering(fontSizeComboBox, midRightPanel, "   ");
			midRightPanel.add(couleurSurlignage);
			fastCentering(rightColorComboBox, midRightPanel, "   ");
			surlignage = fastCheckBox("Surlignage", controleur);
			surlignage.setSelected(false);
			JPanel temp3 = new JPanel();
			temp3.add(surlignage);
			midRightPanel.add(temp3);

			// MID LEFT
			midLeftPanel.add(couleurDeFond);
			fastCentering(bgColorComboBox, midLeftPanel, "   ");
			midLeftPanel.add(segments);
			fastCentering(segmentDeDepart, midLeftPanel, "   ");
			oneHole = fastCheckBox("Un trou par un trou ?", controleur);
			oneHole.setSelected(false);
			JPanel temp = new JPanel();
			temp.add(oneHole);
			midLeftPanel.add(temp);
			fixedField = fastCheckBox("Fenêtre de saisie fixe", controleur);
			fixedField.setSelected(false);
			temp = new JPanel();
			temp.add(fixedField);
			midLeftPanel.add(temp);
			temp = new JPanel();
			temp.add(replayPhrase);
			midLeftPanel.add(temp);
		
			

			waitSlider = new JSlider();
			waitSlider.setMaximum(Constants.MAX_WAIT_TIME_PERCENT);
			waitSlider.setMinimum(Constants.MIN_WAIT_TIME_PERCENT);
			waitSlider.setValue(Constants.DEFAULT_WAIT_TIME_PERCENT);
			waitSlider.setPaintTicks(true);
			waitSlider.setPaintLabels(true);
			waitSlider.setMinorTickSpacing(10);
			waitSlider.setMajorTickSpacing(50);
			waitSlider.addChangeListener(controleur);
			
			timeToShowWord = new JSlider();
			timeToShowWord.setMaximum(Constants.MAX_APPARITION_TIME);
			timeToShowWord.setMinimum(0);
			timeToShowWord.setValue(Constants.DEFAULT_TIME_APPARITION_BY_CARAC);
			timeToShowWord.setPaintTicks(true);
			timeToShowWord.setPaintLabels(true);
			timeToShowWord.setMinorTickSpacing(40);
			timeToShowWord.setMajorTickSpacing(200);
			Dictionary<Integer, JLabel> labels = timeToShowWord.getLabelTable();
			labels.put(Constants.MAX_APPARITION_TIME, fastLabel("Illimité"));
			timeToShowWord.setLabelTable(labels);
			timeToShowWord.addChangeListener(controleur);

			JPanel panelSud = new JPanel(new GridLayout(8, 1));

			panelSud.add(new JLabel());
			panelSud.add(add(attente));
			panelSud.add(waitSlider);
			panelSud.add(new JLabel());
			panelSud.add(add(labelWordApparition));
			panelSud.add(timeToShowWord);
			panelSud.add(new JLabel());
			panelSud.add(valider);

			add(midPanel, BorderLayout.CENTER);
			add(panelSud, BorderLayout.SOUTH);

			applyPreferences();
		}

		/**
		 * Applique les préférences chargées aux pré-sélections de la fenêtre de
		 * paramètres et à la fenêtre principale si elle existe.
		 */
		public void applyPreferences() {
			fontSizeComboBox.setSelectedItem(param.police.getSize());
			fontFamilyComboBox.setSelectedItem(getCorrectFontName(param.police.getFontName()));

			segmentDeDepart.setText("1");

			appliquerCouleur(param.bgColor, bgColorComboBox);
			appliquerCouleur(param.rightColor, rightColorComboBox);

			replayPhrase.setSelected(param.replayPhrase);
			fixedField.setSelected(param.fixedField);
			oneHole.setSelected(param.oneHole);
			surlignage.setSelected(param.highlight);

			waitSlider.setValue(param.timeToWaitToLetStudentRepeat);
			timeToShowWord.setValue(param.timeToShowWord);
		}

		/**
		 * Enregistre les préférences en fonction de la sélection de l'utilisateur.
		 */
		public void savePreferences() {	
			param.replayPhrase = replayPhrase.isSelected();
			param.fixedField = fixedField.isSelected();
			param.oneHole = oneHole.isSelected();
			param.highlight = surlignage.isSelected();
			param.rightColor = rightColorComboBox.getBackground();
			param.bgColor = bgColorComboBox.getBackground();
			param.police = param.police.deriveFont(Float.valueOf((Integer) fontSizeComboBox.getSelectedItem()));
			param.police = ControleurParam.getFont((String) fontFamilyComboBox.getSelectedItem(),
					fontFamilyComboBox.getSelectedIndex(), Font.BOLD, (Integer) fontSizeComboBox.getSelectedItem());
			try {
				param.firstPhrase = Integer.parseInt(segmentDeDepart.getText());
			} catch (NumberFormatException e) {
			}
			param.timeToWaitToLetStudentRepeat = waitSlider.getValue();
			param.timeToShowWord = ( timeToShowWord.getValue() != Constants.MAX_APPARITION_TIME ) ? param.timeToShowWord = timeToShowWord.getValue() : -1;
		}

		private void appliquerCouleur(Color color, ColorComboBox listeCouleurs) {
			listeCouleurs.selectColor(color);
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
			r.addActionListener(controleur);
			r.setBackground(Constants.BG_COLOR);
			r.setFont(new Font("OpenDyslexic", Font.PLAIN, 15));
			return r;
		}

		public JRadioButton fastRadio(String nom, ControleurParam controleur) {
			JRadioButton r = new JRadioButton(nom);
			r.setFont(new Font("OpenDyslexic", Font.ITALIC, 15));
			r.addActionListener(controleur);
			r.setVerticalTextPosition(JRadioButton.TOP);
			r.setHorizontalTextPosition(JRadioButton.CENTER);
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

		Panneau.premierSegment = param.firstPhrase;

		if (param.fixedField) {
			fenetre.pan.panelSud.setLayout(new BorderLayout());
			fenetre.pan.panelFenetreFixe = new JDesktopPane();
			fenetre.pan.panelFenetreFixe.setPreferredSize(new Dimension(fenetre.getWidth(), param.police.getSize()));
			fenetre.pan.panelSud.add(fenetre.pan.panelFenetreFixe);
			fenetre.pan.panelSud.add(fenetre.pan.progressBar, BorderLayout.SOUTH);
		} else {
			fenetre.pan.panelSud.setLayout(new GridLayout(1, 1));
			fenetre.pan.panelSud.add(fenetre.pan.progressBar);
		}
		
		updateOptionsEnabled(false);

		fenetre.pan.panelSud.setVisible(true);
		fenetre.start();

	}

	private void updateOptionsEnabled(boolean etat) {
		pan.segmentDeDepart.setEnabled(etat);
		pan.oneHole.setEnabled(etat);
		pan.surlignage.setEnabled(etat);
		pan.fixedField.setEnabled(etat);
		pan.replayPhrase.setEnabled(etat);
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
		editorPane.lastPhraseToHG = -1;
		updateOptionsEnabled(true);
		//TODO : reparer les bugs de redimentionnement des fenetres arrivant apres un stop exercice
		//en attendant on empeche ce redimentionnement
		fenetre.setResizable(false);
		//Ligne ci dessus = a supprimer quand le todo  ci dessus sera fait
		
		
		fenetre.pan.nbErreurs = 0;
		fenetre.pan.numeroCourant = 0;
		
		eMenuItem2.setEnabled(false);
		fenetre.setVisible(false);
		controlPanel.disableAll();
		fenetre.pan.pilot.doStop();
		fenetre.pan.pilot.initialiseHole();
	}

	public static Color stringToColor(String name) {
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

	public static String getCorrectFontName(String font) {
		String[] deletions = { " Bold", " Basic", " Gras", " Italic" };
		for (int i = 0; i < deletions.length; i++) {
			font = font.replaceAll(deletions[i], "");
		}
		return font;
	}

}
