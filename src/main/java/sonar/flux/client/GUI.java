package sonar.flux.client;

import java.awt.Color;

import net.minecraft.util.text.TextFormatting;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFlux.ConnectionType;
import sonar.flux.api.IFluxController;
import sonar.flux.common.tileentity.TileEntityStorage;

public enum GUI {
	CHANGE_SETTING("network.changeSetting"),//
	NEXT_COLOUR("network.nextColour"),//
	ACCESS_SETTING("network.accessSetting"),//
	PLUGS("plug.plugs"), //
	POINTS("plug.points"), //
	STORAGE("plug.storage"), //
	MAX_SENT("network.energy.maxSent"), //
	MAX_RECEIVE("network.energy.maxReceived"), //
	TRANSFER("network.energy.transfer"), //
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
