package scrum.support;

import scrum.support.services.ContentProvider;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class NewAccountActivity extends Activity {
	private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_token);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        this.setTitle("");
        et = ((EditText) findViewById(R.id.tokenField));
        
        // button
        ((Button) findViewById(R.id.addTokenBtn)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
            	ContentProvider.getInstance().addAccount(et.getText().toString());
                executeDone();
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
    private void executeDone() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("account_added", et.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
