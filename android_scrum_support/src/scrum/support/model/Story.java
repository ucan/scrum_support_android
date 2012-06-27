package scrum.support.model;

import java.util.Set;

public class Story {

	enum Status {
		Done,
		Started,
		NotStarted,
		Blocked
	}
	
	String title;
	Project project;
	Set<Task> task;
	User owner;
	User requestedBy;
	Status currentStatus;
}
