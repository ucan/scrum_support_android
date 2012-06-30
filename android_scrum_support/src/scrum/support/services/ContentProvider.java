package scrum.support.services;

import java.util.List;

import scrum.support.model.Project;
import scrum.support.model.User;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.resting.component.impl.ServiceResponse;

/**
 * Singleton Service the manage the interactions between the 
 * REST requests / responses  and the UI Activities.
 *  
 * @author Dave W
 *
 */
public class ContentProvider {
	
	private final static int OK = 200;
	private final static int CREATED = 201;
	private final static int CONFLICT = 409;
	private final static int BAD_REQUEST = 400;
	private final static int UNAUTHORIZED = 401;
	
	private static ContentProvider instance = null;
	private RESTService rest;
	
	private User user;
	
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
		boolean valid = false;
		ServiceResponse response;
		
		if (user.needsToRegister()) {
			response = rest.registerUser(user);
			if (response.getStatusCode() == CREATED) {
				valid = true;
			}
			else {
				Log.e("USER", "Failed to register user: " + response.getContentData());
			}
		}
		else {
			response = rest.authenicateUser(user);
			if (response.getStatusCode() == OK) {
				valid = true;
			}
			else {
				Log.e("USER", "Failed to authenticate user: " + response.getContentData());
			}
		}
		
		if (valid) {
			this.user = user;
			user.setToken(jsonStringHelper(response, "user", "auth_token"));
		}
		return valid;
	}
	
	public List<Project> getProjects() {
		return rest.getProjects(user.getToken());
	}
	
	/**
	 * A simple helper method to put out json Strings
	 * @param response
	 * @param params
	 * @return
	 */
	private String jsonStringHelper(ServiceResponse response, String...params) {
		JsonElement json = new JsonParser().parse(response.getResponseString());
		for(String s : params) {
    		json = json.getAsJsonObject().get(s);
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
