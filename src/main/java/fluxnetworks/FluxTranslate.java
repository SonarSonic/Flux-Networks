package fluxnetworks;

import net.minecraft.client.resources.I18n;

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
    public static final String TAB_TRANSFER = i("tab.transfer");
    public static final String TAB_CONNECTION = i("tab.connection");
    public static final String TAB_STATISTICS = i("tab.statistics");
    public static final String TAB_MEMBER = i("tab.member");
    public static final String TAB_SETTING = i("tab.setting");
    public static final String TAB_CREATE = i("tab.create");

    public static final String SA_ON = i("superadmin.on");
    public static final String SA_OFF = i("superadmin.off");

    public static final String NETWORK_NAME = i("network.name");

    public static final String ACCESS_DENIED = i("denied.access");
    public static final String ACCESS_OCCUPY = i("denied.occupy");
    public static final String REMOVAL_DENIED = i("denied.removal");
}
