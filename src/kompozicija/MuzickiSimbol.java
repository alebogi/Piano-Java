package kompozicija;

public class MuzickiSimbol {

	protected Trajanje t;
	protected boolean istovremeno;
	
	public MuzickiSimbol(int br, int im) {
		t = new Trajanje(br, im);
		istovremeno = false;
	}

	public MuzickiSimbol(Trajanje tr) {
		t = tr;
	}
	
	public void postaviIstovremeno() {
		istovremeno = true;
	}
	
	public boolean dohvatiFlagIstovremeno() {
		return istovremeno;
	}
	
	public Trajanje dohvatiTrajanje() {
		return t;
	}
	
	public void postaviTrajanje(Trajanje tr) { 
		t = tr;
	}
	
	public String toString() {
		return "";
	} 
	
} 
