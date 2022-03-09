package sonar.fluxnetworks.api;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class FluxTranslate {

    public static final String TOOLTIP = "tooltip";
    public static final String GUI = "gui";

    public static final FluxTranslate
            FLUX_DUST_TOOLTIP = new FluxTranslate("tooltip.fluxnetworks.flux_dust"),
            FLUX_CONTROLLER_TOOLTIP = new FluxTranslate("tooltip.fluxnetworks.flux_controller"),
            FLUX_PLUG_TOOLTIP = new FluxTranslate("tooltip.fluxnetworks.flux_plug"),
            FLUX_POINT_TOOLTIP = new FluxTranslate("tooltip.fluxnetworks.flux_point"),
            FLUX_STORAGE_TOOLTIP = new FluxTranslate("tooltip.fluxnetworks.flux_storage"),
            FLUX_STORAGE_TOOLTIP_2 = new FluxTranslate("tooltip.fluxnetworks.flux_storage_2"),

    TAB_HOME = new FluxTranslate("gui.fluxnetworks.tab.home"),
            TAB_SELECTION = new FluxTranslate("gui.fluxnetworks.tab.selection"),
            TAB_WIRELESS = new FluxTranslate("gui.fluxnetworks.tab.wireless"),
            TAB_CONNECTION = new FluxTranslate("gui.fluxnetworks.tab.connection"),
            TAB_STATISTICS = new FluxTranslate("gui.fluxnetworks.tab.statistics"),
            TAB_MEMBER = new FluxTranslate("gui.fluxnetworks.tab.member"),
            TAB_SETTING = new FluxTranslate("gui.fluxnetworks.tab.setting"),
            TAB_CREATE = new FluxTranslate("gui.fluxnetworks.tab.create"),

    NETWORK_NAME = new FluxTranslate("gui.fluxnetworks.network.name"),
            NETWORK_FULL_NAME = new FluxTranslate("gui.fluxnetworks.network.fullname"),
            NETWORK_SECURITY = new FluxTranslate("gui.fluxnetworks.network.security"),
            NETWORK_PASSWORD = new FluxTranslate("gui.fluxnetworks.network.password"),
            NETWORK_ENERGY = new FluxTranslate("gui.fluxnetworks.network.energy"),
            NETWORK_COLOR = new FluxTranslate("gui.fluxnetworks.network.color"),

    CLICK_ABOVE = new FluxTranslate("gui.fluxnetworks.clickabove"),

    ERROR_NO_SELECTED = new FluxTranslate("gui.fluxnetworks.error.noselected"),
            ERROR_NO_NETWORK = new FluxTranslate("gui.fluxnetworks.error.nonetwork"),

    NAME = new FluxTranslate("gui.fluxnetworks.flux.name"),
            PRIORITY = new FluxTranslate("gui.fluxnetworks.flux.priority"),
            SURGE = new FluxTranslate("gui.fluxnetworks.flux.surge"),
            SURGE_MODE = new FluxTranslate("gui.fluxnetworks.flux.surgemode"),
            TRANSFER_LIMIT = new FluxTranslate("gui.fluxnetworks.flux.transferlimit"),
            DISABLE_LIMIT = new FluxTranslate("gui.fluxnetworks.flux.disablelimit"),
            CHUNK_LOADING = new FluxTranslate("gui.fluxnetworks.flux.chunkloading"),
            ENABLE_WIRELESS = new FluxTranslate("gui.fluxnetworks.flux.wireless"),
            ENERGY = new FluxTranslate("gui.fluxnetworks.flux.energy"),
            ENERGY_STORED = new FluxTranslate("gui.fluxnetworks.flux.energystored"),
            BUFFER = new FluxTranslate("gui.fluxnetworks.flux.buffer"),
            INTERNAL_BUFFER = new FluxTranslate("gui.fluxnetworks.flux.internalbuffer"),
            UNLIMITED = new FluxTranslate("gui.fluxnetworks.flux.unlimited"),
            FORCED_LOADING = new FluxTranslate("gui.fluxnetworks.flux.forcedloading"),
            CHUNK_UNLOADED = new FluxTranslate("gui.fluxnetworks.flux.chunkunloaded"),
            INPUT = new FluxTranslate("gui.fluxnetworks.flux.input"),
            OUTPUT = new FluxTranslate("gui.fluxnetworks.flux.output"),
            CHANGE = new FluxTranslate("gui.fluxnetworks.flux.change"),
            AVERAGE_TICK = new FluxTranslate("gui.fluxnetworks.flux.averagetick"),


    SORT_BY = new FluxTranslate("gui.fluxnetworks.gui.sortby"),
            SELECTED = new FluxTranslate("gui.fluxnetworks.gui.selected"),
            SINGLE_EDIT = new FluxTranslate("gui.fluxnetworks.gui.singleedit"),
            BATCH_EDIT = new FluxTranslate("gui.fluxnetworks.gui.batchedit"),
            EDITING_CONNECTIONS = new FluxTranslate("gui.fluxnetworks.gui.editingconnections"),
            CONNECTIONS = new FluxTranslate("gui.fluxnetworks.gui.connections"),
            CUSTOM_COLOR = new FluxTranslate("gui.fluxnetworks.gui.customcolor"),
            CONNECTING_TO = new FluxTranslate("gui.fluxnetworks.gui.connectingto"),
            TOTAL = new FluxTranslate("gui.fluxnetworks.gui.total"),
            DELETE_NETWORK = new FluxTranslate("gui.fluxnetworks.gui.deletenetwork"),
            DOUBLE_SHIFT = new FluxTranslate("gui.fluxnetworks.gui.doubleshift"),
            TRANSFER_OWNERSHIP = new FluxTranslate("gui.fluxnetworks.gui.transferownership"),
            SET_USER = new FluxTranslate("gui.fluxnetworks.gui.setuser"),
            SET_ADMIN = new FluxTranslate("gui.fluxnetworks.gui.setadmin"),
            USERNAME = new FluxTranslate("gui.fluxnetworks.gui.playername"),
            ACCESS = new FluxTranslate("gui.fluxnetworks.gui.playeraccess"),
            CANCEL_MEMBERSHIP = new FluxTranslate("gui.fluxnetworks.gui.cancelmembership"),
            YOU = new FluxTranslate("gui.fluxnetworks.gui.you"),
            DETAILED_VIEW = new FluxTranslate("gui.fluxnetworks.gui.detailedview"),

    SORTING_SMART = new FluxTranslate("gui.fluxnetworks.gui.sort.smart"),
            SORTING_ID = new FluxTranslate("gui.fluxnetworks.gui.sort.id"),
            SORTING_NAME = new FluxTranslate("gui.fluxnetworks.gui.sort.name"),

    BATCH_CLEAR_BUTTON = new FluxTranslate("gui.fluxnetworks.button.batchclear"),
            BATCH_EDIT_BUTTON = new FluxTranslate("gui.fluxnetworks.button.batchedit"),
            BATCH_DISCONNECT_BUTTON = new FluxTranslate("gui.fluxnetworks.button.batchdisconnect"),
            APPLY = new FluxTranslate("gui.fluxnetworks.button.apply"),
            CANCEL = new FluxTranslate("gui.fluxnetworks.button.cancel"),
            CREATE = new FluxTranslate("gui.fluxnetworks.button.create"),
            CONNECT = new FluxTranslate("gui.fluxnetworks.button.connect"),
            DELETE = new FluxTranslate("gui.fluxnetworks.button.delete"),

    INVENTORY = new FluxTranslate("gui.fluxnetworks.slot.main"),
            HOT_BAR = new FluxTranslate("gui.fluxnetworks.slot.hotbar"),
            MAIN_HAND = new FluxTranslate("gui.fluxnetworks.slot.mainhand"),
            OFF_HAND = new FluxTranslate("gui.fluxnetworks.slot.offhand"),
            ARMOR = new FluxTranslate("gui.fluxnetworks.slot.armor"),
            CURIOS = new FluxTranslate("gui.fluxnetworks.slot.curios"),

    PLUGS = new FluxTranslate("gui.fluxnetworks.stat.plug"),
            POINTS = new FluxTranslate("gui.fluxnetworks.stat.point"),
            CONTROLLERS = new FluxTranslate("gui.fluxnetworks.stat.controller"),
            STORAGES = new FluxTranslate("gui.fluxnetworks.stat.storage"),

    REMOVAL_DENIED = new FluxTranslate("gui.fluxnetworks.denied.removal"),
            SA_ON = new FluxTranslate("gui.fluxnetworks.superadmin.on"),
            SA_OFF = new FluxTranslate("gui.fluxnetworks.superadmin.off"),

    REJECT = new FluxTranslate("gui.fluxnetworks.feedback.reject"),
            NO_OWNER = new FluxTranslate("gui.fluxnetworks.feedback.noowner"),
            NO_ADMIN = new FluxTranslate("gui.fluxnetworks.feedback.noadmin"),
            NO_SPACE = new FluxTranslate("gui.fluxnetworks.feedback.nospace"),
            HAS_CONTROLLER = new FluxTranslate("gui.fluxnetworks.feedback.hascontroller"),

    INVALID_USER = new FluxTranslate("gui.fluxnetworks.feedback.invaliduser"),
            ILLEGAL_PASSWORD = new FluxTranslate("gui.fluxnetworks.feedback.illegalpassword"),
            BANNED_LOADING = new FluxTranslate("gui.fluxnetworks.feedback.bannedloading"),

    OWNER = new FluxTranslate("gui.fluxnetworks.access.owner"),
            ADMIN = new FluxTranslate("gui.fluxnetworks.access.admin"),
            USER = new FluxTranslate("gui.fluxnetworks.access.user"),
            BLOCKED = new FluxTranslate("gui.fluxnetworks.access.blocked"),
            SUPER_ADMIN = new FluxTranslate("gui.fluxnetworks.access.superadmin"),

    JEI_CREATING_FLUX = new FluxTranslate("gui.fluxnetworks.jei.creatingfluxrecipe"),
            JEI_LEFT_CLICK = new FluxTranslate("gui.fluxnetworks.jei.leftclickhelp");

    private final String mKey;

    public FluxTranslate(String key) {
        mKey = key;
    }

    private static final ChatFormatting[] ERROR_STYLE = {ChatFormatting.BOLD, ChatFormatting.DARK_RED};

    public static final Component
            ACCESS_DENIED = new TranslatableComponent("gui.fluxnetworks.access_denied").withStyle(ERROR_STYLE),
            ACCESS_OCCUPY = new TranslatableComponent("gui.fluxnetworks.denied_occupy").withStyle(ERROR_STYLE);

    @Nonnull
    public String t() {
        return Language.getInstance().getOrDefault(mKey);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public String format(Object... args) {
        return I18n.get(mKey, args);
    }

    @Nonnull
    public MutableComponent component() {
        return new TranslatableComponent(mKey);
    }

    @Nonnull
    public MutableComponent component(Object... args) {
        return new TranslatableComponent(mKey, args);
    }

    @Nonnull
    @Override
    public String toString() {
        // also work on server side
        return t();
    }
}
