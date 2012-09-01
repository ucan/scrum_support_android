package scrum.support.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**
 * A simple user model
 * @author Dave W
 *
 */
public class User {
	
	private String password;
	private String email;
	private Token token;
	private List<Account> accounts;
	private boolean hasRegistered;
	
	/**
	 * Constructor
	 * @param email
	 * @param password
	 * @param register
	 */
	public User(String email, String password, boolean hasRegistered) {
		this.email = email;
		this.password = password;
		accounts = new ArrayList<Account>();
		this.hasRegistered = hasRegistered;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isRegistered() {
		return hasRegistered;
	}

	public void setToken(String token) {
		this.token = new Token(token);
	}

	public Token getToken() {
		return token;
	}

	public boolean addAccount(Account account) {    // TODO: Need to check if this is updating an existing account/creating a new one
		if (account != null && !accounts.contains(account)) {
			return accounts.add(account);		
		}
		return false;
	}
	
	// Convenience method, called after user has logged in
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public Account getAccountForProject(int projectId) {
		for (Account account : accounts) {
			Project project = account.getProject(projectId);
			if (project != null) {
				return account;
			}
		}
		return null;
	}
	
	public List<Account> getAccounts() {
		return Collections.unmodifiableList(accounts);
	}
	
	public List<Project> getAllProjects() {
		List<Project> projects = new ArrayList<Project>();
		for (Account account : accounts) {
			projects.addAll(account.getProjects());
		}
		return projects;
	}
}
