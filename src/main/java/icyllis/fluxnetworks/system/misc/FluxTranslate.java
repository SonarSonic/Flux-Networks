package icyllis.fluxnetworks.system.misc;

import net.minecraft.client.resources.I18n;
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

    public static final String TAB_HOME = i("tab.home");
    public static final String TAB_SELECTION = i("tab.selection");
    public static final String TAB_WIRELESS = i("tab.wireless");
    public static final String TAB_CONNECTION = i("tab.connection");
    public static final String TAB_STATISTICS = i("tab.statistics");
    public static final String TAB_MEMBER = i("tab.member");
    public static final String TAB_SETTING = i("tab.setting");
    public static final String TAB_CREATE = i("tab.create");

    public static final String NETWORK_NAME = i("network.name");
    public static final String NETWORK_FULL_NAME = i("network.fullname");
    public static final String NETWORK_SECURITY = i("network.security");
    public static final String NETWORK_PASSWORD = i("network.password");
    public static final String NETWORK_ENERGY = i("network.energy");
    public static final String NETWORK_COLOR = i("network.color");

    public static final String CLICK = i("click");
    public static final String ABOVE = i("above");

    public static final String ERROR_NO_SELECTED = i("error.noselected");
    public static final String ERROR_NO_NETWORK = i("error.nonetwork");

    public static final String NAME = i("flux.name");
    public static final String PRIORITY = i("flux.priority");
    public static final String SURGE = i("flux.surge");
    public static final String SURGE_MODE = i("flux.surgemode");
    public static final String TRANSFER_LIMIT = i("flux.transferlimit");
    public static final String DISABLE_LIMIT = i("flux.disablelimit");
    public static final String CHUNK_LOADING = i("flux.chunkloading");
    public static final String ENABLE_WIRELESS = i("flux.wireless");
    public static final String ENERGY = i("flux.energy");
    public static final String ENERGY_STORED = i("flux.energystored");
    public static final String BUFFER = i("flux.buffer");
    public static final String INTERNAL_BUFFER = i("flux.internalbuffer");
    public static final String UNLIMITED = i("flux.unlimited");
    public static final String FORCED_LOADING = i("flux.forcedloading");
    public static final String CHUNK_UNLOADED = i("flux.chunkunloaded");
    public static final String INPUT = i("flux.input");
    public static final String OUTPUT = i("flux.output");
    public static final String CHANGE = i("flux.change");
    public static final String AVERAGE_TICK = i("flux.averagetick");


    public static final String SORT_BY = i("gui.sortby");
    public static final String SELECTED = i("gui.selected");
    public static final String SINGLE_EDIT = i("gui.singleedit");
    public static final String BATCH_EDIT = i("gui.batchedit");
    public static final String EDITING = i("gui.editing");
    public static final String CONNECTIONS = i("gui.connections");
    public static final String CUSTOM_COLOR = i("gui.customcolor");
    public static final String CONNECTING_TO = i("gui.connectingto");
    public static final String TOTAL = i("gui.total");
    public static final String DELETE_NETWORK = i("gui.deletenetwork");
    public static final String DOUBLE_SHIFT = i("gui.doubleshift");
    public static final String TRANSFER_OWNERSHIP = i("gui.transferownership");
    public static final String SET_USER = i("gui.setuser");
    public static final String SET_ADMIN = i("gui.setadmin");
    public static final String USERNAME = i("gui.playername");
    public static final String ACCESS = i("gui.playeraccess");
    public static final String CANCEL_MEMBERSHIP = i("gui.cancelmembership");
    public static final String YOU = i("gui.you");

    public static final String SORTING_SMART = i("gui.sort.smart");
    public static final String SORTING_ID = i("gui.sort.id");
    public static final String SORTING_NAME = i("gui.sort.name");

    public static final String BATCH_CLEAR_BUTTON = i("button.batchclear");
    public static final String BATCH_EDIT_BUTTON = i("button.batchedit");
    public static final String BATCH_DISCONNECT_BUTTON = i("button.batchdisconnect");
    public static final String APPLY = i("button.apply");
    public static final String CANCEL = i("button.cancel");
    public static final String CREATE = i("button.create");
    public static final String CONNECT = i("button.connect");
    public static final String DELETE = i("button.delete");

    public static final String INVENTORY = i("slot.main");
    public static final String HOT_BAR = i("slot.hotbar");
    public static final String RIGHT_HAND = i("slot.righthand");
    public static final String LEFT_HAND = i("slot.lefthand");
    public static final String ARMOR = i("slot.armor");
    public static final String BAUBLES = i("slot.baubles");

    public static final String PLUGS = i("stat.plug");
    public static final String POINTS = i("stat.point");
    public static final String CONTROLLERS = i("stat.controller");
    public static final String STORAGES = i("stat.storage");


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

    public static String i(String s) {
        return I18n.format("info.fluxnetworks." + s);
    }
}
