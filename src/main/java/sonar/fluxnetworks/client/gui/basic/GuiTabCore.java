package sonar.fluxnetworks.client.gui.basic;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.client.gui.*;
import sonar.fluxnetworks.client.gui.button.NavigationButton;
import sonar.fluxnetworks.client.gui.tab.*;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.register.RegistrySounds;

import javax.annotation.Nonnull;

/**
 * For guis which have navigation tabs along the top
 */
public abstract class GuiTabCore extends GuiFluxCore {

    public GuiTabCore(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    @Override
    public void init() {
        super.init();
        for (EnumNavigationTab tab : EnumNavigationTab.VALUES) {
            NavigationButton button;
            if (tab != EnumNavigationTab.TAB_CREATE) {
                button = new NavigationButton(getMinecraft(), 12 + (18 * tab.ordinal()) + leftPos, -16 + topPos, tab);
            } else {
                button = new NavigationButton(getMinecraft(), 148 + leftPos, -16 + topPos, tab);
            }
            button.setSelected(tab == getNavigationTab());
            mButtons.add(button);
        }
    }

    public abstract EnumNavigationTab getNavigationTab();

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && button instanceof NavigationButton) {
            switchTab(((NavigationButton) button).getTab());
            if (FluxConfig.enableButtonSound) {
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(RegistrySounds.BUTTON_CLICK, 1.0F));
            }
        }
    }

    protected final void switchTab(@Nonnull EnumNavigationTab tab) {
        switch (tab) {
            case TAB_HOME:
                if (menu.mProvider instanceof TileFluxDevice) {
                    getMinecraft().setScreen(new GuiFluxDeviceHome(menu, mPlayer));
                } /*else if (menu.mDevice instanceof ItemFluxConfigurator.MenuBridge) {
                    getMinecraft().setScreen(new GuiFluxConfiguratorHome(menu, mPlayer));
                }*/ else if (menu.mProvider instanceof ItemAdminConfigurator.Provider) {
                    getMinecraft().setScreen(new GuiFluxAdminHome(menu, mPlayer));
                } else {
                    onClose();
                }
                break;
            case TAB_SELECTION:
                if (menu.mProvider instanceof ItemAdminConfigurator.Provider &&
                        ClientCache.sSuperAdmin &&
                        ClientCache.sDetailedNetworkView) {
                    getMinecraft().setScreen(new GuiTabDetailedSelection(menu, mPlayer));
                } else {
                    getMinecraft().setScreen(new GuiTabSelection(menu, mPlayer));
                }
                break;
            /*case TAB_WIRELESS:
                getMinecraft().setScreen(new GuiTabWireless(menu, mPlayer));
                break;
            case TAB_CONNECTION:
                getMinecraft().setScreen(new GuiTabConnections(menu, mPlayer));
                break;*/
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
    }
}
