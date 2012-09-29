package scrum.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import scrum.support.model.Project;
import scrum.support.model.User;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ProjectActivity extends Activity implements Observer {
    /** Called when the activity is first created. */
	
	
	private final int NEW_ACCOUNT = 1;
	private final int SHOW_PEOPLE = 2;
	
	
	private AsyncTask<User, Integer, Boolean> projectThread; 
	private ArrayAdapter<Project> projectsAdapter;
	
	private Activity activity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setTitle("My Projects");
        
        Log.d("PROJECT ACTIVITY", "Starting");
        activity = this;
    	ErrorService.getInstance().addObserver(this);
    	projectsAdapter = new ArrayAdapter<Project>(this, R.layout.project_row, R.id.project_title, new ArrayList<Project>());
    	projectsAdapter.setNotifyOnChange(false);
    	
    	User currentUser = ContentProvider.getInstance().getUser();
    	ArrayAdapter<scrum.support.model.Account> accountAdapter = 
    			new ArrayAdapter<scrum.support.model.Account>(this, R.layout.project_row, R.id.project_title);
    	for(scrum.support.model.Account a : currentUser.getAccounts()) {
    		accountAdapter.add(a);
    	}
    	
    	setContentView(R.layout.projects);
    	
    	ListView lvAccounts = (ListView) findViewById(R.id.accountList);
    	lvAccounts.setAdapter(accountAdapter);
    	ListView lvProjects = (ListView) findViewById(R.id.projectList);
    	lvProjects.setAdapter(projectsAdapter);
    	lvProjects.setOnItemClickListener(projectItemClickListener());

        updateProjects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.projects_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_account:
            	Intent confirmIntent = new Intent(activity, NewAccountActivity.class);
            	activity.startActivityForResult(confirmIntent, NEW_ACCOUNT);	
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public OnItemClickListener projectItemClickListener() {
    	return new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
    			Log.i("PROJECT ACTIVITY", projectsAdapter.getItem(arg2).toString());
    			Intent personIntent = new Intent(activity, TeamActivity.class);
    			Project currentProject = projectsAdapter.getItem(arg2);
    			personIntent.putExtra("android.scrum.support.ProjectActivity.PROJECT", currentProject);
    			activity.startActivityForResult(personIntent, SHOW_PEOPLE);
				
			}
    	};
    }

	/**
	 * Called when an activity returns.
	 * 
	 * NEW_TOKEN is called when the new token activity is returned.
	 * Once returned the app tries to get the new projects.
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_ACCOUNT:
            	int accountId = data.getIntExtra("account_id", -1);
            	Log.d("PROJECT ACTIVITY", "Account id = " + accountId);
                if (accountId != -1) {
                	updateProjects();
                }
        }
    }
    
	public void update(Observable arg0, Object arg1) {
		if(projectThread != null) projectThread.cancel(true);
	}
	
	public void updateProjects() {
		projectThread = new ProjectThread();
    	projectThread.execute();
	}
	
	
	private class ProjectThread extends AsyncTask<User, Integer, Boolean> {
		List<Project> projects;
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(User...params) {
			projects = ContentProvider.getInstance().getAllProjects();
			return true;
		}

		@Override
	     protected void onPostExecute(Boolean result) {
	    	 if(result != null) {
	    		 if(result) {
	    			 projectsAdapter.clear();
	    			 for (Project p : projects) {
	    				 projectsAdapter.add(p);
	    			 }
	    			 projectsAdapter.notifyDataSetChanged();
	    			 setProgressBarIndeterminateVisibility(false);
	    			 Log.d("PROJECT ACTIVITY", "projects size = " + projects.size());
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