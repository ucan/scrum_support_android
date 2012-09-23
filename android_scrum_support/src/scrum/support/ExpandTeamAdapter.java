package scrum.support;

import java.util.ArrayList;
import java.util.SortedSet;

import scrum.support.model.Person;
import scrum.support.model.Project;
import scrum.support.model.Task;
import scrum.support.old.StoryActivity;
import scrum.support.old.TaskViewActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExpandTeamAdapter extends BaseExpandableListAdapter {
	
	private Activity activity;
	private ArrayList<Person> team;
	private Project project;
	
	public ExpandTeamAdapter(Activity activity, Project project) {
		this.activity = activity;
		this.team = new ArrayList<Person>();
	}
		
	public void addTeam(SortedSet<Person> teammembers) {
		team.clear();
		for(Person person : teammembers) {
			if(person.isMe()) {
				team.add(0, person); 
			} else { 
				team.add(person);
			}	
		}
		notifyDataSetChanged();	
	}

	/**
	 * Can only ever be only child task, therefore find the person and return
	 * their task
	 */
	public Object getChild(int personIndex, int arg1) {
		return team.get(personIndex).getTask();
	}

	/**
	 * Can only ever be one child task, there the index is always 0
	 */
	public long getChildId(int groupPosition, int childPosition) {
        return 0;
	}

	public View getChildView(int personIndex, int childPosition, boolean isLastChild, 
			View convertView, ViewGroup parent) {

        LayoutInflater infalInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Person person = team.get(personIndex);
		if(person.isMe()) {
			if(person.hasTask()) {
				convertView = infalInflater.inflate(R.layout.me_expanded_task, null);	
				
			} else {
				convertView = infalInflater.inflate(R.layout.me_expanded_no_task, null);				
			}
			Button taskSelector = (Button) convertView.findViewById(R.id.selectTaskButton);	
			taskSelector.setOnClickListener(changeTaskListener(person));
		}
		else if(person.hasTask()) {
            Task task = team.get(personIndex).getTask();
	      // TODO: Make If statement more robust so it can be used instead of recreating the view each time
            // if (convertView == null  || !(convertView instanceof RelativeLayout)) {
	            convertView = infalInflater.inflate(R.layout.expand_person_task, null);
	     //   }
	
	        TextView tv = (TextView) convertView.findViewById(R.id.personTaskDescription);
	        tv.setText("Current Task: " + task.toString());
	        	// Tag is not used yet
	        	// tv.setTag(task.getTag());
		      // TODO: Make If statement more robust so it can be used instead of recreating the view each time
        } else {//if (convertView == null  || !(convertView instanceof RelativeLayout)) {
            convertView = infalInflater.inflate(R.layout.expand_person_task_empty, null);
        }           
        
        return convertView;
	}

	private OnClickListener changeTaskListener(final Person person) {
		return new OnClickListener() {		

			public void onClick(View arg0) {
				Intent storyIntent = new Intent(activity, StoryActivity.class);
		    	storyIntent.putExtra("android.scrum.support.TaskSelector.PROJECT", project);
		    	storyIntent.putExtra("android.scrum.support.TaskSelector.ME", person);
		    	activity.startActivityForResult(storyIntent, 0);				
			}
		};
	}

	/** 
	 * Return one or zero depending on whether that person has a current task
	 */
	public int getChildrenCount(int personIndex) {
		//return team.get(personIndex).getTask() != null ? 1 : 0;
		return 1;
	}

	public Object getGroup(int personIndex) {
        return team.get(personIndex);
	}

	public int getGroupCount() {
		return team.size();
	}

	public long getGroupId(int personIndex) {
		return personIndex;
	}

	public View getGroupView(int personIndex, boolean isLastChild, View view,
            ViewGroup parent) {
		
		Person person = (Person) getGroup(personIndex);
		
        if (view == null) {		
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
            view = inf.inflate(R.layout.expand_person, null);		
        }
		
        TextView tv = (TextView) view.findViewById(R.id.personName);		
        tv.setText(person.getName());
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
		team.clear();		
	}
}
