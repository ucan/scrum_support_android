package scrum.support.services;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;

import scrum.support.R;
import scrum.support.model.Account;
import scrum.support.model.Iteration;
import scrum.support.model.Project;
import scrum.support.model.Story;
import scrum.support.model.Task;
import scrum.support.model.TeamMember;
import scrum.support.model.Token;
import scrum.support.model.User;
import scrum.support.model.Util.Status;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;
import com.google.resting.component.impl.ServiceResponse;
import com.google.resting.component.impl.json.JSONRequestParams;
import com.google.resting.method.get.GetHelper;
import com.google.resting.method.post.PostHelper;
import com.google.resting.method.put.PutHelper;

//TODO: Every service method in this class needs error handling for possible returned http status codes
public class RESTService {
	
	private int port;
	
	private Gson gson;
	
	Map<Link, String> links;

	private Context context;
	
	private enum Link {
		USER("user"),
		ACCOUNTS("accounts"),
		PROJECTS("projects"),
		ITERATIONS("iterations"),
		STORIES("stories"),
		TASKS("tasks");
		
		private final String link;
		
		private Link(final String link) {
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
		gson = buildGson();
	}
	
	/**
	 * (API: ROOT#Index)
	 * Update the links to the scrum support RESTful api resources
	 * @return
	 */
	public boolean updateLinks() {
		boolean updated = false;
		RequestParams params = new BasicRequestParams();
		String host = ContentProvider.getInstance().getServerAddress().toString();
		Log.i("REST SERVICE", host);
		ServiceResponse response = Resting.get(host, port, params);
		if (response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST LINKS", "The REST Server was unavailable");
		}
		else if (response.getStatusCode() == HttpStatus.SC_OK) {
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("links")) {
				JsonObject jLinks = json.getAsJsonObject().getAsJsonObject("links");
				for (Link link : Link.values()) {
					if (jLinks.has(link.toString())) {
						links.put(link, jLinks.get(link.toString()).getAsString());
					}
					else {
						ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_json_parse)));
						return false; // TODO: Should raise error! Done?
					}
				}
				updated = true;
			}
		}
		return updated;
	}
	
	
	// MANAGE USERS
	/**
	 * (API: User:Show)
	 * Authenticate a pre-registered user
	 * @param user
	 * @return
	 */
	public ServiceResponse authenticateUser(User user) {
		RequestParams params = new BasicRequestParams();
		params.add("email", user.getEmail());
		params.add("password", user.getPassword());	
		ServiceResponse response = Resting.get(makeUrl(Link.USER), port, params);
		
		if(response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("USER", "The REST Server was unavailable");
			return null;
		}
		return response;
	}
	
	/**
	 * (API: User#Create)
	 * Register a new user
	 * @param user
	 * @return
	 */
	public ServiceResponse registerUser(User user) {
		JSONRequestParams params = new JSONRequestParams();
		params.add("email", user.getEmail());
		params.add("password", user.getPassword());
		ServiceResponse response = Resting.post(makeUrl(Link.USER), port, params);
		if(response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("USER", "The REST Server was unavailable");
			return null;
		}
		return response;
	}

	
	//MANAGE ACCOUNTS
	/**
	 * (API: Accounts#Create)
	 * Add a new account
	 * @param token - The users ScrumSupport api token
	 * @param type - The type of account (e.g. ptAccount)
	 * @param email - Login credentials for the new account
	 * @param password - Login credentials for the new account
	 * @return - the new account if successfully added, otherwise null
	 */
	public Account createAccount(Token token, String type, String email, String password) {
		Log.d("REST SERVICE", "Adding Account of type: " + type);
		RequestParams params = new BasicRequestParams();
		params.add("type", type);
		params.add("email", email);
		params.add("password", password);
		return createAccount(token, params);
	}
	
	// Not currently supported by the api
//	public Account addAccount(Token token, String type, String accountToken) {
//		RequestParams params = new BasicRequestParams();
//		params.add("type", type);
//		params.add("api_token", accountToken);
//		return addAccount(token, params);
//	}
	
	private Account createAccount(Token token, RequestParams params) {
		Account account = null;
		ServiceResponse response = PostHelper.post(makeUrl(Link.ACCOUNTS), port, EncodingTypes.UTF8, params, getAuthHeaders(token));
		if(response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST SERVICE", "The REST Server was unavailable");
			return null;
		}
		else if (response.getStatusCode() == HttpStatus.SC_CREATED){
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("account")) {
				account = gson.fromJson(json.getAsJsonObject().get("account"), Account.class);
			}
			if (account == null || account.getId() <= 0) {
				//TODO: Raise error? Account not found
				Log.e("REST SERVICE", response.getResponseString());
				Log.i("REST SERVICE", "Returning new account with id: " + account.getId());
				return null;
			}
		}
		else {
			//TODO: Check forbidden if account already exists (TODO on server also!)
			//TODO: Check bad request if parameters invalid
			//TODO: Check unauthorized if auth token doesn't match account on server
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_add_account)));  // TODO this doesn't seem to do anything...Dave?
			Log.i("REST SERVICE", "Ouch...something or someone fucked up " + (account == null));
		}
		return account;
	}
	
	/**
	 * (API: Accounts#List)
	 * Fetches account list from the server, and updates users accounts
	 * @param user
	 * @return
	 */
	public boolean fetchAccounts(User user) {
		boolean updated = false;
		ServiceResponse response = GetHelper.get(makeUrl(Link.ACCOUNTS), port, null, EncodingTypes.UTF8, getAuthHeaders(user.getToken()));
		if (response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST SERVICE", "The REST Server was unavailable");
		}
		else if (response.getStatusCode() == HttpStatus.SC_OK) {
			// TODO: clear all user accounts
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("accounts")) {
				JsonArray jAccounts = json.getAsJsonObject().getAsJsonArray("accounts");
				for (JsonElement jAccount : jAccounts) {
					Account account = gson.fromJson(jAccount, Account.class);
					if (account != null) {
						user.addAccount(account);
					}
					else {
						Log.d("REST SERVICE", "getProjects: Error deserializing an account");
					}
				}
				updated = true;
			}
		}
		else {
			// TODO: Need to check if response is 403/other ?
		}
		return updated;
	}
	
	//MANAGE PROJECTS
	/**
	 * (API: Projects#List)
	 * Get all of the projects for an account
	 * @param token
	 * @param subUrl
	 * @return boolean indicating whether the account has been synchronized with the server
	 * 
	 *  Returns JsonArray of Projects - {id: <id>, title: <title>} 
	 */
	public boolean fetchProjects(Token token, Account account) {
		boolean updated = false;
		ServiceResponse response = GetHelper.get(makeUrl(Link.PROJECTS), port, null, EncodingTypes.UTF8, getAuthHeaders(token));
		if (response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST SERVICE", "The REST Server was unavailable");
		}
		else if (response.getStatusCode() == HttpStatus.SC_OK) {
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("projects")) {
				// TODO: Clear all current projects for this account
				JsonArray jProjects = json.getAsJsonObject().getAsJsonArray("projects");
				for (JsonElement jProject : jProjects) {
					Project project = gson.fromJson(jProject, Project.class);
					if (project != null) {
						account.addProject(project);
					}
					else {
						Log.d("REST SERVICE", "getProjects: Error deserializing a project");
					}
				}
				updated = true;
			}
		}
		else {
			// TODO: Check other http status codes
		}
		return updated;
	}
	
	/**
	 * (API: Projects#Show)
	 * Get a project (with team members and stories)
	 * @param token
	 * @param projectId
	 * @return
	 */
	public Project fetchProject(Token token, Integer projectId) {
		Project project = null;
		String url = makeUrl(Link.PROJECTS) + "/" + projectId;
		ServiceResponse response = GetHelper.get(url, port, null, EncodingTypes.UTF8, getAuthHeaders(token));
		if(response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST SERVICE", "The REST Server was unavailable");
			return null;
		}
		else if (response.getStatusCode() == HttpStatus.SC_OK) {
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("project")) {
				project = gson.fromJson(json.getAsJsonObject().get("project"), Project.class);
				if (project == null || project.getId() != projectId) {
					//TODO: Raise error? Project not found
					Log.e("REST SERVICE", response.getResponseString());
					return null;
				}
			}
		}
		else {
			//TODO: Raise error? Unknown response code
		}
		return project;
	}

	/**
	 * Testing needed.
	 * 
	 * @param token
	 * @param id
	 * @param id2
	 * @return
	 */
	public boolean fetchStories(Token token, Iteration iteration) {
		boolean updated = false;
		boolean storiesFound = false;
		RequestParams params = new BasicRequestParams();
		params.add("iteration_id", "" + iteration.getId());
		ServiceResponse response = GetHelper.get(makeUrl(Link.STORIES), port, params, EncodingTypes.UTF8, getAuthHeaders(token));
		if (response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST SERVICE", "The REST Server was unavailable");
		}
		else if (response.getStatusCode() == HttpStatus.SC_OK) {
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("stories")) {
				JsonArray jStories = json.getAsJsonObject().getAsJsonArray("stories");
				for (JsonElement jStory : jStories) {
					storiesFound = true;
					Story story = gson.fromJson(jStory, Story.class);
					if (story != null) {
						iteration.addStory(story);
					}
					else {
						Log.d("REST SERVICE", "getProjects: Error deserializing a task");
					}
				}
				updated = true;
			}
		}
		else {
			// TODO: Need to check if response is 403/other ?
		}
		// TODO: Temp fix for testing stories, as there were none being pulled off the REST server.
		/*if(!storiesFound) {
			// TODO: Remove when fixed
			Story tempStory = new Story(1, "test story to see if I work");
			tempStory.addTask(new Task(1, "First test task"));
			tempStory.addTask(new Task(2, "Second test task"));
			tempStory.addTask(new Task(3, "Third test task"));
			iteration.addStory(tempStory);
		}*/
		return updated;
	}
	
	/**
	 * (API: Tasks#list)
	 * @param token
	 * @param story
	 * @return
	 */
	public boolean fetchTasks(Token token, Story story) {
		boolean updated = false;
		RequestParams params = new BasicRequestParams();
		params.add("story_id", "" + story.getId());
		ServiceResponse response = GetHelper.get(makeUrl(Link.TASKS), port, params, EncodingTypes.UTF8, getAuthHeaders(token));
		if (response == null) {
			ErrorService.getInstance().raiseError(new Error(context.getString(R.string.error_connection)));
			Log.e("REST SERVICE", "The REST Server was unavailable");
		}
		else if (response.getStatusCode() == HttpStatus.SC_OK) {
			JsonElement json = new JsonParser().parse(response.getResponseString());
			if (json.isJsonObject() && json.getAsJsonObject().has("tasks")) {
				JsonArray jTasks = json.getAsJsonObject().getAsJsonArray("tasks");
				for (JsonElement jTask : jTasks) {
					Task task = gson.fromJson(jTask, Task.class);
					if (task != null) {
						story.addTask(task);
					}
					else {
						Log.d("REST SERVICE", "getProjects: Error deserializing a task");
					}
				}
				updated = true;
			}
		}
		else {
			// TODO: Need to check if response is 403/other ?
		}
		return updated;
	}
	
	public boolean updateTask(Token token, Task task) {
		boolean updated = false;
		RequestParams params = new BasicRequestParams();
		params.add("status", task.getStatus().toString());
		params.add("description", task.getDescription());
		ServiceResponse response = PutHelper.put(makeUrl(Link.TASKS) + "/" + task.getId(), EncodingTypes.UTF8, port, params, getAuthHeaders(token));
		if (response != null) {
			if (response.getStatusCode() == HttpStatus.SC_OK) {
				updated = true;
			}
			else {
				// TODO error small time little chief
			}
		}
		else {
			// TODO error big time big chief
		}
		return updated;
	}
	
	private Gson buildGson() {
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(Account.class, new AccountDeserializer());
		gb.registerTypeAdapter(Project.class, new ProjectDeserializer());
		gb.registerTypeAdapter(Iteration.class, new IterationDeserializer());
		gb.registerTypeAdapter(TeamMember.class, new TeamMemberDeserializer());
		gb.registerTypeAdapter(Story.class, new StoryDeserializer());
		gb.registerTypeAdapter(Task.class, new TaskDeserializer());
		return gb.create();
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
	
	/**
	 * Helper method to return a list containing only an
	 * Authorization header filled with the provided token
	 * @param token - the authentication token
	 * @return
	 */
	private List<Header> getAuthHeaders(Token token) {
		List<Header> headers = new ArrayList<Header>();
		headers.add(new TokenAuthorizationHeader(token));
		return headers; 
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
	
	private class AccountDeserializer implements JsonDeserializer<Account> {
		
		public Account deserialize(JsonElement element, Type t, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			if (!json.has("id") || !json.has("type") || !json.has("email") || !json.has("team_member")) {
				throw new JsonParseException("Not a valid Account element");
			}
			int id = json.get("id").getAsInt();
			String type = json.get("type").getAsString();
			String email = json.get("email").getAsString();
			TeamMember teamMember = context.deserialize(json.get("team_member"), TeamMember.class);
			return new Account(id, type, email, teamMember);
		}
	}
	
	private class ProjectDeserializer implements JsonDeserializer<Project> {

		public Project deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			if (!json.has("id") || !json.has("title") || !json.has("current_iteration_id")) {
				throw new JsonParseException("Not a valid Project element");
			}
			int id = json.get("id").getAsInt();
			int currentIterationId = json.get("current_iteration_id").getAsInt();
			String title = json.get("title").getAsString();
			
			SortedSet<TeamMember> teamMembers = new TreeSet<TeamMember>();
			if (json.has("team_members")) {
				JsonArray jTeamMembers = json.get("team_members").getAsJsonArray();
				for (JsonElement e : jTeamMembers) {
					TeamMember member = context.deserialize(e, TeamMember.class);
					teamMembers.add(member);
				}
			}
			
			SortedSet<Iteration> iterations = new TreeSet<Iteration>();  // TODO: Sort by start date
			if (json.has("iterations")) {
				JsonArray jIterations = json.get("iterations").getAsJsonArray();
				for (JsonElement elem : jIterations) {
					Iteration iteration = context.deserialize(elem, Iteration.class);
					iterations.add(iteration);
				}
			}
			return new Project(id, currentIterationId, title, iterations, teamMembers);
		}
		
	}
	
	private class IterationDeserializer implements JsonDeserializer<Iteration> {
		public Iteration deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			if (!json.has("id")) { // TODO: || !json.has("start_date") || !json.has("end_date")) {
				throw new JsonParseException("Not a valid Iteration element");
			}
			return new Iteration(json.get("id").getAsInt(), 0, 0);  // TODO: fix dates
		}
	}
	
	private class TeamMemberDeserializer implements JsonDeserializer<TeamMember> {
		
		public TeamMember deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			if (!json.has("id") || !json.has("email") || !json.has("name")) {
				throw new JsonParseException("Not a valid TeamMember element");
			}
			int id = json.get("id").getAsInt();
			String name = json.get("name").getAsString();
			String email = json.get("email").getAsString();
			Task task = null;
			if (json.has("task")) { 
				JsonElement jTask = json.get("task");
				if(!jTask.isJsonNull()) {
					task = context.deserialize(json.get("task"), Task.class);
				}
			}
			return new TeamMember(id, name, email, task);
		}
	}
	
	private class StoryDeserializer implements JsonDeserializer<Story> {
		public Story deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			if (!json.has("id") || !json.has("title")) {
				throw new JsonParseException("Not a valid Story element");
			}
			int id = json.get("id").getAsInt();
			String title = json.get("title").getAsString();
			return new Story(id, title);
		}
	}
		
	private class TaskDeserializer implements JsonDeserializer<Task> {
		public Task deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = element.getAsJsonObject();
			if (!json.has("id") || !json.has("description") || !json.has("status")) {
				throw new JsonParseException("Not a valid Task element");
			}
			int id = json.get("id").getAsInt();			
			String description = json.get("description").getAsString();
			String statusStr = json.get("status").getAsString();
			Status status = Status.fromString(statusStr);
			return new Task(id, description, status);
		}
	}	
}


