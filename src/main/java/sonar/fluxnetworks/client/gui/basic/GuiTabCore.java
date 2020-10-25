package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.GuiFluxConfiguratorHome;
import sonar.fluxnetworks.client.gui.GuiFluxDeviceHome;
import sonar.fluxnetworks.client.gui.button.NavigationButton;
import sonar.fluxnetworks.client.gui.tab.*;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.registry.RegistrySounds;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import java.util.List;

/**
 * for guis which have navigation tabs along the top
 */
public abstract class GuiTabCore extends GuiFluxCore {

    protected List<NavigationButton> navigationButtons = Lists.newArrayList();
    public EnumNavigationTabs[] navigationTabs;

    public GuiTabCore(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        setDefaultTabs();
    }

    @Override
    public void init() {
        super.init();
        navigationButtons.clear();
        buttonLists.add(navigationButtons);
    }

    public abstract EnumNavigationTabs getNavigationTab();

    public void setDefaultTabs() {
        this.navigationTabs = EnumNavigationTabs.values();
    }

    public void setNavigationTabs(EnumNavigationTabs[] navigationTabs) {
        this.navigationTabs = navigationTabs;
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && button instanceof NavigationButton) {
            switchTab(((NavigationButton) button).tab, player, connector);
            if (FluxConfig.enableButtonSound)
                minecraft.getSoundHandler().play(SimpleSound.master(RegistrySounds.BUTTON_CLICK, 1.0F));
        }
    }

    public void configureNavigationButtons(EnumNavigationTabs currentTab, EnumNavigationTabs[] availableTabs) {
        int posCount = 0;
        for (EnumNavigationTabs tab : availableTabs) {
            if (tab != EnumNavigationTabs.TAB_CREATE) {
                navigationButtons.add(new NavigationButton(12 + (18 * posCount), -16, tab));
                posCount++;
            } else {
                navigationButtons.add(new NavigationButton(148, -16, tab));
            }
        }
        navigationButtons.get(currentTab.ordinal()).setMain();
    }

    public static void switchTab(EnumNavigationTabs tab, PlayerEntity player, INetworkConnector connector) {
        switch (tab) {
            case TAB_HOME:
                if (connector instanceof TileFluxDevice) {
                    Minecraft.getInstance().displayGuiScreen(new GuiFluxDeviceHome(player, (TileFluxDevice) connector));
                } else if (connector instanceof ItemAdminConfigurator.NetworkConnector) {
                    Minecraft.getInstance().displayGuiScreen(new GuiFluxAdminHome(player, connector));
                } else if (connector instanceof ItemFluxConfigurator.NetworkConnector) {
                    Minecraft.getInstance().displayGuiScreen(new GuiFluxConfiguratorHome(player, (ItemFluxConfigurator.NetworkConnector) connector));
                } else {
                    player.closeScreen();
                }
                break;
            case TAB_SELECTION:
                if (connector instanceof ItemAdminConfigurator.NetworkConnector && FluxClientCache.detailedNetworkView) {
                    Minecraft.getInstance().displayGuiScreen(new GuiTabDetailedSelection(player, connector));
                    break;
                }
                Minecraft.getInstance().displayGuiScreen(new GuiTabSelection(player, connector));
                break;
            case TAB_WIRELESS:
                Minecraft.getInstance().displayGuiScreen(new GuiTabWireless(player, connector));
                break;
            case TAB_CONNECTION:
                Minecraft.getInstance().displayGuiScreen(new GuiTabConnections(player, connector));
                break;
            case TAB_STATISTICS:
                Minecraft.getInstance().displayGuiScreen(new GuiTabStatistics(player, connector));
                break;
            case TAB_MEMBER:
                Minecraft.getInstance().displayGuiScreen(new GuiTabMembers(player, connector));
                break;
            case TAB_SETTING:
                Minecraft.getInstance().displayGuiScreen(new GuiTabSettings(player, connector));
                break;
            case TAB_CREATE:
                Minecraft.getInstance().displayGuiScreen(new GuiTabCreate(player, connector));
                break;
        }

    }
}
