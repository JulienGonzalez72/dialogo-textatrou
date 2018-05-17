package main.model;

import main.view.Panneau;

public class Lecteur extends Thread {

	Panneau p;
	public Object lock = new Object();
	public boolean notified;
	public boolean needToDead = false;
	public int segmentDeDepart;

	public Lecteur(Panneau p,int segmentDeDepart) {
		this.p = p;
		this.segmentDeDepart = segmentDeDepart;
	}

	public void run() {

		for (int i = segmentDeDepart; i < p.textHandler.getPhrasesCount() - 1; i++) {
			for (int j = 0; j < p.textHandler.motsParSegment.get(i).size(); j++) {
				
				if(needToDead) {
					System.out.println("THREAD MORT");
					return;
				}

				System.out.println();
				System.out.println("Numero courant " + p.numeroCourant+" /// Segment courant "+i);

				try {

					if (p.changementSegment()) {
						p.pilot.goTo(p.pilot.getCurrentPhraseIndex());
					}

					while (p.textHandler.motsParSegment.get(p.pilot.getCurrentPhraseIndex()).isEmpty()) {
						p.pilot.phrase++;
						p.pilot.goTo(p.pilot.getCurrentPhraseIndex());
					}
				} catch (Exception e) {}

				p.pilot.showHole(p.numeroCourant);

				System.out.println("THREAD mis en pause");

				synchronized (lock) {
					if (!notified) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
						}
					}
				}
				notified = false;

				System.out.println("THREAD repartis");
	
				if(needToDead) {
					System.out.println("THREAD MORT");
					return;
				}
				
				System.out.println();
			}
		}
		p.afficherCompteRendu();

	}

}
