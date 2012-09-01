package scrum.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import scrum.support.model.Util.Status;
import android.os.Parcel;
import android.os.Parcelable;

public class Story implements Comparable<Story>, Parcelable {

	int id;
	String title;
	Status currentStatus = Status.NOT_STARTED;
	Set<Task> tasks = new TreeSet<Task>();
	
	public Story(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Status getStatus() {
		return currentStatus;
	}
	
	public Set<Task> getTasks() {
		return Collections.unmodifiableSet(tasks);
	}
	
	public void addTask(Task task) {
//		if (task != null && !tasks.contains(task)) {
//			
		tasks.add(task);
//		}
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Story)) {
			return false;
		}
		Story otherP = (Story)other;
		return otherP.id == this.id;
	}
	
	@Override
	public int hashCode() {
		int result = 21;
		result = 17 * result + id;
		result = 17 * result + title.hashCode();
		return result;
	}

	public int compareTo(Story another) {
		return title.compareTo(another.title); //TODO: Maybe need to sort by project?
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeString(currentStatus.toString());
		dest.writeTypedList(new ArrayList<Task>(tasks));
	}
	
	public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
		public Story createFromParcel(Parcel in) {
		    return new Story(in);
		}
		
		public Story[] newArray(int size) {
		    return new Story[size];
		}
	};

	private Story(Parcel in) {
		id = in.readInt();
		title = in.readString();
		currentStatus = Status.fromString(in.readString());
		tasks = new TreeSet<Task>(in.createTypedArrayList(Task.CREATOR));
	}
}
