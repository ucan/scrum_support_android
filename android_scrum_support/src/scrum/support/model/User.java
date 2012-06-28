package scrum.support.model;

/**
 * A simple user model
 * @author Dave W
 *
 */
public class User {
	
	private String password;
	private String email;
	private String password_confirmation;
	private boolean register;
	private Token token;
	
	/**
	 * Constructor
	 * @param email
	 * @param password
	 * @param register
	 */
	public User(String email, String password, boolean register) {
		this.email = email;
		this.password = password;
		this.register = register;
	}
	
	public String getUsername() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getConfirmedPassowrd() {
		return password_confirmation;
	}
	
	public boolean needToRegistered() {
		return register;
	}

	public void setToken(String token) {
		this.token = new Token(token);
	}

	public Token getToken() {
		return token;
	}

}
