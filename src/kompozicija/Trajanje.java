package kompozicija;

public class Trajanje {

	private int brojilac, imenilac;
	
	public Trajanje(int br, int im) {
		brojilac = br;
		imenilac = im;
	}
	
	public int dohvIm() {
		return imenilac;
	}
	
	public int dohvBr() {
		return brojilac;
	}
	
	public static boolean suJednaki(Trajanje t1, Trajanje t2) {
		if (t1.imenilac == t2.imenilac) {
			if (t1.brojilac == t2.brojilac)
				return true;
			else
				return false;
		}
		else {
			if (t1.imenilac == 4) {
				if (t1.brojilac * 2 == t2.brojilac)
					return true;
				else
					return false;
			}
			else
				if (t1.brojilac == t2.brojilac * 2)
					return true;
				else
					return false;
		}
	}
 
	public String toString() {
		return "->" + brojilac + "/" + imenilac + ";";
	}
	
}
