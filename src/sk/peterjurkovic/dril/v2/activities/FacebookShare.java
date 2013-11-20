package sk.peterjurkovic.dril.v2.activities;

import java.util.Date;

import sk.peterjurkovic.dril.R;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;

public class FacebookShare extends BaseActivity{
	
	private static final String PERMISSION = "publish_actions";
	private final String PENDING_ACTION_BUNDLE_KEY = "sk.peterjurkovic.dril.v2.activities.FacebookShare";
	private UiLifecycleHelper uiHelper;
	private PendingAction pendingAction = PendingAction.NONE;
	private boolean canPresentShareDialog;
	private Button postStatusUpdateButton;
	private GraphUser user;
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
            pendingAction = PendingAction.valueOf(name);
        }
	    postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });
	}
	
	 private void onClickPostStatusUpdate() {
	        //performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
		 
		 FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
	        .setLink("https://developers.facebook.com/android")
	        .build();
		 uiHelper.trackPendingDialogCall(shareDialog.present());
		 
   }
	
	/*
	 private void performPublish(PendingAction action, boolean allowNoSession) {
	        Session session = Session.getActiveSession();
	        if (session != null) {
	            pendingAction = action;
	            if (hasPublishPermission()) {
	                // We can do the action right away.
	                handlePendingAction();
	                return;
	            } else if (session.isOpened()) {
	                // We need to get new permissions, then complete the action when we get called back.
	                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
	                return;
	            }
	        }

	        if (allowNoSession) {
	            pendingAction = action;
	            handlePendingAction();
	        }
	    }
	 */
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
      //  updateUI();
    }
	
	@SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }
	
	
	private void postStatusUpdate() {
        if (canPresentShareDialog) {
            FacebookDialog shareDialog = createShareDialogBuilder().build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (user != null && hasPublishPermission()) {
            final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, null, null, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showPublishResult(message, response.getGraphObject(), response.getError());
                        }
                    });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }
	
	private interface GraphObjectWithId extends GraphObject {
        String getId();
    }
	
	private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = getString(R.string.successfully_posted_post, message, id);
        } else {
            title = getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
	
	 private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }
	
	 private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
        return new FacebookDialog.ShareDialogBuilder(this)
                .setName("Hello Facebook")
                .setDescription("The 'Hello Facebook' sample application showcases simple Facebook integration")
                .setLink("http://developers.facebook.com/android");
    }
}
