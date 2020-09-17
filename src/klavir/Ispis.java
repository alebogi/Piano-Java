package klavir;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import kompozicija.*;
import sviranje.*;

public class Ispis extends Canvas implements Runnable{

	private boolean radi = false;
	private boolean kraj = false;
	private Klavir klavir;
	private boolean nacinIspisa; //true za notni ispis, false za txt ispis
	private ArrayList<MuzickiSimbol> msLista;
	private ArrayList<TekstualniSimbol> tsLista;
	private int tek = 0;
	private int maxIscrt = 0;
	public static final Color LIGHT_RED = new Color(255, 51, 51);
	public static final Color LIGHT_GREEN = new Color(0, 255, 51);
	public StringBuilder akord = new StringBuilder();
	public int visinaAkorda = 0;
	
	public Ispis(Klavir k) {
		klavir = k;
		nacinIspisa = k.nacinIspisa();
		msLista = k.dohvKompoziciju().dohvMuzSimbole();
		tsLista = k.dohvKompoziciju().dohvTxtSimbole();
		setBounds(10, 70, 800, 200); 
		setBackground(Color.GRAY);
		setVisible(true);
	}
	
	/**
	 * Iscrtavaju se note.
	 * Nota trajanja 1/4 zauzima 50px, tako su i markeri na razdaljini od 50px.
	 * Nota trajanja 1/8 zauzima 25px.
	 * Visina pravougaonika note inace je 30px.
	 * Visina pravougaonika akorda je 20px * visinaAkorda, gde visinaAkorda predstavlja broj nota akorda.
	 * Visina markera je 15px.
	 * Note akorda se pisu jedna ispod druge.
	 * Note trajanja 1/4 su crvene boje.
	 * Note trajanja 1/8 su zelene boje.
	 * Pauze su svetlijih nijansi boja od nota.
	 */
	@Override
	public void paint(Graphics g) {
		int x = 10, y = 10, x1 =  10, y1 = 90; 
		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke());
		
		for (int i = 0; i < maxIscrt; i++) { //crtanje markera
			g.drawLine(x1, y1, x1, y1 + 15);
			x1 += 50;
		}
		
		for(int i = tek; (maxIscrt > 0); i++, maxIscrt--) {	
			if ((Trajanje.suJednaki(msLista.get(i).dohvatiTrajanje(), new Trajanje(1, 4))) && (!msLista.get(i).dohvatiFlagIstovremeno())) {
				//u pitanju je pojedinacna nota trajanja 1/4
				
				//crtanje pravougaonika
				if (msLista.get(i) instanceof Pauza) {
					g.setColor(LIGHT_RED);
				}else {
					g.setColor(Color.RED);
				}
				g.drawRect(x, y, 50, 30);
				g.fillRect(x, y, 50, 30);
				
				//ispis teksta unutar pravougaonika
				if (nacinIspisa) { //ako je true ispisujemo note, krecemo se po listi msLista
					g.setColor(Color.BLACK);
					g.drawString(msLista.get(i).toString(), x + 12, y + 10); 
				}else { //ako je false ispisuje se tekst, krecemo se po listi tsLista
					g.setColor(Color.BLACK);
					g.drawString(tsLista.get(i).toString(), x + 12, y + 10); 
				}
			
				x += 50; //pomeramo se za sirinu 1/4
			}else if((Trajanje.suJednaki(msLista.get(i).dohvatiTrajanje(), new Trajanje(1, 4))) && (msLista.get(i).dohvatiFlagIstovremeno())) {
				//u pitanju je prva nota akorda
				
				visinaAkorda = 1; akord = null; akord = new StringBuilder();
				
				//sastavljanje stringa koji sadrzi sve note akorda
				if (nacinIspisa) { //ako je true ispisujemo note, krecemo se po listi msLista
					
					//prolazak kroz listu i ispis svih nota akorda
					akord.append(msLista.get(i).toString());
					i++; 
					while (Trajanje.suJednaki(msLista.get(i).dohvatiTrajanje(), new Trajanje(0, 4))) {
						akord.append("\n");
						akord.append(msLista.get(i).toString());
						visinaAkorda++;
						if ((i + 1) < msLista.size()) {
							i++; // tek++; 																			//dodato
						}else {
							break;
						}
					}
					i--; //tek--;																						//dodato
				}else { //ako je false ispisujemo tekst, krecemo se po listi tsLista
					
					
					//prolazak kroz listu i ispis svih nota akorda
					akord.append(tsLista.get(i).toString());
					i++;
					while (Trajanje.suJednaki(tsLista.get(i).dohvTrajanje(), new Trajanje(0, 4))) {
						akord.append("\n");
						akord.append(tsLista.get(i).toString());
						visinaAkorda++;
						if ((i + 1) < tsLista.size()) {
							i++; tek++;																						//dodato
						}else {
							break;
						} 
					}
					i--;// tek--;																						//dodato
				}
				
				//crtanje pravougaonika
				if (msLista.get(i) instanceof Pauza) {
					g.setColor(LIGHT_RED);
				}else {
					g.setColor(Color.RED);
				}
				g.drawRect(x, y, 50, 30 * visinaAkorda); 
				g.fillRect(x, y, 50, 30 * visinaAkorda); 
				
				//ispis teksta
				g.setColor(Color.BLACK);
				drawStringAkord(g, akord.toString(), x + 12, y);
				
				x += 50; //pomeramo se za sirinu 1/4
			}else{
				//u pitanju je nota koja traje 1/8 
				
				//crtanje pravougaonika
				if (msLista.get(i) instanceof Pauza) {
					g.setColor(LIGHT_GREEN);
				}else {
					g.setColor(Color.GREEN);
				}
				g.drawRect(x, y, 25, 30);
				g.fillRect(x, y, 25, 30);
				
				//ispis teksta unutar pravougaonika
				if (nacinIspisa) { //ako je true ispisujemo note, krecemo se po listi msLista
					g.setColor(Color.BLACK);
					g.drawString(msLista.get(i).toString(), x + 5, y + 10); 
				}else { //ako je false ispisuje se tekst, krecemo se po listi tsLista
					g.setColor(Color.BLACK);
					g.drawString(tsLista.get(i).toString(), x + 5, y + 10); 
				}
			
				x += 25; //pomeramo se za sirinu 1/8
				
			}
		}
		tek++;
		
	} 

	@Override
	public void run() {
		try {
			while(!Thread.interrupted() && !kraj) {
				synchronized (this) {
					if (!radi)
						wait();
				}
				if (maxIscrt == 0) {
					if ((msLista.size() - tek) < 10)
						maxIscrt = msLista.size() - tek - 1;
					else
						maxIscrt = 10;
				}
				repaint();
				if (tek < msLista.size()) {
					if (Trajanje.suJednaki(msLista.get(tek).dohvatiTrajanje(), new Trajanje(1, 4))) {
						Thread.sleep((long)420); //400
					}else if (Trajanje.suJednaki(msLista.get(tek).dohvatiTrajanje(), new Trajanje(1, 8))) {
						Thread.sleep((long)210);
					}
				}
			}
		}catch(InterruptedException ie) {
			//??
		}
		
	}
	
	public synchronized void kreni() {
		radi = true;
		notifyAll();
	}
	
	public synchronized void pauziraj() {
		radi = false;
	}
	
	public synchronized void zavrsi() {
		radi = false;
		kraj = true;
	}
	
	public void drawStringAkord(Graphics g, String s, int x, int y) {
	    for (String line : s.split("\n")) {
	    	 g.drawString(line, x, y += g.getFontMetrics().getHeight());
	    }
	}

}
