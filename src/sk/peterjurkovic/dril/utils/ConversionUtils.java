package sk.peterjurkovic.dril.utils;

public class ConversionUtils {
	
	public static boolean intToBoolean(int value){
		return (value == 1 ? true : false);   
	}
	
	
	public static int booleanToInt(boolean value){
		return (value  ? 1 : 0);   
	}
	
}
