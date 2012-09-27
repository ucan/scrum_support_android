package scrum.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import scrum.support.R;
import scrum.support.R.id;
import scrum.support.R.layout;
import scrum.support.model.Task;
import scrum.support.model.TeamMember;
import scrum.support.model.Project;
import scrum.support.model.Story;
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

public class TaskSelector extends ListActivity implements Observer {	
	
	private Activity activity;
	private StoryAdapter storyAdapter;
	private Project currentProject;
	private TeamMember me;
	private StoryQuery storyQuery;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Log.d("TASK SELECTOR ACTIVITY", "Starting");

        Bundle extras = getIntent().getExtras();
        currentProject = (Project) extras.getParcelable("android.scrum.support.TaskSelector.PROJECT");
        me = (TeamMember) extras.getParcelable("android.scrum.support.TaskSelector.ME");
        activity = this;
    	storyAdapter = new StoryAdapter(this, R.layout.story_row, R.id.story_title, new ArrayList<Story>());
    	setContentView(R.layout.relative_listview);
        setListAdapter(storyAdapter);
        updateIteration();
    }
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	//	Intent taskListIntent = new Intent(activity, TaskListActivity.class);
	//	Story story = storyAdapter.getItem(position);
	//	taskListIntent.putExtra("android.scrum.support.TaskListActivity.STORY", story);
    //	activity.startActivityForResult(taskListIntent, 0);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {}
    
	
	public void update(Observable observable, Object data) {}
	

	private class StoryAdapter extends ArrayAdapter<Story> {
		
		public StoryAdapter(Context context, int resource,	int textViewResourceId, List<Story> stories) {
			super(context, resource, textViewResourceId, stories);
		}
		
		@Override
		public View getView (int position, View convertView, ViewGroup parent) {
			Story story = this.getItem(position);
			View storyRow = View.inflate(getContext(), R.layout.story_row, null);
			if (storyRow != null) {
				TextView storyTitle = (TextView) storyRow.findViewById(R.id.story_title);
				if(storyTitle != null) {
					storyTitle.setText(story.getTitle());
				}
				TextView storyStatus = (TextView) storyRow.findViewById(R.id.story_status);
				if(storyStatus != null) {
					Status status = story.getStatus();
					storyStatus.setText(status.toString());
				}
			}
			return storyRow;
		}		
		public void addAll(Collection<Story> stories) {
			for (Story story : stories) {
				super.add(story);
			}
		}		
	}
	
	public void updateIteration() {
		storyQuery = new StoryQuery();
		storyQuery.execute();
	}
	
	private class StoryQuery extends AsyncTask<Task, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			activity.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(Task...params) {
			ContentProvider.getInstance().getCurrentStories(currentProject);
			return true;
		}

		@Override
	     protected void onPostExecute(Boolean result) {
	    	if(result != null) {
	    		if(result) {
	    			storyAdapter.clear();
	    			storyAdapter.addAll(currentProject.getCurrentIteration().getStories());
	    			storyAdapter.notifyDataSetChanged();
	    			setProgressBarIndeterminateVisibility(false);
		    	 }
	    	 } 
	    	else {
	    		// Result will only be null if an error has occurred. 
	    		// Pull the last error out of the ErrorService.
	    		 
	    		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
    			alertDialog.setTitle(activity.getString(R.string.title_error));
    			alertDialog.setMessage(ErrorService.getInstance().getError());
				alertDialog.setButton(activity.getString(android.R.string.ok), 	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
	    		alertDialog.show();	
	    	 }
		}
	 }
}


