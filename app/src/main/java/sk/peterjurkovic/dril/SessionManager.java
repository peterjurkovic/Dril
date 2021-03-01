package sk.peterjurkovic.dril;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.model.Language;

public class SessionManager {

	 // LogCat tag
    private static final String TAG = SessionManager.class.getSimpleName();
    private final SharedPreferences pref;
    private final Context _context;
    public static final String PREF_NAME = "credentials";   
    public static final int DEFAULT_WORD_LIMIT = 2000;
    public static final int UNLIMITED = -1;

    public static final String KEY_USER_ID = "_uid_";
    public static final String KEY_LOGIN = "_login_";
    public static final String KEY_LOCALE_ID = "_localeId_";
    public static final String KEY_TARGET_LOCALE_ID = "_targetLocaleId_";
    public static final String KEY_FIRST_NAME = "_firstName_";
    public static final String KEY_LAST_NAME = "_lastName_";
    public static final String KEY_TOKEN = "_token_";
    public static final String KEY_WORD_LIMIT = "_wordLimit_";
    public static final String KEY_LANGS_ARE_SET = "_areLangsSet_";
 
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void logout(){
    	final Editor editor = pref.edit();
    	editor.remove(KEY_USER_ID);
    	editor.remove(KEY_LOGIN);
    	editor.remove(KEY_TOKEN);
    	editor.remove(KEY_WORD_LIMIT);
    	editor.commit();
    }
 
    public void setCredentials(JSONObject response) throws JSONException {
    	final JSONObject user = response.getJSONObject("user");
    	final Editor editor = pref.edit();
    	editor.putInt(KEY_USER_ID, user.getInt("id"));
    	editor.putString(KEY_LOGIN, user.getString("login"));
    	editor.putString(KEY_FIRST_NAME, user.getString("firstName"));
    	editor.putString(KEY_LAST_NAME, user.getString("lastName"));
    	editor.putString(KEY_TOKEN, response.getString("token"));
    	editor.putInt(KEY_LOCALE_ID, isNull(user, "localeId") ?  Language.ENGLISH.getId() : user.getInt("localeId"));
    	editor.putInt(KEY_TARGET_LOCALE_ID, isNull(user, "targetLocaleId") ?  Language.ENGLISH.getId() : user.getInt("targetLocaleId"));
    	editor.putInt(KEY_WORD_LIMIT, user.getInt("wordLimit"));
        editor.commit();
        Log.d(TAG, "User login session modified. " );
    }
    
    private boolean isNull(JSONObject obj, String key){
    	return !obj.has(key) || obj.isNull(key);
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
    
    public int getWordLimit(){
    	return pref.getInt(KEY_WORD_LIMIT, DEFAULT_WORD_LIMIT);
    }
    
    public boolean isUserUnlimited(){
    	return getWordLimit() == UNLIMITED;
    }
     
    public boolean isUserLoggedIn(){
        return getToken() != null;
    }
    
    public boolean areLanguagesSet(){
    	return pref.getBoolean(KEY_LANGS_ARE_SET, false);
    }
    
    public void setLanguages(int localeId, int targetLocaleId){
    	final Editor editor = pref.edit();
    	editor.putInt(KEY_LOCALE_ID, localeId);
    	editor.putInt(KEY_TARGET_LOCALE_ID, targetLocaleId);
    	editor.putBoolean(KEY_LANGS_ARE_SET, true);
    	editor.commit();
    }
}	
