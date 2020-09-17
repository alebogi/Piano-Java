package sviranje;

import kompozicija.*;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.sound.midi.MidiUnavailableException;

public class AutomatskoSviranje extends Thread {

	private Kompozicija kompozicija;
	private MidiPlayer mp;
	private ArrayList<MuzickiSimbol> ms;
	private boolean radi = false;
	
	public AutomatskoSviranje(Kompozicija k) {
		kompozicija = k;
		ms = kompozicija.dohvMuzSimbole();
		try {
			mp = new MidiPlayer();
		} catch (MidiUnavailableException e) {
			// ??
		}
		start();
	} 

	/**
	 * Na osnovu opisa note pronalazi u hash mapi midi vrednost note.
	 * @param n tipa Nota, nota za koju pronalazimo midi vrednst.
	 * @return  midi vrednost note.
	 */
	public int midiVrednost(Nota n) {
		String opis_note = n.dohvVisinu();
		opis_note += n.dohvOktavu();
		int midi_br = 0;
		
		for (Entry<String, Par<String, Integer>> ulaz : kompozicija.mapa().dohv().entrySet()) {
			if (Objects.equals(opis_note, ulaz.getValue().first())) {
				return ulaz.getValue().second();
			}
		}
		
		return midi_br; 
	}
	
	public void run() {
		try {
			while (!interrupted()) {
				synchronized (this) {
					if (!radi)
						wait();
				}
				
				for (int i = 0; i < ms.size(); ) {
					synchronized (this) {
						if (!radi)
							wait();
					}
					MuzickiSimbol elem = ms.get(i);
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
							mp.play(midiVrednost((Nota)elem), length);	
							i++;
						}else if((Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(1, 4))) && (elem.dohvatiFlagIstovremeno())) {
							//u pitanju je prva nota akorda
							ArrayList<Integer> akord = new ArrayList<Integer>();
							akord.add(midiVrednost((Nota)elem));
							i++; 
							elem = ms.get(i);
							//moramo da sakupimo sve note koje cine akord u jednu listu
							while (Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(0, 4))) {
								akord.add(midiVrednost((Nota)elem));
								if ((i + 1) < ms.size()) {
									i++;
									elem = ms.get(i);
								}else {
									i++;
									break;
								}
							}
							mp.playAkord(akord, length);	
						}else{
							//u pitanju je nota koja traje 1/8 
							mp.play(midiVrednost((Nota)elem), length);
							i++;
						}
						
					}else {
						//u pitanju je pauza
						mp.playPause(length);
						i++;
					}
				}
				zaustavi();
			}
		}catch(InterruptedException gr) {
			//??
		}
	}
	
	public void sviraj() {
		start();
		radi = true;
	}
	
	public synchronized void kreni() {
		radi = true;
		notifyAll();
	}
	
	public synchronized void pauziraj() {
		radi = false;
	}
	
	public synchronized void zaustavi() {
		radi = false;
		interrupt();
	}

}
