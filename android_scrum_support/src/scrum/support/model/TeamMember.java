package scrum.support.model;

import scrum.support.model.Util.Status;
import android.os.Parcel;
import android.os.Parcelable;

public class TeamMember implements Comparable<TeamMember>, Parcelable {

	private int id;
	private String name;
	private String email;
	private Task task;
	private boolean me;
	
	
	/**
	 * Create a new team member.
	 * @param name
	 * @param email
	 * @param task - Can be null
	 */
	public TeamMember(int id, String name, String email, Task task) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.task = task;
		me = false;
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
		task.setStatus(Status.STARTED);
	}
	
	public Task getTask() {
		return task;
	}

	public boolean hasTask() {
		return task != null;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TeamMember)) {
			return false;
		}
		TeamMember otherP = (TeamMember)other;
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

	public int compareTo(TeamMember another) {
		return name.compareTo(another.name);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeValue(task);
	}
	
	public static final Parcelable.Creator<TeamMember> CREATOR = new Parcelable.Creator<TeamMember>() {
		public TeamMember createFromParcel(Parcel in) {
		    return new TeamMember(in);
		}
		
		public TeamMember[] newArray(int size) {
		    return new TeamMember[size];
		}
	};
	
	private TeamMember(Parcel in) {
		id = in.readInt();
		email = in.readString();
		name = in.readString();
		task = (Task)in.readValue(Task.class.getClassLoader());
	}
	
	public boolean isMe() {
		return name.contains("Jon");
	}

	public void setMe() {
		me = true;		
	}
}
