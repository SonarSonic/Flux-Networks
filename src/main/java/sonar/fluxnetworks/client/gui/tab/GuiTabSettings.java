package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.SimpleButton;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;

public class GuiTabSettings extends GuiTabEditAbstract {

    public SimpleButton mApply;
    public SimpleButton mDelete;
    public int mDeleteCount;

    public GuiTabSettings(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mSecurityLevel = getNetwork().getSecurityLevel();
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_SETTING;
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (getNetwork().isValid()) {
            if (mDelete.isMouseHovered(mouseX, mouseY)) {
                if (mDelete.isClickable()) {
                    drawCenteredString(poseStack, font,
                            ChatFormatting.BOLD + FluxTranslate.DELETE_NETWORK.get(),
                            mDelete.x + mDelete.width / 2, mDelete.y - 12, 0xff0000);
                } else {
                    drawCenteredString(poseStack, font,
                            FluxTranslate.DOUBLE_SHIFT.get(),
                            mDelete.x + mDelete.width / 2, mDelete.y - 12, 0xffffff);
                }
            }
        } else {
            renderNavigationPrompt(poseStack, FluxTranslate.ERROR_NO_SELECTED.get(), FluxTranslate.TAB_SELECTION.get());
        }
    }

    @Override
    public void init() {
        super.init();
        if (getNetwork().isValid()) {
            mNetworkName.setValue(getNetwork().getNetworkName());

            mApply = new SimpleButton(minecraft, leftPos + (imageWidth / 2) + 12, topPos + 150, 48, 12);
            mApply.setText(FluxTranslate.APPLY.get());
            mApply.setClickable(false);
            mButtons.add(mApply);

            mDelete = new SimpleButton(minecraft, leftPos + (imageWidth / 2) - 12 - 48, topPos + 150, 48, 12);
            mDelete.setText(FluxTranslate.DELETE.get());
            mDelete.setColor(0xFFFF5555);
            mDelete.setClickable(false);
            mButtons.add(mDelete);

            boolean colorSet = false;
            // two rows
            for (int i = 0; i < EnumNetworkColor.VALUES.length; i++) {
                final EnumNetworkColor color = EnumNetworkColor.VALUES[i];
                ColorButton button = new ColorButton(minecraft,
                        leftPos + 48 + (i % 7) * 16, topPos + 87 + (i / 7) * 16, color.getRGB());
                if (!colorSet && color.getRGB() == getNetwork().getNetworkColor()) {
                    mColorButton = button;
                    button.setSelected(true);
                    colorSet = true;
                }
                mButtons.add(button);
            }

            // it's a custom color
            if (!colorSet) {
                ColorButton button = new ColorButton(minecraft,
                        leftPos + 32, topPos + 107, getNetwork().getNetworkColor());
                mColorButton = button;
                button.setSelected(true);
                mButtons.add(button);
            }
        }
    }

    @Override
    public void onEditSettingsChanged() {
        if (mApply != null) {
            mApply.setClickable((!mSecurityLevel.isEncrypted() ||
                    !mPassword.getValue().isEmpty() ||
                    getNetwork().getSecurityLevel().isEncrypted()) &&
                    !mNetworkName.getValue().isEmpty());
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (button == mApply) {
                ClientMessages.editNetwork(getToken(), getNetwork(),
                        mNetworkName.getValue(), mColorButton.mColor, mSecurityLevel, mPassword.getValue(), -1);
                mApply.setClickable(false);
            } else if (button == mDelete) {
                ClientMessages.deleteNetwork(getToken(), getNetwork());
                mDeleteCount = 0;
                mDelete.setClickable(false);
            }
        }
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (!getNetwork().isValid()) {
                if (mouseX >= leftPos + 20 && mouseX < leftPos + 155 && mouseY >= topPos + 16 && mouseY < topPos + 36) {
                    switchTab(EnumNavigationTab.TAB_SELECTION);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (mDelete != null) {
            if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                mDeleteCount++;
                if (mDeleteCount > 1) {
                    mDelete.setClickable(true);
                }
            } else {
                mDeleteCount = 0;
                mDelete.setClickable(false);
            }
        }
        return super.onKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (code == FluxConstants.RESPONSE_REJECT) {
            switchTab(EnumNavigationTab.TAB_HOME);
            return;
        }
        if (code == FluxConstants.RESPONSE_SUCCESS) {
            if (key == FluxConstants.REQUEST_DELETE_NETWORK) {
                switchTab(EnumNavigationTab.TAB_HOME);
            } else if (key == FluxConstants.REQUEST_EDIT_NETWORK) {
                // ignored
            }
        }
    }
}
