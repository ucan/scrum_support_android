package scrum.support;

import java.util.List;

import scrum.support.model.Project;
import scrum.support.services.ContentProvider;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ProjectActivity extends ListActivity {
    /** Called when the activity is first created. */
	
	private List<Project> projects;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        projects = ContentProvider.getInstance().getProjects();
        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_1, projects);
        setListAdapter(adapter);
        
        
//        // Create a progress bar to display while the list loads
//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setLayoutParams(new LayoutParams(
//                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//        progressBar.setIndeterminate(true);
//        getListView().setEmptyView(progressBar);
//        
//        // Must add the progress bar to the root of the layout
//        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        root.addView(progressBar);
//
//        // For the cursor adapter, specify which columns go into which views
//        String[] fromColumns = {"Title"};
//
//        // WIP
//        ListAdapter mAdapter = new ProjectListAdapter(this, 0, 0, projects);

    }
}