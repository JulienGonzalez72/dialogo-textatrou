package main.reading;

public enum ReadMode {

	/**
	 * Tous les segments passés sont surlignés en vert, l'utilisateur peut donc voir
	 * le début du segment qu'il doit répéter.
	 */
	SUIVI,

	/**
	 * La lecture se fait automatiquement en surlignant en vert le segment en train
	 * d'être lu.<br>
	 * Aucun clic n'est pris en compte dans ce mode.
	 */
	GUIDEE,

	/**
	 * Aucun surlignage n'indique au patient sur quel segment il se trouve, seules
	 * les erreurs sont surlignées.
	 */
	SEGMENTE,

	/**
	 * Lecture anticipée
	 */
	ANTICIPE;

	public static ReadMode parse(String s) {
		ReadMode mode = null;
		switch (s) {
		case "GUIDEE":
			mode = ReadMode.GUIDEE;
			break;
		case "SUIVI":
			mode = ReadMode.SUIVI;
			break;
		case "SEGMENTE":
			mode = ReadMode.SEGMENTE;
			break;
		case "ANTICIPE":
			mode = ReadMode.ANTICIPE;
			break;
		}
		return mode;
	}

	public static String stringer(ReadMode r) {
		String temp = "";
		switch (r) {
		case GUIDEE:
			temp = "GUIDEE";
			break;
		case SUIVI:
			temp = "SUIVI";
			break;
		case SEGMENTE:
			temp = "SEGMENTE";
			break;
		case ANTICIPE:
			temp = "ANTICIPE";
			break;
		}
		return temp;
	}

}
