package scrum.support.model;

import java.util.ArrayList;
import java.util.List;

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

	public boolean addAccount(Account account) {
		if (account != null && !accounts.contains(account)) {
			return accounts.add(account);		
		}
		return false;
	}
}
