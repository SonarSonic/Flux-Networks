package sonar.fluxnetworks.client.gui.tab;

/*
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;
import sonar.fluxnetworks.common.network.C2SNetMsg;

import javax.annotation.Nonnull;

public class GuiTabSettings extends GuiTabEditAbstract {

    public InvisibleButton redirectButton;

    public NormalButton apply, delete;
    public int deleteCount;

    public GuiTabSettings(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        if (networkValid) {
            mSecurityLevel = network.getSecurity().getLevel();
        }
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_SETTING;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (networkValid) {
            if (mouseX > 30 + guiLeft && mouseX < 66 + guiLeft && mouseY > 140 + guiTop && mouseY < 152 + guiTop) {
                if (delete.clickable) {
                    drawCenterText(matrixStack, TextFormatting.BOLD + FluxTranslate.DELETE_NETWORK.t(), 48, 128, 0xff0000);
                } else {
                    drawCenterText(matrixStack, FluxTranslate.DOUBLE_SHIFT.t(), 48, 128, 0xffffff);
                }
            }
            drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 88, 156, FluxClientCache.getFeedbackColor());
        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void init() {
        super.init();
        if (networkValid) {
            nameField.setText(network.getNetworkName());

            buttons.add(apply = new NormalButton(FluxTranslate.APPLY.t(), 112, 140, 36, 12, 3).setUnclickable());
            buttons.add(delete = new NormalButton(FluxTranslate.DELETE.t(), 30, 140, 36, 12, 4).setUnclickable());

            int i = 0;
            boolean colorSet = false;
            for (EnumNetworkColor color : EnumNetworkColor.values()) {
                ColorButton b = new ColorButton(48 + ((i >= 7 ? i - 7 : i) * 16), 91 + ((i >= 7 ? 1 : 0) * 16), color.getRGB());
                colorButtons.add(b);
                if (!colorSet && color.getRGB() == network.getNetworkColor()) {
                    this.colorBtn = b;
                    this.colorBtn.selected = true;
                    colorSet = true;
                }
                i++;
            }
            if (!colorSet) {
                ColorButton c = new ColorButton(32, 107, network.getNetworkColor());
                colorButtons.add(c);
                this.colorBtn = c;
                this.colorBtn.selected = true;
            }
        } else {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
            addButton(redirectButton);
        }
    }

    @Override
    public void onEditSettingsChanged() {
        if (networkValid && apply != null) {
            apply.clickable = ((mSecurityLevel != SecurityLevel.ENCRYPTED || passwordField.getText().length() != 0) && nameField.getText().length() != 0);
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (networkValid && button instanceof NormalButton) {
            switch (button.id) {
                case 3:
                    C2SNetMsg.editNetwork(network.getNetworkID(),
                            nameField.getText(), colorBtn.color, mSecurityLevel, passwordField.getText());
                    break;
                case 4:
                    C2SNetMsg.deleteNetwork(network.getNetworkID());
                    break;
            }
        }
    }

    @Override
    public boolean keyPressedMain(int keyCode, int scanCode, int modifiers) {
        if (delete != null) {
            if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
                deleteCount++;
                if (deleteCount > 1) {
                    delete.clickable = true;
                }
            } else {
                deleteCount = 0;
                delete.clickable = false;
            }
        }
        return super.keyPressedMain(keyCode, scanCode, modifiers);
    }

    @Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (info == FeedbackInfo.SUCCESS) {
            switchTab(EnumNavigationTab.TAB_HOME);
        } else if (info == FeedbackInfo.SUCCESS_2) {
            apply.clickable = false;
        }
    }
}
*/
