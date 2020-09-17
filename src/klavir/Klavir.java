/**
 * Copyrights (C) 2019 by Aleksandra Bogicevic
 * 
 * @author Aleksandra Bogicevic
 * @version 1.0
 * 
 * Univerzitet u Beogradu, Elektrotehnicki fakultet
 * Projektni zadatak iz Praktikuma iz objektno orijentisanog programiranja
 * Godina 2018/19
 * 
 * Virtual piano
 * 
 * !!! NAPOMENA !!
 * Putanje koje treba izmeniti:
 * Klasa Klavir, polje mapa.
 * Klasa Klavir, metoda dodajMeni(), dodatne info, putanja do slike.
 * Klasa Klavijatura, polje mapa.
 */

package klavir;

import kompozicija.*;
import konvertori.*;
import sviranje.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

public class Klavir extends Frame {

	private Kompozicija kompozicija = null;
	private AutomatskoSviranje automatskoSviranje;
	private boolean svira_automatski = false;
	private boolean svira_rucno = false;
	private boolean eksportovano = false;
	private boolean notniZapis = true;
	private boolean txtZapis = false;
	private Mapa mapa = new Mapa("F:\\etf\\2. godina\\4. semestar\\poop\\projekat java\\input\\map.csv");
	private Ispis prikaz;
	private Thread nitPrikaza = null;
	private Klavijatura klavijatura;
	private Thread nitKlavijature = null;
	private IspisRucnoSviranje prikazRucno;
	
	public Klavir() {
		super("Virtual piano");
		setBounds(50, 200, 1410, 500);
		setResizable(false); 
		mapa.ucitajMapu();
		klavijatura = new Klavijatura(); klavijatura.addKeyListener(klavijatura);  klavijatura.addMouseListener(klavijatura);
		dodajMeni();
		popuniProzor();
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				//pitati da li smo sigurni

				Frame f = new Frame(); // !pomocni frame za dijaloge
				if (!eksportovano) {
					int opcija = JOptionPane.showConfirmDialog(
						    f, "Da li zelite da eksportujete fajl?", "Pre izlaska...", JOptionPane.YES_NO_OPTION);
					if(opcija == JOptionPane.YES_OPTION) {
						//eksportovanje
						Object[] options = {"Txt file", "Midi file", "Oba"};
						opcija = JOptionPane.showOptionDialog(f, "Kako zelite da eksportujete fajl?", "Eksportovanje",
							    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
						
						
						String s = (String)JOptionPane.showInputDialog(f, "Unesite putanju za eksportovanje:",
								"Eksportovanje...", JOptionPane.PLAIN_MESSAGE, null, null, null);

						String putanja = "";
						if ((s != null) && (s.length() > 0)) {
							putanja = s;
							if (kompozicija != null) {
								if (opcija == JOptionPane.YES_OPTION) {
									TxtKonvertor tk = new TxtKonvertor(kompozicija, putanja + ".txt");
									tk.eksportuj();
									eksportovano = true;
								}else if (opcija == JOptionPane.NO_OPTION) {
									MidiKonvertor mk = new MidiKonvertor(kompozicija, putanja + ".mid");
									mk.eksportuj();
									eksportovano = true;
								}else if(opcija == JOptionPane.CANCEL_OPTION) {
									TxtKonvertor tk = new TxtKonvertor(kompozicija, putanja + ".txt");
									tk.eksportuj();
									MidiKonvertor mk = new MidiKonvertor(kompozicija, putanja + ".mid");
									mk.eksportuj();
									eksportovano = true;
								} 
							}
							else {
								JOptionPane.showMessageDialog(f, "Kompozicija nije ni ucitana.",
									    "Zasto si uopste kliknuo eksportuj?", JOptionPane.WARNING_MESSAGE);
							}
							JOptionPane.showMessageDialog(f, "Sada napustate program.",
								    "Dovidjenja!", JOptionPane.WARNING_MESSAGE);
							if (automatskoSviranje != null)
								if (automatskoSviranje.isAlive())
									automatskoSviranje.zaustavi();
							//nesto za nit ispisa??
							if (nitPrikaza != null)
								if (nitPrikaza.isAlive()) {
									prikaz.zavrsi();
									nitPrikaza.interrupt();
								}
							//nesto za nit klavijature
							if (nitKlavijature != null) {
								if (nitKlavijature.isAlive()) {
									klavijatura.zaustavi();
									nitKlavijature.interrupt();
								}
							}
							f.dispose();
							dispose();
						}else {
							JOptionPane.showMessageDialog(f, "Prazna putanja!",
								    "Prati uputstva i ne pravi greske!!!", JOptionPane.WARNING_MESSAGE);
							f.dispose(); 
						}
						
						
					}else {
						if (automatskoSviranje != null)
							if (automatskoSviranje.isAlive())
								automatskoSviranje.zaustavi();
						//nesto za nit ispisa??
						if (nitPrikaza != null)
							if (nitPrikaza.isAlive()) {
								prikaz.zavrsi();
								nitPrikaza.interrupt();
							}
						//nesto za nit klavijature
						if (nitKlavijature != null) {
							if (nitKlavijature.isAlive()) {
								klavijatura.zaustavi();
								nitKlavijature.interrupt();
							}
						}
						f.dispose();
						dispose();
					}
				}else {
					if (automatskoSviranje != null)
						if (automatskoSviranje.isAlive())
							automatskoSviranje.zaustavi();
					//nesto za nit ispisa??
					if (nitPrikaza != null)
						if (nitPrikaza.isAlive()) {
							prikaz.zavrsi();
							nitPrikaza.interrupt();
						}
					//nesto za nit klavijature
					if (nitKlavijature != null) {
						if (nitKlavijature.isAlive()) {
							klavijatura.zaustavi();
							nitKlavijature.interrupt();
						}
					}
					f.dispose();
					dispose();
				}
			}
			
		});
		setVisible(true);
	}
	
	/**
	 * Pravi GUI meni.
	 */
	public void dodajMeni() {
		Frame fp = new Frame(); //pomocni frame za dijaloge
		
		MenuBar traka = new MenuBar();
		setMenuBar(traka);
		
		Menu meni = new Menu("File");
		traka.add(meni);
		
		MenuItem stavka = new MenuItem("Ucitaj kompoziciju za automatsko sviranje"); 			//automatsko sviranje
		stavka.setShortcut(new MenuShortcut('A'));
		stavka.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Frame f = new Frame();
				Dialog dialog = new Dialog(f, "Ucitavanje kompozicije", true);
				dialog.setLayout(new BorderLayout());
				
				Panel centar = new Panel();
				centar.setLayout(new GridLayout(2, 2, 4, 2));
				
				Label l = new Label("Naziv kompozicije:");
				centar.add(l);
				TextField ime = new TextField();
				ime.setSize(100, 10);
				centar.add(ime);
				l = new Label("Putanja do kompozicije:");
				centar.add(l);
				TextField putanja = new TextField();
				putanja.setSize(100, 10);
				centar.add(putanja);
				
				dialog.add(centar, "Center");
				
				Button b = new Button("Ucitaj");
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						kompozicija = null; prikaz = null; nitPrikaza = null;
						kompozicija = new Kompozicija(mapa, ime.getText(), putanja.getText());
						try {
							kompozicija.ucitajKompoziciju();
							
							//thread
					//		prikaz = new Ispis(Klavir.this);
					//		nitPrikaza = new Thread(prikaz);
					//		Klavir.this.add(prikaz, "Center");
					//		nitPrikaza.start();
							//thread
							
							JOptionPane.showMessageDialog(fp, "Kompozicija je ucitana", "Info", JOptionPane.PLAIN_MESSAGE);
							fp.dispose();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(fp, "Neuspesno ucitavanje!", "Greska", JOptionPane.ERROR_MESSAGE);
							fp.dispose();
						}
					}
				});
				
				dialog.add(b, "South");
				dialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						dialog.dispose();
						f.dispose();
					}
					
				});
				dialog.setBounds(300, 300, 300, 200);
				dialog.setVisible(true);
			}
		});
		meni.add(stavka);
		
		stavka = new MenuItem("Ucitaj kompoziciju za rucno sviranje");            //rucno sviranje
		stavka.setShortcut(new MenuShortcut('R'));
		stavka.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Frame f = new Frame();
				Dialog dialog = new Dialog(f, "Ucitavanje kompozicije", true);
				dialog.setLayout(new BorderLayout());
				
				Panel centar = new Panel();
				centar.setLayout(new GridLayout(2, 2, 4, 2));
				
				Label l = new Label("Naziv kompozicije:");
				centar.add(l);
				TextField ime = new TextField();
				ime.setSize(100, 10);
				centar.add(ime);
				l = new Label("Putanja do kompozicije:");
				centar.add(l);
				TextField putanja = new TextField();
				putanja.setSize(100, 10);
				centar.add(putanja);
				
				dialog.add(centar, "Center");
				
				Button b = new Button("Ucitaj");
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						kompozicija = null; prikaz = null; nitPrikaza = null;
						kompozicija = new Kompozicija(mapa, ime.getText(), putanja.getText());
						try {
							kompozicija.ucitajKompoziciju();
							JOptionPane.showMessageDialog(fp, "Kompozicija je ucitana", "Info", JOptionPane.PLAIN_MESSAGE);
							fp.dispose();
							prikazRucno = new IspisRucnoSviranje(Klavir.this);
							prikazRucno.iscrtaj(); 
//POKRENUTI NIT							
						} catch (IOException e) {
							JOptionPane.showMessageDialog(fp, "Neuspesno ucitavanje!", "Greska", JOptionPane.ERROR_MESSAGE);
							fp.dispose();
						}
						
					}
				});
				
				
				
				dialog.add(b, "South");
				dialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						dialog.dispose();
						f.dispose();
					}
					
				});
				dialog.setBounds(300, 300, 300, 200);
				dialog.setVisible(true);
				
			}
		});
		meni.add(stavka);
		
		meni = new Menu("View"); 													//meni za izbor prikaza nota
		traka.add(meni);
		
		Menu submenu = new Menu("Prikaz nota...");
		meni.add(submenu);
		
		stavka = new MenuItem("kao slova");
		stavka.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				txtZapis = true; notniZapis = false;
			}
		});
		submenu.add(stavka);
		
		stavka = new MenuItem("kao note");
		stavka.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				notniZapis = true; txtZapis = false;
			}
		});
		submenu.add(stavka);
		
		//dodatne info
		meni = new Menu("Help");
		traka.add(meni);
		stavka = new MenuItem("About");
		stavka.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Frame f = new Frame();
				JDialog dialog = new JDialog(f, "About", true);
				dialog.setLayout(new BorderLayout());
				ImageIcon slika = new ImageIcon("F:\\etf\\2. godina\\4. semestar\\poop\\projekat java\\dodatak\\about.png");
				JLabel label = new JLabel();
				label.setIcon(slika);
				dialog.add(label, "Center");
				JButton b =new JButton("OK");
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						dialog.dispose();
						f.dispose();
					}
				});
				dialog.add(b, "South");
				
				
				dialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						dialog.dispose();
						f.dispose();
					}
					
				});
				dialog.setResizable(false);
				f.setResizable(false);
				dialog.setBounds(100, 100, 600, 350);
				dialog.setVisible(true);
				
			}
		}); 
		meni.add(stavka);
	}
	
	
	public void popuniProzor() {
		Frame fp = new Frame(); // pomocni frame za dijaloge
		
		Panel glIstok = new Panel(new BorderLayout());
		
		Panel centar = new Panel(new GridLayout(2, 3));
		Panel jug = new Panel(new FlowLayout());
		
		Button play = new Button("PLAY");
		play.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (kompozicija == null) {
					JOptionPane.showMessageDialog(fp, "Kompozicija nije ucitana!", "Greska", JOptionPane.WARNING_MESSAGE);
					fp.dispose();
					return;
				}
				if (!svira_automatski)
					if (automatskoSviranje != null) {
						svira_automatski = true;
						automatskoSviranje.kreni();
						prikaz.kreni();
						klavijatura.kreni(); //dodato
					}else {
						automatskoSviranje = new AutomatskoSviranje(kompozicija);
						svira_automatski = true;
						automatskoSviranje.kreni();
						//prikaz.kreni();
						//thread
						prikaz = new Ispis(Klavir.this);
						nitPrikaza = new Thread(prikaz);
						Klavir.this.add(prikaz, "Center");
						nitPrikaza.start();
						prikaz.kreni();
						//thread
						
						//dodato
						klavijatura.dodajKompoziciju(kompozicija);
						nitKlavijature = new Thread(klavijatura);
						nitKlavijature.start();
						klavijatura.kreni();
					}
			}
		});
		Button stop = new Button("STOP");
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (kompozicija == null) {
					JOptionPane.showMessageDialog(fp, "Kompozicija nije ucitana!", "Greska", JOptionPane.WARNING_MESSAGE);
					nitPrikaza = null;
					fp.dispose();
					return;
				}
				if (automatskoSviranje != null) {
						automatskoSviranje.zaustavi();
						automatskoSviranje = null;
						svira_automatski = false;
						prikaz.zavrsi();
						nitPrikaza.interrupt();
						prikaz = null;
						nitPrikaza = null;
						//dodato
						klavijatura.zaustavi();
						nitKlavijature.interrupt();
						nitKlavijature = null;
				}
						
			}
		});
		Button pause = new Button("PAUSE");
		pause.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (kompozicija == null) {
					JOptionPane.showMessageDialog(fp, "Kompozicija nije ucitana!", "Greska", JOptionPane.WARNING_MESSAGE);
					fp.dispose();
					return;
				}
				if (svira_automatski)
					if (automatskoSviranje != null) {
						automatskoSviranje.pauziraj();
						svira_automatski = false;
						prikaz.pauziraj();
						klavijatura.pauziraj();
					}
			}
		});
		
		Button recordStart = new Button("START REC");
		recordStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				klavijatura.snimaj();
			}
		});
		Button recordEnd = new Button("STOP REC");
		recordEnd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				kompozicija = klavijatura.zavrsiSnimanje(); 
			}
		});
		
		Panel p1 = new Panel(new GridLayout(2, 1));
		CheckboxGroup grupa = new CheckboxGroup();
		Checkbox rbAutomatsko = new Checkbox("Automatsko sviranje", true, grupa);
		Checkbox rbRucno = new Checkbox("Rucno sviranje", false, grupa);
		rbRucno.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (rbRucno.getState()) {
					svira_rucno = true;
					klavijatura.setFocusable(true); 
					klavijatura.requestFocus();
				}
				
				if (rbAutomatsko.getState()) {
					svira_rucno = false;
					klavijatura.setFocusable(false);
				}
				
			}
		});
		p1.add(rbAutomatsko);
		p1.add(rbRucno);
		
		centar.add(play);
		centar.add(pause);
		centar.add(stop);	
		centar.add(recordStart);
		centar.add(recordEnd);
		centar.add(p1);
		
		Label l = new Label("Eksportuj kao:");
		Checkbox chkTxtExport = new Checkbox("txt fajl");
		Checkbox chkMidiExport = new Checkbox("midi fajl");
		Button export = new Button("EKSPORTUJ");
		export.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Frame f = new Frame();
				Dialog dialog = new Dialog(f, "Eksportovanje kompozicije", true);
				dialog.setLayout(new BorderLayout());
				
				Panel centar = new Panel();
				centar.setLayout(new GridLayout(2, 1, 4, 2));
				
				Label l = new Label("Putanja za eksportovanje (bez ekstenzije):");
				centar.add(l);
				TextField putanja = new TextField();
				putanja.setSize(100, 10);
				centar.add(putanja);
				
				dialog.add(centar, "Center");
				
				Button b = new Button("Eksportuj");
				b.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (kompozicija == null) {
							JOptionPane.showMessageDialog(new Frame(), "Kompozicija nije ucitana!", "Greska", JOptionPane.WARNING_MESSAGE);
							return;
						}
						if (chkMidiExport.getState()) {
							MidiKonvertor mk = new MidiKonvertor(kompozicija, putanja.getText() + ".mid");
							mk.eksportuj();
							eksportovano = true;
						}
						if (chkTxtExport.getState()) {
							TxtKonvertor tk = new TxtKonvertor(kompozicija, putanja.getText() + ".txt");
							tk.eksportuj();
							eksportovano = true;
						}
					}
				});
				
				dialog.add(b, "South");
				dialog.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						dialog.dispose();
						f.dispose();
					}
					
				});
				dialog.setBounds(300, 300, 300, 200);
				dialog.setVisible(true);
			}
		});
		
		jug.add(l);
		jug.add(chkTxtExport);
		jug.add(chkMidiExport);
		jug.add(export);
		
		glIstok.add(centar, "Center");
		glIstok.add(jug, "South");
		
		add(glIstok, "East"); 
		add(klavijatura, "South");
		
		
	}
	

	/**
	 * Metoda sluzi da se utvrdi koji nacin ispisa je aktuelan.
	 * 
	 * @return true ako je notni zapis, false ako je txt zapis
	 */
	public boolean nacinIspisa() {
		if (notniZapis)
			return true;
		else
			return false; 
	}
	
	public Kompozicija dohvKompoziciju() {
		return kompozicija; 
	}
	
	
	public static void main(String[] args) {
		Klavir k = new Klavir();
	}
	
	
}
