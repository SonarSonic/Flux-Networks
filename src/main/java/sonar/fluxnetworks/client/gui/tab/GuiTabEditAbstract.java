package sonar.fluxnetworks.client.gui.tab;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.*;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.CustomColorButton;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.client.gui.popup.PopupCustomColor;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

/**
 * the base class for settings and creation guis
 */
public abstract class GuiTabEditAbstract extends GuiTabCore {

    protected SecurityLevel mSecurityLevel;
    public ColorButton mColorButton;
    public CustomColorButton mCustomColorButton;
    public FluxEditBox mNetworkName;
    public FluxEditBox mPassword;

    public GuiTabEditAbstract(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    public abstract void onEditSettingsChanged();

    @Override
    public void init() {
        super.init();
        if (getNetwork().isValid() || getNavigationTab() == EnumNavigationTab.TAB_CREATE) {
            mNetworkName = FluxEditBox.create(FluxTranslate.NETWORK_NAME.get() + ": ", font,
                            leftPos + 16, topPos + 28, 144, 12)
                    .setOutlineColor(0xFF808080);
            mNetworkName.setMaxLength(FluxNetwork.MAX_NETWORK_NAME_LENGTH);
            mNetworkName.setResponder(string -> onEditSettingsChanged());
            addRenderableWidget(mNetworkName);

            mPassword = FluxEditBox.create(FluxTranslate.NETWORK_PASSWORD.get() + ": ", font,
                            leftPos + 16, topPos + 62, 144, 12)
                    .setOutlineColor(0xFF808080)
                    .setTextInvisible();
            mPassword.setFilter(string -> string != null && (string.isEmpty() || !FluxUtils.isBadPassword(string)));
            mPassword.setMaxLength(FluxNetwork.MAX_PASSWORD_LENGTH);
            mPassword.setResponder(string -> onEditSettingsChanged());
            mPassword.setVisible(mSecurityLevel == SecurityLevel.ENCRYPTED);
            addRenderableWidget(mPassword);
        }
    }

    @Override
    protected void drawForegroundLayer(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(gr, mouseX, mouseY, deltaTicks);
        if (getNetwork().isValid() || getNavigationTab() == EnumNavigationTab.TAB_CREATE) {
            gr.drawCenteredString(font, getNavigationTab().getTranslatedName(),
                    leftPos + 88, topPos + 10, 0xFFB4B4B4);
            gr.drawString(font,
                    FluxTranslate.NETWORK_SECURITY.get() + ": " + ChatFormatting.AQUA + mSecurityLevel.getName(),
                    leftPos + 16, topPos + 47, 0xFF808080);
            //font.drawString(matrixStack, FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType
            // .getName(), 14, 78, 0x606060);
            gr.drawString(font, FluxTranslate.NETWORK_COLOR.get() + ":", leftPos + 16, topPos + 89, 0xFF808080);

            // "Edit color" tooltip
            if (getCurrentPopup() == null) {
                for (GuiButtonCore button : mButtons) {
                    if (button instanceof ColorButton colorButton && colorButton.isMouseHovered(mouseX, mouseY)) {
                        // TODO: should probably be replaced with a custom tooltip
                        setTooltipForNextRenderPass(FluxTranslate.CUSTOM_COLOR_EDIT.makeComponent());
                        renderTooltip(gr, mouseX, mouseY);
                    }
                }
            }

            renderNetwork(gr, mNetworkName.getValue(), mColorButton.mColor, topPos + 126);
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
                mPassword.setVisible(mSecurityLevel == SecurityLevel.ENCRYPTED);
                onEditSettingsChanged();
                return true;
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
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof ColorButton colorButton) {
            if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                mColorButton.setSelected(false);
                mColorButton = colorButton;
                mColorButton.setSelected(true);
                onEditSettingsChanged();
            } else if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                openPopup(new PopupCustomColor(this, colorButton.mColor));
            }
        }
    }

    protected void initColorSelector(int posX, int posY, boolean selectFirst) {
        int selectedIndex = selectFirst ? 0 : EnumNetworkColor.getColorIndex(getNetwork().getNetworkColor());

        for (int i = 0; i < EnumNetworkColor.VALUES.length; i++) {
            ColorButton button = new ColorButton(this, posX + (i % 7) * 16, posY + (i / 7) * 16, EnumNetworkColor.VALUES[i].getRGB());
            if (i == selectedIndex) {
                mColorButton = button;
                button.setSelected(true);
            }
            mButtons.add(button);
        }

        // Custom color button
        mCustomColorButton = new CustomColorButton(this, leftPos + 32, topPos + 103);
        // non-default color is selected
        if (mColorButton == null) {
            mCustomColorButton.mColor = getNetwork().getNetworkColor();
            mColorButton = mCustomColorButton;
            mCustomColorButton.setSelected(true);
        }
        mButtons.add(mCustomColorButton);
    }

    @Override
    public void onPopupClose(GuiPopupCore<?> popUp) {
        super.onPopupClose(popUp);
        if (popUp instanceof PopupCustomColor colorPopup && !colorPopup.mCancelled) {
            mCustomColorButton.mColor = colorPopup.mCurrentColor;
            mColorButton.setSelected(false);
            mColorButton = mCustomColorButton;
            mColorButton.setSelected(true);
            onEditSettingsChanged();
        }
    }
}
