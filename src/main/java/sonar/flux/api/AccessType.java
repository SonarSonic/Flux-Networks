package sonar.flux.api;

public enum AccessType {
	PUBLIC, PRIVATE, RESTRICTED;

	public String getName() {
		switch (this) {
		case PUBLIC:
			return "network.public";
		case PRIVATE:
			return "network.private";
		case RESTRICTED:
			return "network.restricted";
		}
		return "";
	}
}