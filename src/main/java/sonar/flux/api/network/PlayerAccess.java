package sonar.flux.api.network;

import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;

public enum PlayerAccess {
	OWNER(FluxTranslate.PLAYERS_NETWORK_OWNER), //
	SHARED_OWNER(FluxTranslate.PLAYERS_NETWORK_OWNER), //
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

	public boolean canDelete() {
		return this == OWNER || this == CREATIVE;
	}

	public boolean canEdit() {
		return this == OWNER || this == SHARED_OWNER || this == CREATIVE;
	}

	public boolean canConnect() {
		return this == OWNER || this == SHARED_OWNER || this == USER || this == CREATIVE;
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
