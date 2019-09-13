package fluxnetworks.client.gui.button;

import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.basic.GuiCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class InventoryButton extends GuiButtonCore {

    private int texX, texY, guiLeft, guiTop;
    public boolean selected;

    public InventoryButton(int x, int y, int texX, int texY, int width, int height, int guiLeft, int guiTop, int id, boolean selected, String text) {
        super(x, y, width, height, id);
        this.texX = texX;
        this.texY = texY;
        this.selected = selected;
        this.text = text;
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0f);

        boolean b = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);
        mc.getTextureManager().bindTexture(GuiCore.INVENTORY);
        drawTexturedRectangular(x, y, texX, texY + height * (selected ? 1 : 0), width, height);

        if(b) {
            mc.fontRenderer.drawString(text, x + (width - mc.fontRenderer.getStringWidth(text)) / 2, y - 10, 0xFFFFFF);
        }

        GlStateManager.popMatrix();
    }
}
