package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.*;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;

public class GuiTabWireless extends GuiTabCore {

    public SwitchButton mEnable;
    public SimpleButton mApply;

    public int mWirelessMode;

    public GuiTabWireless(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    @Override
    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_WIRELESS;
    }

    @Override
    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (getNetwork().isValid()) {
            int color = getNetwork().getNetworkColor();
            drawCenteredString(poseStack, font, FluxTranslate.TAB_WIRELESS.get(), leftPos + 88, topPos + 10, 0xb4b4b4);
            font.draw(poseStack, FluxTranslate.ENABLE_WIRELESS.get(), leftPos + 20, topPos + 148, color);

            FluxPlayer fp = FluxUtils.get(mPlayer, FluxPlayer.FLUX_PLAYER);
            if (fp != null) {
                int wireless = fp.getWirelessNetwork();
                if (wireless == getNetwork().getNetworkID()) {
                    drawCenteredString(poseStack, font,
                            '(' + FluxTranslate.EFFECTIVE_WIRELESS_NETWORK.get() + ')',
                            leftPos + 88, topPos + 158, color);
                } else {
                    drawCenteredString(poseStack, font,
                            '(' + FluxTranslate.INEFFECTIVE_WIRELESS_NETWORK.get() + ')',
                            leftPos + 88, topPos + 158, 0xb4b4b4);
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

            FluxPlayer fp = FluxUtils.get(mPlayer, FluxPlayer.FLUX_PLAYER);
            if (fp != null) {
                mWirelessMode = fp.getWirelessMode();
            }

            mEnable = new SwitchButton(minecraft, leftPos + 140, topPos + 148,
                    WirelessType.ENABLE_WIRELESS.isActivated(mWirelessMode));
            mButtons.add(mEnable);
            mButtons.add(new InventoryButton(minecraft, leftPos + 24, topPos + 28, 52, 16,
                    WirelessType.ARMOR, 0, 80, this));
            mButtons.add(new InventoryButton(minecraft, leftPos + 100, topPos + 28, 52, 16,
                    WirelessType.CURIOS, 0, 80, this));
            mButtons.add(new InventoryButton(minecraft, leftPos + 32, topPos + 52, 112, 40,
                    WirelessType.INVENTORY, 0, 0, this));
            mButtons.add(new InventoryButton(minecraft, leftPos + 32, topPos + 100, 112, 16,
                    WirelessType.HOT_BAR, 112, 0, this));
            mButtons.add(new InventoryButton(minecraft, leftPos + 136, topPos + 124, 16, 16,
                    WirelessType.MAIN_HAND, 52, 80, this));
            mButtons.add(new InventoryButton(minecraft, leftPos + 24, topPos + 124, 16, 16,
                    WirelessType.OFF_HAND, 52, 80, this));

            mApply = new SimpleButton(minecraft, leftPos + (imageWidth / 2) - 24, topPos + 126, 48, 12);
            mApply.setText(FluxTranslate.APPLY.get());
            mApply.setClickable(false);
            mButtons.add(mApply);
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        if (button instanceof InventoryButton btn) {
            if (btn.mType != WirelessType.INVENTORY) {
                mWirelessMode ^= 1 << btn.mType.ordinal();
                mApply.setClickable(true);
            }
        } else if (button instanceof SwitchButton btn) {
            btn.toggle();
            if (btn.isChecked()) {
                mWirelessMode |= 1 << WirelessType.ENABLE_WIRELESS.ordinal();
            } else {
                mWirelessMode &= ~(1 << WirelessType.ENABLE_WIRELESS.ordinal());
            }
            mApply.setClickable(true);
        } else if (button == mApply) {
            ClientMessages.wirelessMode(getToken(), mWirelessMode, getNetwork().getNetworkID());
            mApply.setClickable(false);
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
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (code == FluxConstants.RESPONSE_REJECT) {
            FluxPlayer fp = FluxUtils.get(mPlayer, FluxPlayer.FLUX_PLAYER);
            if (fp != null) {
                mWirelessMode = fp.getWirelessMode();
            }
            if (mEnable != null) {
                mEnable.setChecked(WirelessType.ENABLE_WIRELESS.isActivated(mWirelessMode));
            }
        }
    }

    /* @Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (apply != null && info == FeedbackInfo.SUCCESS) {
            apply.clickable = false;
        }
    }*/
}
