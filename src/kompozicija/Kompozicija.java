package kompozicija;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream; 

public class Kompozicija {

	private String naziv;
	private String putanja; 
	private ArrayList<MuzickiSimbol> muzSimboli = new ArrayList<MuzickiSimbol>();//sadrzi opis note - visina i oktava
	private ArrayList<TekstualniSimbol> txtSimboli = new ArrayList<TekstualniSimbol>();//sadzri txt zapis note, kao u pocetnom fajlu
	private Mapa mapa;
	private File file;
	
	public Kompozicija(Mapa m, String n, String p) {
		mapa = m;
		naziv = n;
		putanja = p;
	}
	
	/**
	 * Ucitavamo postojecu kompoziciju iz fajla cija je putanja polje ove klase.
	 * Kompozicija se ucitava u dve odvojene liste tipa MuzickiSimbol i TekstualniSimbol.
	 * Lista muzickih simbola sadrzi opis nota (C, D, E, F, G, A, B).
	 * Lista tekstualnih simbola sadrzi sifrovani zapis note, tj tekst (npr w, x, ! ...).
	 * @throws IOException 
	 */
	public void ucitajKompoziciju() throws IOException {
			file = new File(putanja);
			BufferedReader br = new BufferedReader(new FileReader(file));
			Stream<String> s = br.lines();
			
			s.forEach((linija)->{
				Pattern p = Pattern.compile("(([^\\[\\]]{1})|\\[([^\\]]+)\\])");
				Matcher matcher = p.matcher(linija);
				
				while(matcher.find()) {
					if(matcher.group(1).length()==1) { //procitan jeda karakter
						if(matcher.group(1).equals(" ")) { //kratka pauza
							muzSimboli.add(new Pauza(1, 8));
							txtSimboli.add(new TekstualniSimbol(matcher.group(1), new Trajanje(1, 8)));
						}else if (matcher.group(1).equals("|")) { //duga pauza
							muzSimboli.add(new Pauza(1, 4));
							txtSimboli.add(new TekstualniSimbol(matcher.group(1), new Trajanje(1, 4)));
						}else { //nota van uglastih zagrada
							String vis_okt = mapa.dohv().get(matcher.group(1)).first();
							Par<String, Integer> par = razdvojiVisOkt(vis_okt);
							String visina = par.first();
							int oktava = par.second();
							muzSimboli.add(new Nota(1, 4, oktava, visina));
							txtSimboli.add(new TekstualniSimbol(matcher.group(1), new Trajanje(1, 4)));
						}
					}else { //procitao je [...]
						String res = matcher.group(1); //smestili [...] u string
						boolean razmak = false;
						
						for (char c : res.toCharArray()) {
							if (c == ' ') {
								razmak = true;
								break;
							}
						}
						
						if (razmak) { //note se sviraju jedna za drugom i traju  1/8
							
							for (int i = 1; i < res.length() - 1; i++) {
								if (!(Character.toString(res.charAt(i)).equals(" "))) {
									String vis_okt = mapa.dohv().get(Character.toString(res.charAt(i))).first();
									Par<String, Integer> par = razdvojiVisOkt(vis_okt);
									String visina = par.first();
									int oktava = par.second();
									muzSimboli.add(new Nota(1, 8, oktava, visina));
									txtSimboli.add(new TekstualniSimbol(Character.toString(res.charAt(i)), new Trajanje(1, 8)));
								}
							}
							
						}else { //note se sviraju istovremeno i traju 1/4
							
							for (int i = 1; i < res.length() - 1; i++) {
								String slovo = Character.toString(res.charAt(i));
								String vis_okt = mapa.dohv().get(Character.toString(res.charAt(i))).first();
								Par<String, Integer> par = razdvojiVisOkt(vis_okt);
								String visina = par.first();
								int oktava = par.second();
								if (i == 1) {
									Nota nota = new Nota(1, 4, oktava, visina);
									nota.postaviIstovremeno();
									muzSimboli.add(nota);
									TekstualniSimbol txt = new TekstualniSimbol(Character.toString(res.charAt(i)), new Trajanje(1, 4));
									txt.postaviIstovremeno();
									txtSimboli.add(txt);
								}else {
									Nota nota = new Nota(0, 4, oktava, visina);
									nota.postaviIstovremeno();
									muzSimboli.add(nota);
									TekstualniSimbol txt = new TekstualniSimbol(Character.toString(res.charAt(i)), new Trajanje(0, 4));
									txt.postaviIstovremeno();
									txtSimboli.add(txt);
								}	
							}
						
							
						}
					}	
				}
			});
			
			br.close();
	}

	/**
	 * Metoda unutar jednog stringa koji predstavlja notu razaznaje visinu i oktavu
	 * @param vis_okt tipa String koji predstavlja spojenu visinu i oktavu
	 * @return tipa par stringa i inta, gde string predstavlja visinu, a int oktavu
	 */
	public Par<String, Integer> razdvojiVisOkt(String vis_okt) {
		Par<String, Integer> res = null;
		Pattern pRazdvajac = Pattern.compile("([A-Z]{1}[#]?)([0-9]{1})");
		Matcher mRazdvajac = pRazdvajac.matcher(vis_okt);
		if (mRazdvajac.matches()) {
			String visina = mRazdvajac.group(1);
			String okravaStr = mRazdvajac.group(2);
			int oktava = Integer.parseInt(okravaStr);
			res = new Par(visina, oktava);
		}
		return res;
	}
	
	/**
	 * Sluzi za situacije kada mi stvaramo kompoziciju.
	 * @param listaMs lista muzickih simbola koja je prethodno stvorena i sad je samo dodeljujemo kompoziciji.
	 * @param listaTs lista tekstualnih simbola koja je prethodno stvorena i sad je samo dodeljujemo kompoziciji.
	 */
	public void stvoriKompoziciju(ArrayList<MuzickiSimbol> listaMs, ArrayList<TekstualniSimbol> listaTs) {
		muzSimboli.clear(); txtSimboli.clear();
		muzSimboli = listaMs;
		txtSimboli = listaTs;
	}
	
	public Mapa mapa() {
		return mapa; 
	}
	
	public ArrayList<MuzickiSimbol> dohvMuzSimbole(){
		return muzSimboli;
	}
	
	public ArrayList<TekstualniSimbol> dohvTxtSimbole(){
		return txtSimboli;
	}
	
}
