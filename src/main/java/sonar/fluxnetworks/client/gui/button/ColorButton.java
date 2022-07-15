package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.vertex.PoseStack;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;

public class ColorButton extends GuiButtonCore {

    public int mColor;
    private boolean mSelected;

    public ColorButton(GuiFocusable screen, int x, int y, int color) {
        super(screen, x, y, 12, 12);
        mColor = color;
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        if (mSelected) {
            drawOuterFrame(poseStack, x, y, width, height, 0xFFFFFFFF);
        }
        fill(poseStack, x, y, x + width, y + height, mColor | 0xAA000000);
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}
