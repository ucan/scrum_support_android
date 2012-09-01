package scrum.support.model;

import android.os.Parcel;
import android.os.Parcelable;
import scrum.support.model.Util.Status;

public class Task implements Comparable<Task>, Parcelable {
	
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
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Status getStatus() {
		return currentStatus;
	}
	
	public void setStatus(Status status) {
		currentStatus = status;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Task)) {
			return false;
		}
		Task otherTask = (Task)other;
		return otherTask.id == this.id;
	}
	
	@Override
	public int hashCode() {
		int result = 9;
		result = 7 * result + id;
		result = 7 * result + description.hashCode();
		return result;
	}
	
	public int compareTo(Task other) {
		return description.compareTo(other.description); //TODO: Maybe need to sort by project?
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(description);
		dest.writeString(currentStatus.toString());
	}
	
	public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
		public Task createFromParcel(Parcel in) {
		    return new Task(in);
		}

		public Task[] newArray(int size) {
		    return new Task[size];
		}
	};

	private Task(Parcel in) {
		id = in.readInt();
		description = in.readString();
		currentStatus = Status.fromString(in.readString());
	}

}
