package sonar.fluxnetworks.client.gui;

import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;

public enum EnumNavigationTab {
    TAB_HOME(FluxTranslate.TAB_HOME),
    TAB_SELECTION(FluxTranslate.TAB_SELECTION),
    TAB_WIRELESS(FluxTranslate.TAB_WIRELESS),
    TAB_CONNECTION(FluxTranslate.TAB_CONNECTION),
    TAB_STATISTICS(FluxTranslate.TAB_STATISTICS),
    TAB_MEMBER(FluxTranslate.TAB_MEMBER),
    TAB_SETTING(FluxTranslate.TAB_SETTING),
    TAB_CREATE(FluxTranslate.TAB_SETTING);

    private final FluxTranslate tabName;
    private final int id;

    EnumNavigationTab(FluxTranslate tabName) {
        this.tabName = tabName;
        id = ordinal() + 1;
    }

    @Nonnull
    public String getTranslatedName(){
        return tabName.t();
    }

    public int getId() {
        return id;
    }
}
