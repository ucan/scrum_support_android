package scrum.support.model;

public class Person implements Comparable<Person> {

	private int id;
	private String name;
	private String email;
	private Task task;
	
	
	/**
	 * Create a new team member.
	 * @param name
	 * @param email
	 * @param task - Can be null
	 */
	public Person(int id, String name, String email, Task task) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.task = task;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	public Task getTask() {
		return task;
	}

	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Person)) {
			return false;
		}
		Person otherP = (Person)other;
		return otherP.id == this.id && otherP.email == this.email;
	}
	
	@Override
	public int hashCode() {
		int result = 5;
		result = 7 * result + id;
		result = 7 * result + name.hashCode();
		result = 7 * result + email.hashCode();
		//result = 7 * result + task.hashCode();
		return result;
	}

	public int compareTo(Person another) {
		return name.compareTo(another.name);
	}
}
