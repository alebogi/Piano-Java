package konvertori;

import kompozicija.*;

public class Konvertor {

	protected Kompozicija kompozicija;
	protected String putanja; //putanja do destinacionog fajla
	
	
	public Konvertor(Kompozicija k, String put) {
		kompozicija = k;
		putanja = put;
	}

}
