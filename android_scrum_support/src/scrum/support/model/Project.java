package scrum.support.model;

import java.util.SortedSet;

public class Project {

	private int id;
	private String title;
	private SortedSet<Story> stories;
	
	public String getTitle() {
		return title;
	}
	
	public String toString() {
		return title;
	}
	
}
