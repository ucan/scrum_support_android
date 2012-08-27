package scrum.support;

import scrum.support.services.ContentProvider;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewAccountActivity extends Activity {

	private EditText usernameET;
	private EditText passwordET;
	
	private enum AccountType {
		PIVOTAL_TRACKER("PtAccount");
		//AGILEFANT,
		//FULCRUM;
		
		private final String name;
		
		private AccountType(String name) {
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_account);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
//                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//        this.setTitle("");
        usernameET = ((EditText) findViewById(R.id.newAccountUsernameET));
        passwordET = ((EditText) findViewById(R.id.newAccountPasswordET));
        
        //TODO:  Remove me before release
        usernameET.setText("jrr66@uclive.ac.nz");
        
        
        ((Button) findViewById(R.id.addAccountBtn)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String username = usernameET.getText().toString();
            	String password = passwordET.getText().toString();
            	int accountId = ContentProvider.getInstance().addAccount(AccountType.PIVOTAL_TRACKER.toString(), username, password); //TODO: Account type selector
                executeDone(accountId);
            }
        });
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    /**
     *
     */
    private void executeDone(int accountId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("account_id", accountId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
    