package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.client.gui.basic.GuiButton;

public class ColorButton extends GuiButton {

    private final int mColor;
    private boolean mSelected;

    public ColorButton(Minecraft mc, int x, int y, int color) {
        super(mc, x, y, 12, 12);
        this.mColor = color;
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
