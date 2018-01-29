package sonar.flux.api;

public enum FluxError {

	NONE("You can't do that, but I don't know why... (contact mod dev)"), //
	HAS_CONTROLLER("network.error.hasController"), //
	NOT_OWNER("network.error.notOwner"), //
	ACCESS_DENIED("network.error.accessDenied"), //
	EDIT_NETWORK("network.error.editNetwork"), //
	NETWORK_MAX_REACHED("network.error.networkMaxReached"), //
	INVALID_USER("network.error.invalidUser");//
		
	String message;

	FluxError(String message) {
		this.message = message;
	}

	public String getErrorMessage() {
		return message;
	}

}
