package fluxnetworks.client.gui.button;

import net.minecraft.client.Minecraft;

public class NormalButton extends GuiButtonCore {

    public int color = 0xffb4b4b4;

    public NormalButton(String text, int x, int y, int width, int height, int id) {
        super(x, y, width, height, id);
        this.text = text;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int guiLeft, int guiTop) {

        if(isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop))
            color = 0xffffffff;
        else
            color = 0xffb4b4b4;

        drawRect(x - 1, y - 1, x + width + 1, y, color);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, color);
        drawRect(x - 1, y, x, y + height, color);
        drawRect(x + width, y, x + width + 1, y + height, color);
        drawCenteredString(mc.fontRenderer, text, x + width / 2, y + height / 2 - 5, color);
    }
}
