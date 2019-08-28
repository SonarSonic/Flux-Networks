package fluxnetworks.client.gui.button;

import fluxnetworks.api.NetworkColor;
import net.minecraft.client.Minecraft;

public class ColorButton extends GuiButtonCore {

    public NetworkColor color;
    public boolean selected;

    public ColorButton(int x, int y, NetworkColor color) {
        super(x, y, 12, 12, 0);
        this.color = color;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

        if(selected) {
            drawRect(x - 1, y - 1, x + width + 1, y, 0xffffffff);
            drawRect(x - 1, y + height, x + width + 1, y + height + 1, 0xffffffff);
            drawRect(x - 1, y, x, y + height, 0xffffffff);
            drawRect(x + width, y, x + width + 1, y + height, 0xffffffff);
        }
        drawRect(x ,y , x + width, y + height, color.getColor() + 0xaa000000);

    }
}
