package fluxnetworks.api.translate;

import java.util.ArrayList;
import java.util.List;

public class FluxTranslate {

    public static final FluxTranslate INSTANCE = new FluxTranslate();

    public static final List<Translation> translations = new ArrayList<>();

    public static final Translation EMPTY = new Translation("");

    public static final Translation FLUX_TOOLTIP = t("tooltip.fluxnetworks.flux");
    public static final Translation FLUX_CONTROLLER_TOOLTIP = t("tooltip.fluxnetworks.fluxcontroller");
    public static final Translation FLUX_PLUG_TOOLTIP = t("tooltip.fluxnetworks.fluxplug");
    public static final Translation FLUX_POINT_TOOLTIP = t("tooltip.fluxnetworks.fluxpoint");
    public static final Translation FLUX_STORAGE_TOOLTIP = t("tooltip.fluxnetworks.fluxstorage");

    public static final Translation TAB_HOME = i("tab.home");
    public static final Translation TAB_SELECTION = i("tab.selection");
    public static final Translation TAB_WIRELESS = i("tab.wireless");
    public static final Translation TAB_CONNECTION = i("tab.connection");
    public static final Translation TAB_STATISTICS = i("tab.statistics");
    public static final Translation TAB_MEMBER = i("tab.member");
    public static final Translation TAB_SETTING = i("tab.setting");
    public static final Translation TAB_CREATE = i("tab.create");

    public static final Translation NETWORK_NAME = i("network.name");
    public static final Translation NETWORK_FULL_NAME = i("network.fullname");
    public static final Translation NETWORK_SECURITY = i("network.security");
    public static final Translation NETWORK_PASSWORD = i("network.password");
    public static final Translation NETWORK_ENERGY = i("network.energy");
    public static final Translation NETWORK_COLOR = i("network.color");

    public static final Translation CLICK = i("click");
    public static final Translation ABOVE = i("above");

    public static final Translation ERROR_NO_SELECTED = i("error.noselected");
    public static final Translation ERROR_NO_NETWORK = i("error.nonetwork");

    public static final Translation NAME = i("flux.name");
    public static final Translation PRIORITY = i("flux.priority");
    public static final Translation SURGE = i("flux.surge");
    public static final Translation SURGE_MODE = i("flux.surgemode");
    public static final Translation TRANSFER_LIMIT = i("flux.transferlimit");
    public static final Translation DISABLE_LIMIT = i("flux.disablelimit");
    public static final Translation CHUNK_LOADING = i("flux.chunkloading");
    public static final Translation ENABLE_WIRELESS = i("flux.wireless");
    public static final Translation ENERGY = i("flux.energy");
    public static final Translation ENERGY_STORED = i("flux.energystored");
    public static final Translation BUFFER = i("flux.buffer");
    public static final Translation INTERNAL_BUFFER = i("flux.internalbuffer");
    public static final Translation UNLIMITED = i("flux.unlimited");
    public static final Translation FORCED_LOADING = i("flux.forcedloading");
    public static final Translation CHUNK_UNLOADED = i("flux.chunkunloaded");
    public static final Translation INPUT = i("flux.input");
    public static final Translation OUTPUT = i("flux.output");
    public static final Translation CHANGE = i("flux.change");
    public static final Translation AVERAGE_TICK = i("flux.averagetick");


    public static final Translation SORT_BY = i("gui.sortby");
    public static final Translation SELECTED = i("gui.selected");
    public static final Translation SINGLE_EDIT = i("gui.singleedit");
    public static final Translation BATCH_EDIT = i("gui.batchedit");
    public static final Translation EDITING = i("gui.editing");
    public static final Translation CONNECTIONS = i("gui.connections");
    public static final Translation CUSTOM_COLOR = i("gui.customcolor");
    public static final Translation CONNECTING_TO = i("gui.connectingto");
    public static final Translation TOTAL = i("gui.total");
    public static final Translation DELETE_NETWORK = i("gui.deletenetwork");
    public static final Translation DOUBLE_SHIFT = i("gui.doubleshift");
    public static final Translation TRANSFER_OWNERSHIP = i("gui.transferownership");
    public static final Translation SET_USER = i("gui.setuser");
    public static final Translation SET_ADMIN = i("gui.setadmin");
    public static final Translation USERNAME = i("gui.playername");
    public static final Translation ACCESS = i("gui.playeraccess");
    public static final Translation CANCEL_MEMBERSHIP = i("gui.cancelmembership");
    public static final Translation YOU = i("gui.you");

    public static final Translation SORTING_SMART = i("gui.sort.smart");
    public static final Translation SORTING_ID = i("gui.sort.id");
    public static final Translation SORTING_NAME = i("gui.sort.name");

    public static final Translation BATCH_CLEAR_BUTTON = i("button.batchclear");
    public static final Translation BATCH_EDIT_BUTTON = i("button.batchedit");
    public static final Translation BATCH_DISCONNECT_BUTTON = i("button.batchdisconnect");
    public static final Translation APPLY = i("button.apply");
    public static final Translation CANCEL = i("button.cancel");
    public static final Translation CREATE = i("button.create");
    public static final Translation CONNECT = i("button.connect");
    public static final Translation DELETE = i("button.delete");

    public static final Translation INVENTORY = i("slot.main");
    public static final Translation HOT_BAR = i("slot.hotbar");
    public static final Translation RIGHT_HAND = i("slot.righthand");
    public static final Translation LEFT_HAND = i("slot.lefthand");
    public static final Translation ARMOR = i("slot.armor");
    public static final Translation BAUBLES = i("slot.baubles");

    public static final Translation PLUGS = i("stat.plug");
    public static final Translation POINTS = i("stat.point");
    public static final Translation CONTROLLERS = i("stat.controller");
    public static final Translation STORAGES = i("stat.storage");


    /** Server Only (Translation key) **/
    public static final String ACCESS_DENIED_KEY = "info.fluxnetworks.denied.access";
    public static final String ACCESS_OCCUPY_KEY = "info.fluxnetworks.denied.occupy";
    public static final String REMOVAL_DENIED_KEY = "info.fluxnetworks.denied.removal";
    public static final String SA_ON_KEY = "info.fluxnetworks.superadmin.on";
    public static final String SA_OFF_KEY = "info.fluxnetworks.superadmin.off";

    /** Server Only **/
    public static final Translation REJECT = t("info.fluxnetworks.feedback.reject");
    public static final Translation NO_OWNER = t("info.fluxnetworks.feedback.noowner");
    public static final Translation NO_ADMIN = t("info.fluxnetworks.feedback.noadmin");
    public static final Translation NO_SPACE = t("info.fluxnetworks.feedback.nospace");
    public static final Translation HAS_CONTROLLER = t("info.fluxnetworks.feedback.hascontroller");
    public static final Translation INVALID_USER = t("info.fluxnetworks.feedback.invaliduser");
    public static final Translation ILLEGAL_PASSWORD = t("info.fluxnetworks.feedback.illegalpassword");
    public static final Translation HAS_LOADER = t("info.fluxnetworks.feedback.hasloader");
    public static final Translation BANNED_LOADING = t("info.fluxnetworks.feedback.bannedloading");
    public static final Translation REJECT_SOME = t("info.fluxnetworks.feedback.rejectsome");

    public static final Translation OWNER = t("info.fluxnetworks.access.owner");
    public static final Translation ADMIN = t("info.fluxnetworks.access.admin");
    public static final Translation USER = t("info.fluxnetworks.access.user");
    public static final Translation BLOCKED = t("info.fluxnetworks.access.blocked");
    public static final Translation SUPER_ADMIN = t("info.fluxnetworks.access.superadmin");

    public static final Translation ENCRYPTED = t("info.fluxnetworks.security.encrypted");
    public static final Translation PUBLIC = t("info.fluxnetworks.security.public");

    public static Translation t(String s) {
        Translation l = new Translation(s);
        translations.add(l);
        return l;
    }

    public static Translation i(String s) {
        return t("info.fluxnetworks." + s);
    }
}
