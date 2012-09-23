package scrum.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import android.os.Parcel;
import android.os.Parcelable;

public class Iteration implements Comparable<Iteration>, Parcelable {
	
	private int id;
	private long startDate;
	private long endDate;
	private SortedSet<Story> stories;

	public Iteration(int id, long startDate, long endDate) {
		this(id, startDate, endDate, new TreeSet<Story>());
	}
	
	public Iteration(int id, long startDate, long endDate, SortedSet<Story> stories) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.stories = stories;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean addStory(Story story) {
		if (story != null) {
			return stories.add(story);
		}
		return false;
	}
	
	public boolean removeStory(Iteration story) {
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
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Iteration)) {
			return false;
		}
		Iteration otherIteration = (Iteration)other;
		return otherIteration.id == this.id;
	}
	
	@Override
	public int hashCode() {
		int result = 13;
		result = 5 * result + id;
		result = 5 * result + stories.hashCode();
		return result;
	}
	
	public int compareTo(Iteration another) {
		return ((Integer)id).compareTo(another.id); //TODO: Maybe need to sort by project?
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeLong(startDate);
		dest.writeLong(endDate);
		dest.writeTypedList(new ArrayList<Story>(stories));
	}
	
	public static final Parcelable.Creator<Iteration> CREATOR = new Parcelable.Creator<Iteration>() {
		public Iteration createFromParcel(Parcel in) {
		    return new Iteration(in);
		}

		public Iteration[] newArray(int size) {
		    return new Iteration[size];
		}
	};
	
	private Iteration(Parcel in) {
		id = in.readInt();
		startDate = in.readLong();
		endDate = in.readLong();
		stories = new TreeSet<Story>(in.createTypedArrayList(Story.CREATOR));
	}
}
