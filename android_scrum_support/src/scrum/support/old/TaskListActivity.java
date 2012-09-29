/*package scrum.support.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import scrum.support.R;
import scrum.support.R.id;
import scrum.support.R.layout;
import scrum.support.R.string;
import scrum.support.model.Story;
import scrum.support.model.Task;
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

public class TaskListActivity extends ListActivity implements Observer {
	
	private TaskThread taskThread;
	private TaskAdapter taskAdapter;
	private Story currentStory;
	private Activity activity;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Log.d("TASK LIST ACTIVITY", "Starting");

        Bundle extras = getIntent().getExtras();
        currentStory = (Story) extras.getParcelable("android.scrum.support.TaskListActivity.STORY");
        activity = this;
        taskAdapter = new TaskAdapter(this, R.layout.tasklist_row, R.id.task_description, new ArrayList<Task>());
    	setContentView(R.layout.relative_listview);
        setListAdapter(taskAdapter);

        taskThread = new TaskThread();
        taskThread.execute();
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
  
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
	
	
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
	}
	
	private class TaskThread extends AsyncTask<Task, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(Task...params) {
			ContentProvider.getInstance().fetchTasks(currentStory); // TODO: Change to iteration?
			return true;
		}

		@Override
	     protected void onPostExecute(Boolean result) {
	    	if(result != null) {
	    		if(result) {
	    			taskAdapter.clear();
	    			taskAdapter.addAll(currentStory.getTasks());
	    			taskAdapter.notifyDataSetChanged();
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

	private class TaskAdapter extends ArrayAdapter<Task> {
		
		public TaskAdapter(Context context, int resource, int textViewResourceId, List<Task> tasks) {
			super(context, resource, textViewResourceId, tasks);
		}
		
		@Override
		public View getView (int position, View convertView, ViewGroup parent) {
			Task task = this.getItem(position);
			View taskRow = View.inflate(getContext(), R.layout.tasklist_row, null);
			if (taskRow != null) {
				TextView taskDescription = (TextView) taskRow.findViewById(R.id.task_description);
				if(taskDescription != null) {
					taskDescription.setText(task.getDescription());
				}
				TextView taskStatus = (TextView) taskRow.findViewById(R.id.task_status);
				if(taskStatus != null) {
					taskStatus.setText(task.getStatus().toString());
				}
			}
			return taskRow;
		}
		
		public void addAll(Collection<Task> tasks) {
			for (Task task : tasks) {
				super.add(task);
			}
		}
	}
}


*/