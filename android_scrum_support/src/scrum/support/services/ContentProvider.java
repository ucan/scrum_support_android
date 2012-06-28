package scrum.support.services;

import java.util.Collections;
import java.util.List;

import scrum.support.model.User;
import scrum.support.model.Project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.resting.component.impl.ServiceResponse;
import com.google.resting.json.JSONArray;
import com.google.resting.json.JSONException;
import com.google.resting.json.JSONObject;

/**
 * Singleton Service the manage the interactions between the 
 * REST requests / responses  and the UI Activities.
 *  
 * @author Dave W
 *
 */
public class ContentProvider {
	
	private final static int CONFLICT = 409;
	private final static int BAD_REQUEST = 400;
	private final static int UNAUTHORIZED = 401;
	
	private static ContentProvider instance = null;
	private RESTService rest;
	
	private User user;
	private List<Project> projects;
	
	private enum RequestType {
		User,
		Project,
		Story,
		Task
	}
	
	/**
	 * Singleton Service the manage the interactions between the 
	 * REST requests / responses and the UI Activities.
	 */
	private ContentProvider() {
		rest = new RESTService();
	}
	
	/**
	 *
	 * @return the singleton instance of the service.
	 */
	public static ContentProvider getInstance() {
		if(instance == null) {
			instance = new ContentProvider();
		}
		return instance;
	}
	
	/**
	 * Validate the user - whether registering or logging in.
	 * @param user the user to check for
	 * @return true if the user was created or successfully authenticated.
	 */
	public boolean validateUser(User user) {
		
		ServiceResponse response = rest.getUserLink();
			// Get the user url
		String userLink = jsonStringHelper(response, "user");
		
		response = (user.needToRegistered()) ? rest.registerUser(user, userLink) : 
											   rest.authenicateUser(user, userLink);
		
			// Checks for a valid response
		if(invalid(RequestType.User, response.getStatusCode())) return false;

		this.user = user;
			// Get the auth token from the JSON reply
		user.setToken(jsonStringHelper(response, "auth_token"));
		
			// If the user has just registered, then they won't have any accounts, 
			// just return true so the next activity can start.
		if(user.needToRegistered()) return true;
		
			// Else get the account link and all of the relevant projects
		String accountLink = jsonStringHelper(response, "links", "projects");
		return updateProjects(accountLink);
	}
	
	public List<Project> getProjects() {
		return Collections.unmodifiableList(projects);
	}

	/**
	 * A method to get all of the projects for the user
	 * @param REST response to get the relevant links from
	 * @return
	 */
	private boolean updateProjects(String link) {
		projects = rest.getProjects(user.getToken(), link);
		
		/*JsonArray jsonProjects = jsonArrayHelper(response, "projects");
		for(int i = 0; i < jsonProjects.size(); i++) {
			JsonObject json = jsonProjects.get(i).getAsJsonObject();
		}*/
		
		
		return false;
	}
	
	/**
	 * A simple helper method to put out json Strings
	 * @param response
	 * @param params
	 * @return
	 */
	private String jsonStringHelper(ServiceResponse response, String...params) {
		JsonElement json = new JsonParser().parse(response.getResponseString());
    	for(int i = 0; i < params.length; i++) {
    		json = json.getAsJsonObject().get(params[0]);
    	}		
		return json.getAsString();
		
	}
	
	private JsonArray jsonArrayHelper(ServiceResponse response, String...params) {
		JsonElement json = new JsonParser().parse(response.getResponseString());
    	for(int i = 0; i < params.length; i++) {
    		json = json.getAsJsonObject().get(params[0]);
    	}		
		return json.getAsJsonArray();
	}
	
	private boolean invalid(RequestType type, int status) {
		switch(type) {
			case User : 
				return status == CONFLICT ||
						status == BAD_REQUEST ||
						status == UNAUTHORIZED;			
		}
		return false;
	}
	
}
