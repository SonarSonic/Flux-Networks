package sonar.fluxnetworks.api.text;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public enum FluxTranslate {
    FLUX_DUST_TOOLTIP(false, "tooltip.fluxnetworks.flux_dust"),
    FLUX_CONTROLLER_TOOLTIP(false, "tooltip.fluxnetworks.flux_controller"),
    FLUX_PLUG_TOOLTIP(false, "tooltip.fluxnetworks.flux_plug"),
    FLUX_POINT_TOOLTIP(false, "tooltip.fluxnetworks.flux_point"),
    FLUX_STORAGE_TOOLTIP(false, "tooltip.fluxnetworks.flux_storage"),

    TAB_HOME(true, "tab.home"),
    TAB_SELECTION(true, "tab.selection"),
    TAB_WIRELESS(true, "tab.wireless"),
    TAB_CONNECTION(true, "tab.connection"),
    TAB_STATISTICS(true, "tab.statistics"),
    TAB_MEMBER(true, "tab.member"),
    TAB_SETTING(true, "tab.setting"),
    TAB_CREATE(true, "tab.create"),

    NETWORK_NAME(true, "network.name"),
    NETWORK_FULL_NAME(true, "network.fullname"),
    NETWORK_SECURITY(true, "network.security"),
    NETWORK_PASSWORD(true, "network.password"),
    NETWORK_ENERGY(true, "network.energy"),
    NETWORK_COLOR(true, "network.color"),

    CLICK(true, "click"),
    ABOVE(true, "above"),

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
    EDITING(true, "gui.editing"),
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
    RIGHT_HAND(true, "slot.righthand"),
    LEFT_HAND(true, "slot.lefthand"),
    ARMOR(true, "slot.armor"),
    BAUBLES(true, "slot.baubles"),

    PLUGS(true, "stat.plug"),
    POINTS(true, "stat.point"),
    CONTROLLERS(true, "stat.controller"),
    STORAGES(true, "stat.storage"),


    ACCESS_DENIED_KEY(true, "denied.access"),
    ACCESS_OCCUPY_KEY(true, "denied.occupy"),
    REMOVAL_DENIED_KEY(true, "denied.removal"),
    SA_ON_KEY(true, "superadmin.on"),
    SA_OFF_KEY(true, "superadmin.off"),

    REJECT(true, "feedback.reject"),
    NO_OWNER(true, "feedback.noowner"),
    NO_ADMIN(true, "feedback.noadmin"),
    NO_SPACE(true, "feedback.nospace"),
    HAS_CONTROLLER(true, "feedback.hascontroller"),

    INVALID_USER(true, "feedback.invaliduser"),
    ILLEGAL_PASSWORD(true, "feedback.illegalpassword"),
    HAS_LOADER(true, "feedback.hasloader"),
    BANNED_LOADING(true, "feedback.bannedloading"),
    REJECT_SOME(true, "feedback.rejectsome"),

    OWNER(true, "access.owner"),
    ADMIN(true, "access.admin"),
    USER(true, "access.user"),
    BLOCKED(true, "access.blocked"),
    SUPER_ADMIN(true, "access.superadmin"),

    ENCRYPTED(true, "security.encrypted"),
    PUBLIC(true, "security.public"),
    PRIVATE(true, "security.private"),

    JEI_CREATING_FLUX(true, "jei.creatingfluxrecipe"),
    JEI_LEFT_CLICK(true, "jei.leftclickhelp");

    String key;

    FluxTranslate(boolean prefix, String key) {
        this.key = prefix ? "info.fluxnetworks." + key : key;
    }

    @Nonnull
    public String t() {
        return LanguageMap.getInstance().func_230503_a_(key);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public String format(Object... args) {
        return I18n.format(key, args);
    }

    @Nonnull
    public TranslationTextComponent getTextComponent() {
        return new TranslationTextComponent(key);
    }

    @Nonnull
    @Override
    public String toString() {
        // work on server side which uses en_us
        return t();
    }
}
