package scrum.support;

import java.util.List;

import scrum.support.model.Project;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Incomplete
 * @author admin
 *
 */
public class ProjectListAdapter extends ArrayAdapter<Project> {
	
    private List<Project> items;
    private Context context;

	public ProjectListAdapter(Context context, int resource,
			int textViewResourceId, List<Project> projects) {
		super(context, resource, textViewResourceId, projects);
        this.context = context;
        this.items = projects;
	}

    public View getView(int position, View convertView, ViewGroup parent) {
        /*View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item, null);
        }

        Project item = items.get(position);
        if (item!= null) {
            // My layout has only one TextView
            TextView itemView = (TextView) view.findViewById(R.id.ItemView);
            if (itemView != null) {
                // do whatever you want with your string and long
                itemView.setText(String.format("%s %d", item.reason, item.long_val));
            }
         }
         return view*/

        return null;
    }

}
