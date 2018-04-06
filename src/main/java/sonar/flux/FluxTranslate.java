package sonar.flux;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import sonar.core.helpers.FontHelper;
import sonar.core.translate.Localisation;

public class FluxTranslate {
	public static final List<Localisation> locals = new ArrayList<>();
	
	//// TOOL TIPS \\\\
	public static final Localisation FLUX_ITEM_TOOLTIP = t("flux.item.flux.tooltip");
	public static final Localisation FLUX_CONTROLLER_TOOLTIP = t("flux.item.fluxcontroller.tooltip");
	public static final Localisation FLUX_PLUG_TOOLTIP = t("flux.item.fluxplug.tooltip");
	public static final Localisation FLUX_POINT_TOOLTIP = t("flux.item.fluxpoint.tooltip");
	public static final Localisation FLUX_STORAGE_TOOLTIP = t("flux.item.fluxstorage.tooltip");
		
	//// GENERAL VALUES \\\\
	public static final Localisation TRUE = t("flux.value.true");
	public static final Localisation FALSE = t("flux.value.false");
	public static final Localisation ON = t("flux.value.on");
	public static final Localisation OFF = t("flux.value.off");
	public static final Localisation ENABLED = t("flux.value.enabled");
	public static final Localisation DISABLED = t("flux.value.disabled");
	
	//// GENERAL TRANSLATION \\\\
	public static final Localisation ADD = t("flux.general.add");
	public static final Localisation REMOVE = t("flux.general.remove");
	public static final Localisation DELETE = t("flux.general.delete");
	public static final Localisation BACK = t("flux.general.back");
	public static final Localisation RENAME = t("flux.general.rename");		
	public static final Localisation NAME = t("flux.general.name");
	public static final Localisation COLOR = t("flux.general.color");
	public static final Localisation STORED = t("flux.general.stored");
	public static final Localisation CANCEL = t("flux.general.cancel");
	public static final Localisation BANNED = t("flux.general.banned");
	public static final Localisation CHUNK = t("flux.general.chunk");
	public static final Localisation SHOW = t("flux.general.show");
	public static final Localisation RESET = t("flux.general.reset");
	public static final Localisation CREATE = t("flux.general.create");
	public static final Localisation SAVE_CHANGE = t("flux.general.savechanges");
	public static final Localisation CONFIG = t("flux.general.config");
	public static final Localisation CHANGE = t("flux.general.change");
	public static final Localisation RIGHT_CLICK_TO_CHANGE = t("flux.general.rightclick");
	public static final Localisation CLICK = t("flux.general.click");
	public static final Localisation ABOVE = t("flux.general.above");
	public static final Localisation TYPE = t("flux.general.type");
	public static final Localisation UUID = t("flux.general.uuid");
	
	//// GUI TAB NAMES \\\\
	public static final Localisation GUI_TAB_INDEX = t("network.tab.home");
	public static final Localisation GUI_TAB_CONNECTIONS = t("network.tab.connections");
	public static final Localisation GUI_TAB_NETWORK_SELECTION = t("network.tab.networks");
	public static final Localisation GUI_TAB_STATISTICS = t("network.tab.statistics");
	public static final Localisation GUI_TAB_NETWORK_EDIT = t("network.tab.editnetwork");
	public static final Localisation GUI_TAB_NETWORK_CREATE = t("network.tab.createnetwork");
	public static final Localisation GUI_TAB_PLAYERS = t("network.tab.players");
	
	//// INDEX TAB \\\\
	public static final Localisation PRIORITY = t("connection.config.priority");
	public static final Localisation TRANSFER_LIMIT = t("connection.config.transferlimit");
	public static final Localisation ENABLE_LIMIT = t("connection.config.enablelimit");
	public static final Localisation NO_LIMIT = t("connection.config.nolimit");
	public static final Localisation PHANTOM = t("connection.config.phantom");
	
	// PRIORITY MODE \\
	public static final Localisation PRIORITY_DEFAULT = t("network.priority.default");
	public static final Localisation PRIORITY_LARGEST = t("network.priority.largest");
	public static final Localisation PRIORITY_SMALLEST = t("network.priority.smallest");
	
	//// CONTROLLER INDEX TAB \\\\	
	public static final Localisation SEND_MODE = t("network.sendMode");
	public static final Localisation RECEIVE_MODE = t("network.receiveMode");
	public static final Localisation TRANSFER_MODE = t("network.transferMode");
	public static final Localisation TRANSMITTER_MODE = t("network.transmitterMode");	
	
	// TRANSFER MODE \\
	public static final Localisation TRANSFER_NONE = t("network.transfer.notransfer");
	public static final Localisation TRANSFER_EVEN = t("network.transfer.even");
	public static final Localisation TRANSFER_SURGE = t("network.transfer.surge");
	public static final Localisation TRANSFER_HYPER = t("network.transfer.hyper");
	public static final Localisation TRANSFER_GOD = t("network.transfer.god");
	
	// TRANSMITTER MODE \\
	public static final Localisation TRANSMITTER_HOTBAR_ONLY = t("network.transmitter.hotbar");	
	public static final Localisation TRANSMITTER_HELD_ITEM_ONLY = t("network.transmitter.held");	
	
	//// NETWORK SELECTION \\\\
	public static final Localisation NETWORK_OWNER = t("network.stats.owner");	
	public static final Localisation NETWORK_NAME = t("network.stats.name");
	
	// DELETE NETWORK TAB \\
	public static final Localisation NETWORK_DELETE = t("network.delete.hover");	
	public static final Localisation NETWORK_CONFIRM_DELETE = t("network.delete.confirm");	
		
	//// NETWORK CONNECTIONS \\\\
	public static final Localisation SORTING_NAME = t("network.sorting.name");	
	public static final Localisation SORTING_TYPE = t("network.sorting.type");	
	public static final Localisation SORTING_DIMENSION = t("network.sorting.dim");	
	public static final Localisation SORTING_TRANSFER = t("network.sorting.transfer");	
	public static final Localisation SORTING_CLEAR = t("network.sorting.clear");	
	public static final Localisation SORTING_SHOW_CONNECTED = t("network.sorting.showconnected");	
	public static final Localisation SORTING_DIRECTION = t("network.sorting.direction");	
	public static final Localisation SORTING_BY = t("network.sorting.sortby");	
	
	// CHUNK DISPLAY OPTIONS \\	
	public static final Localisation SORTING_BOTH = t("network.sorting.both");	
	public static final Localisation SORTING_LOADED = t("network.sorting.loaded");	
	public static final Localisation SORTING_UNLOADED = t("network.sorting.unloaded");	
		
	//// NETWORK STATISTICS \\\\
	public static final Localisation PLUGS = t("network.stats.plugs");
	public static final Localisation POINTS = t("network.stats.points");
	public static final Localisation STORAGE = t("network.stats.storage");
	public static final Localisation CONTROLLERS = t("network.stats.controllers");
	public static final Localisation TOTAL_INPUT = t("network.energy.input");
	public static final Localisation TOTAL_OUTPUT = t("network.energy.output");
	public static final Localisation TOTAL_STORAGE_CHANGE = t("network.energy.storedchange");
	public static final Localisation TOTAL_NETWORK_TRANSFER = t("network.energy.totaltransfer");
	public static final Localisation NETWORK_BUFFER = t("network.energy.networkbuffer");
	public static final Localisation LOCAL_BUFFER = t("network.energy.localbuffer");
	
	//// NETWORK PLAYERS \\\\
	
	
	//// NETWORK EDIT/CREATE \\\\
	public static final Localisation CHANGE_SETTING = t("network.edit.changeSetting");
	public static final Localisation NEXT_COLOUR = t("network.edit.nextColour");
	public static final Localisation ACCESS_SETTING = t("network.edit.accessSetting");
	public static final Localisation ENERGY_TYPE = t("network.edit.energytype");
	public static final Localisation ALLOW_CONVERSION = t("network.edit.allowconversion");
	public static final Localisation PREVIEW = t("network.edit.preview");
	public static final Localisation COLOUR_RED_CHAR = t("network.edit.color.r");
	public static final Localisation COLOUR_GREEN_CHAR = t("network.edit.color.g");
	public static final Localisation COLOUR_BLUE_CHAR = t("network.edit.color.b");
	
	
	// ACCESS SETTING \\
	public static final Localisation ACCESS_RESTRICTED = t("network.edit.restricted");
	public static final Localisation ACCESS_PRIVATE = t("network.edit.private");
	public static final Localisation ACCESS_PUBLIC = t("network.edit.public");

	//// NETWORK ERRORS \\\\
	public static final Localisation ERROR_NONE_ERROR = t("network.error.none");
	public static final Localisation ERROR_WAITING_FOR_SERVER = t("network.error.waiting");
	public static final Localisation ERROR_NO_CONNECTIONS = t("network.error.noconnections");
	public static final Localisation ERROR_NO_NETWORKS = t("network.error.nonetworks");
	public static final Localisation ERROR_NO_STATISTICS = t("network.error.nostatistics");
	public static final Localisation ERROR_NO_CONNECTED_NETWORK = t("network.error.noconnected");
	public static final Localisation ERROR_NO_NETWORK_TO_EDIT = t("network.error.nonetworktoedit");
	public static final Localisation ERROR_NO_MATCHES = t("network.error.nomatches");
	public static final Localisation ERROR_HAS_CONTROLLER = t("network.error.hasController");
	public static final Localisation ERROR_NOT_OWNER = t("network.error.notOwner");
	public static final Localisation ERROR_ACCESS_DENIED = t("network.error.accessDenied");
	public static final Localisation ERROR_EDIT_NETWORK = t("network.error.editNetwork");
	public static final Localisation ERROR_NETWORK_MAX_REACHED = t("network.error.networkMaxReached");
	public static final Localisation ERROR_INVALID_USER = t("network.error.invalidUser");
	public static final Localisation ERROR_NO_PLAYERS_CAN_BE_ADDED = t("network.error.noplayerscanbeadded");
	public static final Localisation ERROR_UNAVAILABLE_IN_PRIVATE = t("network.error.unavailableinprivate");
	public static final Localisation ERROR_CHUNK_UNLOADED = t("network.error.chunkunloaded");
	public static final Localisation ERROR_NO_PERMISSION = t("network.error.nopermisson");
	
	//// PLAYER ACCESS \\\\
	public static final Localisation PLAYERS_NETWORK_USER = t("network.player.user");
	public static final Localisation PLAYERS_NETWORK_OWNER = t("network.player.owner");
	public static final Localisation PLAYERS_NETWORK_SHARED_OWNER = t("network.player.sharedowner");
	public static final Localisation PLAYERS_NETWORK_BLOCKED = t("network.player.blocked");
	public static final Localisation PLAYERS_NETWORK_CREATIVE = t("network.player.creative");
	
	
	String text;

	FluxTranslate(String text) {
		this.text = text;
	}
	
	public static Localisation t(String s) {
		Localisation l = new Localisation(s);
		locals.add(l);
		return l;
	}

	public static Localisation i(Item item) {
		if (item == null)
			return null;
		Localisation l = new Localisation(item.getUnlocalizedName() + ".name");
		locals.add(l);
		return l;
	}

	public static Localisation b(Block block) {
		if (block == null)
			return null;
		Localisation l = new Localisation(block.getUnlocalizedName() + ".name");
		locals.add(l);
		return l;
	}
	
	public static String translateBoolean(boolean bool){
		return bool ? TRUE.t() : FALSE.t();
	}
	
	public static String translateToggle(boolean bool){
		return bool ? ENABLED.t() : DISABLED.t();
	}

}
