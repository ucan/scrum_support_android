package scrum.support.model;

public class Task {

	public enum Status {
		DONE,
		STARTED,
		NOT_STARTED,
		BLOCKED;
	}
	
	int id;
	String description;
	Status currentStatus;
	
	public Task(int id, String description) {
		this(id, description, Status.NOT_STARTED);
	}
	
	public Task(int id, String description, Status status) {
		this.id = id;
		this.description = description;
		currentStatus = status != null ? status : Status.NOT_STARTED;
	}
	
	public String getDescription() {
		return description;
	}
}
