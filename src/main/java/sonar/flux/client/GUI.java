package sonar.flux.client;

import sonar.core.helpers.FontHelper;

public enum GUI {
	CHANGE_SETTING("network.changeSetting"),//
	NEXT_COLOUR("network.nextColour"),//
	ACCESS_SETTING("network.accessSetting"),//
	PLUGS("plug.plugs"), //
	POINTS("plug.points"), //
	STORAGE("plug.storage"), //
	CONTROLLERS("plug.controllers"), //
	
	TOTAL_INPUT("network.energy.input"),
	TOTAL_OUTPUT("network.energy.output"),
	TOTAL_STORAGE_CHANGE("network.energy.storedchange"),
	TOTAL_NETWORK_TRANSFER("network.energy.totaltransfer"),
	
	MAX_SENT("network.energy.maxSent"), //
	MAX_RECEIVE("network.energy.maxReceived"), //
	TRANSFER("network.energy.transfer"), //
	TRANSFER_LIMIT("network.energy.transferlimit"), //
	NETWORK_NAME("network.name"), //
	CREATE_NETWORK("network.create"), //
	EDIT_NETWORK("network.edit"), //
	MAX("point.max"), //
	PRIORITY("point.priority"), //
	IGNORE_LIMIT("flux.ignorelimit");

	String text;

	GUI(String text) {
		this.text = text;
	}

	public String toString() {
		return FontHelper.translate(text);
	}

}
