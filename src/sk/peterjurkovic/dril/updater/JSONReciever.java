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

import android.util.Log;

public class JSONReciever {
	
		public static final int FOR_CHECK_ACTION = 1;
		public static final int FOR_UPDATE_ACTION = 2;
		public static final int FOR_OWN_WORD_ACTION = 3;
		
		public static final int LANG_EN_INDEX = 1;
		
		private  String checkServiceURL = "http://www.drilapp.com/export.php?lang="+
					LANG_EN_INDEX+"&act="+ FOR_CHECK_ACTION+"&ver=";
		private  String updateServiceURL = "http://www.drilapp.com/export.php?lang="+
					LANG_EN_INDEX+"&act="+FOR_UPDATE_ACTION+"&ver=";
		private  String ownWordsServiceUrl = "http:www.//drilapp.dev/export.php?importId=";
		
		private String importId = null;
		
	 	static InputStream is = null;
	    static JSONObject jObj = null;
	    static String json = "";
	    
	    public JSONReciever(String importId) {
	    	this.importId = importId;
	    }
	    
	    public JSONReciever(long version) {
	    	checkServiceURL = checkServiceURL + version;
	    	updateServiceURL = updateServiceURL + version;
	    }
	    
	    public JSONObject getJSONData(int action) throws IllegalArgumentException {
	    	 
	        // Making HTTP request
	        try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            
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
	            
	             
	            HttpPost httpPost = new HttpPost(url);
	 
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();           
	 
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	 
	        try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    is, "iso-8859-1"), 8);
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	sb.append(line + "n");
	            }
	            is.close();
	            json = sb.toString();
	        } catch (Exception e) {
	            Log.e("Buffer Error", "Error converting result " + e.toString());
	        }
	 
	        // try parse the string to a JSON object
	        try {
	            jObj = new JSONObject(json);
	        } catch (JSONException e) {
	            Log.e("JSON Parser", "Error parsing data " + e.toString());
	        }
	 
	        // return JSON String
	        return jObj;
	 
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	 
}
