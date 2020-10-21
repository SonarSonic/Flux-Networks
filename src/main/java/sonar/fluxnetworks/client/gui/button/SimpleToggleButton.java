package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;

public class SimpleToggleButton extends GuiButtonCore {

    public boolean checked;
    public int color, guiLeft, guiTop;

    public SimpleToggleButton(int x, int y, int guiLeft, int guiTop, int id) {
        super(x, y, 6, 6, id);
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
    }

    @Override
    public void drawButton(Minecraft mc, MatrixStack matrixStack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        GlStateManager.pushMatrix();

        if (isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
            color = 0xccffffff;
        } else {
            color = 0xccb4b4b4;
        }

        drawRect(x - 1, y - 1, x + width + 1, y, color);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, color);
        drawRect(x - 1, y, x, y + height, color);
        drawRect(x + width, y, x + width + 1, y + height, color);
        if (checked)
            drawRect(x + 1, y + 1, x + width - 1, y + height - 1, 0xddffffff);

        GlStateManager.popMatrix();
    }
}
