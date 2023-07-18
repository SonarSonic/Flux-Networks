package sonar.fluxnetworks.client.gui.basic;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxTranslate;
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
                button = new NavigationButton(this, leftPos + 12 + (18 * tab.ordinal()), topPos - 16, tab);
            } else {
                button = new NavigationButton(this, leftPos + 148, topPos - 16, tab);
            }
            button.setSelected(tab == getNavigationTab());
            mButtons.add(button);
        }
    }

    public abstract EnumNavigationTab getNavigationTab();

    @Override
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && button instanceof NavigationButton) {
            switchTab(((NavigationButton) button).getTab(), true);
        }
    }

    protected void renderNavigationPrompt(GuiGraphics gr, @Nonnull FluxTranslate error,
                                          @Nonnull EnumNavigationTab tab) {
        gr.drawCenteredString(font, error.get(), width / 2, topPos + 16, 0xff808080);
        gr.pose().pushPose();
        gr.pose().scale(0.75f, 0.75f, 1);
        gr.drawCenteredString(font,
                FluxTranslate.CLICK_ABOVE.format(ChatFormatting.AQUA + tab.getTranslatedName() + ChatFormatting.RESET),
                (int) ((width / 2f) / 0.75f), (int) ((topPos + 28f) / 0.75f), 0x808080);
        gr.pose().popPose();
    }

    protected boolean redirectNavigationPrompt(double mouseX, double mouseY, int mouseButton,
                                               @Nonnull EnumNavigationTab tab) {
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (mouseX >= leftPos + 20 && mouseX < leftPos + 155 && mouseY >= topPos + 16 && mouseY < topPos + 36) {
                switchTab(tab, false);
                return true;
            }
        }
        return false;
    }

    protected final void switchTab(@Nonnull EnumNavigationTab tab, boolean playSound) {
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
                        ClientCache.sDetailedNetworkView &&
                        ClientCache.sSuperAdmin) {
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
        if (playSound && FluxConfig.enableButtonSound) {
            getMinecraft().getSoundManager().play(
                    SimpleSoundInstance.forUI(RegistrySounds.BUTTON_CLICK.get(), 1.0F));
        }
    }
}
