package sonar.fluxnetworks.client.gui.popup;

import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiPopupCore;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.client.gui.button.SimpleButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabEditAbstract;

import javax.annotation.Nonnull;
import java.util.Locale;

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
        mCancel = new SimpleButton(this, leftPos + (imageWidth / 2) + 12, topPos + 86, 48, 12,
                FluxTranslate.CANCEL.get());
        mButtons.add(mCancel);

        mApply = new SimpleButton(this, leftPos + (imageWidth / 2) - 12 - 48, topPos + 86, 48, 12,
                FluxTranslate.APPLY.get());
        mButtons.add(mApply);

        mColor = FluxEditBox.create("0x", font, leftPos + (imageWidth / 2) - 40, topPos + 64, 80, 12)
                .setHexOnly();
        mColor.setMaxLength(6);
        mColor.setValue(Integer.toHexString(mCurrentColor).toUpperCase(Locale.ROOT));
        mColor.setResponder(string -> mApply.setClickable(string.length() == 6));
        addRenderableWidget(mColor);
    }

    @Override
    public void drawForegroundLayer(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(gr, mouseX, mouseY, deltaTicks);
        gr.drawCenteredString(font, FluxTranslate.CUSTOM_COLOR.get(), leftPos + 88, topPos + 48, 0xffffff);
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
