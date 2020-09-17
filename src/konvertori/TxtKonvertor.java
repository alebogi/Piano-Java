package konvertori;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import kompozicija.Kompozicija;
import kompozicija.TekstualniSimbol;
import kompozicija.Trajanje;

public class TxtKonvertor extends Konvertor {

	boolean otvorenaZagrada = false;
	
	public TxtKonvertor(Kompozicija k, String putanja) {
		super(k, putanja);
		
	}
	
	/*
	 * kompozicija vec sadrzi niz txt simbola, samo ih treba ispisati u fajl na odredjeni nacin.
	 * Putanja do destinacionog fajla je data u polju klase.
	 */
	public void eksportuj(){
		try {
			File file = new File(putanja);
			if (!file.exists()) {
                file.createNewFile();
            }
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			ArrayList<TekstualniSimbol> lista = kompozicija.dohvTxtSimbole();
			
			for(int i = 0; i < lista.size(); ) {
				try {
					//pojedinacni element duzine 1/4
					if ((Trajanje.suJednaki(lista.get(i).dohvTrajanje(), new Trajanje(1, 4))) && (!lista.get(i).dohvIstovremeno())) {
						bw.write(lista.get(i).dohvOznaku()); 
						i++;
					}
					//element 1/4 koji se svira istovremeno (prvi unutar [...])
					else if ((Trajanje.suJednaki(lista.get(i).dohvTrajanje(), new Trajanje(1, 4))) && (lista.get(i).dohvIstovremeno())) {
						bw.write("[");
						bw.write(lista.get(i).dohvOznaku());
						i++;
						//idemo kroz listu sve dok su trajanja elemenata == 0/4, jer se svi ti elementi sviraju istovremeno
						while (Trajanje.suJednaki(lista.get(i).dohvTrajanje(), new Trajanje(0, 4))){
							bw.write(lista.get(i).dohvOznaku());
							if ((i + 1) < lista.size())
								i++;
							else {
								i++;
								break;
							}
						}
						bw.write("]");
					}
					//element traje 1/8
					else if(Trajanje.suJednaki(lista.get(i).dohvTrajanje(), new Trajanje(1, 8))){
						//u slucaju da je " " samo ispisuje znak pauze
						if (lista.get(i).dohvOznaku().equals(" ")) {
							bw.write(" ");
							i++;
						}
						//u slucaju da je nota mora da se pise unutar [..] odvojena razmacima
						else {
							bw.write("[");
							bw.write(lista.get(i).dohvOznaku());
							i++;
							if (i == lista.size())
								break;
							//gledamo da li ima jos nota iza ove koje traju 1/8 i pisemo ih unutar istih [...]
							while((Trajanje.suJednaki(lista.get(i).dohvTrajanje(), new Trajanje(1, 8))) && (!lista.get(i).dohvOznaku().equals(" "))){
								bw.write(" ");
								bw.write(lista.get(i).dohvOznaku());
								if ((i + 1) < lista.size())
									i++;
								else {
									i++;
									break;
								}
							}
							bw.write("]");
						}
					}
					
				}catch(IOException g) {
					System.err.println("Greska txt konvertovanje");
					//??
				}
			}
			
			
			bw.close();
		}catch(IOException gr) {
			System.err.println("Greska txt konvertovanje");
			//??
		}
	}

}
