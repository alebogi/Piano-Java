package kompozicija;

public class Pauza extends MuzickiSimbol {

	public Pauza(int br, int im) {
		super(br, im);
	}

	public Pauza(Trajanje tr) {
		super(tr);
	}
	
	public String toString() {
		return " ";
		/*String s;
		if (Trajanje.suJednaki(t, new Trajanje(1, 8))){
			s = ". ->1/8";
		}else if  (Trajanje.suJednaki(t, new Trajanje(1, 4))) {
			s = "_ ->1/4";
		}else {
			s = "istovr";
		}
		return s;*/
	} 

}
