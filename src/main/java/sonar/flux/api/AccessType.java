package sonar.flux.api;

import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;

public enum AccessType {
	PUBLIC(FluxTranslate.ACCESS_PUBLIC),//
	PRIVATE(FluxTranslate.ACCESS_PRIVATE),//
	RESTRICTED(FluxTranslate.ACCESS_RESTRICTED);//

	Localisation message;

	AccessType(Localisation message) {
		this.message = message;
	}

	public String getDisplayName() {
		return message.t();
	}
}