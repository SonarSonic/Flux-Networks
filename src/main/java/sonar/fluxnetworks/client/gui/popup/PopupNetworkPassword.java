package sonar.fluxnetworks.client.gui.popup;


import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiPopupCore;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.client.gui.button.SimpleButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabSelection;
import sonar.fluxnetworks.common.connection.FluxNetwork;

import javax.annotation.Nonnull;

public class PopupNetworkPassword extends GuiPopupCore<GuiTabSelection> {

    private SimpleButton mCancel;
    private SimpleButton mConnect;
    public FluxEditBox mPassword;

    public PopupNetworkPassword(GuiTabSelection host) {
        super(host);
    }

    @Override
    public void init() {
        super.init();
        mCancel = new SimpleButton(minecraft, leftPos + (imageWidth / 2) - 12 - 48, topPos + 86, 48, 12);
        mCancel.setText(FluxTranslate.CANCEL.get());
        mButtons.add(mCancel);

        mConnect = new SimpleButton(minecraft, leftPos + (imageWidth / 2) + 12, topPos + 86, 48, 12);
        mConnect.setText(FluxTranslate.CONNECT.get());
        mConnect.setClickable(false);
        mButtons.add(mConnect);

        mPassword = FluxEditBox.create(FluxTranslate.NETWORK_PASSWORD.get() + ": ", font,
                leftPos + (imageWidth / 2) - 60, topPos + 64, 120, 12);
        mPassword.setTextInvisible();
        mPassword.setMaxLength(FluxNetwork.MAX_PASSWORD_LENGTH);
        mPassword.setResponder(s -> mConnect.setClickable(!s.isEmpty()));
        addRenderableWidget(mPassword);
    }

    @Override
    public void drawForegroundLayer(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        if (mHost.mSelectedNetwork != null) {
            drawCenteredString(poseStack, font,
                    FluxTranslate.CONNECTING_TO.format(mHost.mSelectedNetwork.getNetworkName()),
                    leftPos + 88, topPos + 48, 0xffffff);
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (button == mCancel) {
                mHost.closePopup();
            } else if (button == mConnect) {
                mHost.setConnectedNetwork(mHost.mSelectedNetwork.getNetworkID(), mPassword.getValue());
                mPassword.setValue("");
            }
        }
    }
}
