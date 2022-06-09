package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

/**
 * A button with text and outer stroke.
 */
public class SimpleButton extends GuiButtonCore {

    private int mColor = 0xFFFFFFFF;
    private String mText;

    public SimpleButton(Minecraft mc, int x, int y, int width, int height) {
        super(mc, x, y, width, height);
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        int color;
        int r = mColor >> 16 & 0xFF;
        int g = mColor >> 8 & 0xFF;
        int b = mColor & 0xFF;

        if (mClickable) {
            if (isMouseHovered(mouseX, mouseY)) {
                color = mColor;
            } else {
                color = 0xFF000000 | (int) (r * 0.75) << 16 | (int) (g * 0.75) << 8 | (int) (b * 0.75);
            }
        } else {
            color = 0xFF000000 | (int) (r * 0.375) << 16 | (int) (g * 0.375) << 8 | (int) (b * 0.375);
        }

        // outer stroke
        drawOuterFrame(poseStack, x, y, width, height, color);

        drawCenteredString(poseStack, mc.font, mText, x + width / 2, y + (height - 8) / 2, color);
    }

    public void setColor(int color) {
        mColor = color;
    }

    public void setText(String text) {
        mText = text;
    }
}
