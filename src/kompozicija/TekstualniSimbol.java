package kompozicija;

public class TekstualniSimbol {

	private String oznaka;
	private Trajanje t;
	private boolean istovremeno;
	
	public TekstualniSimbol(String o, Trajanje tt) {
		oznaka = o;
		t = tt;
		istovremeno = false;
	}
	
	public String dohvOznaku() {
		return oznaka;
	}
	
	public Trajanje dohvTrajanje() {
		return t;
	}
	
	public boolean dohvIstovremeno() {
		return istovremeno;
	}
	
	public void postaviIstovremeno() {
		istovremeno = true;
	}
	
	public String toString() {
		if (oznaka == "|")
			return "  "; 
		return oznaka;
		/*String s;
		if (Trajanje.suJednaki(t, new Trajanje(1, 8))){
			s = oznaka + " ->1/8";
		}else if  (Trajanje.suJednaki(t, new Trajanje(1, 4))) {
			s = oznaka + " ->1/4";
		}else {
			s = oznaka + " ->0";
		}
		if (this.istovremeno)
			s+= "  istovremeno";
		return s;*/
	}

}
