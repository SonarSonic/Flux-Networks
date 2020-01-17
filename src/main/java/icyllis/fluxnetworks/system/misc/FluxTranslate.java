package icyllis.fluxnetworks.system.misc;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class FluxTranslate {

    public static final ITextComponent FLUX_TOOLTIP = t("tooltip.fluxnetworks.flux");
    public static final ITextComponent FLUX_CONTROLLER_TOOLTIP = t("tooltip.fluxnetworks.fluxcontroller");
    public static final ITextComponent FLUX_PLUG_TOOLTIP = t("tooltip.fluxnetworks.fluxplug");
    public static final ITextComponent FLUX_POINT_TOOLTIP = t("tooltip.fluxnetworks.fluxpoint");
    public static final ITextComponent FLUX_STORAGE_TOOLTIP = t("tooltip.fluxnetworks.fluxstorage");

    public static final ITextComponent TAB_HOME = i("tab.home");
    public static final ITextComponent TAB_SELECTION = i("tab.selection");
    public static final ITextComponent TAB_WIRELESS = i("tab.wireless");
    public static final ITextComponent TAB_CONNECTION = i("tab.connection");
    public static final ITextComponent TAB_STATISTICS = i("tab.statistics");
    public static final ITextComponent TAB_MEMBER = i("tab.member");
    public static final ITextComponent TAB_SETTING = i("tab.setting");
    public static final ITextComponent TAB_CREATE = i("tab.create");

    public static final ITextComponent NETWORK_NAME = i("network.name");
    public static final ITextComponent NETWORK_FULL_NAME = i("network.fullname");
    public static final ITextComponent NETWORK_SECURITY = i("network.security");
    public static final ITextComponent NETWORK_PASSWORD = i("network.password");
    public static final ITextComponent NETWORK_ENERGY = i("network.energy");
    public static final ITextComponent NETWORK_COLOR = i("network.color");

    public static final ITextComponent CLICK = i("click");
    public static final ITextComponent ABOVE = i("above");

    public static final ITextComponent ERROR_NO_SELECTED = i("error.noselected");
    public static final ITextComponent ERROR_NO_NETWORK = i("error.nonetwork");

    public static final ITextComponent NAME = i("flux.name");
    public static final ITextComponent PRIORITY = i("flux.priority");
    public static final ITextComponent SURGE = i("flux.surge");
    public static final ITextComponent SURGE_MODE = i("flux.surgemode");
    public static final ITextComponent TRANSFER_LIMIT = i("flux.transferlimit");
    public static final ITextComponent DISABLE_LIMIT = i("flux.disablelimit");
    public static final ITextComponent CHUNK_LOADING = i("flux.chunkloading");
    public static final ITextComponent ENABLE_WIRELESS = i("flux.wireless");
    public static final ITextComponent ENERGY = i("flux.energy");
    public static final ITextComponent ENERGY_STORED = i("flux.energystored");
    public static final ITextComponent BUFFER = i("flux.buffer");
    public static final ITextComponent INTERNAL_BUFFER = i("flux.internalbuffer");
    public static final ITextComponent UNLIMITED = i("flux.unlimited");
    public static final ITextComponent FORCED_LOADING = i("flux.forcedloading");
    public static final ITextComponent CHUNK_UNLOADED = i("flux.chunkunloaded");
    public static final ITextComponent INPUT = i("flux.input");
    public static final ITextComponent OUTPUT = i("flux.output");
    public static final ITextComponent CHANGE = i("flux.change");
    public static final ITextComponent AVERAGE_TICK = i("flux.averagetick");


    public static final ITextComponent SORT_BY = i("gui.sortby");
    public static final ITextComponent SELECTED = i("gui.selected");
    public static final ITextComponent SINGLE_EDIT = i("gui.singleedit");
    public static final ITextComponent BATCH_EDIT = i("gui.batchedit");
    public static final ITextComponent EDITING = i("gui.editing");
    public static final ITextComponent CONNECTIONS = i("gui.connections");
    public static final ITextComponent CUSTOM_COLOR = i("gui.customcolor");
    public static final ITextComponent CONNECTING_TO = i("gui.connectingto");
    public static final ITextComponent TOTAL = i("gui.total");
    public static final ITextComponent DELETE_NETWORK = i("gui.deletenetwork");
    public static final ITextComponent DOUBLE_SHIFT = i("gui.doubleshift");
    public static final ITextComponent TRANSFER_OWNERSHIP = i("gui.transferownership");
    public static final ITextComponent SET_USER = i("gui.setuser");
    public static final ITextComponent SET_ADMIN = i("gui.setadmin");
    public static final ITextComponent USERNAME = i("gui.playername");
    public static final ITextComponent ACCESS = i("gui.playeraccess");
    public static final ITextComponent CANCEL_MEMBERSHIP = i("gui.cancelmembership");
    public static final ITextComponent YOU = i("gui.you");

    public static final ITextComponent SORTING_SMART = i("gui.sort.smart");
    public static final ITextComponent SORTING_ID = i("gui.sort.id");
    public static final ITextComponent SORTING_NAME = i("gui.sort.name");

    public static final ITextComponent BATCH_CLEAR_BUTTON = i("button.batchclear");
    public static final ITextComponent BATCH_EDIT_BUTTON = i("button.batchedit");
    public static final ITextComponent BATCH_DISCONNECT_BUTTON = i("button.batchdisconnect");
    public static final ITextComponent APPLY = i("button.apply");
    public static final ITextComponent CANCEL = i("button.cancel");
    public static final ITextComponent CREATE = i("button.create");
    public static final ITextComponent CONNECT = i("button.connect");
    public static final ITextComponent DELETE = i("button.delete");

    public static final ITextComponent INVENTORY = i("slot.main");
    public static final ITextComponent HOT_BAR = i("slot.hotbar");
    public static final ITextComponent RIGHT_HAND = i("slot.righthand");
    public static final ITextComponent LEFT_HAND = i("slot.lefthand");
    public static final ITextComponent ARMOR = i("slot.armor");
    public static final ITextComponent BAUBLES = i("slot.baubles");

    public static final ITextComponent PLUGS = i("stat.plug");
    public static final ITextComponent POINTS = i("stat.point");
    public static final ITextComponent CONTROLLERS = i("stat.controller");
    public static final ITextComponent STORAGES = i("stat.storage");


    /** Server Only (ITextComponent key) **/
    public static final String ACCESS_DENIED_KEY = "info.fluxnetworks.denied.access";
    public static final String ACCESS_OCCUPY_KEY = "info.fluxnetworks.denied.occupy";
    public static final String REMOVAL_DENIED_KEY = "info.fluxnetworks.denied.removal";
    public static final String SA_ON_KEY = "info.fluxnetworks.superadmin.on";
    public static final String SA_OFF_KEY = "info.fluxnetworks.superadmin.off";

    /** Server Only **/
    public static final ITextComponent REJECT = t("info.fluxnetworks.feedback.reject");
    public static final ITextComponent NO_OWNER = t("info.fluxnetworks.feedback.noowner");
    public static final ITextComponent NO_ADMIN = t("info.fluxnetworks.feedback.noadmin");
    public static final ITextComponent NO_SPACE = t("info.fluxnetworks.feedback.nospace");
    public static final ITextComponent HAS_CONTROLLER = t("info.fluxnetworks.feedback.hascontroller");
    public static final ITextComponent INVALID_USER = t("info.fluxnetworks.feedback.invaliduser");
    public static final ITextComponent ILLEGAL_PASSWORD = t("info.fluxnetworks.feedback.illegalpassword");
    public static final ITextComponent HAS_LOADER = t("info.fluxnetworks.feedback.hasloader");
    public static final ITextComponent BANNED_LOADING = t("info.fluxnetworks.feedback.bannedloading");
    public static final ITextComponent REJECT_SOME = t("info.fluxnetworks.feedback.rejectsome");

    public static final ITextComponent OWNER = t("info.fluxnetworks.access.owner");
    public static final ITextComponent ADMIN = t("info.fluxnetworks.access.admin");
    public static final ITextComponent USER = t("info.fluxnetworks.access.user");
    public static final ITextComponent BLOCKED = t("info.fluxnetworks.access.blocked");
    public static final ITextComponent SUPER_ADMIN = t("info.fluxnetworks.access.superadmin");

    public static final ITextComponent ENCRYPTED = t("info.fluxnetworks.security.encrypted");
    public static final ITextComponent PUBLIC = t("info.fluxnetworks.security.public");

    public static ITextComponent t(String s) {
        return new TranslationTextComponent(s);
    }

    public static ITextComponent i(String s) {
        return t("info.fluxnetworks." + s);
    }
}
