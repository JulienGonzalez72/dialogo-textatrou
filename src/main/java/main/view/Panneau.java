package main.view;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.List;
import java.util.Timer;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import main.Constants;
import main.controler.ControlerText;
import main.controler.Pilot;
import main.Parametres;
import main.controler.ControlerKey;
import main.controler.ControlerMask;
import main.model.Player;
import main.model.TextHandler;
import main.reading.ReaderAllHoleFF;

public class Panneau extends JDesktopPane {

	public static int premierSegment;
	public static int defautNBEssaisParSegment;

	// panneau du texte
	public TextPane editorPane;
	public TextHandler textHandler;
	public int pageActuelle;
	public int nbPages;
	public int nbEssaisParSegment = defautNBEssaisParSegment;
	public int nbEssaisRestantPourLeSegmentCourant = defautNBEssaisParSegment;
	public int nbErreurs;
	public Fenetre fenetre;
	public ControlPanel controlPanel;
	public ControlerText controlerGlobal;
	public ControlerKey controlerKey;
	public ControlerMask controlerMask;
	public Pilot pilot;
	public Map<Integer, List<Integer>> segmentsEnFonctionDeLaPage = new HashMap<Integer, List<Integer>>();
	public Player player;
	public FenetreParametre fenetreParam;
	public List<Mask> fenetreMasque = new ArrayList<>();
	public Parametres param;
	public ReaderAllHoleFF lecteur;
	public int numeroCourant = 0;
	public Mask fenetreFixe;
	int nbMotsDansLaPage;

	/**
	 * Barre de progression
	 */
	public JProgressBar progressBar;

	public JPanel panelSud;

	public Panneau(Fenetre fenetre, FenetreParametre fenetreParam, Parametres param) throws IOException {
		this.fenetre = fenetre;
		this.param = param;
		this.fenetreParam = fenetreParam;
		this.controlerGlobal = new ControlerText(this);
		this.controlerMask = new ControlerMask();
		this.fenetre = fenetre;
		this.pilot = new Pilot(this);
		String texteCesures = getTextFromFile("ressources/textes/" + Constants.TEXT_FILE_NAME);
		/// enlï¿½ve la consigne ///
		if (Constants.HAS_INSTRUCTIONS) {
			texteCesures = texteCesures.substring(texteCesures.indexOf("/") + 1, texteCesures.length());
		}
		textHandler = new TextHandler(texteCesures, param);

		if (!textHandler.oneHoleEqualOneWord()) {
			fenetreParam.pan.fixedField.setSelected(true);
			// fenetreParam.pan.fixedField.setText("Un seul mode disponible pour ce texte");
			fenetreParam.pan.fixedField.setEnabled(false);
		}

		this.setLayout(new BorderLayout());
		editorPane = new TextPane(param);
		editorPane.setEditable(false);
		add(editorPane);

		nbMotsDansLaPage = Panneau.stringOccur(textHandler.txt, " _");

		panelSud = new JPanel();

		progressBar = new JProgressBar(0, (textHandler.getHolesCount()));
		progressBar.setStringPainted(true);
		progressBar.setForeground(Color.GREEN);

		add(panelSud, BorderLayout.SOUTH);
		panelSud.setVisible(false);

	}

	public JDesktopPane panelFenetreFixe = null;

	/**
	 * S'exï¿½cute lorsque le panneau s'est bien intï¿½grï¿½ ï¿½ la fenï¿½tre.
	 */
	public void init() {
		param.appliquerPreferenceTaillePosition(fenetre);
		fenetreParam.editorPane = editorPane;
		updateBar(param.firstPhrase);
		editorPane.setBackground(param.bgColor);
		editorPane.setFont(param.police);
		pageActuelle = 0;

		/// construit la mise en page virtuelle ///
		rebuildPages();

		/// initialise le lecteur et le dï¿½marre ///
		player = new Player(textHandler, param);
		player.load(param.firstPhrase - 1);

		controlPanel = fenetreParam.controlPanel;
		fenetreParam.controlPanel.init();
		controlerKey = new ControlerKey(pilot);
		editorPane.addKeyListener(controlerKey);
		editorPane.requestFocus();
	}

	public void setCursor(String fileName) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = tk.getImage(fileName);
		Cursor monCurseur = tk.createCustomCursor(img, new Point(16, 16), fileName);
		setCursor(monCurseur);
	}

	public String getCursorName() {
		return getCursor().getName();
	}

	public void updateBar(int hole) {
		progressBar.setValue(hole + 1);
		progressBar.setString((hole + 1) + "/" + (textHandler.getHolesCount()));
	}

	/**
	 * retourne le contenu du fichier .txt situï¿½ ï¿½ l'emplacement du paramï¿½tre
	 */
	public static String getTextFromFile(String emplacement) throws IOException {
		File fichierTxt = new File(emplacement);
		InputStream ips = null;
		ips = new FileInputStream(fichierTxt);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String toReturn = "";
		String ligneCourante = br.readLine();
		while (ligneCourante != null) {
			toReturn += ligneCourante + " ";
			ligneCourante = br.readLine();
		}
		br.close();
		return toReturn;
	}

	/**
	 * passe a la page suivante et l'affiche
	 *
	 */
	public void afficherPageSuivante() {
		showPage(pageActuelle + 1);
	}

	/**
	 * Construit les pages et affiche la premiï¿½re.
	 */
	public void rebuildPages() {
		buildPages(param.firstPhrase - 1);
		pageActuelle = 0;
		afficherPageSuivante();
		/// calcule le nombre de pages total ///
		nbPages = segmentsEnFonctionDeLaPage.size();
	}

	public void afficherPagePrecedente() {
		if (pageActuelle > 0) {
			showPage(pageActuelle - 1);
		}
	}

	/**
	 * Construit les pages
	 */
	public void buildPages(int startPhrase) {
		segmentsEnFonctionDeLaPage.clear();
		String text = textHandler.getShowText();
		int lastOffset = 0;
		int page = 1;
		int lastPhrase = startPhrase - 1;
		while (lastPhrase < textHandler.getPhrasesCount()) {
			List<Integer> phrases = new ArrayList<>();
			editorPane.setText(text);
			int h = 0;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				h = editorPane.modelToView(0).height;
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
			}
			int off = textHandler.getAbsoluteOffset(lastPhrase, editorPane.viewToModel(new Point(
					(int) (editorPane.getWidth() - Constants.TEXTPANE_MARGING),
					(int) (editorPane.getHeight() - h - (param.fixedField ? panelFenetreFixe.getHeight() : 0)))));
			for (int i = lastOffset; i < off; i++) {
				int phraseIndex = textHandler.getPhraseIndex(i);
				if (phraseIndex == -1) {
					lastOffset = textHandler.getShowText().length();
				} else if (!phrases.contains(phraseIndex) && phraseIndex > lastPhrase
						&& phraseIndex != textHandler.getPhraseIndex(off)) {
					lastPhrase = phraseIndex;
					phrases.add(phraseIndex);
					lastOffset = i;
				}
			}
			if (!phrases.isEmpty()) {
				segmentsEnFonctionDeLaPage.put(page, phrases);
				page++;
			}

			String newText = textHandler.getShowText().substring(lastOffset);
			/// derniï¿½re page ///
			if (newText.equals(text)) {
				if (!segmentsEnFonctionDeLaPage.get(page - 1).contains(textHandler.getPhraseIndex(off))
						&& textHandler.getPhraseIndex(off) >= 0) {
					segmentsEnFonctionDeLaPage.get(page - 1).add(textHandler.getPhraseIndex(off));
				}
				break;
			} else {
				text = newText;
			}
		}
	}

	/**
	 * Va a la page numero page
	 * 
	 * @param page
	 */
	public void showPage(int page) {
		/// on ne fait rien si on est déjà sur cette page ///
		if (pageActuelle == page) {
			return;
		}

		pageActuelle = page;
		// mise a jour du titre de la fenêtre
		fenetre.setTitle("Lexidia - Texte à Trou - Page " + page);
		updateText();
	}

	public void updateText() {		
		String texteAfficher = "";
		// on recupere les segments a afficher dans la page
		List<String> liste = new ArrayList<String>();

		for (int i = 0; i < segmentsEnFonctionDeLaPage.get(pageActuelle).size(); i++) {
			int index = segmentsEnFonctionDeLaPage.get(pageActuelle).get(i);
			liste.add(textHandler.getPhrase(index));
		}
		for (int i = 0; i < liste.size(); i++) {
			texteAfficher += liste.get(i);
		}
		
		editorPane.setText(texteAfficher);

		if (editorPane.lastPhraseToHG != -1) {
			controlerGlobal.highlightUntilPhrase(param.rightColor, editorPane.lastPhraseToHG);
		}
	}

	public boolean correctSize() {
		List<Integer> lastPage = segmentsEnFonctionDeLaPage.get(segmentsEnFonctionDeLaPage.size());
		return lastPage.get(lastPage.size() - 1) == textHandler.getPhrasesCount() - 1;
	}

	public boolean pageFinis() {
		// la page actuelle contient t-elle le segment suivant ? si non elle est finis
		return (!segmentsEnFonctionDeLaPage.get(pageActuelle).contains(player.getCurrentPhraseIndex() + 1))
				|| player.getCurrentPhraseIndex() == textHandler.getPhrasesCount() - 1;
	}

	public int getFirstPhraseShowed() {
		return segmentsEnFonctionDeLaPage.get(pageActuelle).get(0);
	}

	public void afficherCompteRendu() {

		// desactivation des autres fenetres fixes
		if (true) {
			for (JInternalFrame j : getAllFrames()) {
				if (!(j instanceof Mask)) {
					j.dispose();
				}
			}
		}

		// met a jour la barre de progression
		updateBar(textHandler.getHolesCount());

		Object optionPaneBG = UIManager.get("OptionPane.background");
		Object panelBG = UIManager.get("Panel.background");
		try {
			UIManager.put("OptionPane.background", Color.WHITE);
			UIManager.put("Panel.background", Color.WHITE);
			String message = "L'exercice est terminï¿½." + "\n" + "Le patient a fait " + nbErreurs + " erreur"
					+ (nbErreurs > 1 ? "s" : "");
			JOptionPane.showMessageDialog(this, message, "Compte Rendu", JOptionPane.INFORMATION_MESSAGE);
		} finally {
			UIManager.put("OptionPane.background", optionPaneBG);
			UIManager.put("Panel.background", panelBG);
		}
		/// rï¿½active la taille et la police et le segment de dï¿½part
		fenetreParam.pan.fontFamilyComboBox.setEnabled(true);
		fenetreParam.pan.fontSizeComboBox.setEnabled(true);
		fenetreParam.pan.segmentDeDepart.setEnabled(true);
		fenetre.setResizable(true);
		fenetreParam.stopExercice();
	}

	/**
	 * Affiche une fenetre correspondant au mot dï¿½limitï¿½ par start et end, d'indice
	 * numeroCourant, et met le masque correspondant dans la liste des masques
	 */
	public void afficherFrame(int start, int end, int h) throws BadLocationException {
		Mask frame = new Mask(start, end, null);
		((javax.swing.plaf.basic.BasicInternalFrameUI) frame.getUI()).setNorthPane(null);
		frame.setBorder(null);

		setLayout(null);

		frame.initField(param.police.deriveFont(param.police.getSize() / 1.5f), controlerMask);

		frame.setVisible(true);
		frame.motCouvert = textHandler.getHidedWord(h);
		frame.page = controlerGlobal.getPageOf(h);
		frame.n = h;
		fenetreMasque.add(frame);

		Rectangle r = editorPane.modelToView(start).union(editorPane.modelToView(end));

		frame.setBounds(r.x, r.y, r.width, r.height / 2);
		add(frame);

		for (Component c : fenetre.pan.getComponents()) {
			if (c instanceof JInternalFrame) {
				((JInternalFrame) c).toFront();
			}
		}

	}

	public void blink(Color c) {
		Timer blinkTimer = new Timer();
		final long interval = 250;
		blinkTimer.scheduleAtFixedRate(new TimerTask() {
			private long time;

			public void run() {
				time += interval;
				if (time >= interval * 4) {
					editorPane.setBackground(param.bgColor);
					cancel();
					return;
				}
				if (time % (interval * 2) != 0)
					editorPane.setBackground(c);
				else
					editorPane.setBackground(param.bgColor);
			}
		}, 0, interval);
	}

	/**
	 * Renvoie le nombre d'occurrences de la sous-chaine de caractï¿½res spï¿½cifiï¿½e
	 * dans la chaine de caractï¿½res spï¿½cifiï¿½e
	 * 
	 * @param text
	 *            chaine de caractï¿½res initiale
	 * @param string
	 *            sous-chaine de caractï¿½res dont le nombre d'occurrences doit etre
	 *            comptï¿½
	 * @return le nombre d'occurrences du pattern spï¿½cifiï¿½ dans la chaine de
	 *         caractï¿½res spï¿½cifiï¿½e
	 */
	public static final int stringOccur(String text, String string) {
		return regexOccur(text, Pattern.quote(string));
	}

	/**
	 * Renvoie le nombre d'occurrences du pattern spï¿½cifiï¿½ dans la chaine de
	 * caractï¿½res spï¿½cifiï¿½e
	 * 
	 * @param text
	 *            chaine de caractï¿½res initiale
	 * @param regex
	 *            expression rï¿½guliï¿½re dont le nombre d'occurrences doit etre comptï¿½
	 * @return le nombre d'occurrences du pattern spï¿½cifiï¿½ dans la chaine de
	 *         caractï¿½res spï¿½cifiï¿½e
	 */
	public static final int regexOccur(String text, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(text);
		int occur = 0;
		while (matcher.find()) {
			occur++;
		}
		return occur;
	}

	/*
	 * replace une fenetre invisible, la rendnat visible
	 */
	public void replacerMasque(Mask frame) throws BadLocationException {
		try {
			int start = frame.start;
			int end = frame.end;
			Rectangle r = editorPane.modelToView(start).union(editorPane.modelToView(end));
			frame.setBounds(r.x, r.y, r.width, r.height / 2);
			frame.setVisible(true);
		} catch (Exception e) {
		}
	}

	public void replaceAllMask() {
		for (int i = 0; i < fenetreMasque.size(); i++) {
			if (fenetreMasque.get(i).isVisible()) {
				try {
					replacerMasque(fenetreMasque.get(i));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// donne le numero d'un masque
	public int getNumero(Mask m) {
		return fenetreMasque.indexOf(m);
	}

	public void removeAllMasks() {
		for (int i = 0; i < fenetreMasque.size(); i++) {
			Mask m = fenetreMasque.get(i);
			m.setVisible(false);
			remove(m);
		}
		fenetreMasque.clear();
	}

	/**
	 * Colorie tout jusqu'au segment n en couleur c
	 */
	public void surlignerJusquaSegment(Color c, int n) {
		if (textHandler.getPhrase(n) != null) {
			int debutRelatifSegment = textHandler.getRelativeStartPhrasePosition(getFirstPhraseShowed(), n);
			int finRelativeSegment = debutRelatifSegment + textHandler.getPhrase(n).length();
			editorPane.surlignerPhrase(0, finRelativeSegment, param.rightColor);
		}
	}

	public void updateHG(int n) {
		if (param.highlight) {
			editorPane.enleverSurlignageVert();
			editorPane.lastPhraseToHG = n;
			editorPane.repaint();
		}
	}

}
