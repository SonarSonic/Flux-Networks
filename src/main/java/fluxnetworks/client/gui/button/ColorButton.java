package fluxnetworks.client.gui.button;

import fluxnetworks.client.gui.basic.GuiButtonCore;
import net.minecraft.client.Minecraft;

public class ColorButton extends GuiButtonCore {

    public int color;
    public boolean selected;

    public ColorButton(int x, int y, int color) {
        this(x, y, color, 0);
    }

    public ColorButton(int x, int y, int color, int id) {
        super(x, y, 12, 12, id);
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if(selected) {
            drawRect(x - 1, y - 1, x + width + 1, y, 0xffffffff);
            drawRect(x - 1, y + height, x + width + 1, y + height + 1, 0xffffffff);
            drawRect(x - 1, y, x, y + height, 0xffffffff);
            drawRect(x + width, y, x + width + 1, y + height, 0xffffffff);
        }
        drawRect(x ,y , x + width, y + height, color + 0xaa000000);

    }
}
