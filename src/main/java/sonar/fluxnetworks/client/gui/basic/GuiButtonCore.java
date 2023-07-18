package sonar.fluxnetworks.client.gui.basic;

import net.minecraft.client.gui.GuiGraphics;

public abstract class GuiButtonCore {

    public final GuiFocusable screen;

    public int x;
    public int y;
    public int width;
    public int height;

    protected boolean mClickable = true;

    protected GuiButtonCore(GuiFocusable screen, int x, int y, int width, int height) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected abstract void drawButton(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks);

    public boolean isClickable() {
        return mClickable;
    }

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }

    public final boolean isMouseHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static void drawOuterFrame(GuiGraphics gr, int x, int y, int width, int height, int color) {
        gr.fill(x - 1, y - 1, x + width + 1, y, color);
        gr.fill(x - 1, y + height, x + width + 1, y + height + 1, color);
        gr.fill(x - 1, y, x, y + height, color);
        gr.fill(x + width, y, x + width + 1, y + height, color);
    }
}
