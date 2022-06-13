package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.*;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.client.gui.popup.PopupCustomColor;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

/**
 * the base class for settings and creation guis
 */
public abstract class GuiTabEditAbstract extends GuiTabCore {

    protected SecurityLevel mSecurityLevel;
    public ColorButton mColorButton;
    public FluxEditBox mNetworkName;
    public FluxEditBox mPassword;

    public GuiTabEditAbstract(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    public abstract void onEditSettingsChanged();

    @Override
    public void init() {
        super.init();
        if (mNetwork.isValid() || getNavigationTab() == EnumNavigationTab.TAB_CREATE) {
            mNetworkName = FluxEditBox.create(FluxTranslate.NETWORK_NAME.get() + ": ", font,
                            leftPos + 16, topPos + 28, 144, 12)
                    .setOutlineColor(0xFF808080);
            mNetworkName.setMaxLength(FluxNetwork.MAX_NETWORK_NAME_LENGTH);
            mNetworkName.setResponder(string -> onEditSettingsChanged());
            addRenderableWidget(mNetworkName);

            mPassword = FluxEditBox.create(FluxTranslate.NETWORK_PASSWORD.get() + ": ", font,
                            leftPos + 16, topPos + 63, 144, 12)
                    .setOutlineColor(0xFF808080)
                    .setTextInvisible();
            mPassword.setMaxLength(FluxNetwork.MAX_PASSWORD_LENGTH);
            mPassword.setResponder(string -> onEditSettingsChanged());
            mPassword.setVisible(mSecurityLevel.isEncrypted());
            addRenderableWidget(mPassword);
        }
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (mNetwork.isValid() || getNavigationTab() == EnumNavigationTab.TAB_CREATE) {
            drawCenteredString(poseStack, font, getNavigationTab().getTranslatedName(),
                    leftPos + 88, topPos + 10, 0xb4b4b4);
            font.draw(poseStack,
                    FluxTranslate.NETWORK_SECURITY.get() + ": " + ChatFormatting.AQUA + mSecurityLevel.getName(),
                    leftPos + 16, topPos + 48, 0x808080);
            //font.drawString(matrixStack, FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType
            // .getName(), 14, 78, 0x606060);
            font.draw(poseStack, FluxTranslate.NETWORK_COLOR.get() + ":", leftPos + 16, topPos + 92, 0x808080);
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (mouseX >= leftPos + 36 && mouseX < leftPos + 96 && mouseY >= topPos + 48 && mouseY < topPos + 56) {
                mSecurityLevel = FluxUtils.cycle(mSecurityLevel, SecurityLevel.VALUES);
                mPassword.setVisible(mSecurityLevel.isEncrypted());
                onEditSettingsChanged();
                return true;
            }
            if (!mNetwork.isValid() && getNavigationTab() != EnumNavigationTab.TAB_CREATE) {
                if (mouseX >= leftPos + 20 && mouseX < leftPos + 154 && mouseY >= topPos + 16 && mouseY < topPos + 36) {
                    switchTab(EnumNavigationTab.TAB_SELECTION);
                }
            }
            /*if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() +
            88) {
                energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
                onEditSettingsChanged();
                return true;
            }*/
        }
        return false;
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof ColorButton) {
            mColorButton.setSelected(false);
            mColorButton = (ColorButton) button;
            mColorButton.setSelected(true);
            onEditSettingsChanged();
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                openPopup(new PopupCustomColor(this, mColorButton.mColor));
            }
        }
    }

    @Override
    public void onPopupClose(GuiPopupCore<?> popUp) {
        super.onPopupClose(popUp);
        if (popUp instanceof PopupCustomColor) {
            mColorButton.mColor = ((PopupCustomColor) popUp).mCurrentColor;
            onEditSettingsChanged();
        }
    }
}
