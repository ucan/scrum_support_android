package scrum.support.services;

import scrum.support.model.User;

import com.google.resting.Resting;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;
import com.google.resting.component.impl.ServiceResponse;


public class RESTService {
	
	protected RESTService() {}
	
	/**
	 * Authenticate a pre-registered user
	 * @param user
	 * @return
	 */
	public ServiceResponse authenicateUser(User user) {	
		RequestParams params = new BasicRequestParams(); 	
		params.add("user", user.getUsername());	
		params.add("password", user.getPassword());	
		return Resting.get("https://localhost", 3000, params);	
	}
	
	/**
	 * Register a new user
	 * @param user
	 * @return
	 */
	public ServiceResponse registerUser(User user) {
		RequestParams params = new BasicRequestParams(); 	
		params.add("user", user.getUsername());	
		params.add("password", user.getPassword());	
		params.add("password_confirmation", user.getConfirmedPassowrd());
		return Resting.post("https://localhost", 3000, params);	
	}
}
