package scrum.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {
	
	private int id;
	private int currentIterationId;
	private String title;
	private SortedSet<Iteration> iterations;
	private SortedSet<TeamMember> teamMembers;
	private TeamMember me;
	
	public Project(int id, int currentIterationId, String title) {
		this(id, currentIterationId, title, new TreeSet<Iteration>(), new TreeSet<TeamMember>());
	}
	
	public Project(int id, int currentIterationId, String title, SortedSet<Iteration> iterations, SortedSet<TeamMember> teamMembers) {
		this.id = id;
		this.currentIterationId = currentIterationId;
		//this.currentIterationId = 1;
		this.title = title;
		this.iterations = iterations;
		this.teamMembers = teamMembers;
	}
	
	public int getId() {
		return id;
	}
	
	public Iteration getCurrentIteration() {
		for (Iteration iteration : iterations) {
			if (iteration.getId() == currentIterationId) {
				return iteration;
			}
		}
		return null;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean addTeamMember(TeamMember member) {
		if (member != null) {
			return teamMembers.add(member);
		}
		return false;
	}
	
	public boolean removeTeamMember(TeamMember member) {
		if (member != null) {
			return teamMembers.remove(member);
		}
		return false;
	}
	
	public SortedSet<TeamMember> getTeamMembers() {
		return Collections.unmodifiableSortedSet(teamMembers);
	}
	
	public void setTeamMembers(SortedSet<TeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}
	
	public boolean addIteration(Iteration iteration) {
		if (iteration != null) {
			return iterations.add(iteration);
		}
		return false;
	}
	
	public boolean removeIteration(Iteration iteration) {
		if (iteration != null) {
			return iterations.remove(iteration);
		}
		return false;
	}
	
	public SortedSet<Iteration> getIterations() {
		return Collections.unmodifiableSortedSet(iterations);
	}
	
	public void setIterations(SortedSet<Iteration> iterations) {
		this.iterations = iterations;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString() {
		return title;
	}
	
	public void setMe(TeamMember me) {
		this.me = me;
	}

	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeInt(currentIterationId);
		// TODO: Need to make me parcelable
		//	if(me != null) dest.writeParcelable(me, flags);
		dest.writeTypedList(new ArrayList<Iteration>(iterations));
		dest.writeTypedList(new ArrayList<TeamMember>(teamMembers));
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
		currentIterationId = in.readInt();
		iterations = new TreeSet<Iteration>(in.createTypedArrayList(Iteration.CREATOR));
		teamMembers = new TreeSet<TeamMember>(in.createTypedArrayList(TeamMember.CREATOR));
		// TODO: Need to make me parcelable
		//me = in.readParcelable(TeamMember.class.getClassLoader());
	}

	public TeamMember getMe() {
		return me;
	}
}
