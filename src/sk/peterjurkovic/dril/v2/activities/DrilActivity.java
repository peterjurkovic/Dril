package sk.peterjurkovic.dril.v2.activities;


import java.util.Locale;
import java.util.regex.Pattern;

import sk.peterjurkovic.dril.DrilService;
import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
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
	
	public static final String TAG = "DRIL";


	public static final int DATA_CHECK_CODE = 0;
	
	public static final String STATISTIC_ID_KEY = "statisticId";
	
	private TextToSpeech textToSpeachService;
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
	private Word currentWord = null;
	private LinearLayout answerLayout;
	SharedPreferences preferences;
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_dril_layout);
	    
       
        //slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.left_ight);
        TranslateAnimation anim = new TranslateAnimation(1000, 0, 0, 0);
        anim.setDuration(500);
       // anim.setFillAfter(true);
    	slideLeftIn = anim;
    	layout = (RelativeLayout) findViewById(R.id.dril);
    	layout.startAnimation(slideLeftIn);
	 
	    speachQuestionBtn = (ImageButton) findViewById(R.id.speakQuestion);
	    speachAnswerBtn = (ImageButton) findViewById(R.id.speakAnswer);
	    showAnswerBtn = (Button) findViewById(R.id.showAnswer);
        question = (TextView) findViewById(R.id.question);
        answer = (TextView) findViewById(R.id.answer);
        answerLabel = (TextView) findViewById(R.id.answerLabel);
        drilheaderInfo = (TextView) findViewById(R.id.drilLabel1);
        answerLayout = (LinearLayout)findViewById(R.id.answerLayout);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, DATA_CHECK_CODE);
        
        showAnswerBtn.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				showAnswer();				
			}
		});

        
        speachQuestionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				speakWords( getWordToSpeak( true ) );
			}
		});
        speachAnswerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				speakWords( getWordToSpeak( false ) );
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
    	String shouldBeShown = preferences.getString("list_dril_test_values", "question");
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
    }
    
    private void setVisibleQuestion(Word word){
    	question.setText( word.getQuestion() );
        answer.setText( word.getAnsware() );
    }
    
    private void setVisibleAnswer(Word word){
    	question.setText( word.getAnsware()  );
        answer.setText(  word.getQuestion());
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
    		// TODO finis msg + sharing resulrs
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
    
    
    public String getLastRate(){
    	return (currentWord.getRate() == 0 ? " -" : currentWord.getRate()+"");
    }
    
    
    private void speakWords(String word) {
    	if(word == null || word.length() == 0){
    		Toast.makeText(this, R.string.nothing_to_speeach, Toast.LENGTH_LONG).show();
    	}else{
    		if(textToSpeachService != null)
    			textToSpeachService.speak(clearWord( word ), TextToSpeech.QUEUE_FLUSH, null);
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {      
               textToSpeachService = new TextToSpeech(this, this);
            }
            else {
            	PackageManager pm = getPackageManager();
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                ResolveInfo resolveInfo = pm.resolveActivity( installTTSIntent, PackageManager.MATCH_DEFAULT_ONLY );
                if( resolveInfo == null ) {
                		Toast.makeText(this, R.string.speach_failed, Toast.LENGTH_LONG).show();
                	} else {
                		startActivity(installTTSIntent);
                	}
               
            }
            }
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    
    @Override
    public void onInit(int initStatus) {
    	if (initStatus == TextToSpeech.SUCCESS) {
            textToSpeachService.setLanguage(Locale.ENGLISH);
        }else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, R.string.speach_failed, Toast.LENGTH_LONG).show();
        }
    }
    
    
    @Override
    protected void onDestroy() {
    	if (textToSpeachService != null) {
        	textToSpeachService.stop();
        	textToSpeachService.shutdown();
	    }
    	super.onDestroy();
    }
    
    
    /**
     * Clean word witch will by pronaucmend
     * 
     * escapten caractes:
     * s n v abj adv conj (s) (n) (v) (abj) (adv) (conj)
     * and [.*]
     * 
     * @param String word to escape
     * @return String escaped string
     */
    public String clearWord(String word){
    	Pattern pat = Pattern.compile("(\\s(n|v|adj|adv|st|conj)(\\s)?)|(\\s(\\(n\\)|"+
    									"\\(v\\)|\\(adj\\)|\\(adv\\)|\\(conj\\))(\\s)?)|(\\[.*\\])");   
    	return pat.matcher(word).replaceAll("");  
    }
    
   
    
    
    public String getWordToSpeak(boolean isQuestion){
    	return null;
    }
    
}
