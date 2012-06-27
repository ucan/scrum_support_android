package scrum.support.model;

public class Task {

	enum Status {
		Done,
		NotStarted,
	}
	
	String description;
	Status currentStatus;
	
	public Task(String description) {
		this.description = description;
		currentStatus = Status.NotStarted;
	}
	
	public Task(String description, Status status) {
		this.description = description;
		currentStatus = status;
	}
}
