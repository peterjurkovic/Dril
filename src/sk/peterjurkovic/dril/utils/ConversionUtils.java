package sk.peterjurkovic.dril.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ConversionUtils {
	
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy/HH:mm");
	
	public static boolean intToBoolean(int value){
		return (value == 1 ? true : false);   
	}
	
	
	public static int booleanToInt(boolean value){
		return (value  ? 1 : 0);   
	}
	
	
	public static String timestampToString(final long timestamp){
		if(timestamp > 0){
			Date date = new Date(timestamp);
			return DATE_FORMAT.format(date);			
		}
		return "";
	}

	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static int convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return Math.round(px);
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static int convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return Math.round(dp);
	}


}
