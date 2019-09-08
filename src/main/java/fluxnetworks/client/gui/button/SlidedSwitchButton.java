package fluxnetworks.client.gui.button;

import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.basic.GuiCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

/**
 * A sliding switch button
 */
public class SlidedSwitchButton extends GuiButtonCore {

    // switch on/off
    public boolean slideControl = false;

    // control movement
    protected float center;

    private int guiLeft, guiTop;

    public SlidedSwitchButton(int x, int y, int id, int guiLeft, int guiTop, String text, boolean defaultControl) {
        super(x, y, 16, 8, id);
        if(defaultControl) {
            slideControl = true;
            center = 8;
        }
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

        int s = getState(mc, mouseX, mouseY);

        mc.fontRenderer.drawString(text, x - mc.fontRenderer.getStringWidth(text), y, 0xffffff);

        mc.getTextureManager().bindTexture(GuiCore.BUTTONS);

        // Green background
        //drawTexturedModalRect(x, y, 32, 32, center * 2, 8);
        drawTexturedRectangular(x, y, 32, 32, center * 2, 8);

        // Circular
        drawTexturedModalRect(x + center, y, 16 * s, 40, 8, 8);

        // Button Bar
        drawTexturedModalRect(x, y, 16 * s, 32, 16, 8);

        GlStateManager.popMatrix();
    }

    private int getState(Minecraft mc, int mouseX, int mouseY) {
        return slideControl || isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop) ? 0 : 1;
    }

    public void switchButton() {
        slideControl = !slideControl;
    }

    public void updatePosition(float par) {
        if(slideControl) {
            if(center <= 8 - par) {
                center += par;
            } else {
                center = 8;
            }
        } else {
            if(center >= par) {
                center -= par;
            } else {
                center = 0;
            }
        }
    }
}
