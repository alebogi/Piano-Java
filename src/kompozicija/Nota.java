package kompozicija;

public class Nota extends MuzickiSimbol {

	private int oktava;
	private String visina;
	
	public Nota(int br, int im, int o, String v) {
		super(br, im);
		oktava = o;
		visina = v;
	}

	public Nota(Trajanje tr, int o, String v) {
		super(tr);
		oktava = o;
		visina = v;
	}
	
	public int dohvOktavu() {
		return oktava;
	}
	
	public String dohvVisinu() {
		return visina;
	} 
	
	public boolean imaPovisicu() {
		if (visina.contains("#"))
			return true;
		else
			return false;		
	}
	
	public void dodajPovisicu() {
		if (imaPovisicu())
			return;
		else {
			if (visina == "E" || visina == "B")
				return;
			visina += "#";
		}		
	}

	
	public String toString() {
		return visina + oktava; 
		/*String s;
		if (Trajanje.suJednaki(t, new Trajanje(1, 8))){
			s = visina + oktava + " ->1/8";
		}else if  (Trajanje.suJednaki(t, new Trajanje(1, 4))) {
			s = visina + oktava + " ->1/4";
		}else {
			s = visina + oktava + " ->0";
		}
		if (this.istovremeno)
			s+= "  istovremeno";
		return s;*/
	} 
}
