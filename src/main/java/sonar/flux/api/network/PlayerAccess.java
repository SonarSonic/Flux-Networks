package sonar.flux.api.network;

import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;

public enum PlayerAccess {
	OWNER(FluxTranslate.PLAYERS_NETWORK_OWNER), //
	SHARED_OWNER(FluxTranslate.PLAYERS_NETWORK_SHARED_OWNER), //
	USER(FluxTranslate.PLAYERS_NETWORK_USER), //
	BLOCKED(FluxTranslate.PLAYERS_NETWORK_BLOCKED), //
	CREATIVE(FluxTranslate.PLAYERS_NETWORK_CREATIVE);

	Localisation message;

	PlayerAccess(Localisation message) {
		this.message = message;
	}

	public String getDisplayName() {
		return message.t();
	}

	////CAN DELETE THE NETWORK ENTIRELY \\\\
	public boolean canDelete() {
		return this == OWNER || this == CREATIVE;
	}

	////EDIT IMPORTANT NETWORK SETTINGS \\\\
	public boolean canEdit() {
		return canDelete() || this == SHARED_OWNER;
	}

	///OPEN THE GUIS OF FLUX CONNECTIONS \\\\
	public boolean canView() {
		return canEdit() || this == USER;
	}

	//// JOIN THE NETWORK \\\\
	public boolean canConnect() {
		return this != BLOCKED;
	}

	public String getName() {
		return message.t();
	}

	public PlayerAccess incrementAccess() {
		switch (this) {
		case USER:
			return SHARED_OWNER;
		case SHARED_OWNER:
			return BLOCKED;
		case BLOCKED:
			return USER;
		default:
			return USER;
		}
	}
}
