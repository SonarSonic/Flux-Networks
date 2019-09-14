package fluxnetworks.client.gui.button;

import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.basic.GuiCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class BatchEditButton extends GuiButtonCore {

    public boolean clickable = true;
    private int guiLeft, guiTop;

    public BatchEditButton(int x, int y, int guiLeft, int guiTop, int id, String text) {
        super(x, y, 12, 12, id);
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
        this.text = text;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        if(clickable) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0f);
        } else {
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0f);
        }

        boolean b = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);

        mc.getTextureManager().bindTexture(GuiCore.BUTTONS);
        drawTexturedModalRect(x, y, 16 * id, 48 + (b ? 16 : 0), 12, 12);

        if(clickable && b) {
            mc.fontRenderer.drawString(text, x - mc.fontRenderer.getStringWidth(text) / 2 + 6, y - 9, 0xFFFFFF);
        }

        GlStateManager.popMatrix();
    }

    public BatchEditButton setUnclickable() {
        clickable = false;
        return this;
    }
}
