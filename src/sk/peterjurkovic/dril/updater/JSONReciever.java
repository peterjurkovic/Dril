package sk.peterjurkovic.dril.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.v2.constants.Constants;
import android.os.Looper;
import android.util.Log;

public class JSONReciever {
	
		public static final int FOR_CHECK_ACTION = 1;
		public static final int FOR_UPDATE_ACTION = 2;
		public static final int FOR_OWN_WORD_ACTION = 3;
		
		public static final int LANG_EN_INDEX = 1;
		
		private  String checkServiceURL = Constants.API_URL+ "?lang="+
					LANG_EN_INDEX+"&act="+ FOR_CHECK_ACTION+"&ver=";
		private  String updateServiceURL = Constants.API_URL+ "?lang="+
					LANG_EN_INDEX+"&act="+FOR_UPDATE_ACTION+"&ver=";
		
		private  String ownWordsServiceUrl = Constants.API_URL+ "?importId=";
		
		private String importId = null;
	    
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
	           	            
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(getRequestURL(action));
	            Log.d("HTTP", "Sending request: " + getRequestURL(action));
	            HttpResponse response = httpClient.execute(httpPost);
	            Log.i("HTTP",response.getStatusLine().toString());
	            
	        
	            if(response == null || response.getEntity() == null){
	            	Log.e("HTTP", "Response is null");
	            	throw new Error("HTTP Response is null");
	            }
	            
	            HttpEntity httpEntity = response.getEntity();

	            
	            is = httpEntity.getContent();           
	            Log.e("HTTP", "DATA RECIEVED!");
	        } catch (UnsupportedEncodingException e) {
	        	Log.e("HTTP Error", e.getMessage());
	        } catch (ClientProtocolException e) {
	        	Log.e("HTTP ClientProtocolException", e.getMessage());
	        } catch (IOException e) {
	        	Log.e("IO EX Error", e.getMessage());
	        } catch (Exception e) {
	        	Log.e("Error", e.getMessage());
	        }

	        return toJSON(toStreamToString(is));
	    }
	    
	    
	    private JSONObject toJSON(final String jsonString){
	    	if(jsonString == null){
	        	return null;
	        }
	    
	        try {
	            return new JSONObject(jsonString);
	        } catch (JSONException e) {
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	        }
	        return null;
	    }
	    
	    
	    
	    private String toStreamToString(final InputStream is){
	    	if(is == null){
	    		return null;
	    	}
	    	 try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
		            StringBuilder sb = new StringBuilder();
		            String line = null;
		            while ((line = reader.readLine()) != null) {
		            	sb.append(line + "n");
		            }
		            Log.d("JSON", "Data SUCCESSFULLY recieved");
		            return sb.toString();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }finally{
	        	if(is != null){
	        		 try {
						is.close();
					} catch (IOException e) {
						Log.e("HTTP INPUT STREAM", e.getMessage());
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
