package sonar.flux.api;

public enum FluxError {

	NONE,
	HAS_CONTROLLER;
	
	public String getErrorMessage(){
		switch(this){
		case HAS_CONTROLLER:
			return "network.error.hasController";
		
		default:
			return "";
		}
	}
}
