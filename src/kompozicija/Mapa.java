package kompozicija;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Mapa {

	private String putanja;
	private HashMap<String, Par<String, Integer>> mapa = new HashMap<>();
	private File file;
	
	public Mapa(String put) {
		putanja = put;
	}
	
	
	public static Par<String, Par<String, Integer>> parsiraj(String s){
		Par result = new Par("", new Par("", 0));
		
		Pattern p = Pattern.compile("(.),([A-Z]{1}[#]?[0-9]{1}),([0-9]{2})(\\n)?");
		Matcher m = p.matcher(s);
		
		if (m.matches()) {
			String s1 = m.group(1);
			String s2 = m.group(2);
			String is = m.group(3);
			int i = Integer.parseInt(is);
			result.setFirst(s1);
			result.setSecond(new Par(s2,i));
		}
		
		return result; 
	}
	

	public void ucitajMapu() {
		try {
			file = new File(putanja);
			BufferedReader br = new BufferedReader(new FileReader(file));
			Stream<String> s = br.lines();
			
			s.forEach((linija)->{
				Par res = parsiraj(linija);
				
				//String key = (String)res.first();
				//Par<String, Integer> value = (Par<String, Integer>)res.second();
				//mapa.put(key, value);
				
				mapa.put((String)res.first(), (Par<String, Integer>)res.second());
			});
			
			br.close();
		}catch(IOException exc) {
			System.err.println("Neuspesno otvaranje");
		}
	}
	
	public HashMap<String, Par<String, Integer>> dohv(){
		return mapa; 
	}
	
}
