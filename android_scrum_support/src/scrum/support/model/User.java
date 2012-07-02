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
	private String password_confirmation;
	private Token token;
	private List<String> accountTokens;
	
	/**
	 * Constructor
	 * @param email
	 * @param password
	 * @param register
	 */
	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.password_confirmation = ""; 
		accountTokens = new ArrayList<String>();
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getConfirmedPassword() {
		return password_confirmation;
	}
	
	public boolean needsToRegister() {
		return password_confirmation.length() > 0;
	}

	public void setToken(String token) {
		this.token = new Token(token);
	}

	public Token getToken() {
		return token;
	}

	public void confirmPass(String confirmPass) {
		password_confirmation = confirmPass;		
	}

	public void addAccount(String account) {
		accountTokens.add(account);		
	}
}
