package sonar.fluxnetworks.client.gui.button;

import net.minecraft.client.gui.GuiGraphics;
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
    protected void drawButton(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        if (mSelected) {
            drawOuterFrame(gr, x, y, width, height, 0xFFFFFFFF);
        }
        gr.fill(x, y, x + width, y + height, mColor | 0xAA000000);
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }
}
