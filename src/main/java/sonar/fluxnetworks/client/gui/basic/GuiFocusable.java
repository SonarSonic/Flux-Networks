package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.client.gui.GuiTabType;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.register.RegistrySounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fixes the focus of popup host and dialog popup.
 */
public abstract class GuiFocusable extends AbstractContainerScreen<FluxDeviceMenu> {

    public GuiFocusable(FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player.getInventory(), TextComponent.EMPTY);
    }

    /**
     * Un-focus other text elements
     */
    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
        for (GuiEventListener child : children()) {
            if (child != listener && child instanceof FluxEditBox editBox) {
                if (editBox.isFocused()) {
                    editBox.setFocused(false);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (getFocused() != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setFocused(null);
                return true;
            }
            if (getMinecraft().options.keyInventory.isActiveAndMatches(mouseKey)) {
                return false; // allows the typing of "E"
            }
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || getMinecraft().options.keyInventory.isActiveAndMatches(mouseKey)) {
            if (this instanceof GuiPopupCore core) {
                core.mHost.closePopup();
                return true;
            }
            if (this instanceof GuiTabCore core) {
                if (core.getCurrentTab() == GuiTabType.TAB_HOME) {
                    onClose();
                } else {
                    //FIXME
                    //core.switchTab(EnumNavigationTab.TAB_HOME);
                    if (FluxConfig.enableButtonSound) {
                        getMinecraft().getSoundManager().play(
                                SimpleSoundInstance.forUI(RegistrySounds.BUTTON_CLICK, 1.0F));
                    }
                }
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (GuiEventListener child : children()) {
            if (child instanceof FluxEditBox editBox) {
                editBox.tick();
            }
        }
    }
}
