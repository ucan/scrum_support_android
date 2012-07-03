package scrum.support.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Project implements Serializable {

	private int id;
	private String title;
	private SortedSet<Story> stories;
	private SortedSet<Person> people;
	
	public Project(int id, String title) {
		this(id, title, new TreeSet<Story>(), new TreeSet<Person>());
	}
	
	public Project(int id, String title, SortedSet<Story> stories, SortedSet<Person> people) {
		this.id = id;
		this.title = title;
		this.stories = stories;
		this.people = people;
	}
	
	public int getId() {
		return id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean addPerson(Person person) {
		if (person != null) {
			return people.add(person);
		}
		return false;
	}
	
	public boolean removePerson(Person person) {
		if (person != null) {
			return people.remove(person);
		}
		return false;
	}
	
	public SortedSet<Person> getPeople() {
		return Collections.unmodifiableSortedSet(people);
	}
	
	public void setPeople(SortedSet<Person> people) {
		this.people = people;
	}
	
	public boolean addStory(Story story) {
		if (story != null) {
			return stories.add(story);
		}
		return false;
	}
	
	public boolean removeStory(Story story) {
		if (story != null) {
			return stories.remove(story);
		}
		return false;
	}
	
	public SortedSet<Story> getStories() {
		return Collections.unmodifiableSortedSet(stories);
	}
	
	public void setStories(SortedSet<Story> stories) {
		this.stories = stories;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString() {
		return title;
	}
}
