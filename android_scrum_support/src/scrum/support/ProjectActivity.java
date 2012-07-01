package scrum.support;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import scrum.support.model.Project;
import scrum.support.services.ContentProvider;
import scrum.support.services.ErrorService;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ProjectActivity extends ListActivity implements Observer {
    /** Called when the activity is first created. */
	
	private List<Project> projects;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
    	ErrorService.getInstance().addObserver(this);
        projects = ContentProvider.getInstance().getProjects();
        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_1, projects);
        setListAdapter(adapter);
    }

	public void update(Observable arg0, Object arg1) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("An Error has ocurred");
		alertDialog.setMessage(arg1.toString());
		alertDialog.show();			
	}
}