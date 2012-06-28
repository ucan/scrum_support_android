package scrum.support.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import scrum.support.model.Project;
import scrum.support.model.Token;
import scrum.support.model.User;

import com.google.resting.Resting;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;
import com.google.resting.component.impl.ServiceResponse;


public class RESTService {
	
	private static URL baseURL;
	
	protected RESTService() {
		try {
			baseURL = new URL("http://localhost/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
	}
	
	public ServiceResponse getUserLink() {
		RequestParams params = new BasicRequestParams(); 	
		return Resting.get(baseURL.toString(), 3000, params);			
	}
	
	/**
	 * Authenticate a pre-registered user
	 * @param user
	 * @return
	 */
	public ServiceResponse authenicateUser(User user, String subUrl) {	
		RequestParams params = new BasicRequestParams(); 	
		params.add("user", user.getUsername());	
		params.add("password", user.getPassword());	
		return Resting.get(makeUrl(subUrl), 3000, params);	
	}
	
	/**
	 * Register a new user
	 * @param user
	 * @return
	 */
	public ServiceResponse registerUser(User user, String subUrl) {
		RequestParams params = new BasicRequestParams(); 	
		params.add("user", user.getUsername());	
		params.add("password", user.getPassword());	
		params.add("password_confirmation", user.getConfirmedPassowrd());
		return Resting.post(makeUrl(subUrl), 3000, params);	
	}

	/**
	 * Get all of the projects related to a users token
	 * @param token
	 * @param subUrl
	 * @return
	 * 
	 * 
	 *  Returns JsonArray of Projects - {id: <id>, title: <title>} 
	 */
	public List<Project> getProjects(Token token, String subUrl) {
		RequestParams params = new BasicRequestParams(); 	
		params.add("auth_token", token.toString());	
		return Resting.getByJSON(makeUrl(subUrl), 3000, params, Project.class, "projects");	
	}
	
	/**
	 * Helper method to join together the base and sub urls.
	 * @param subUrl
	 * @return
	 */
	private String makeUrl(String subUrl) {
		URL url = baseURL;
		try {
			url = new URL(baseURL, subUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url.toString();
	}
}
