package sk.peterjurkovic.dril.utils;


public class DeviceUtils {
	
	private DeviceUtils(){}

    public static String OSVERSION = System.getProperty("os.version");

   
    public static String getDeviceInfo(){
    	return 
    	 "OS Version: " + OSVERSION + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
    	 "OS API Level: " + android.os.Build.VERSION.SDK_INT +
    	 "Device: " + android.os.Build.DEVICE +
    	 "Model: " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
    }

   
}
