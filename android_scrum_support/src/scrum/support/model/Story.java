package scrum.support.model;

import java.util.Set;

public class Story implements Comparable<Story> {

	int id;
	String title;
	Project project;
	Set<Task> task;
	User owner;
	User requestedBy;
	Status currentStatus;
	
	enum Status {
		Done,
		Started,
		NotStarted,
		Blocked
	}
	
	public Story(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public String getTitle() {
		return title;
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
}
