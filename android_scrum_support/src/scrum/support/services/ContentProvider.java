package scrum.support.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import scrum.support.R;
import scrum.support.model.Project;
import scrum.support.model.User;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
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
	private Context context;
	private URL baseURL;
	
	/**
	 * Singleton Service the manage the interactions between the 
	 * REST requests / responses and the UI Activities.
	 */
	private ContentProvider() {
		try {
			baseURL =  new URL("http://132.181.15.56/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
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
		rest = new RESTService(context);
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
	
	public URL getServerAddress() {
		return baseURL;
	}

	public void updateServer(String address) throws MalformedURLException {
		baseURL = new URL(address);		
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

	public void setContext(Context applicationContext) {
		context = applicationContext;
	}
}
