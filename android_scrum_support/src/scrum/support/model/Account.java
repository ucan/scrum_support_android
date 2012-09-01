package scrum.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// May need to be changed to PtAccount implements Account...
public class Account {
	
	private int id;
//	private String apiKey;
	private String type;
	private String email;
	private List<Project> projects = new ArrayList<Project>();
	
	public Account(int id, String type, String email) {
		this.id = id;
//		this.apiKey = apiKey;
		this.type = type;
		this.email = email;
	}
	
	public int getId() {
		return id;
	}
	
//	public void setApiKey(String apiKey) {
//		this.apiKey = apiKey;
//	}
//	
//	public String getApiKey() {
//		return apiKey;
//	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public boolean addProject(Project p) {   // TODO: Need to check if this is updating an existing project/creating a new one
		if (p != null && !projects.contains(p)) {
			return projects.add(p);
		}
		return false;
	}
	
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
	public List<Project> getProjects() {
		return Collections.unmodifiableList(projects);
	}
	
	public Project getProject(int id) {
		for (Project project : projects) {
			if (project.getId() == id) {
				return project;
			}
		}
		return null;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Account)) {
			return false;
		}
		Account otherA = (Account)other;
		return otherA.id == this.id && otherA.email == this.email;
	}
	
	@Override
	public int hashCode() {
		int result = 13;
		result = 5 * result + id;
		result = 5 * result + email.hashCode();
		return result;
	}
}
