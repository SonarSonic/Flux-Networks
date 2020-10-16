package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.NetworkSecurity;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.CEditNetworkMessage;
import sonar.fluxnetworks.common.network.GeneralPacket;
import sonar.fluxnetworks.common.network.GeneralPacketEnum;
import sonar.fluxnetworks.common.network.GeneralPacketHandler;

public class GuiTabSettings extends GuiTabEditAbstract {

    public InvisibleButton redirectButton;

    public NormalButton apply, delete;
    public int deleteCount;

    public GuiTabSettings(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        if (networkValid) {
            securityType = network.getSecurity().getType();
            energyType = EnergyType.FE;
        }
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_SETTING;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (getNavigationTab() == EnumNavigationTabs.TAB_CREATE || networkValid) {
            if (mouseX > 30 + guiLeft && mouseX < 66 + guiLeft && mouseY > 140 + guiTop && mouseY < 152 + guiTop) {
                if (delete.clickable) {
                    drawCenteredString(matrixStack, font, TextFormatting.BOLD + FluxTranslate.DELETE_NETWORK.t(), 48, 128, 0xff0000);
                } else {
                    drawCenteredString(matrixStack, font, FluxTranslate.DOUBLE_SHIFT.t(), 48, 128, 0xffffff);
                }
            }
            drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getInfo(), 88, 156, 0xffffff);
        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void init() {
        super.init();

        if (networkValid) {
            nameField.setText(network.getNetworkName());

            //TODO only players that have permission can receive password from server
            //passwordField.setText(network.getNetworkPassword());
            passwordField.setVisible(network.getSecurity().isEncrypted());

            buttons.add(apply = new NormalButton(FluxTranslate.APPLY.t(), 112, 140, 36, 12, 3).setUnclickable());
            buttons.add(delete = new NormalButton(FluxTranslate.DELETE.t(), 30, 140, 36, 12, 4).setUnclickable());

            int i = 0;
            boolean colorSet = false;
            for (EnumNetworkColor color : EnumNetworkColor.values()) {
                ColorButton b = new ColorButton(48 + ((i >= 7 ? i - 7 : i) * 16), 96 + ((i >= 7 ? 1 : 0) * 16), color.getRGB());
                colorButtons.add(b);
                if (!colorSet && color.getRGB() == network.getNetworkColor()) {
                    this.colorBtn = b;
                    this.colorBtn.selected = true;
                    colorSet = true;
                }
                i++;
            }
            if (!colorSet) {
                ColorButton c = new ColorButton(32, 112, network.getNetworkColor());
                colorButtons.add(c);
                this.colorBtn = c;
                this.colorBtn.selected = true;
            }
        } else {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
            addButton(redirectButton);
        }

    }


    @Override
    public void onEditSettingsChanged() {
        if (networkValid && apply != null) {
            apply.clickable = ((securityType != NetworkSecurity.Type.ENCRYPTED || passwordField.getText().length() != 0) && nameField.getText().length() != 0);
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (networkValid && button instanceof NormalButton) {
            switch (button.id) {
                case 3:
                    NetworkHandler.INSTANCE.sendToServer(new CEditNetworkMessage(
                            nameField.getText(), colorBtn.color, securityType, passwordField.getText(), network.getNetworkID()));
                    break;
                case 4:
                    PacketHandler.CHANNEL.sendToServer(new GeneralPacket(GeneralPacketEnum.DELETE_NETWORK, GeneralPacketHandler.getDeleteNetworkPacket(connector.getNetworkID())));
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
    public void tick() {
        super.tick();
        if (FluxClientCache.getFeedback(true) == EnumFeedbackInfo.SUCCESS) {
            switchTab(EnumNavigationTabs.TAB_HOME, player, connector);
            FluxClientCache.setFeedback(EnumFeedbackInfo.NONE, true);
        }
        if (FluxClientCache.getFeedback(true) == EnumFeedbackInfo.SUCCESS_2) {
            apply.clickable = false;
            FluxClientCache.setFeedback(EnumFeedbackInfo.NONE, true);
        }
    }
}
