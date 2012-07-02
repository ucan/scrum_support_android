package scrum.support.services;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

import scrum.support.R;
import scrum.support.model.Project;
import scrum.support.model.Token;
import scrum.support.model.User;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;
import com.google.resting.component.impl.ServiceResponse;
import com.google.resting.component.impl.json.JSONRequestParams;


public class RESTService {
	
	private int port;
	
	Map<Link, String> links;

	private Context context;
	
	private enum Link {
		USER("user"),
		ACCOUNTS("accounts"),
		PROJECTS("projects"),
		STORIES("stories"),
		TASKS("tasks");
		
		private String link;
		
		private Link(String link) {
			this.link = link;
		}
		
		public String toString() {
			return link;
		}
	}

	protected RESTService(Context context) {
		port = 3000;
		links = new HashMap<Link, String>();
		updateLinks();
		this.context = context;
	}
	
	public boolean updateLinks() {
		RequestParams params = new BasicRequestParams();
		ServiceResponse response = Resting.get(
				ContentProvider.getInstance().getServerAddress().toString(), port, params);
		if(response == null) {
			ErrorService.getInstance().raiseError(
					new Error(context.getString(R.string.error_connection)));
			Log.e("REST LINKS", "The REST Server was unavailable");
			return false;
		}
		JsonElement json = new JsonParser().parse(response.getResponseString());
		JsonObject jLinks = json.getAsJsonObject().getAsJsonObject("links");
		for (Link link : Link.values()) {
			if (jLinks.has(link.toString())) {
				links.put(link, jLinks.get(link.toString()).getAsString());
			}
			else {
				ErrorService.getInstance().raiseError(
						new Error(context.getString(R.string.error_json_parse)));
				return false; // TODO: Should raise error! Done?
			}
		}
		return true;
	}
	
	/**
	 * Authenticate a pre-registered user
	 * @param user
	 * @return
	 */
	public ServiceResponse authenicateUser(User user) {
		RequestParams params = new BasicRequestParams();
		params.add("email", user.getEmail());
		params.add("password", user.getPassword());	
		ServiceResponse response = Resting.get(makeUrl(Link.USER), port, params);
		
		if(response == null) {
			ErrorService.getInstance().raiseError(
					new Error(context.getString(R.string.error_connection)));
			Log.e("USER", "The REST Server was unavailable");
			return null;
		}
		return response;
	}
	
	/**
	 * Register a new user
	 * @param user
	 * @return
	 */
	public ServiceResponse registerUser(User user) {
		JSONRequestParams params = new JSONRequestParams();
		params.add("email", user.getEmail());
		params.add("password", user.getPassword());
		params.add("password_confirmation", user.getConfirmedPassword());
		ServiceResponse response = Resting.post(makeUrl(Link.USER), port, params);
		if(response == null) {
			ErrorService.getInstance().raiseError(
					new Error(context.getString(R.string.error_connection)));
			Log.e("USER", "The REST Server was unavailable");
			return null;
		}
		return response;
	}

	/**
	 * Get all of the projects related to a users token
	 * @param token
	 * @param subUrl
	 * @return
	 * 
	 *  Returns JsonArray of Projects - {id: <id>, title: <title>} 
	 */
	public List<Project> getProjects(Token token) {
		RequestParams params = new BasicRequestParams();
		List<Header> headers = new ArrayList<Header>();
		headers.add(new TokenAuthorizationHeader(token));
		List<Project> response = Resting.getByJSON(
				makeUrl(Link.PROJECTS), port, params, Project.class, "projects", EncodingTypes.UTF8, headers);
		
		if(response == null) {
			ErrorService.getInstance().raiseError(
					new Error(context.getString(R.string.error_connection)));
			Log.e("PROJECTS", "The REST Server was unavailable");
			return null;
		}
		return response;
		
	}
	
	/**
	 * Get all of the members and stories of a project
	 * @param token
	 * @param projectId
	 * @return
	 */
	public List<Project> getProject(Integer projectId, Token token) throws SocketException {
		RequestParams params = new BasicRequestParams();
		params.add("id", projectId.toString());
		List<Header> headers = new ArrayList<Header>();
		headers.add(new TokenAuthorizationHeader(token));
		List<Project> response = Resting.getByJSON(
				makeUrl(Link.PROJECTS), port, params, Project.class, "projects", EncodingTypes.UTF8, headers);
		
		if(response == null) {
			ErrorService.getInstance().raiseError(
					new Error(context.getString(R.string.error_connection)));
			Log.e("PROJECTS", "The REST Server was unavailable");
			return null;
		}
		return response;
	}
	
	/**
	 * Helper method to join together the base and sub urls.
	 * @param subUrl
	 * @return
	 */
	private String makeUrl(Link link) {
		URL url = ContentProvider.getInstance().getServerAddress();
		try {
			url = new URL(url, links.get(link));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url.toString();
	}
	
	private class TokenAuthorizationHeader implements Header {

		Token token;
		
		public TokenAuthorizationHeader(Token token) {
			this.token = token;
		}
		
		public HeaderElement[] getElements() throws ParseException {
			return new HeaderElement[]{};
		}

		public String getName() {
			return "Authorization";
		}

		public String getValue() {
			return "Token token=" + token.toString();
		}
		
	}
}


