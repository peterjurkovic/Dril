package sk.peterjurkovic.dril;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.model.Language;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {

	 // LogCat tag
    private static final String TAG = SessionManager.class.getSimpleName();
    private final SharedPreferences pref;
    private final Context _context;
    private final String PREF_NAME = "credentials";   
     

    private static final String KEY_USER_ID = "_uid_";
    private static final String KEY_LOGIN = "_login_";
    private static final String KEY_LOCALE_ID = "_localeId_";
    private static final String KEY_TARGET_LOCALE_ID = "_targetLocaleId_";
    private static final String KEY_TOKEN = "_token_";
 
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void logout(){
    	final Editor editor = pref.edit();
    	editor.remove(KEY_USER_ID);
    	editor.remove(KEY_LOGIN);
    	editor.remove(KEY_TOKEN);
    	editor.commit();
    }
 
    public void setCredentials(JSONObject response) throws JSONException {
    	final JSONObject user = response.getJSONObject("user");
    	final Editor editor = pref.edit();
    	editor.putInt(KEY_USER_ID, user.getInt("id"));
    	editor.putString(KEY_LOGIN, user.getString("login"));
    	editor.putString(KEY_TOKEN, response.getString("token"));
    	editor.putInt(KEY_LOCALE_ID, isNull(user, "localeId") ?  Language.ENGLISH.getId() : user.getInt("localeId"));
    	editor.putInt(KEY_TARGET_LOCALE_ID, isNull(user, "targetLocaleId") ?  Language.ENGLISH.getId() : user.getInt("targetLocaleId"));
        editor.commit();
        Log.d(TAG, "User login session modified.");
    }
    
    private boolean isNull(JSONObject obj, String key){
    	return obj.has(key) && !obj.isNull(key);
    }
    
    public int getTargetLocaleId(){
    	return pref.getInt(KEY_TARGET_LOCALE_ID, Language.ENGLISH.getId());
    }
    
    public int getLocaleId(){
    	return pref.getInt(KEY_LOCALE_ID,  Language.ENGLISH.getId());
    }
    
    public int getUserId(){
    	return pref.getInt(KEY_USER_ID, -1);
    }
    
    public String getToken(){
    	return pref.getString(KEY_TOKEN, null);
    }
     
    public boolean isLoggedIn(){
        return getToken() != null;
    }
}	
