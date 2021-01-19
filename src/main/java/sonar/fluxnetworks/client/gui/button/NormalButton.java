package sonar.fluxnetworks.client.gui.button;

import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import net.minecraft.client.Minecraft;

/**
 * A clickable button
 */
public class NormalButton extends GuiButtonCore {

    public int color = 0xffffffff;

    public NormalButton(String text, int x, int y, int width, int height, int id) {
        super(x, y, width, height, id);
        this.text = text;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int guiLeft, int guiTop) {

        boolean hovered = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);
        int color;
        int r = this.color >> 16 & 0xff;
        int g = this.color >> 8 & 0xff;
        int b = this.color & 0xff;

        if(clickable) {
            if (hovered)
                color = this.color;
            else
                color = 0xff000000 | (int) (r * 0.7) << 16 | (int) (g * 0.7) << 8 | (int) (b * 0.7);
        } else {
            color = 0xff000000 | (int) (r * 0.375) << 16 | (int) (g * 0.375) << 8 | (int) (b * 0.375);
        }

        drawRect(x - 1, y - 1, x + width + 1, y, color);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, color);
        drawRect(x - 1, y, x, y + height, color);
        drawRect(x + width, y, x + width + 1, y + height, color);

        drawCenteredString(mc.fontRenderer, text, x + width / 2, y + height / 2 - 5, color);
    }

    public NormalButton setUnclickable() {
        clickable = false;
        return this;
    }

    public NormalButton setTextColor(int c) {
        color = c;
        return this;
    }
}
