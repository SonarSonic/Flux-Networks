package fluxnetworks;

import fluxnetworks.common.core.ILocalizationProvider;
import fluxnetworks.common.core.Localization;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class FluxTranslate implements ILocalizationProvider {

    public static final FluxTranslate INSTANCE = new FluxTranslate();

    public static final List<Localization> localizations = new ArrayList<>();

    public static final Localization EMPTY = new Localization("");

    public static final Localization FLUX_TOOLTIP = t("tooltip.fluxnetworks.flux");
    public static final Localization FLUX_CONTROLLER_TOOLTIP = t("tooltip.fluxnetworks.fluxcontroller");
    public static final Localization FLUX_PLUG_TOOLTIP = t("tooltip.fluxnetworks.fluxplug");
    public static final Localization FLUX_POINT_TOOLTIP = t("tooltip.fluxnetworks.fluxpoint");
    public static final Localization FLUX_STORAGE_TOOLTIP = t("tooltip.fluxnetworks.fluxstorage");

    public static final Localization TAB_HOME = i("tab.home");
    public static final Localization TAB_SELECTION = i("tab.selection");
    public static final Localization TAB_WIRELESS = i("tab.wireless");
    public static final Localization TAB_CONNECTION = i("tab.connection");
    public static final Localization TAB_STATISTICS = i("tab.statistics");
    public static final Localization TAB_MEMBER = i("tab.member");
    public static final Localization TAB_SETTING = i("tab.setting");
    public static final Localization TAB_CREATE = i("tab.create");

    public static final Localization NETWORK_NAME = i("network.name");
    public static final Localization NETWORK_FULL_NAME = i("network.fullname");
    public static final Localization NETWORK_SECURITY = i("network.security");
    public static final Localization NETWORK_PASSWORD = i("network.password");
    public static final Localization NETWORK_ENERGY = i("network.energy");
    public static final Localization NETWORK_COLOR = i("network.color");

    public static final Localization CLICK = i("click");
    public static final Localization ABOVE = i("above");

    public static final Localization ERROR_NO_SELECTED = i("error.noselected");
    public static final Localization ERROR_NO_NETWORK = i("error.nonetwork");

    public static final Localization NAME = i("flux.name");
    public static final Localization PRIORITY = i("flux.priority");
    public static final Localization SURGE = i("flux.surge");
    public static final Localization SURGE_MODE = i("flux.surgemode");
    public static final Localization TRANSFER_LIMIT = i("flux.transferlimit");
    public static final Localization DISABLE_LIMIT = i("flux.disablelimit");
    public static final Localization CHUNK_LOADING = i("flux.chunkloading");
    public static final Localization ENABLE_WIRELESS = i("flux.wireless");
    public static final Localization ENERGY = i("flux.energy");
    public static final Localization ENERGY_STORED = i("flux.energystored");
    public static final Localization BUFFER = i("flux.buffer");
    public static final Localization INTERNAL_BUFFER = i("flux.internalbuffer");
    public static final Localization UNLIMITED = i("flux.unlimited");
    public static final Localization FORCED_LOADING = i("flux.forcedloading");
    public static final Localization CHUNK_UNLOADED = i("flux.chunkunloaded");
    public static final Localization INPUT = i("flux.input");
    public static final Localization OUTPUT = i("flux.output");
    public static final Localization CHANGE = i("flux.change");

    public static final Localization SORT_BY = i("gui.sortby");
    public static final Localization SELECTED = i("gui.selected");
    public static final Localization SINGLE_EDIT = i("gui.singleedit");
    public static final Localization BATCH_EDIT = i("gui.batchedit");
    public static final Localization EDITING = i("gui.editing");
    public static final Localization CONNECTIONS = i("gui.connections");
    public static final Localization CUSTOM_COLOR = i("gui.customcolor");
    public static final Localization CONNECTING_TO = i("gui.connectingto");
    public static final Localization TOTAL = i("gui.total");
    public static final Localization DELETE_NETWORK = i("gui.deletenetwork");
    public static final Localization DOUBLE_SHIFT = i("gui.doubleshift");
    public static final Localization TRANSFER_OWNERSHIP = i("gui.transferownership");
    public static final Localization SET_USER = i("gui.setuser");
    public static final Localization SET_ADMIN = i("gui.setadmin");
    public static final Localization USERNAME = i("gui.playername");
    public static final Localization ACCESS = i("gui.playeraccess");
    public static final Localization CANCEL_MEMBERSHIP = i("gui.cancelmembership");
    public static final Localization YOU = i("gui.you");

    public static final Localization BATCH_CLEAR_BUTTON = i("button.batchclear");
    public static final Localization BATCH_EDIT_BUTTON = i("button.batchedit");
    public static final Localization BATCH_DISCONNECT_BUTTON = i("button.batchdisconnect");
    public static final Localization APPLY = i("button.apply");
    public static final Localization CANCEL = i("button.cancel");
    public static final Localization CREATE = i("button.create");
    public static final Localization CONNECT = i("button.connect");
    public static final Localization DELETE = i("button.delete");

    public static final Localization INVENTORY = i("slot.main");
    public static final Localization HOT_BAR = i("slot.hotbar");
    public static final Localization RIGHT_HAND = i("slot.righthand");
    public static final Localization LEFT_HAND = i("slot.lefthand");
    public static final Localization ARMOR = i("slot.armor");
    public static final Localization BAUBLES = i("slot.baubles");

    public static final Localization PLUGS = i("stat.plug");
    public static final Localization POINTS = i("stat.point");
    public static final Localization CONTROLLERS = i("stat.controller");
    public static final Localization STORAGES = i("stat.storage");


    /** Server Only (Translation key) **/
    public static final String ACCESS_DENIED_KEY = "info.fluxnetworks.denied.access";
    public static final String ACCESS_OCCUPY_KEY = "info.fluxnetworks.denied.occupy";
    public static final String REMOVAL_DENIED_KEY = "info.fluxnetworks.denied.removal";
    public static final String SA_ON_KEY = "info.fluxnetworks.superadmin.on";
    public static final String SA_OFF_KEY = "info.fluxnetworks.superadmin.off";

    /** Server Only **/
    public static final Localization REJECT = t("info.fluxnetworks.feedback.reject");
    public static final Localization NO_OWNER = t("info.fluxnetworks.feedback.noowner");
    public static final Localization NO_ADMIN = t("info.fluxnetworks.feedback.noadmin");
    public static final Localization NO_SPACE = t("info.fluxnetworks.feedback.nospace");
    public static final Localization HAS_CONTROLLER = t("info.fluxnetworks.feedback.hascontroller");
    public static final Localization INVALID_USER = t("info.fluxnetworks.feedback.invaliduser");
    public static final Localization ILLEGAL_PASSWORD = t("info.fluxnetworks.feedback.illegalpassword");
    public static final Localization HAS_LOADER = t("info.fluxnetworks.feedback.hasloader");
    public static final Localization BANNED_LOADING = t("info.fluxnetworks.feedback.bannedloading");
    public static final Localization REJECT_SOME = t("info.fluxnetworks.feedback.rejectsome");

    public static final Localization OWNER = t("info.fluxnetworks.access.owner");
    public static final Localization ADMIN = t("info.fluxnetworks.access.admin");
    public static final Localization USER = t("info.fluxnetworks.access.user");
    public static final Localization BLOCKED = t("info.fluxnetworks.access.blocked");
    public static final Localization SUPER_ADMIN = t("info.fluxnetworks.access.superadmin");

    public static final Localization ENCRYPTED = t("info.fluxnetworks.security.encrypted");
    public static final Localization PUBLIC = t("info.fluxnetworks.security.public");

    public static Localization t(String s) {
        Localization l = new Localization(s);
        localizations.add(l);
        return l;
    }

    public static Localization i(String s) {
        return t("info.fluxnetworks." + s);
    }

    @Override
    public List<Localization> getLocalizations(List<Localization> localizations) {
        return FluxTranslate.localizations;
    }
}
