package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

/**
 * A sliding switch button
 */
public class SlidedSwitchButton extends GuiButtonCore {

    // switch on/off
    public boolean toggled = false;

    // control movement
    protected float center;

    private int guiLeft, guiTop;

    public SlidedSwitchButton(int x, int y, int id, int guiLeft, int guiTop, boolean defaultControl) {
        super(x, y, 16, 8, id);
        if (defaultControl) {
            toggled = true;
            center = 8;
        }
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
    }

    @Override
    public void drawButton(Minecraft mc, MatrixStack matrixStack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        GlStateManager.pushMatrix();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0f);

        int s = getState(mc, mouseX, mouseY);

        //mc.fontRenderer.drawString(text, x - mc.fontRenderer.getStringWidth(text), y, 0xffffff);

        mc.getTextureManager().bindTexture(ScreenUtils.BUTTONS);

        // Green background
        //drawTexturedModalRect(x, y, 32, 32, center * 2, 8);
        drawTexturedRectangular(x, y, 32, 32, center * 2, 8);

        // Circular
        accurateBlit(x + center, y, 16 * s, 40, 8, 8);

        // Button Bar
        blit(matrixStack, x, y, 16 * s, 32, 16, 8);

        GlStateManager.popMatrix();
    }

    private int getState(Minecraft mc, int mouseX, int mouseY) {
        return toggled || isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop) ? 0 : 1;
    }

    public void switchButton() {
        toggled = !toggled;
    }


    public void updateButton(float partialTicks, int mouseX, int mouseY) {
        float par = partialTicks * 4;
        if (toggled) {
            if (center <= 8 - par) {
                center += par;
            } else {
                center = 8;
            }
        } else {
            if (center >= par) {
                center -= par;
            } else {
                center = 0;
            }
        }
    }
}
