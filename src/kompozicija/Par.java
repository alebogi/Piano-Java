package kompozicija;

public class Par<T, U> {

	private T first;
	private U second;
	
	public Par(T fst, U scnd) {
		first = fst;
		second = scnd;
	}
	
	public T first() {
		return first;
	}
	
	public U second() {
		return second;
	}
	
	public void setFirst(T f) {
		first = f;
	}
	
	public void setSecond(U s) {
		second = s;
	}
	
	public String toString() {
		return "(" + first + "," + second + ")";
	} 

}
