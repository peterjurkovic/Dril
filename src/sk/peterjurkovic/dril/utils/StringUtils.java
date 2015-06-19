package sk.peterjurkovic.dril.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Nov 5, 2013
 *
 */
public class StringUtils {
	
	private final static String ENCODING = "utf-8";	
	private final static Pattern pattern = Pattern.compile("(\\[|\\/)[^\\]]*?(\\]|\\/)");
	
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
		
		return pattern.matcher(value).replaceAll("");
		
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
		
		int result = determineSimularityForWord(word, inputText);
		if(result < max){
			return result;
		}
		String[] words = word.split(",");
		String[] inputWords = StringUtils.removeSpecialCharacters(inputText).split(",");
	
		for(String w : words){
			for(String iw : inputWords){
				result = determineSimularityForWord(w, iw);
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
			case 2:
				return 2;
			case 3:
			case 4:
				return 3;
			case 5:
			case 6:			
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
	
	
	/**
	 * Check if character at given position is space
	 * 
	 * @param value
	 * @param position
	 * @return TRUE, if is '\s' 
	 */
	public static boolean isSpace(final String value, final int position){
		if(StringUtils.isBlank(value) || value.length() <= position){
			return false;
		}
		
		return StringUtils.isWhitespace( value.charAt(position) );
	}
	
	
	/**
	 * 
	 * @param character
	 * @return
	 */
	public static boolean isWhitespace(final char character){
		if (character == ' ' || character == '\t' || character == '\n' || character == '\r'){
			return true;
		}
		return false;
	}
	
	
	
	public static int getCountOfWhitespacesToPostion(final String value, final int position){
		int count = 0;
		if(StringUtils.isBlank(value)){
			return count;
		}
		for (char c : value.toCharArray()) {
		    if (StringUtils.isWhitespace(c)) {
		    	count++;
		    }
		} 	
		return count;
	}
	
	
	public static String getDrilHelpMessage(final String answer, final int hit){
		Log.i("StringUtils", "Creating help for: " + answer);
		if(!StringUtils.isBlank(answer)){
			if(StringUtils.hasNextCharacter(answer, hit)){
				String[] words = answer.split(" ");
				if(words.length > 3 && answer.length() > 10){
					return StringUtils.getNexWord(words, hit);
				}
				return StringUtils.getHelpMessage(answer, hit);
			}
			return answer;
		}
		return "";
	}
	
	
	private static String getHelpMessage(final String answer, final int hit){
		if(StringUtils.hasNextCharacter(answer, hit)){
			int nextIndex = hit + 1;
			int countOfSpaces = StringUtils.getCountOfWhitespacesToPostion(answer, hit);
			Log.i("String Utils", "Count of whitesaces to "+nextIndex+" position: " + countOfSpaces);
			nextIndex += countOfSpaces;
			
			if(answer.length() > nextIndex){
				return answer.substring(0, nextIndex) + "...";
			}
			
		}
		return answer;
	}
	
	
	private static String getNexWord(String[] words, final int hit){
		if(words != null){
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < words.length; i++){
				builder.append(words[i]);
				if(i == hit){
					if((i+1) != words.length){
						builder.append(" ...");
					}
					return builder.toString();
				}
				builder.append(" ");
			}
		}
		return "";
	}
	
	
	private static boolean hasNextCharacter(final String answer, final int hit){
		return (answer.trim().length() > (hit + 1));
	}
	
	public static boolean hasExcention(final String fileName, final String ext){
		if(StringUtils.isBlank(fileName) || fileName.length() < 4){
			return false;
		}
		int dotIndex = fileName.lastIndexOf(".");
		if(dotIndex == -1 || dotIndex >=  fileName.length()){
			return false;
		}
		dotIndex++;
		String fileExt = fileName.substring(dotIndex, fileName.length());
		return fileExt.equals(ext);
	}
	
	
	public static String extractError(NetworkResponse response, Context context){
		try {
			JSONObject jsonRes = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers, ENCODING)));
			if(jsonRes.has("error")){
				JSONObject jsonError = jsonRes.getJSONObject("error");
				return jsonError.getString("message");
			}
		} catch (Exception e) {
			GoogleAnalyticsUtils.logException(e, context);
		}
		return null;
	}
}
