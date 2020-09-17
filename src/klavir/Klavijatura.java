package klavir;

import kompozicija.*;
import sviranje.*;
import konvertori.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map.Entry;

import javax.sound.midi.MidiUnavailableException;

public class Klavijatura extends Canvas implements KeyListener, Runnable, MouseListener{

	private MidiPlayer mp;
	private Kompozicija kompozicija = null;
	private boolean snimase = false;
	private ArrayList<MuzickiSimbol> muzsimb = new  ArrayList<MuzickiSimbol>();
	private ArrayList<TekstualniSimbol> txtsimb = new ArrayList<TekstualniSimbol>();
	private Mapa mapa = new Mapa("F:\\etf\\2. godina\\4. semestar\\poop\\projekat java\\input\\map.csv");
	private String [] noteObicne = {"C", "D", "E", "F", "G", "A", "B"};
	private String [] notePovisene = {"C#", "D#", " ", "F#", "G#", "A#", " "};
	private int [] oktave = {2, 3, 4, 5, 6};
	private String pritisnuto = "";
	private ArrayList<String> pritisnutoAkord = new ArrayList<String>();
	private long startTime;
	private long endTime;
	private boolean radi = false, kraj = false;
	
	public Klavijatura() {
		try {
			mp = new MidiPlayer();
		} catch (MidiUnavailableException e) {
			//??
			System.err.println("greska midi plejer u klavijaturi.");
		}
		mapa.ucitajMapu();
		setSize(1410, 200);
		setBackground(Color.GRAY);
		repaint();
		setVisible(true);
	}

	public void dodajKompoziciju(Kompozicija k) {
		kompozicija = k;
		muzsimb = k.dohvMuzSimbole();
		txtsimb = k .dohvTxtSimbole();
	}
	
	/**
	 * Iscrtavanje klavijature.
	 * Krece se od pozicije (0, 0). 
	 * Bele dirke su sirine 40px i visine 200px.
	 * Crne dirke su sirine 20px i visine 100px. 
	 */
	@Override
	public void paint(Graphics g) {
		final int WHITE_KEY_WIDTH = 40;
		final int WHITE_KEY_HEIGHT = 200;
		final int BLACK_KEY_WIDTH = WHITE_KEY_WIDTH / 2;
		final int BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT / 2;
		
		final int NUMBER_OF_WHITE_KEYS = 7;
		final int NUMBER_OF_BLACK_KEYS = 5;
		final int NUMBER_OF_OCTAVES = 5;
		
		int x = 0, y = 0;
		//crtanje belih dirki
		for(int o = 0; o < NUMBER_OF_OCTAVES; o++) { //5 puta iscrtavamo jedan isti set dirki 
			for (int w = 0; w < NUMBER_OF_WHITE_KEYS; w++) {
				int oktava = o + 2;
				String nota = noteObicne[w] + oktava; 
				
				if (pritisnuto.equals(nota))
					g.setColor(Color.RED); //pritisnutu dirku iscrtava crveno
				else 
					g.setColor(Color.WHITE);
				
				//dodato
				if (pritisnutoAkord != null) {
					for (int j = 0; j < pritisnutoAkord.size(); j++) {
						if (pritisnutoAkord.get(j).equals(nota)) {
							g.setColor(Color.RED);
							break;
						}
					}
				}
				//dodato
				
				g.fillRect(x, y, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
				g.setColor(Color.DARK_GRAY);
				g.drawRect(x, y, WHITE_KEY_WIDTH, WHITE_KEY_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawString(nota, x + 15, y + 120);
				g.drawString(nadjiSlovoZaNotu(nota), x + 15, y + 170);
				x += WHITE_KEY_WIDTH;
			}			
			 
		}
		
		x = 0; x += WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2;
		//crtanje crnih dirki
		for(int o = 0; o < NUMBER_OF_OCTAVES; o++) { // 5 puta jedan isti set dirki
			for(int b = 0; b < notePovisene.length; b++) {
				if (notePovisene[b] == " ") {
					x += WHITE_KEY_WIDTH;
					continue;
				}
				int oktava = o + 2;
				String nota = notePovisene[b] + oktava;
				
				if (pritisnuto.equals(nota))
					g.setColor(Color.RED); //pritisnutu dirku iscrtava crveno
				else 
					g.setColor(Color.BLACK);
				
				//dodato
				if (pritisnutoAkord != null) {
					for (int j = 0; j < pritisnutoAkord.size(); j++) {
						if (pritisnutoAkord.get(j).equals(nota)) {
							g.setColor(Color.RED);
							break;
						}
					}
				}
				//dodato
				
				g.fillRect(x, y, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
				g.setColor(Color.WHITE);
				g.drawString(nota, x, y + 50);
				g.drawString(nadjiSlovoZaNotu(nota), x + 7, y + 70);
				x += WHITE_KEY_WIDTH; pritisnutoAkord = null;
			}
		}
		
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		int midibr = nadjiMidiZaSlovo(String.valueOf(e.getKeyChar()));
		if (midibr != -1) {
			mp.play(midibr);
			pritisnuto = nadjiNotuZaSlovo(String.valueOf(e.getKeyChar())); 
			if (snimase) {
				startTime = System.currentTimeMillis();
			}
			repaint();
		}
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
		int midibr = nadjiMidiZaSlovo(String.valueOf(e.getKeyChar()));
		if (midibr != -1) {
			mp.release(midibr);
			if (snimase) {
				endTime = System.currentTimeMillis();
				long proteklo = endTime - startTime;
				String visina; int oktava;
				if (pritisnuto.contains("#")) {
					visina = String.valueOf(pritisnuto.charAt(0));
					visina += "#";
					oktava = Integer.parseInt(String.valueOf(pritisnuto.charAt(2)));
				}else {
					visina = String.valueOf(pritisnuto.charAt(0));
					oktava = Integer.parseInt(String.valueOf(pritisnuto.charAt(1)));
				}
				if (proteklo > 200) {
					muzsimb.add(new Nota(1, 4, oktava, visina));
					txtsimb.add(new TekstualniSimbol(String.valueOf(e.getKeyChar()), new Trajanje(1, 4)));
				}
				else if (proteklo > 100) {
					muzsimb.add(new Nota(1, 8, oktava, visina));
					txtsimb.add(new TekstualniSimbol(String.valueOf(e.getKeyChar()), new Trajanje(1, 8)));
				}else if (proteklo < 100) {
					muzsimb.add(new Nota(0, 4, oktava, visina));
					txtsimb.add(new TekstualniSimbol(String.valueOf(e.getKeyChar()), new Trajanje(0, 4)));
				}		
				
				
			}
		}
		pritisnuto = "";
		repaint();
	}


	@Override
	public void keyTyped(KeyEvent e) { 
		//nista
	}
	
	public String nadjiSlovoZaNotu(String n) {
		for (Entry<String, Par<String, Integer>> ulaz : mapa.dohv().entrySet()) {
			if (Objects.equals(n, ulaz.getValue().first())) {
				return ulaz.getKey();
			}
		}
		return null;
	}
	
	public int nadjiMidiZaSlovo(String s) {
		if (mapa.dohv().containsKey(s)) {
			return mapa.dohv().get(s).second();
		}
		else
			return -1; 
	}
	
	public String nadjiNotuZaSlovo(String s) {
		if (mapa.dohv().containsKey(s))
			return mapa.dohv().get(s).first();
		else
			return null;
	}
	
	public void snimaj() {
		muzsimb = null; txtsimb = null;
		muzsimb = new ArrayList<MuzickiSimbol>();
		txtsimb = new ArrayList<TekstualniSimbol>();
		snimase = true;
	}
	
	public Kompozicija zavrsiSnimanje() {
		snimase = false;
		if (kompozicija != null)
			kompozicija.stvoriKompoziciju(muzsimb, txtsimb);
		else {
			kompozicija = new Kompozicija(mapa, "Snimljena kompozicija", "");
			kompozicija.stvoriKompoziciju(muzsimb, txtsimb);
		}
		for(int i = 0; i < txtsimb.size(); i++)
			System.out.println(txtsimb.get(i).dohvOznaku()+ " trajanje " + txtsimb.get(i).dohvTrajanje().toString());
		return kompozicija; 
	}

	@Override
	public void run() {
		try {
			while(!Thread.interrupted() && !kraj) {
				synchronized (this) {
					if (!radi)
						wait();
				}
				for (int i = 0; i < muzsimb.size(); ) {
					synchronized (this) {
						if (!radi)
							wait();
					}
					MuzickiSimbol elem = muzsimb.get(i);
					long length;//predstavlja duzinu muzickog simbola
					//u slucaju da muz.simb. traje 1/4, length iznosi 400
					//u slucaju da muz.simb. traje 1/8, length iznosi 200
					if (Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(1, 4)))
						length = 400;
					else 
						length = 200;
					if (elem instanceof Nota) {
						if ((Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(1, 4))) && (!elem.dohvatiFlagIstovremeno())) {
							//u pitanju je pojedinacna nota trajanja 1/4
							pritisnuto = ((Nota) elem).dohvVisinu();
							pritisnuto += ((Nota) elem).dohvOktavu(); 
							repaint(); 
							Thread.sleep(length);	
							i++;
						}else if((Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(1, 4))) && (elem.dohvatiFlagIstovremeno())) {
							//u pitanju je prva nota akorda
							pritisnutoAkord = null; pritisnutoAkord = new ArrayList<String>();
							pritisnuto = ((Nota) elem).dohvVisinu();
							pritisnuto += ((Nota) elem).dohvOktavu();
							pritisnutoAkord.add(pritisnuto);
							pritisnuto = "";
							i++; 
							elem = muzsimb.get(i);
							//moramo da sakupimo sve note koje cine akord u jednu listu
							while (Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(0, 4))) {
								pritisnuto = ((Nota) elem).dohvVisinu();
								pritisnuto += ((Nota) elem).dohvOktavu();
								pritisnutoAkord.add(pritisnuto);
								pritisnuto = "";
								if ((i + 1) < muzsimb.size()) {
									i++;
									elem = muzsimb.get(i);
								}else {
									i++;
									break;
								}
							}
							repaint();
							Thread.sleep(length);	
						}else{
							//u pitanju je nota koja traje 1/8 
							pritisnuto = ((Nota) elem).dohvVisinu();
							pritisnuto += ((Nota) elem).dohvOktavu();
							repaint(); 
							Thread.sleep(length);
							i++;
						}
						
					}else {
						//u pitanju je pauza
						repaint();
						Thread.sleep(length); 
						i++;
					}
				}
				pritisnuto = "";
				repaint();
				zaustavi();
			}
		}catch(InterruptedException e) {
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
	
	public synchronized void zaustavi() {
		kraj = true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// nista
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// nista
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// nista
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Mouse pressed na koordinati " + e.getX() + ", " + e.getY());
		int x, y;
		x = e.getX(); 
		y = e.getY(); 
		
		int oktava;
		if (x < 280) {
			oktava = 2;
		}else if (x < 560) {
			oktava = 3;
		}else if (x < 840) {
			oktava = 4;
		}else if (x < 1120) {
			oktava = 5;
		}else {
			oktava = 6;
		}
		
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released na koordinati " + e.getX() + ", " + e.getY());
		
	}
	
	
	
	
}
