package sonar.fluxnetworks.client.gui.tab;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.button.SimpleButton;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;

public class GuiTabCreate extends GuiTabEditAbstract {

    public SimpleButton mCreate;

    public GuiTabCreate(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
        mSecurityLevel = SecurityLevel.PRIVATE;
    }

    @Override
    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_CREATE;
    }

    @Override
    public void init() {
        super.init();
        mNetworkName.setValue(FluxTranslate.PLAYERS_NETWORK.format(mPlayer.getGameProfile().getName()));

        initColorSelector(leftPos + 48, topPos + 87, true);

        mCreate = new SimpleButton(this, leftPos + (imageWidth / 2) - 24, topPos + 150, 48, 12,
                FluxTranslate.CREATE.get());
        mButtons.add(mCreate);
        onEditSettingsChanged();
    }

    @Override
    protected void drawForegroundLayer(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(gr, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, float mouseX, float mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && button == mCreate) {
            //PacketHandler.CHANNEL.sendToServer(new GeneralPacket(GeneralPacketEnum.CREATE_NETWORK,
            // GeneralPacketHandler.getCreateNetworkPacket(name.getText(), color.color, securityType, energyType,
            // password.getText())));

            //C2SNetMsg.createNetwork(nameField.getText(), colorBtn.color, mSecurityLevel, passwordField.getText());

            ClientMessages.createNetwork(getToken(), mNetworkName.getValue(),
                    mColorButton.mColor, mSecurityLevel, mPassword.getValue());
            mCreate.setClickable(false);
        }
    }

    @Override
    public void onEditSettingsChanged() {
        if (mCreate != null) {
            mCreate.setClickable((mSecurityLevel != SecurityLevel.ENCRYPTED || !mPassword.getValue().isEmpty()) &&
                    !mNetworkName.getValue().isEmpty());
        }
    }

    @Override
    protected void onResponseAction(int key, int code) {
        super.onResponseAction(key, code);
        if (key == FluxConstants.REQUEST_CREATE_NETWORK) {
            if (code == FluxConstants.RESPONSE_SUCCESS) {
                switchTab(EnumNavigationTab.TAB_SELECTION, false);
            }
        }
    }
}
