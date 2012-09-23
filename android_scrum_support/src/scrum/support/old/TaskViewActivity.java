package scrum.support.old;

import java.util.Observable;
import java.util.Observer;

import scrum.support.R;
import scrum.support.R.id;
import scrum.support.R.layout;
import scrum.support.model.Person;
import scrum.support.model.Project;
import scrum.support.model.Task;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TaskViewActivity extends Activity implements Observer {
	
	private Project currentProject;
	private Task currentTask;
	private Person person;
	private boolean isUser;
	private Activity activity;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Log.d("TASK LIST ACTIVITY", "Starting");

        Bundle extras = getIntent().getExtras();
        currentProject = (Project) extras.getParcelable("android.scrum.support.TaskViewActivity.PROJECT");
        person = (Person) extras.getParcelable("android.scrum.support.TaskViewActivity.PERSON");
        currentTask = person.getTask();
        isUser = extras.getBoolean("android.scrum.support.TaskViewActivity.IS_USER");
        activity = this;
        
        setContentView(R.layout.taskview);
        setupContent();
        setupClickHandlers();
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// If task has changed
		// get it from data
		// setupContent()
    }
	
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
	}	
	
	private void setupClickHandlers() {
		Button changeTask = (Button) findViewById(R.id.taskview_change_task);
		changeTask.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent storyIntent = new Intent(activity, StoryActivity.class);
		    	storyIntent.putExtra("android.scrum.support.StoryActivity.PROJECT", currentProject);
		    	activity.startActivityForResult(storyIntent, 0);
			}
		});
	}
	
	private void setupContent() {
		if (currentTask != null) {
			TextView taskDescriptionTV = (TextView) findViewById(R.id.taskview_description);
			taskDescriptionTV.setText(currentTask.getDescription());
			
			TextView taskStatusTV = (TextView) findViewById(R.id.taskview_status);
			taskStatusTV.setText(currentTask.getStatus().toString());
			
			TextView taskTimeSpentTV = (TextView) findViewById(R.id.taskview_time_spent);
			taskTimeSpentTV.setText("Time Spent: 0");
			
			TextView taskCommentsTV = (TextView) findViewById(R.id.taskview_comments);
			taskCommentsTV.setText("Comments: Jon Rules!");
		}
		else {
			TextView taskDescriptionTV = (TextView) findViewById(R.id.taskview_description);
			taskDescriptionTV.setText("No current task");  // TODO: Suggest/set a task?
		}
	}
}



