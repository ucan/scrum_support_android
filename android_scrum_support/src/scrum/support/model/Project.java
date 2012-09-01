package scrum.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
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
	
	public List<Task> getAllTasks() {
		List<Task> tasks = new ArrayList<Task>();
		for (Story story : stories) {
			tasks.addAll(story.getTasks());
		}
		return tasks;
	}
	
	public String toString() {
		return title;
	}

	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeTypedList(new ArrayList<Story>(stories));
		dest.writeTypedList(new ArrayList<Person>(people));
	}
	
	public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
		public Project createFromParcel(Parcel in) {
		    return new Project(in);
		}

		public Project[] newArray(int size) {
		    return new Project[size];
		}
	};

	private Project(Parcel in) {
		id = in.readInt();
		title = in.readString();
		stories = new TreeSet<Story>(in.createTypedArrayList(Story.CREATOR));
		people = new TreeSet<Person>(in.createTypedArrayList(Person.CREATOR));
	}
}
