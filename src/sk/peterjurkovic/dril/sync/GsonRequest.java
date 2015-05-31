package sk.peterjurkovic.dril.sync;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONObject;

import sk.peterjurkovic.dril.sync.respones.BaseResponse;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonRequest<T extends BaseResponse> extends JsonRequest<T> {
   
	private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;

     
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, String requestBody, Class<T> clazz, Map<String, String> headers,
            Listener<T> listener, ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        this.clazz = clazz;
        this.headers = headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

   
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
    
    public static class RequestBuilder<T extends BaseResponse>{
    	private int method = Method.POST;
    	private String url;
    	private String data;
    	private Class<T> clazz;
        private Map<String, String> headers;
        private Listener<T> listener;
        private ErrorListener errorListener;
        
        public RequestBuilder<T> method(final int method){
        	this.method = method;
        	return this;
        }
        
        public RequestBuilder<T> url(final String url){
        	this.url = url;
        	return this;
        }
        
        public RequestBuilder<T> withHeaders(Map<String, String> headers){
        	this.headers = headers;
        	return this;
        }
        
        public RequestBuilder<T> successListener(Listener<T> listener){
        	this.listener = listener;
        	return this;
        }
        
        public RequestBuilder<T> errorListener(ErrorListener listener){
        	this.errorListener = listener;
        	return this;
        }
        
        public RequestBuilder<T> data(final Object data){
        	if(data != null){
        		this.data = new Gson().toJson(data);
        	}
        	return this;
        }
        
        public RequestBuilder<T> data(final JSONObject data){
        	if(data != null){
        		this.data = data.toString();
        	}
        	return this;
        }
        
        public GsonRequest<T> build(){
    		return 
    		new GsonRequest<T>(
    				this.method, this.url, this.data, this.clazz, 
    				this.headers, this.listener, this.errorListener
    		);
        }	
    }

	
}
