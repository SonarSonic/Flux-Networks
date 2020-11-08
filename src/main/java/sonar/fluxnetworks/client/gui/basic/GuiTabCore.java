package sonar.fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.GuiFluxConfiguratorHome;
import sonar.fluxnetworks.client.gui.GuiFluxDeviceHome;
import sonar.fluxnetworks.client.gui.button.NavigationButton;
import sonar.fluxnetworks.client.gui.tab.*;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.common.registry.RegistrySounds;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * for guis which have navigation tabs along the top
 */
public abstract class GuiTabCore extends GuiFluxCore {

    protected List<NavigationButton> navigationButtons = Lists.newArrayList();
    public EnumNavigationTab[] navigationTabs;

    public GuiTabCore(@Nonnull FluxMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        setDefaultTabs();
    }

    @Override
    public void init() {
        super.init();
        navigationButtons.clear();
        buttonLists.add(navigationButtons);
    }

    public abstract EnumNavigationTab getNavigationTab();

    public void setDefaultTabs() {
        this.navigationTabs = EnumNavigationTab.values();
    }

    public void setNavigationTabs(EnumNavigationTab[] navigationTabs) {
        this.navigationTabs = navigationTabs;
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && button instanceof NavigationButton) {
            switchTab(((NavigationButton) button).tab);
            if (FluxConfig.enableButtonSound) {
                minecraft.getSoundHandler().play(SimpleSound.master(RegistrySounds.BUTTON_CLICK, 1.0F));
            }
        }
    }

    public void configureNavigationButtons(EnumNavigationTab currentTab, @Nonnull EnumNavigationTab[] availableTabs) {
        int posCount = 0;
        for (EnumNavigationTab tab : availableTabs) {
            if (tab != EnumNavigationTab.TAB_CREATE) {
                navigationButtons.add(new NavigationButton(12 + (18 * posCount), -16, tab));
                posCount++;
            } else {
                navigationButtons.add(new NavigationButton(148, -16, tab));
            }
        }
        navigationButtons.get(currentTab.ordinal()).setMain();
    }

    protected final void switchTab(@Nonnull EnumNavigationTab tab) {
        switch (tab) {
            case TAB_HOME:
                if (container.bridge instanceof TileFluxDevice) {
                    Minecraft.getInstance().displayGuiScreen(new GuiFluxDeviceHome(container, player));
                } else if (container.bridge instanceof ItemFluxConfigurator.MenuBridge) {
                    Minecraft.getInstance().displayGuiScreen(new GuiFluxConfiguratorHome(container, player));
                } else if (container.bridge instanceof ItemAdminConfigurator.MenuBridge) {
                    Minecraft.getInstance().displayGuiScreen(new GuiFluxAdminHome(container, player));
                } else {
                    closeScreen();
                }
                break;
            case TAB_SELECTION:
                if (container.bridge instanceof ItemAdminConfigurator.MenuBridge && FluxClientCache.detailedNetworkView) {
                    Minecraft.getInstance().displayGuiScreen(new GuiTabDetailedSelection(container, player));
                } else {
                    Minecraft.getInstance().displayGuiScreen(new GuiTabSelection(container, player));
                }
                break;
            case TAB_WIRELESS:
                Minecraft.getInstance().displayGuiScreen(new GuiTabWireless(container, player));
                break;
            case TAB_CONNECTION:
                Minecraft.getInstance().displayGuiScreen(new GuiTabConnections(container, player));
                break;
            case TAB_STATISTICS:
                Minecraft.getInstance().displayGuiScreen(new GuiTabStatistics(container, player));
                break;
            case TAB_MEMBER:
                Minecraft.getInstance().displayGuiScreen(new GuiTabMembers(container, player));
                break;
            case TAB_SETTING:
                Minecraft.getInstance().displayGuiScreen(new GuiTabSettings(container, player));
                break;
            case TAB_CREATE:
                Minecraft.getInstance().displayGuiScreen(new GuiTabCreate(container, player));
                break;
        }
    }
}
