package fluxnetworks.client.gui.button;

import fluxnetworks.client.gui.basic.GuiButtonCore;
import net.minecraft.client.Minecraft;

public class PageLabelButton extends GuiButtonCore {

    public int page, pages, color;
    public double currentLeft, singleWidth;

    public PageLabelButton(int x, int y, int page, int pages, int color) {
        super(x, y, 148, 4, 0);
        this.page = page;
        this.pages = pages;
        this.color = color;
        singleWidth = (double) page / pages * 146;
        currentLeft = (page - 1) * singleWidth + x + 1;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        drawRect(x, y, x + width, y + 1, 0x80ffffff);
        drawRect(x, y + height - 1, x + width, y + height, 0x80ffffff);
        drawRect(currentLeft, y + 1, currentLeft + singleWidth, y + 3, color | 0xc0000000);
    }

    public void refreshPages(int page, int pages) {
        singleWidth = (double) 146 / pages;
        currentLeft = (page - 1) * singleWidth + x + 1;
    }
}
