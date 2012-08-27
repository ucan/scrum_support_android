package scrum.support;

import java.net.MalformedURLException;
import java.util.Observable;
import java.util.Observer;

import scrum.support.model.User;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//TODO: Check network connection


/**
 * The login activity which is the first activity seen by 
 * users who are not logged in automatically.
 * 
 * @author Dave W
 *
 */
public class LoginActivity extends Activity implements Observer {

	private static final int CONFIRM_PASS = 1;

	private Button loginButton;
	private Button registerButton;
	
	private Button serverBtn;
	private EditText serverIP;
	private TextView serverLbl;
	
	private ProgressDialog pd;
	private Activity activity;
	private AsyncTask<User, Integer, Boolean> authThread;
	
	private User currentUser;
	
	private boolean hideServerFields;
	
    /** 
     * Called when the activity is first created. 
     * Is the first (main) activity
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	activity = this;
    	ErrorService.getInstance().addObserver(this);
    	ContentProvider.getInstance().setContext(this.getApplicationContext());
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setupServerAddressConfig();
        
        //TODO: Remove following two lines before release
        ((EditText) findViewById(R.id.emailField)).setText("abc@hello.com");
        ((EditText) findViewById(R.id.passwordField)).setText("t");

		loginButton = (Button)findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener(){  
            public void onClick(View v) { 
        		String email = ((EditText) findViewById(R.id.emailField)).getText().toString();
            	String password = ((EditText) findViewById(R.id.passwordField)).getText().toString();
            	login(email, password, true);
            }  
        });
		
		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(new OnClickListener(){  
            public void onClick(View v) { 
            	Intent confirmIntent = new Intent(activity, PassConfirmActivity.class);
            	activity.startActivityForResult(confirmIntent, CONFIRM_PASS);
            }  
        });
    }
    
    /**
     * Confirm the server address OK button to update the new address.
     */
    private void setupServerAddressConfig() {
    	serverBtn = (Button) findViewById(R.id.serverOkButton);
		serverIP = (EditText) findViewById(R.id.serverField);
		serverLbl = (TextView) findViewById(R.id.serverAddressLbl); 
		serverBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					ContentProvider.getInstance().updateServer(serverIP.getText().toString());
					toggleServerFields();
				} catch (MalformedURLException e) {
					AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
					alertDialog.setTitle("Server Address");
					alertDialog.setMessage("The address you entered was not valid");
					alertDialog.show();
				}
			}
		});
		hideServerFields = true;
		toggleServerFields();
    }
    
    /**
     * When the menu button is used the server related buttons will
     * toggle visible and invisible.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	toggleServerFields();
    	return true;
    }

	/**
     * Used to show and hide the server setting widgets.
     * @param hide
     */
    private void toggleServerFields() {		
		if(hideServerFields) {
			serverBtn.setVisibility(View.GONE);
			serverIP.setVisibility(View.GONE);
			serverLbl.setVisibility(View.GONE);
			hideServerFields = false;
		} else {
			serverBtn.setVisibility(View.VISIBLE);
			serverIP.setText(ContentProvider.getInstance().getServerAddress().toString());
			serverIP.setVisibility(View.VISIBLE);
			serverLbl.setVisibility(View.VISIBLE);	
			hideServerFields = true;
		}		
	}
    
    private void login(String email, String password, boolean hasRegistered) {
    	currentUser = new User(email, password, hasRegistered);
    	authThread = new AuthenticateUser();
    	authThread.execute();
    }
    
	/**
	 * Called when an activity returns.
	 * 
	 * CONFIRM_PASS is called when the password confirmation activity is returned.
	 * Once returned the app tries to register the new user.
	 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONFIRM_PASS:
                String confirmPass = data.getStringExtra("confirmedPass");
                if (confirmPass != null && confirmPass.length() > 0) {
                	String email = ((EditText) findViewById(R.id.emailField)).getText().toString();
                	String password = ((EditText) findViewById(R.id.passwordField)).getText().toString();
                	if (confirmPass.equals(password)) {
                		login(email, password, false);
                	}
                	else {
                		Toast t = Toast.makeText(activity, R.string.passwordsDontMatch, 2);
                		t.show();
                	}
                }
                else {
                	// TODO: ??
                }
                break;
            default:
                break;
        }
    }

    /**
     * Interrupts the thread and posts an error message.
     */
	public void update(Observable arg0, Object arg1) {
		if(authThread != null) authThread.cancel(true);
	}
	
	/**
	 * A worker thread to manage the authenticate and network activity
	 *
	 */
	private class AuthenticateUser extends AsyncTask<User, Integer, Boolean> {
				
		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(activity, 
					activity.getString(R.string.title_auth_progress), 
					activity.getString(R.string.msg_auth), true, false);
		}

		@Override
		protected Boolean doInBackground(User...params) {
			return ContentProvider.getInstance().validateUser(currentUser);
		}

		@Override
	     protected void onPostExecute(Boolean result) {
	    	 pd.dismiss();
	    	 if(result != null) {
	    		 if(result) {
	    			 activity.startActivity(new Intent(activity, ProjectActivity.class));
		    	 } else {
		    		 
	    	 		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
					if(currentUser.isRegistered()) {
						alertDialog.setTitle(activity.getString(R.string.title_auth));
						alertDialog.setMessage(activity.getString(R.string.error_authentication));
					} else {
						alertDialog.setTitle(activity.getString(R.string.title_reg));
						alertDialog.setMessage(activity.getString(R.string.error_registration));
					}
					alertDialog.setButton(activity.getString(android.R.string.ok), 
							new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					});
	    	 		alertDialog.show();
		    	 }
	    	 } else {
	    		 
	    		 // Result will only be null if an error has occurred. 
	    		 // Pull the last error out of the ErrorService.
	    		 
	    			final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
	    			alertDialog.setTitle(activity.getString(R.string.title_error));
	    			alertDialog.setMessage(ErrorService.getInstance().getError());
					alertDialog.setButton(activity.getString(android.R.string.ok), 
							new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					});
	    			alertDialog.show();	
	    	 }
		}
	 }
}