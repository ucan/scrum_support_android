package scrum.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import scrum.support.model.Account;
import scrum.support.model.Person;
import scrum.support.model.Project;
import scrum.support.model.Task;
import scrum.support.model.User;
import scrum.support.model.Util.Status;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.app.Activity;
import android.app.AlertDialog;
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


public class PeopleActivity extends ListActivity implements Observer {

	private PersonThread personThread;
	private PersonAdapter personAdapter;
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
    	personAdapter = new PersonAdapter(this, R.layout.person_row, R.id.person_name, new ArrayList<Person>());
    	setContentView(R.layout.relative_listview);
        setListAdapter(personAdapter);
        updatePeople();
    }
    
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
    
    private void startStoryIntent() {
		Log.i("PERSON ACTIVITY", "StartStoryIntent");
		Intent storyIntent = new Intent(activity, StoryActivity.class);
    	storyIntent.putExtra("android.scrum.support.StoryActivity.PROJECT", currentProject);
    	activity.startActivityForResult(storyIntent, 0);
    }
    
    private void startTaskViewIntent(Person person, boolean isUser) {
    	Intent taskViewIntent = new Intent(activity, TaskViewActivity.class);
    	taskViewIntent.putExtra("android.scrum.support.TaskViewActivity.PROJECT", currentProject);
    	taskViewIntent.putExtra("android.scrum.support.TaskViewActivity.PERSON", person);
    	taskViewIntent.putExtra("android.scrum.support.TaskViewActivity.IS_USER", isUser);
    	activity.startActivityForResult(taskViewIntent, 1);
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
	    			 personAdapter.clear();
	    			 User user = ContentProvider.getInstance().getUser();
	    			 Account account = user.getAccountForProject(currentProject.getId());
	    			 
	    			 // Put the user first in the list
	    			 for (Person person : currentProject.getPeople()) {
	    				 if (person.getEmail().equals(account.getEmail())) {
	    					 personAdapter.insert(person, 0);
	    				 }
	    				 else {
	    					 personAdapter.add(person);
	    				 }
	    			 }
	    			 personAdapter.notifyDataSetChanged();
	    			 setProgressBarIndeterminateVisibility(false);
	    			 Log.d("PERSON ACTIVITY", "projects size = " + currentProject.getPeople().size());
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

	private class PersonAdapter extends ArrayAdapter<Person> {
		
		public PersonAdapter(Context context, int resource,	int textViewResourceId, List<Person> people) {
			super(context, resource, textViewResourceId, people);
		}
		
		@Override
		public View getView (int position, View convertView, ViewGroup parent) {
			Person person = this.getItem(position);
			View personRow = View.inflate(getContext(), R.layout.person_row, null);
			if (personRow != null) {
				TextView personName = (TextView) personRow.findViewById(R.id.person_name);
				if(personName != null) {
					personName.setText(person.getName());
				}
				TextView personTask = (TextView) personRow.findViewById(R.id.person_task);
				if(personTask != null) {
					Task task = person.getTask();
					String taskLabel = (task == null) ? activity.getString(R.string.noTask) : task.getDescription();
					personTask.setText(taskLabel);
				}
			}
			return personRow;
		}
		
	}
}


