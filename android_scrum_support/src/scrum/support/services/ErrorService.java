package scrum.support.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

public class ErrorService extends Observable {

	private static ErrorService instance;
	private List<Error> errors;
	
	private ErrorService() {
		errors = new ArrayList<Error>();
	}
	
	public static ErrorService getInstance() {
		if(instance == null) {
			instance = new ErrorService();
		}
		return instance;
	}
	
	public List<Error> getErrors() {
		return Collections.unmodifiableList(errors);		
	}
	
	public String getError() {
		return errors.isEmpty() ? "" : errors.get(errors.size()-1).getMessage();
	}
	
	public void raiseError(Error error) {
		errors.add(error);
		this.setChanged();
		this.notifyObservers(error);
	}
}
