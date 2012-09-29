package scrum.support;

import java.util.ArrayList;

import scrum.support.model.Iteration;
import scrum.support.model.Story;
import scrum.support.model.TeamMember;
import scrum.support.model.Project;
import scrum.support.model.Task;
import scrum.support.model.Util.Status;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ExpandableStoriesAdapter extends BaseExpandableListAdapter {
	
	private Activity activity;
	private Project project;
	private Iteration iteration;
	private ArrayList<Story> stories;
	private TeamMember me;
	
	public ExpandableStoriesAdapter(Activity activity, Project project) {
		this.activity = activity;
		this.me = me;
		stories = new ArrayList<Story>();
		this.project = project;
	}
		
	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
		stories.clear();
		for(Story story : iteration.getStories()) {
			stories.add(story);
		}
		notifyDataSetChanged();	
	}

	/**
	 * Get the specific task at the story index
	 */
	public Object getChild(int storyIndex, int taskIndex) {
		return getGroupCount() > storyIndex ? stories.get(storyIndex).getTasks().get(taskIndex) : 0;
	}

	/**
	 * Returns the id of the task
	 */
	public long getChildId(int storyIndex, int taskIndex) {
		return getGroupCount() > storyIndex ? 
				stories.get(storyIndex).getTasks().get(taskIndex).getId() : 0;
	}

	public View getChildView(int storyIndex, int taskIndex, boolean isLastChild, 
			View convertView, ViewGroup parent) {

        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       	Task task = stories.get(storyIndex).getTasks().get(taskIndex);
		if(task.getStatus().selectable()) {
			convertView = infalInflater.inflate(R.layout.taskview, null);
			
			TextView taskText = (TextView) convertView.findViewById(R.id.task_description);
			taskText.setText(task.getDescription());
			
			Button taskSelector = (Button) convertView.findViewById(R.id.selectTask);	
			taskSelector.setOnClickListener(selectTaskListener(task));
		}        
        
        return convertView;
	}

	/**
	 * Selects the task for this person
	 * @param task
	 * @return
	 */
	private OnClickListener selectTaskListener(final Task task) {
		return new OnClickListener() {		

			public void onClick(View arg0) {
				// TODO: Select task for me, need to make it parcelable first.
				//	project.getMe().setTask(task);
				Intent intent = new Intent(activity, TeamActivity.class);
		        intent.putExtra("android.scrum.support.ProjectActivity.PROJECT", project);
		    	activity.startActivityForResult(intent, 0);		
			}
		};
	}

	/** 
	 * The number of tasks for this story
	 */
	public int getChildrenCount(int storyIndex) {
		return getGroupCount() > storyIndex ? stories.get(storyIndex).getTasks().size() : 0;
	}

	/**
	 * Returns the story object
	 */
	public Object getGroup(int storyIndex) {
		return getGroupCount() > storyIndex ? stories.get(storyIndex) : null;
	}

	/**
	 * Returns the number of stories in this iteration
	 */
	public int getGroupCount() {
		return stories.size();
	}

	/**
	 * Returns the ID for this story
	 */
	public long getGroupId(int storyIndex) {
		return getGroupCount() > storyIndex ? stories.get(storyIndex).getId() : 0;
	}

	public View getGroupView(int storyIndex, boolean isLastChild, View view,
            ViewGroup parent) {
		
		Story story = stories.get(storyIndex);
		
        if (view == null) {		
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
            view = inf.inflate(R.layout.story_row, null);		
        }
		
        TextView tv = (TextView) view.findViewById(R.id.story_title);		
        tv.setText(story.getTitle());
        return view;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public void clear() {
		stories.clear();		
	}
}
