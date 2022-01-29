package sonar.fluxnetworks.api;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

public enum FluxTranslate {
    FLUX_DUST_TOOLTIP(false, "tooltip.fluxnetworks.flux_dust"),
    FLUX_CONTROLLER_TOOLTIP(false, "tooltip.fluxnetworks.flux_controller"),
    FLUX_PLUG_TOOLTIP(false, "tooltip.fluxnetworks.flux_plug"),
    FLUX_POINT_TOOLTIP(false, "tooltip.fluxnetworks.flux_point"),
    FLUX_STORAGE_TOOLTIP(false, "tooltip.fluxnetworks.flux_storage"),
    FLUX_STORAGE_TOOLTIP_2(false, "tooltip.fluxnetworks.flux_storage_2"),

    TAB_HOME(true, "tab.home"),
    TAB_SELECTION(true, "tab.selection"),
    TAB_WIRELESS(true, "tab.wireless"),
    TAB_CONNECTION(true, "tab.connection"),
    TAB_STATISTICS(true, "tab.statistics"),
    TAB_MEMBER(true, "tab.member"),
    TAB_SETTING(true, "tab.setting"),

    NETWORK_NAME(true, "network.name"),
    NETWORK_FULL_NAME(true, "network.fullname"),
    NETWORK_PASSWORD(true, "network.password"),
    NETWORK_ENERGY(true, "network.energy"),
    NETWORK_COLOR(true, "network.color"),

    CLICK_ABOVE(true, "clickabove"),

    ERROR_NO_SELECTED(true, "error.noselected"),
    ERROR_NO_NETWORK(true, "error.nonetwork"),

    NAME(true, "flux.name"),
    PRIORITY(true, "flux.priority"),
    SURGE(true, "flux.surge"),
    SURGE_MODE(true, "flux.surgemode"),
    TRANSFER_LIMIT(true, "flux.transferlimit"),
    DISABLE_LIMIT(true, "flux.disablelimit"),
    CHUNK_LOADING(true, "flux.chunkloading"),
    ENABLE_WIRELESS(true, "flux.wireless"),
    ENERGY(true, "flux.energy"),
    ENERGY_STORED(true, "flux.energystored"),
    BUFFER(true, "flux.buffer"),
    INTERNAL_BUFFER(true, "flux.internalbuffer"),
    UNLIMITED(true, "flux.unlimited"),
    FORCED_LOADING(true, "flux.forcedloading"),
    CHUNK_UNLOADED(true, "flux.chunkunloaded"),
    INPUT(true, "flux.input"),
    OUTPUT(true, "flux.output"),
    CHANGE(true, "flux.change"),
    AVERAGE_TICK(true, "flux.averagetick"),


    SORT_BY(true, "gui.sortby"),
    SELECTED(true, "gui.selected"),
    SINGLE_EDIT(true, "gui.singleedit"),
    BATCH_EDIT(true, "gui.batchedit"),
    EDITING_CONNECTIONS(true, "gui.editingconnections"),
    CONNECTIONS(true, "gui.connections"),
    CUSTOM_COLOR(true, "gui.customcolor"),
    CONNECTING_TO(true, "gui.connectingto"),
    TOTAL(true, "gui.total"),
    DELETE_NETWORK(true, "gui.deletenetwork"),
    DOUBLE_SHIFT(true, "gui.doubleshift"),
    TRANSFER_OWNERSHIP(true, "gui.transferownership"),
    SET_USER(true, "gui.setuser"),
    SET_ADMIN(true, "gui.setadmin"),
    USERNAME(true, "gui.playername"),
    ACCESS(true, "gui.playeraccess"),
    CANCEL_MEMBERSHIP(true, "gui.cancelmembership"),
    YOU(true, "gui.you"),
    DETAILED_VIEW(true, "gui.detailedview"),

    SORTING_SMART(true, "gui.sort.smart"),
    SORTING_ID(true, "gui.sort.id"),
    SORTING_NAME(true, "gui.sort.name"),

    BATCH_CLEAR_BUTTON(true, "button.batchclear"),
    BATCH_EDIT_BUTTON(true, "button.batchedit"),
    BATCH_DISCONNECT_BUTTON(true, "button.batchdisconnect"),
    APPLY(true, "button.apply"),
    CANCEL(true, "button.cancel"),
    CREATE(true, "button.create"),
    CONNECT(true, "button.connect"),
    DELETE(true, "button.delete"),

    INVENTORY(true, "slot.main"),
    HOT_BAR(true, "slot.hotbar"),
    MAIN_HAND(true, "slot.mainhand"),
    OFF_HAND(true, "slot.offhand"),
    ARMOR(true, "slot.armor"),
    CURIOS(true, "slot.curios"),

    PLUGS(true, "stat.plug"),
    POINTS(true, "stat.point"),
    CONTROLLERS(true, "stat.controller"),
    STORAGES(true, "stat.storage"),

    REMOVAL_DENIED(true, "denied.removal"),
    SA_ON(true, "superadmin.on"),
    SA_OFF(true, "superadmin.off"),

    REJECT(true, "feedback.reject"),
    NO_OWNER(true, "feedback.noowner"),
    NO_ADMIN(true, "feedback.noadmin"),
    NO_SPACE(true, "feedback.nospace"),
    HAS_CONTROLLER(true, "feedback.hascontroller"),

    INVALID_USER(true, "feedback.invaliduser"),
    ILLEGAL_PASSWORD(true, "feedback.illegalpassword"),
    BANNED_LOADING(true, "feedback.bannedloading"),

    OWNER(true, "access.owner"),
    ADMIN(true, "access.admin"),
    USER(true, "access.user"),
    BLOCKED(true, "access.blocked"),
    SUPER_ADMIN(true, "access.superadmin"),

    JEI_CREATING_FLUX(true, "jei.creatingfluxrecipe"),
    JEI_LEFT_CLICK(true, "jei.leftclickhelp");

    String key;

    FluxTranslate(boolean prefix, String key) {
        this.key = prefix ? "info." + FluxNetworks.MODID + "." + key : key;
    }

    private static final ChatFormatting[] ERROR_STYLE = {ChatFormatting.BOLD, ChatFormatting.DARK_RED};

    public static final Component
            ACCESS_DENIED = new TranslatableComponent("gui.fluxnetworks.access_denied").withStyle(ERROR_STYLE),
            ACCESS_OCCUPY = new TranslatableComponent("gui.fluxnetworks.denied_occupy").withStyle(ERROR_STYLE);

    public static final String
            TAB_CREATE = "gui.fluxnetworks.tab.create",
            NETWORK_SECURITY = "gui.fluxnetworks.network.security";

    @Nonnull
    public String t() {
        return Language.getInstance().getOrDefault(key);
    }

    @Nonnull
    public static String translate(String key) {
        return Language.getInstance().getOrDefault(key);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public String format(Object... args) {
        return I18n.get(key, args);
    }

    @Nonnull
    public TranslatableComponent getTextComponent() {
        return new TranslatableComponent(key);
    }

    @Nonnull
    public TranslatableComponent getTextComponent(Object... args) {
        return new TranslatableComponent(key, args);
    }

    @Nonnull
    @Override
    public String toString() {
        // work on server side which uses en_us
        return t();
    }
}
