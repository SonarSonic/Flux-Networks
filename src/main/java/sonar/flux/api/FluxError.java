package sonar.flux.api;

import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;

public enum FluxError {

	NONE(FluxTranslate.ERROR_NONE_ERROR), //
	HAS_CONTROLLER(FluxTranslate.ERROR_HAS_CONTROLLER), //
	NOT_OWNER(FluxTranslate.ERROR_NOT_OWNER), //
	ACCESS_DENIED(FluxTranslate.ERROR_ACCESS_DENIED), //
	EDIT_NETWORK(FluxTranslate.ERROR_EDIT_NETWORK), //
	NETWORK_MAX_REACHED(FluxTranslate.ERROR_NETWORK_MAX_REACHED), //
	INVALID_USER(FluxTranslate.ERROR_INVALID_USER);//
		
	Localisation message;

	FluxError(Localisation message) {
		this.message = message;
	}	

	public String getErrorMessage() {
		return message.t();
	}

}
