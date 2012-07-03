package scrum.support.model;

// May need to be changed to PtAccount implements Account...
public class Account {
	
	private int id;
	private String apiKey;
	private String type;
	
	public Account(int id, String type, String apiKey) {
		this.id = id;
		this.apiKey = apiKey;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Account)) {
			return false;
		}
		Account otherA = (Account)other;
		return otherA.id == this.id && otherA.apiKey == this.apiKey;
	}
	
	@Override
	public int hashCode() {
		int result = 13;
		result = 5 * result + id;
		result = 5 * result + apiKey.hashCode();
		return result;
	}
}
