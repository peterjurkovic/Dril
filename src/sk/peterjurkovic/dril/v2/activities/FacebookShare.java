package sk.peterjurkovic.dril.v2.activities;

import sk.peterjurkovic.dril.R;
import sk.peterjurkovic.dril.v2.constants.Constants;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

/**
 * 
 * @author Peter Jurkovič
 * @date Nov 23, 2013
 *
 */
public class FacebookShare extends BaseActivity{
	
	public static final String EXTRA_LEARNED_DARDS = "noLearnedCards";

	private final String PENDING_ACTION_BUNDLE_KEY = "sk.peterjurkovic.dril.v2.activities.FacebookShare";
	private UiLifecycleHelper uiHelper;
	private PendingAction pendingAction = PendingAction.NONE;
	private Button sharOnFacebookBtn;
	private Button activateNewWordsBtn;
	private Context context;
	private TextView label;
	private int learnedCards = 1;
	
	private enum PendingAction {
	        NONE,
	        POST_PHOTO,
	        POST_STATUS_UPDATE
	    }
	
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	       onSessionStateChange(session, state, exception);
	    }
	};
	    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.v2_facebook_share_layout);
	    this.uiHelper = new UiLifecycleHelper(this, callback);
	    this.uiHelper.onCreate(savedInstanceState);
	    
	    if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            if(name != null){
            	pendingAction = PendingAction.valueOf(name);
            }
        }
	    this.context = this;
	    initViews();
	    
	}
	
	protected void initViews(){
		sharOnFacebookBtn = (Button) findViewById(R.id.facebookShareBtn);
		
		if(isFacebookSharingEnabled()){
			sharOnFacebookBtn.setVisibility(View.VISIBLE);
			sharOnFacebookBtn.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View view) {
	                onClickPostStatusUpdate();
	            }
	        });
		}
		
	    activateNewWordsBtn = (Button) findViewById(R.id.drilActivateNextWords);
	    activateNewWordsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, BookListActivity.class);
				startActivity(intent);
				
			}
		});
	    
	    Intent intent = getIntent();
	    learnedCards = intent.getIntExtra(EXTRA_LEARNED_DARDS, 1);
	    label = (TextView) findViewById(R.id.drilFinishedLabel);
	    label.setText(getString(R.string.dril_finished, learnedCards));
	    
		
	}
	
	private boolean isFacebookSharingEnabled(){
		return FacebookDialog.canPresentShareDialog(getApplicationContext(),  
			   FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
	}
	
	 private void onClickPostStatusUpdate() {
		if (isFacebookSharingEnabled()) {
			 FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
			 .setLink(Constants.DRIL_HOMEPAGE_URL)
			 .setPicture(getPictureUrl())
			 .setName(getFacebokTitle())
			 .setCaption(getString(R.string.facebook_caption))
		        .build();
			 uiHelper.trackPendingDialogCall(shareDialog.present());
			 logSharing();
		}else{
			Toast.makeText(getApplicationContext(), "No FB app instalated", Toast.LENGTH_LONG).show();
		}
 
   }
	 
   private void logSharing(){
	   EasyTracker.getInstance(this)
	   .send(MapBuilder.createEvent("Social", "Dril result share", "Clicked", (long) 1).build());
	   
   }

   private String getPictureUrl(){
	   return Constants.FB_IMAGE_URL.replace("{locale}", Constants.APP_VARIANT);
   }
   
   private String getFacebokTitle(){
	   String name = getResources().getString(R.string.facebook_share_tile, learnedCards);
	   return name;
   }
   
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e("Activity", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.i("Activity", "Success!");
	        }
	    });
	}
	
	
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}


	
	 private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (pendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                exception instanceof FacebookAuthorizationException)) {
                new AlertDialog.Builder(FacebookShare.this)
                    .setTitle(R.string.cancelled)
                    .setMessage(R.string.permission_not_granted)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
    }
	
	@SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        pendingAction = PendingAction.NONE;
        switch (previouslyPendingAction) {
            case POST_STATUS_UPDATE:
            	 onClickPostStatusUpdate();
                break;
        }
    }
	


}
