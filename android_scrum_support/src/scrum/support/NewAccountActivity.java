package scrum.support;

import java.util.List;

import scrum.support.model.Project;
import scrum.support.model.User;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class NewAccountActivity extends Activity {
	private EditText et;

	private enum AccountType {
		PIVOTAL_TRACKER("PtAccount");
		//AGILEFANT,
		//FULCRUM
		
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

        setContentView(R.layout.add_token);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
//                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//        this.setTitle("");u.
        et = ((EditText) findViewById(R.id.tokenField));
        
        //TODO:  Remove me before commit
        et.setText("79fecc5af7fb6eb27462f02be67b2d53");
        
        // button
        ((Button) findViewById(R.id.addTokenBtn)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
            	int accountId = ContentProvider.getInstance().addAccount(AccountType.PIVOTAL_TRACKER.toString(), et.getText().toString()); //TODO: Account type selector
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
    