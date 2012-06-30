package scrum.support;

import java.util.List;

import scrum.support.model.Project;
import scrum.support.services.ContentProvider;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

public class ProjectActivity extends ListActivity {
    /** Called when the activity is first created. */
	
	private List<Project> projects;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project);
        
        projects = ContentProvider.getInstance().getProjects();
        
        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
        
        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {"Title"};

        // WIP
        ListAdapter mAdapter = new ProjectListAdapter(this, 0, 0, projects);
        setListAdapter(mAdapter);

    }
}