package sk.peterjurkovic.dril.utils;

import java.util.regex.Pattern;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Nov 5, 2013
 *
 */
public class StringUtils {
	
	private final static Pattern pattern = Pattern.compile("(\\s(n|v|adj|adv|st|conj)(\\s)?)|(\\s(\\(n\\)|\\(v\\)|\\(adj\\)|\\(adv\\)|\\(conj\\))(\\s)?)|(\\[.*\\])");
	
	
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
	
}
