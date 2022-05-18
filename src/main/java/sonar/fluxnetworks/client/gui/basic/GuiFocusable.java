package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.common.device.FluxDeviceMenu;
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
     * de-focus other text elements
     */
    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
        children().forEach(child -> {
            if (child != listener && child instanceof FluxEditBox editBox) {
                if (editBox.isFocused()) {
                    //editBox.setFocused2(false);
                    //FIXME
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (getFocused() != null) {
            if (keyCode == 256) {
                this.setFocused(null);
                return true;
            }
            if (minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
                return false; // allows the typing of "E"
            }
        } else if (keyCode == 256 || minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            if (this instanceof GuiPopup core) {
                core.mHost.closePopup();
                return true;
            }
            if (this instanceof GuiTab core) {
                if (core.getNavigationTab() == EnumNavigationTab.TAB_HOME) {
                    onClose();
                } else {
                    //core.switchTab(EnumNavigationTab.TAB_HOME);
                    //FIXME
                    if (FluxConfig.enableButtonSound) {
                        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(RegistrySounds.BUTTON_CLICK, 1.0F));
                    }
                }
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
