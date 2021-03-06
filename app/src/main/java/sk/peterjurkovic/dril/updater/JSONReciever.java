package sk.peterjurkovic.dril.updater;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import okhttp3.Request;
import okhttp3.Response;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;

public class JSONReciever {
	
		public static final int FOR_CHECK_ACTION = 1;
		public static final int FOR_UPDATE_ACTION = 2;
		public static final int FOR_OWN_WORD_ACTION = 3;
		
		public static final int LANG_EN_INDEX = 1;
		
		private  String checkServiceURL = buildUrl() + FOR_CHECK_ACTION+"&ver=";
		private  String updateServiceURL = buildUrl() + FOR_UPDATE_ACTION+"&ver=";
		
		private  String ownWordsServiceUrl = Constants.API_URL+ "?importId=";
		
		private String importId = null;
	    
		private final static String buildUrl(){
			if(Constants.APP_VARIANT.equals("en")){
				return Constants.API_URL+ "?lang=1&act=";
			}else{
				return Constants.API_URL+ "?lang=2&act=";
			}
		}
		
	    public JSONReciever(String importId) {
	    	this.importId = importId;
	    }
	    
	    public JSONReciever(long version) {
	    	checkServiceURL = checkServiceURL + version;
	    	updateServiceURL = updateServiceURL + version;
	    }
	    
	    public JSONObject getJSONData(int action) throws IllegalArgumentException {
	    	InputStream is = null; 
	        // Making HTTP request
	        try {

				Request request = new Request.Builder()
						.header("Authorization" , Constants.HTTP_AUTH_VAL)
						.url(getRequestURL(action))
						.build();

				try (Response response = HttpClient.INSTANCE.newCall(request).execute()) {
					GoogleAnalyticsUtils.log(request, response);
					if (response.isSuccessful())
						return toJSON(response.body().string());
					else {
						Log.w(getClass().getName(), "Http response: " + response.code());
					}

				}
	        } catch (Exception e) {
				Log.e(getClass().getName(), e.getMessage());
				GoogleAnalyticsUtils.logException(e);
	        }

	        return toJSON(toStreamToString(is));
	    }	
	    
	    
	    private JSONObject toJSON(final String jsonString){
	    	if(StringUtils.isBlank(jsonString)){
	        	return null;
	        }
	    
	        try {
	            return new JSONObject(jsonString);
	        } catch (JSONException e) {
				Log.e(getClass().getName(), e.getMessage());
				GoogleAnalyticsUtils.logException(e);
	        }
	        return null;
	    }
	    
	    
	    
	    private String toStreamToString(final InputStream is){
	    	if(is == null){
	    		return null;
	    	}
	    	 try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		            StringBuilder sb = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		            	sb.append(line).append("n");
		            }
		            return sb.toString();
	        } catch (Exception e) {
				 Log.e(getClass().getName(), e.getMessage());
				 GoogleAnalyticsUtils.logException(e);
	        }finally{
	        	if(is != null){
	        		 try {
						is.close();
					} catch (IOException e) {
						 Log.e(getClass().getName(), e.getMessage());
						 GoogleAnalyticsUtils.logException(e);
					}
	        	}
	        }
	    	 return null;
	    }
	    
	    
	    
	   private String getRequestURL(final int action){
		   String url = "";
           switch( action ){
           	case FOR_CHECK_ACTION :
           		url = checkServiceURL;
           	break;
           	case FOR_UPDATE_ACTION :
           		url = updateServiceURL;
           	break;
           	case FOR_OWN_WORD_ACTION :
           		if(importId == null)
           			throw new IllegalArgumentException("Invalid import ID");
           		url = ownWordsServiceUrl  + importId;
           	break;
           	default:
           		throw new IllegalArgumentException("JSONRecievers action is not defined");
           }
           return url;
	   }
	    
	 
}
