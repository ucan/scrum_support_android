package scrum.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import scrum.support.model.Account;
import scrum.support.model.TeamMember;
import scrum.support.model.Project;
import scrum.support.model.Task;
import scrum.support.model.User;
import scrum.support.model.Util.Status;
import scrum.support.old.StoryActivity;
import scrum.support.old.TaskViewActivity;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class PeopleActivity extends ExpandableListActivity  implements Observer {

	private PersonThread personThread;
	private ExpandTeamAdapter teamAdapter;
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
    	
    	teamAdapter = new ExpandTeamAdapter(this);
        setListAdapter(teamAdapter);
        updatePeople();
    }
    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Person person = personAdapter.getItem(position);
    	ContentProvider contentProvider = ContentProvider.getInstance();
    	boolean isUser = contentProvider.isUser(currentProject, person);
    	if (isUser && person.getTask() == null) {
    		startStoryIntent();
	    	return;
    	}
		else {
			startTaskViewIntent(person, isUser);
		}
    	
    	// Doesn't do much...just sets the current task status to some random value
//    	Person person = personAdapter.getItem(position);
//    	Log.i("PROJECT ACTIVITY", person.getName());
//    	Task task = person.getTask();
//    	if (task != null) {
//    		Status[] whatAmIdoingHereArrrggghhhh = Status.values();
//    		task.setStatus(whatAmIdoingHereArrrggghhhh[new Random().nextInt(whatAmIdoingHereArrrggghhhh.length)]);
//    		ContentProvider.getInstance().updateTask(task);
//    	}
    }
    */
/*    private void startStoryIntent() {
		Log.i("PERSON ACTIVITY", "StartStoryIntent");
		Intent storyIntent = new Intent(activity, StoryActivity.class);
    	storyIntent.putExtra("android.scrum.support.StoryActivity.PROJECT", currentProject);
    	activity.startActivityForResult(storyIntent, 0);
    }
    
    private void startTaskViewIntent(TeamMember person, boolean isUser) {
    	Intent taskViewIntent = new Intent(activity, TaskViewActivity.class);
    	taskViewIntent.putExtra("android.scrum.support.TaskViewActivity.PROJECT", currentProject);
    	taskViewIntent.putExtra("android.scrum.support.TaskViewActivity.PERSON", person);
    	taskViewIntent.putExtra("android.scrum.support.TaskViewActivity.IS_USER", isUser);
    	activity.startActivityForResult(taskViewIntent, 1);
    }*/
    
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


