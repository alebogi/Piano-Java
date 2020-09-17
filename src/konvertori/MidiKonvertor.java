package konvertori;

import kompozicija.*;
import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Objects;

public class MidiKonvertor extends Konvertor {

	private ArrayList<Integer> midiBrojevi = new ArrayList<Integer>();
	private ArrayList<Integer> trajanja = new ArrayList<Integer>();
	
	public MidiKonvertor(Kompozicija k, String putanja) {
		super(k, putanja);
		konverzijaMidiBrojevi();
	} 
	
	/**
	 * Na osnovu opisa note pronalazi u hash mapi midi vrednost note.
	 * @param n tipa Nota, nota za koju pronalazimo midi vrednst.
	 * @return tipa int, midi vrednost note.
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
	
	/**
	 * Metoda vrsi popunjavanje lista koje su polja ove klase.
	 * Popunjava listu midiBrojevi i listu trajanja, na osnovu liste muzickih simbola iz kompozicije.
	 */
	public void konverzijaMidiBrojevi() {
		kompozicija.dohvMuzSimbole().stream().forEach((elem)->{
			if (elem instanceof Nota) {
				int br = midiVrednost((Nota)elem);
				midiBrojevi.add(br);
			}else { //u slucaju da je pauza dodajemo broj 0
				int br = 0;
				midiBrojevi.add(br);
			}
			if (Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(1, 4))) {
				//nota koja traje 1/4
				if (elem.dohvatiFlagIstovremeno() == false)
					trajanja.add(2);
				else
					trajanja.add(3);
			}else if (Trajanje.suJednaki(elem.dohvatiTrajanje(), new Trajanje(1, 8))) {
				//koji br dodajemo za duzinu 1/8 ??
				trajanja.add(1);
			}else { //trajanje je 0
				trajanja.add(0);
			}
		});
	}
	
	/**
	 * Eksportujemo u fajl ciju zadatu putanju vec imamo.
	 */
	public void eksportuj() {
		try {
			konverzijaMidiBrojevi();
			long actionTime = 0, tpq = 48;
			
			Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);
			Track t = s.createTrack();
			
			//General MIDI sysex -- turn on General MIDI sound set
			byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
			SysexMessage sm = new SysexMessage();
			sm.setMessage(b, 6);
			MidiEvent me = new MidiEvent(sm,(long)0);
			t.add(me);
			
			//set tempo (meta event)
			MetaMessage mt = new MetaMessage();
	        byte[] bt = {0x02, (byte)0x00, 0x00};
			mt.setMessage(0x51 ,bt, 3);
			me = new MidiEvent(mt,(long)0);
			t.add(me);
			
			//set instrument to piano
			ShortMessage mm = new ShortMessage();
			mm.setMessage(0xC0, 0x00, 0x00);
			me = new MidiEvent(mm,(long)0);
			t.add(me);
			
			//dodavanje nota
			actionTime = 1; long pom_noteon = 1; long pom_noteoff = 1;
			for (int i = 0; i < midiBrojevi.size(); i++) {
				int midibr = midiBrojevi.get(i);
				int rhythm= trajanja.get(i);
					
				if(rhythm == 3) //prva nota akorda, moramo da upamtimo vreme pritiska
					pom_noteon = actionTime;
				
				if (rhythm == 0) //ostale note akorda, imaju vreme pritiska isto kao prva nota
					actionTime = pom_noteon;
					
				if (midibr != 0) { //ako nije pauza dodajemo event za pritisak
					mm = new ShortMessage();
					mm.setMessage(ShortMessage.NOTE_ON, midibr, 100);
					me = new MidiEvent(mm, actionTime);
					t.add(me);
				}
				
				if (rhythm == 3) { //prva nota akorda
					actionTime += tpq / 2 * 2;
					pom_noteoff = actionTime;
				}else if (rhythm == 0) { //ostale note akorda koje imaju isti aciton time otpustanja kao prva nota
					actionTime = pom_noteoff;
				}else {
					actionTime += tpq / 2 * rhythm;
				}
				
				if (midibr != 0) { //ako nije pauza dodajemo event za otpustanje
					mm = new ShortMessage();
					mm.setMessage(ShortMessage.NOTE_OFF, midibr, 100);
					me = new MidiEvent(mm, actionTime);
					t.add(me);
				}
				
			}	
			
			//set end of track (meta event) 19 ticks later
			actionTime += tpq;
			mt = new MetaMessage();
	        byte[] bet = {}; // empty array
			mt.setMessage(0x2F,bet,0);
			me = new MidiEvent(mt, actionTime);
			t.add(me);
			
			//write the MIDI sequence to a MIDI file
			File f = new File(putanja);
			MidiSystem.write(s,1,f);
			
		
		} catch (InvalidMidiDataException | IOException e) {
			System.err.println("Greska midi fajl");
			// ??
		}
		 
	}
}
