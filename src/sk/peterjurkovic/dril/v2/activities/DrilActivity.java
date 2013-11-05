package sk.peterjurkovic.dril.v2.activities;


import java.util.Locale;
import java.util.regex.Pattern;

import sk.peterjurkovic.dril.DrilService;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.dto.WordToPronauceDto;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.StringUtils;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DrilActivity extends BaseActivity implements OnInitListener{
	
	private static final String QUESTION_TAG = "question";
	private static final String ANSWER_TAG = "answer";
	
	public static final String TAG = "DRIL";
	public static final int DATA_CHECK_CODE = 0;
	public static final String STATISTIC_ID_KEY = "statisticId";
	
	private TextToSpeech tts;
	private DrilService drilService;

	private Button showAnswerBtn;
	private ImageButton speachQuestionBtn;
	private ImageButton speachAnswerBtn;
	private TextView question;
	private TextView answer;
	private TextView drilheaderInfo;
	private TextView answerLabel;
	private Animation slideLeftIn;
	private View  layout;
	private LinearLayout answerLayout;
	SharedPreferences preferences;
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dril_layout);
	    
       
        //slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.left_ight);
        slideLeftIn = new TranslateAnimation(1000, 0, 0, 0);
        slideLeftIn.setDuration(500);
        slideLeftIn.setFillAfter(true);
    	layout = (RelativeLayout) findViewById(R.id.dril);
    	layout.startAnimation(slideLeftIn);
	 
	    speachQuestionBtn = (ImageButton) findViewById(R.id.speakQuestion);
	    speachAnswerBtn = (ImageButton) findViewById(R.id.speakAnswer);
	    showAnswerBtn = (Button) findViewById(R.id.showAnswer);
        question = (TextView) findViewById(R.id.question);
        answer = (TextView) findViewById(R.id.answer);
        answerLabel = (TextView) findViewById(R.id.answerLabel);
        drilheaderInfo = (TextView) findViewById(R.id.drilHeaderInfo);
        answerLayout = (LinearLayout)findViewById(R.id.answerLayout);
        
        checkTTSDataForLocale();
        
        showAnswerBtn.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				showAnswer();				
			}
		});

        
        speachQuestionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				speakWords( getQuestionToPronauce() );
			}
		});
        speachAnswerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				speakWords( getAnserToPronauce() );
			}
		});
        
        init();
    }
    
    private void init(){
    	preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	drilService = new DrilService(new WordDBAdapter(this));
        if(!drilService.hasNext()){
        	showNoCardsAlert();
        }else{
        	layout.setVisibility(View.VISIBLE);
        	tryNextWord();
        }
    }
    
    private void tryNextWord(){
    	hideAnswer();
    	Word currentWord = drilService.getNext();
    	setWordIntoViews(currentWord);
    }
    
    private void setWordIntoViews(Word word){
    	if(word == null){
    		Log.e(TAG, "Word is NULL");
    		return;
    	}
    	String shouldBeShown = preferences.getString(Constants.PREF_TEST_VALUE, "question");
    	if(shouldBeShown.equals("question")){
    		setVisibleQuestion(word);
    	}else if(shouldBeShown.equals("answer")){
    		setVisibleAnswer(word);
    	}else{
    		long timestamp = System.currentTimeMillis();
    		if(timestamp % 2 == 0){
    			setVisibleQuestion(word);
    		}else{
    			setVisibleAnswer(word);
    		}
    	}
    	
    	 drilheaderInfo.setText( 
	        		getString(R.string.activated_words, 
						drilService.getCountOfWords(), 
						word.getHit(),
						word.getLastRate()
    				));
    	 layout.startAnimation(slideLeftIn);
    }
    
    private void setVisibleQuestion(Word word){
    	question.setText( word.getQuestion() );
    	question.setTag(QUESTION_TAG);
        answer.setText( word.getAnsware() );
        answer.setTag(ANSWER_TAG);
    }
    
    private void setVisibleAnswer(Word word){
    	question.setText( word.getAnsware()  );
    	question.setTag(ANSWER_TAG);
        answer.setText(  word.getQuestion());
        answer.setTag(QUESTION_TAG);
    }
    
    public void showNoCardsAlert(){
    	layout.setVisibility(View.INVISIBLE);
    	TextView alertBox = (TextView)findViewById(R.id.drilAlertBox);
    	alertBox.setText(R.string.zero_cards_alert);
    	alertBox.setVisibility(View.VISIBLE);
    	
    }
    
    public void processRate(View view){
    	int rate = Integer.valueOf((String)view.getTag());
    	Log.d(TAG, "clicked: "+ rate);
    	drilService.precessRating(rate);
    	if(drilService.hasNext()){
    		tryNextWord();
    	}else{
    		showNoCardsAlert();
    	}
    	
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	//outState.putLong(STATISTIC_ID_KEY, statisticId);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	//saveStatisticId();
    }
   

    
    
    public void drilFinished(int resourceId){
    	layout.setVisibility(View.INVISIBLE);
    	TextView alertBox = (TextView)findViewById(R.id.drilAlertBox);
    	alertBox.setText(resourceId);
    	alertBox.setVisibility(View.VISIBLE);
    	
    }
    
    
   
    
    public void showAnswer(){
    	answerLayout.setVisibility(View.VISIBLE);
    	answerLabel.setVisibility(View.VISIBLE);
    	answer.setVisibility(View.VISIBLE);
    	speachAnswerBtn.setVisibility(View.VISIBLE);
    	showAnswerBtn.setVisibility(View.GONE);
    }
    
    public void hideAnswer(){
    	answerLayout.setVisibility(View.GONE);
    	answerLabel.setVisibility(View.GONE);
    	answer.setVisibility(View.GONE);
    	speachAnswerBtn.setVisibility(View.GONE);
    	showAnswerBtn.setVisibility(View.VISIBLE);
    }
    
    
   
    
    
    private void speakWords(WordToPronauceDto wordDto) {
    	if(StringUtils.isBlank(wordDto.getValue())){
    		Toast.makeText(this, R.string.nothing_to_speeach, Toast.LENGTH_LONG).show();
    	}
    	Log.i(TAG, tts.getLanguage().toString());
    	if(wordDto.getLanguage() == null){
    		setEnglishTTSLocale();
    	}else{
    		Locale locale = wordDto.getLanguage().getLocale();
    		if(isLanguageAvailable(locale)){
    			tts.setLanguage(locale);
    		}else{
    			Toast.makeText(this, 
    					getString(R.string.error_tts_locale, getString( wordDto.getLanguage().getResource())) , 
    					Toast.LENGTH_LONG).show();
    		}
    	}
    	tts.speak(StringUtils.removeSpecialCharacters(wordDto.getValue()), TextToSpeech.QUEUE_FLUSH, null);
    	
    }
    
    @Override
    protected void onActivityResult(final int requestCode,final int resultCode, Intent data) {
    	if (requestCode == DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {      
               tts = new TextToSpeech(this, this);
            }
            else {
            	// missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction( TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    
    @Override
    public void onInit(int initStatus) {
    	if (initStatus == TextToSpeech.SUCCESS) {
    		
        }else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, R.string.speach_failed, Toast.LENGTH_LONG).show();
        }
    }
    
    
    @Override
    protected void onDestroy() {
    	if (tts != null) {
        	tts.stop();
        	tts.shutdown();
	    }
    	super.onDestroy();
    }
    
    
    
    
    public WordToPronauceDto getQuestionToPronauce(){
    	String tagValue = (String) question.getTag();
    	return determineWord(tagValue);
    }
    
    public WordToPronauceDto getAnserToPronauce(){
    	String tagValue = (String) answer.getTag();
    	return determineWord(tagValue);
    }
    
    public WordToPronauceDto determineWord(final String tagValue){
    	Log.i(TAG, tagValue);
    	WordToPronauceDto wordDto = new WordToPronauceDto();
    	if(tagValue.equals(QUESTION_TAG)){
    		wordDto.setValue(drilService.getCurrentWord().getQuestion());
    		wordDto.setLanguage( drilService.getCurrentWord().getQuestionLanguage() );
    	}else{
    		wordDto.setValue(drilService.getCurrentWord().getAnsware());
    		wordDto.setLanguage( drilService.getCurrentWord().getAnserLanguage() );
    	}
    	return wordDto;
    }
    
    public boolean isLanguageAvailable(Locale speechLocale) {
        return tts.isLanguageAvailable(speechLocale) != TextToSpeech.LANG_MISSING_DATA
                && tts.isLanguageAvailable(speechLocale) != TextToSpeech.LANG_NOT_SUPPORTED;
    }

    
    private void setEnglishTTSLocale(){
		tts.setLanguage(Locale.ENGLISH);
    }
    
    private void checkTTSDataForLocale(){
    	 Intent checkTTSIntent = new Intent();
         checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
         startActivityForResult(checkTTSIntent, DATA_CHECK_CODE);
    }
    
}
