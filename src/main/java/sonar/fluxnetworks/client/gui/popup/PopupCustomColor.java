package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiPopupCore;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.client.gui.button.SimpleButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabEditAbstract;

import javax.annotation.Nonnull;

public class PopupCustomColor extends GuiPopupCore<GuiTabEditAbstract> {

    public FluxEditBox mColor;
    public SimpleButton mCancel;
    public SimpleButton mApply;
    public int mCurrentColor;

    public PopupCustomColor(GuiTabEditAbstract host, int currentColor) {
        super(host);
        mCurrentColor = currentColor;
    }

    @Override
    public void init() {
        super.init();
        mCancel = new SimpleButton(minecraft, leftPos + 24, topPos + 86, 48, 12);
        mCancel.setText(FluxTranslate.CANCEL.get());
        mButtons.add(mCancel);

        mApply = new SimpleButton(minecraft, leftPos + 104, topPos + 86, 48, 12);
        mApply.setText(FluxTranslate.APPLY.get());
        mButtons.add(mApply);

        mColor = FluxEditBox.create("0x", font, leftPos + 52, topPos + 64, 72, 12)
                .setHexOnly();
        mColor.setMaxLength(6);
        mColor.setValue(Integer.toHexString(mCurrentColor));
        mColor.setResponder(string -> mApply.setClickable(string.length() == 6));
        addRenderableWidget(mColor);
    }

    @Override
    public void drawForegroundLayer(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
        drawCenteredString(poseStack, font, FluxTranslate.CUSTOM_COLOR.get(), leftPos + 88, topPos + 48, 0xffffff);
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (button == mCancel) {
                mHost.closePopup();
            } else if (button == mApply) {
                mCurrentColor = mColor.getIntegerFromHex();
                mHost.closePopup();
            }
        }
    }
}
