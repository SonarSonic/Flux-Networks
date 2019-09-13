package fluxnetworks;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

public class FluxTranslate {

    public static final String EMPTY = "";

    private static final String INFO = "info.";
    private static final String TOOLTIP = "tooltip.";

    private static String i(String root) {
        return I18n.format(INFO + FluxNetworks.MODID + '.' + root);
    }

    private static String t(String root) {
        return I18n.format(TOOLTIP + FluxNetworks.MODID + '.' + root);
    }

    public static final String FLUX_TOOLTIP = t("flux");
    public static final String FLUX_CONTROLLER_TOOLTIP = t("fluxcontroller");
    public static final String FLUX_PLUG_TOOLTIP = t("fluxplug");
    public static final String FLUX_POINT_TOOLTIP = t("fluxpoint");
    public static final String FLUX_STORAGE_TOOLTIP = t("fluxstorage");

    public static final String TAB_HOME = i("tab.home");
    public static final String TAB_SELECTION = i("tab.selection");
    public static final String TAB_WIRELESS = i("tab.wireless");
    public static final String TAB_CONNECTION = i("tab.connection");
    public static final String TAB_STATISTICS = i("tab.statistics");
    public static final String TAB_MEMBER = i("tab.member");
    public static final String TAB_SETTING = i("tab.setting");
    public static final String TAB_CREATE = i("tab.create");

    public static final String NETWORK_NAME = i("network.name");
    public static final String WIRELESS_CHARGING = i("network.wireless");

    public static final String CLICK = i("click");
    public static final String ABOVE = i("above");

    public static final String ERROR_NO_SELECTED = i("error.noselected");

    public static final String NAME = i("flux.name");
    public static final String PRIORITY = i("flux.priority");
    public static final String SURGE = i("flux.surge");
    public static final String SURGE_MODE = i("flux.surgemode");
    public static final String TRANSFER_LIMIT = i("flux.transferlimit");
    public static final String DISABLE_LIMIT = i("flux.disablelimit");
    public static final String CHUNK_LOADING = i("flux.chunkloading");

    /** Server Only (Translation key) **/
    public static final String ACCESS_DENIED_KEY = INFO + FluxNetworks.MODID + ".denied.access";
    public static final String ACCESS_OCCUPY_KEY = INFO + FluxNetworks.MODID + ".denied.occupy";
    public static final String REMOVAL_DENIED_KEY = INFO + FluxNetworks.MODID + ".denied.removal";
    public static final String SA_ON_KEY = INFO + FluxNetworks.MODID + ".sa.on";
    public static final String SA_OFF_KEY = INFO + FluxNetworks.MODID + ".sa.off";
}
