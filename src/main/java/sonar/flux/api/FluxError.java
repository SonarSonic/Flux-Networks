package sonar.flux.api;

public enum FluxError {

	NONE,
	HAS_CONTROLLER,
	NOT_OWNER,
	ACCESS_DENIED,
	EDIT_NETWORK,
	INVALID_USER;
	
	public String getErrorMessage(){
		switch(this){
		case HAS_CONTROLLER:
			return "network.error.hasController";
		case NOT_OWNER:
			return "network.error.notOwner";
		case ACCESS_DENIED:
			return "network.error.accessDenied";
		case EDIT_NETWORK:
			return "network.error.editNetwork";
		case INVALID_USER:
			return "network.error.invalidUser";
		default:
			return "";
		}
	}
}
