package scrum.support.model;

public class Util {

	public enum Status {
		DONE("done"),
		STARTED("started"),
		NOT_STARTED("not_started"),
		BLOCKED("blocked");
		
		private String status;
		
		private Status(String status) {
			this.status = status;
		}
		
		@Override
		public String toString() {
			return status;
		}
		
		public static Status fromString(String value) {
			for (Status status : Status.values()) {
				if (status.toString().equals(value)) {
					return status;
				}
			}
			return null;
		}
		
		public boolean selectable() {
			if(status == Status.DONE.toString() || status == Status.STARTED.toString()) {
				return false;
			} else {
				return true;
			}
		}
	}
}
