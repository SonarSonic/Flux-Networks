package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.button.NavigationButton;
import sonar.fluxnetworks.common.device.FluxDeviceMenu;
import sonar.fluxnetworks.register.RegistrySounds;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * For guis which have navigation tabs along the top
 */
public abstract class GuiTab extends GuiFlux {

    protected List<NavigationButton> navigationButtons = Lists.newArrayList();
    public EnumNavigationTab[] navigationTabs;

    public GuiTab(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
        setDefaultTabs();
    }

    @Override
    public void init() {
        super.init();
        navigationButtons.clear();
    }

    public abstract EnumNavigationTab getNavigationTab();

    public void setDefaultTabs() {
        this.navigationTabs = EnumNavigationTab.values();
    }

    public void setNavigationTabs(EnumNavigationTab[] navigationTabs) {
        this.navigationTabs = navigationTabs;
    }

    @Override
    public void onButtonClicked(GuiButton button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && button instanceof NavigationButton) {
            //switchTab(((NavigationButton) button).getTab());
            if (FluxConfig.enableButtonSound) {
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(RegistrySounds.BUTTON_CLICK, 1.0F));
            }
        }
    }

    public void configureNavigationButtons(EnumNavigationTab currentTab, @Nonnull EnumNavigationTab[] availableTabs) {
       /* int posCount = 0;
        for (EnumNavigationTab tab : availableTabs) {
            if (tab != EnumNavigationTab.TAB_CREATE) {
                navigationButtons.add(new NavigationButton(12 + (18 * posCount), -16, tab));
                posCount++;
            } else {
                navigationButtons.add(new NavigationButton(148, -16, tab));
            }
        }
        navigationButtons.get(currentTab.ordinal()).setSelected();*/
    }

    /*protected final void switchTab(@Nonnull EnumNavigationTab tab) {
        switch (tab) {
            case TAB_HOME:
                if (menu.mDevice instanceof FluxDeviceEntity) {
                    getMinecraft().setScreen(new GuiFluxDeviceHome(menu, mPlayer));
                } else if (menu.mDevice instanceof ItemFluxConfigurator.MenuBridge) {
                    getMinecraft().setScreen(new GuiFluxConfiguratorHome(menu, mPlayer));
                } else if (menu.mDevice instanceof ItemAdminConfigurator.MenuBridge) {
                    getMinecraft().setScreen(new GuiFluxAdminHome(menu, mPlayer));
                } else {
                    closeScreen();
                }
                break;
            case TAB_SELECTION:
                if (menu.mDevice instanceof ItemAdminConfigurator.MenuBridge && FluxClientCache.detailedNetworkView) {
                    getMinecraft().setScreen(new GuiTabDetailedSelection(menu, mPlayer));
                } else {
                    getMinecraft().setScreen(new GuiTabSelection(menu, mPlayer));
                }
                break;
            case TAB_WIRELESS:
                getMinecraft().setScreen(new GuiTabWireless(menu, mPlayer));
                break;
            case TAB_CONNECTION:
                getMinecraft().setScreen(new GuiTabConnections(menu, mPlayer));
                break;
            case TAB_STATISTICS:
                getMinecraft().setScreen(new GuiTabStatistics(menu, mPlayer));
                break;
            case TAB_MEMBER:
                getMinecraft().setScreen(new GuiTabMembers(menu, mPlayer));
                break;
            case TAB_SETTING:
                getMinecraft().setScreen(new GuiTabSettings(menu, mPlayer));
                break;
            case TAB_CREATE:
                getMinecraft().setScreen(new GuiTabCreate(menu, mPlayer));
                break;
        }
    }*/
}
