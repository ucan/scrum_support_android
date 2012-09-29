package scrum.support;

import java.util.Observable;
import java.util.Observer;
import scrum.support.model.Project;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;


public class TeamActivity extends ExpandableListActivity  implements Observer {

	private PersonThread personThread;
	private ExpandableTeamAdapter teamAdapter;
	private Project currentProject;
	private Activity activity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Log.d("PERSON ACTIVITY", "Starting");

        Bundle extras = getIntent().getExtras();
        currentProject = (Project) extras.getParcelable("android.scrum.support.ProjectActivity.PROJECT");
        activity = this;
    	
    	setContentView(R.layout.team);
        
        setTitle(currentProject.getTitle() + " Team Members");
    	teamAdapter = new ExpandableTeamAdapter(this);
        setListAdapter(teamAdapter);
        updatePeople();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}
	
	public void updatePeople() {
		personThread = new PersonThread();
		personThread.execute();
	}
	
	public void update(Observable observable, Object data) {
		if(personThread != null) personThread.cancel(true);
	}
	
	private class PersonThread extends AsyncTask<Project, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(Project...params) {
			currentProject = ContentProvider.getInstance().updateProject(currentProject.getId());
			return true;
		}

		@Override
	     protected void onPostExecute(Boolean result) {
	    	 if(result != null) {
	    		 if(result) {
	    			 teamAdapter.addTeam(currentProject);
	    			 teamAdapter.notifyDataSetChanged();
	    			 setProgressBarIndeterminateVisibility(false);
	    			 Log.d("PERSON ACTIVITY", "projects size = " + currentProject.getTeamMembers().size());
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


