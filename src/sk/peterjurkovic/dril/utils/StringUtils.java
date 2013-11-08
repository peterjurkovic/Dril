package sk.peterjurkovic.dril.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Nov 5, 2013
 *
 */
public class StringUtils {
	
	private final static Pattern pattern = Pattern.compile("(\\(n\\)|\\(v\\)|\\(adj\\)|\\(s\\)|\\(conj\\)|\\|-|\\*|\\/)");
	
	public static String toSeoUrl(String string) {
		if(StringUtils.isBlank(string)){
			return "";
		}
	    return Normalizer.normalize(string.toLowerCase(), Form.NFD)
	        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
	        .replaceAll("[^\\p{Alnum}]+", " ");
	}
	
	
	/**
	 * Test is given value is blank.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isBlank(String value){
		if(value == null){
			return true;
		}else if(value.trim().length() > 1){
			return false;
		}
		return true;
	}
	
	
	
	public static String removeSpecialCharacters(String value){
		if(StringUtils.isBlank(value)){
			return "";
		}
		
		return pattern.matcher(value).replaceAll(" ");
		
	}
	
	
	
	public static int determineSimularity(String word, String inputText){
		if(StringUtils.isBlank(inputText) || StringUtils.isBlank(word)){
			return 5;
		}
		
		word = StringUtils.removeSpecialCharacters( StringUtils.toSeoUrl(word) );
		inputText = StringUtils.removeSpecialCharacters( StringUtils.toSeoUrl( inputText ) );
		
		if(StringUtils.isBlank(inputText) || StringUtils.isBlank(word)){
			return 5;
		}
		
		if(word.equals(inputText)){
			return 1;
		}
		
		
		int max = 5;
		String[] words = word.split(",");
		String[] inputWords = StringUtils.removeSpecialCharacters(inputText).split(",");
	
		for(String w : words){
			for(String iw : inputWords){
				int result = determineSimularityForWord(w, iw);
				if(result < max){
					result = max;
				}
			}
		}
		
		return max;
	}
	
	public static int determineSimularityForWord(String word, String inputText){
		if(StringUtils.isBlank(inputText) || StringUtils.isBlank(word)){
			return 5;
		}
		Log.d("test", "CLEANED word: " + word + " / " +inputText );
		if(word.endsWith(inputText)){
			return 1;
		}
		int levenshtein = StringUtils.getLevenshteinDistance(word, inputText);
		
		Log.i("SIM", "levenshtein " + levenshtein);
		
		switch(levenshtein){
			case 1 :
				return 2;
			case 2: 
				return 2;
			case 3:
				return 3;
			case 4:
				return 4;
			default :
				return 5;
					
		}
	}
	
	public static int getLevenshteinDistance(String s, String t) {
	      int n = s.length(); 
	      int m = t.length(); 
	      if (n == 0) {
	          return m;
	      } else if (m == 0) {
	          return n;
	      }
	      if (n > m) {
	          // swap the input strings to consume less memory
	          String tmp = s;
	          s = t;
	          t = tmp;
	          n = m;
	          m = t.length();
	      }
	      int p[] = new int[n+1]; //'previous' cost array, horizontally
	      int d[] = new int[n+1]; // cost array, horizontally
	      int _d[]; //placeholder to assist in swapping p and d
	      int i;
	      int j; 
	      char t_j; 
	      int cost; 
	      for (i = 0; i<=n; i++) {
	          p[i] = i;
	      }
	      for (j = 1; j<=m; j++) {
	          t_j = t.charAt(j-1);
	          d[0] = j;

	          for (i=1; i<=n; i++) {
	              cost = s.charAt(i-1)==t_j ? 0 : 1;
	              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
	              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
	          }

	          // copy current distance counts to 'previous row' distance counts
	          _d = p;
	          p = d;
	          d = _d;
	      }
	      // our last action in the above loop was to switch d and p, so p now 
	      // actually has the most recent cost counts
	      return p[n];
	  }

	
	
	
}
