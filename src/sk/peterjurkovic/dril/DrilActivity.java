package sk.peterjurkovic.dril;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import sk.peterjurkovic.dril.db.StatisticDbAdapter;
import sk.peterjurkovic.dril.db.WordDBAdapter;
import sk.peterjurkovic.dril.model.Word;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DrilActivity extends MainActivity implements OnInitListener{
	
	public static final String TAG = "DRIL";
	
	public static final int RATE_1 = 1;
	public static final int RATE_2 = 2;
	public static final int RATE_3 = 3;
	public static final int RATE_4 = 4;
	public static final int RATE_5 = 5;
	public static final int DATA_CHECK_CODE = 0;
	
	public static final String STATISTIC_ID_KEY = "statisticId";
	
	private TextToSpeech textToSpeachService;
	
	
	Button rateButton1;
	Button rateButton2;
	Button rateButton3;
	Button rateButton4;
	Button rateButton5;
	Button showAnswerBtn;
	
	ImageButton speachQuestionBtn;
	ImageButton speachAnswerBtn;
	
	TextView question;
	TextView answer;
	TextView drilheaderInfo;
	TextView answerLabel;
	
	Animation slideLeftIn;
	View  layout;
	Word currentWord = null;
	
	LinearLayout answerLayout;
	
	// ak je pocet aktivovanych karticiek nad nastavenu hodntu, vyberaju sa nahodne
	int graduallyAlgorithmLimit = 5; 
	
	// je vymenena otazka / odpoved
	boolean isCardSwitched = false;
	
	int position = 0;
	
	long statisticId = 0;
	
	boolean isAnswerVisible = false;
	
	List<Word> activatedWords = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dril);
        
        ImageButton goHome = (ImageButton) findViewById(R.id.home);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity( new Intent(DrilActivity.this, DashboardActivity.class) );
            }
        });

        if(savedInstanceState != null)
        	statisticId = savedInstanceState.getLong(STATISTIC_ID_KEY, 0);
        
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.left_ight);

        layout = (RelativeLayout) findViewById(R.id.dril);
        
        layout.startAnimation(slideLeftIn);
        
        rateButton1 = (Button) findViewById(R.id.btn_1);
        rateButton2 = (Button) findViewById(R.id.btn_2);
        rateButton3 = (Button) findViewById(R.id.btn_3);
        rateButton4 = (Button) findViewById(R.id.btn_4);
        rateButton5 = (Button) findViewById(R.id.btn_5);
        
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
        
        
        /* RATE BUTTONs listeners -----------------------------------------------*/
        
        rateButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentWord.setRate(RATE_1);
				currentWord.setActive(false);
				updateRatedWord();
				activatedWords.remove(currentWord);
				if( graduallyAlgorithmLimit >= activatedWords.size()){
						position--;
				}
			    nextWord();
			}
		});
        
        rateButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentWord.setRate(RATE_2);
				updateRatedWord();
				nextWord();
				
			}
		});
        
        rateButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentWord.setRate(RATE_3);
				updateRatedWord();
				nextWord();
				
			}
		});
        
        rateButton4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentWord.setRate(RATE_4);
				updateRatedWord();
				nextWord();
				
			}
		});
        
        rateButton5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentWord.setRate(RATE_5);
				updateRatedWord();
				nextWord();
			}
		});
        
        /* SPEACHs listeners -----------------------------------------------*/
        
        
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
        
        inicializeDril();
        
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putLong(STATISTIC_ID_KEY, statisticId);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	saveStatisticId();
    }
        
    public void inicializeDril(){
    	activatedWords = inicializeWords();
         
 	   	if(activatedWords == null){
         	throw new Error("Can not recieve activated words.");
         }
         
        if(activatedWords.size() == 0){
        	 drilFinished( R.string.zero_cards_alert );         	
        }else{
        	layout.setVisibility(View.VISIBLE);
        	statisticId = loadStatisticId();
        	if(statisticId == 0 ){
        		initStatistic();
        	}
         	nextWord();
            hideAnswer();
        }
    }
       
    
    
    private void initStatistic() {
    	StatisticDbAdapter statisticDbAdapter = new StatisticDbAdapter(this);
  	    try{
  	    	statisticId = statisticDbAdapter.createNewDrilSession();
  	    } catch (Exception e) {
  			Log.d( TAG , "ERROR: " + e.getMessage());
  		} finally {
  			statisticDbAdapter.close();
  		}	
	}



	public List<Word> inicializeWords(){
    	WordDBAdapter wordDbAdapter = new WordDBAdapter(this);
    	List<Word> activatedWords = null;
  	    try{
  	    	activatedWords = wordDbAdapter.getActivatedWords();
  	    } catch (Exception e) {
  			Log.d( TAG , "ERROR: " + e.getMessage());
  		} finally {
  			wordDbAdapter.close();
  		}
  	    return activatedWords;
    }
    
    
    
    
    public void nextWord(){
    	if( goToNextWord() ){ 					
			hideAnswer();	 
			
			isCardSwitched = ( generateRandomNumber( 2 )  == 1);
			
			if( isCardSwitched ){
		    	question.setText( currentWord.getQuestion() );
		        answer.setText( currentWord.getAnsware() );
			}else{
				question.setText( currentWord.getAnsware()  );
		        answer.setText(  currentWord.getQuestion());
			}
	        drilheaderInfo.setText( 
	        		getString(R.string.activated_words, 
								activatedWords.size(), 
								(currentWord.getHit()),
								getLastRate()
	        				));
	        layout.startAnimation(slideLeftIn);
    	}
    }
    
    
    public boolean goToNextWord(){
    	if(activatedWords.size() == 0){
    		drilFinished( R.string.dril_finished );
    		return false;
    	}
    	selectPosition();
    	currentWord = activatedWords.get(position);
		currentWord.increaseHit();
    	return true;
    }
    
    
    
    
    public void selectPosition(){
    	try{
    	if(activatedWords.size() < graduallyAlgorithmLimit){
    		getNextPosition();
    	}else{
    		position = getRandomPosition();
    	}
    	}catch (Exception e){
    		getNextPosition();
    	}
    }
    
    public void getNextPosition(){
    	if((position + 1) == activatedWords.size()){ 
    		position = 0;
    	}else{
    		position++;
    	}
    }
    
    public int getRandomPosition(){	
    	List<Integer> randWords = new ArrayList<Integer>();	
    		while(randWords.size() < 2 ){
    			int pos = generateRandomNumber( activatedWords.size()  );
    			if(pos > (position +1) || pos < (position - 1))
    				randWords.add(pos);
    		}
    		
    		Word word0 = activatedWords.get(randWords.get(0));
    		if(word0.getRate() == 0){
    			return randWords.get(0);
    		}else{
    			Word word1 = activatedWords.get(randWords.get(1));
    			if(word1.getRate() == 0){
    				return randWords.get(1);
    			}else{
    				return( word0.getRate() > word1.getRate() ? randWords.get(0) : randWords.get(1));
    			}
    		}
    }
    
    
    public int generateRandomNumber(int end){
    	Random r = new Random();
    	return r.nextInt( end);
    } 
    
    
    
    public void drilFinished(int resourceId){
    	layout.setVisibility(View.INVISIBLE);
    	TextView alertBox = (TextView)findViewById(R.id.drilAlertBox);
    	alertBox.setText(resourceId);
    	alertBox.setVisibility(View.VISIBLE);
    	
    }
    
    
    public void updateRatedWord(){
    	WordDBAdapter wordDbAdapter = new WordDBAdapter(this);
  	    try{
  	    	wordDbAdapter.updateReatedWord(currentWord, statisticId);
  	    } catch (Exception e) {
  			Log.d( TAG , "ERROR: " + e.getMessage());
  		} finally {
  			wordDbAdapter.close();
  		}
    }
    
    
    public void showAnswer(){
    	answerLayout.setVisibility(View.VISIBLE);
    	answerLabel.setVisibility(View.VISIBLE);
    	answer.setVisibility(View.VISIBLE);
    	speachAnswerBtn.setVisibility(View.VISIBLE);
    	showAnswerBtn.setVisibility(View.GONE);
    	isAnswerVisible = true;
    }
    
    public void hideAnswer(){
    	answerLayout.setVisibility(View.GONE);
    	answerLabel.setVisibility(View.GONE);
    	answer.setVisibility(View.GONE);
    	speachAnswerBtn.setVisibility(View.GONE);
    	showAnswerBtn.setVisibility(View.VISIBLE);
    	isAnswerVisible = false;
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
    
    
    private void saveStatisticId() {
        SharedPreferences sharedPreferences = getSharedPreferences(STATISTIC_ID_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(STATISTIC_ID_KEY, statisticId);
        editor.commit();
    }

    private long loadStatisticId() { 
        SharedPreferences sharedPreferences = getSharedPreferences(STATISTIC_ID_KEY, MODE_PRIVATE);
        return sharedPreferences.getLong(STATISTIC_ID_KEY, 0);
    }
    
    
    public String getWordToSpeak(boolean isQuestion){
    	if(isQuestion)
    		return (isCardSwitched ?  currentWord.getQuestion() :  currentWord.getAnsware());
    	return (!isCardSwitched ?  currentWord.getQuestion() :  currentWord.getAnsware());
    }
    
}
