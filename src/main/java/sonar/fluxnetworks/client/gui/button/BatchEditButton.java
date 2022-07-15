package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;

public class BatchEditButton extends GuiButtonCore {

    private final int mU0;
    private final String mText;

    public BatchEditButton(GuiFocusable screen, int x, int y, int u0, String text) {
        super(screen, x, y, 12, 12);
        mU0 = u0;
        mText = text;
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        boolean hovered = isMouseHovered(mouseX, mouseY);

        RenderSystem.enableBlend();
        if (mClickable) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
        }
        RenderSystem.setShaderTexture(0, BUTTONS);

        blit(poseStack, x, y, mU0, 48 + (hovered ? 16 : 0), width, height);

        if (hovered && mClickable) {
            Font font = screen.getMinecraft().font;
            font.draw(poseStack, mText, x - font.width(mText) / 2f + 6, y - 9, 0xFFFFFF);
        }
    }
}
