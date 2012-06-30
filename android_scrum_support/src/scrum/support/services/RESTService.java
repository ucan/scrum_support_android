package scrum.support.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

import scrum.support.model.Project;
import scrum.support.model.Token;
import scrum.support.model.User;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;
import com.google.resting.component.impl.ServiceResponse;


public class RESTService {
	
	private URL baseURL;
	private int port;
	
	Map<Link, String> links;
	
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
	
	protected RESTService() {
		try {
			baseURL = new URL("http://132.181.15.56/");
			port = 3000;
			links = new HashMap<Link, String>();
			updateLinks();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean updateLinks() {
		RequestParams params = new BasicRequestParams();
		ServiceResponse response = Resting.get(baseURL.toString(), port, params);
		JsonElement json = new JsonParser().parse(response.getResponseString());
		JsonObject jLinks = json.getAsJsonObject().getAsJsonObject("links");
		for (Link link : Link.values()) {
			if (jLinks.has(link.toString())) {
				links.put(link, jLinks.get(link.toString()).getAsString());
			}
			else {
				return false; // TODO: Should raise error!
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
		return Resting.get(makeUrl(Link.USER), port, params);
	}
	
	/**
	 * Register a new user
	 * @param user
	 * @return
	 */
	public ServiceResponse registerUser(User user) {
		RequestParams params = new BasicRequestParams();
		params.add("email", user.getEmail());
		params.add("password", user.getPassword());
		params.add("password_confirmation", user.getConfirmedPassword());
		return Resting.post(makeUrl(Link.USER), port, params);
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
		return Resting.getByJSON(makeUrl(Link.PROJECTS), port, params, Project.class, "projects", EncodingTypes.UTF8, headers);
	}
	
	/**
	 * Get all of the members and stories of a project
	 * @param token
	 * @param projectId
	 * @return
	 */
	public List<Project> getProject(Integer projectId, Token token) {
		RequestParams params = new BasicRequestParams();
		params.add("id", projectId.toString());
		List<Header> headers = new ArrayList<Header>();
		headers.add(new TokenAuthorizationHeader(token));
		return Resting.getByJSON(makeUrl(Link.PROJECTS), port, params, Project.class, "projects", EncodingTypes.UTF8, headers);
	}
	
	/**
	 * Helper method to join together the base and sub urls.
	 * @param subUrl
	 * @return
	 */
	private String makeUrl(Link link) {
		URL url = baseURL;
		try {
			url = new URL(baseURL, links.get(link));
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


