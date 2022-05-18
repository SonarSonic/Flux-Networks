package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;

public abstract class GuiButton extends GuiComponent {

    public static final ResourceLocation BUTTONS = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_button.png");
    public static final int TEXTURE_SIZE = 512;

    public final Minecraft mc;

    public int x;
    public int y;
    public int width;
    public int height;

    protected boolean mClickable = true;

    protected GuiButton(Minecraft mc, int x, int y, int width, int height) {
        this.mc = mc;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected abstract void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks);

    public boolean isClickable() {
        return mClickable;
    }

    public void setClickable(boolean clickable) {
        mClickable = clickable;
    }

    public final boolean isMouseHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static void blit(PoseStack poseStack, float x, float y, float width, float height,
                            float uOffset, float vOffset, float uWidth, float vHeight) {
        Matrix4f matrix = poseStack.last().pose();

        float minU = uOffset / TEXTURE_SIZE;
        float minV = vOffset / TEXTURE_SIZE;
        float maxU = (uOffset + uWidth) / TEXTURE_SIZE;
        float maxV = (vOffset + vHeight) / TEXTURE_SIZE;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x, y + height, 0).uv(minU, maxV).endVertex();
        builder.vertex(matrix, x + width, y + height, 0).uv(maxU, maxV).endVertex();
        builder.vertex(matrix, x + width, y, 0).uv(maxU, minV).endVertex();
        builder.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();
        Tesselator.getInstance().end();
    }

    public static void drawOuterFrame(PoseStack poseStack, int x, int y, int width, int height, int color) {
        fill(poseStack, x - 1, y - 1, x + width + 1, y, color);
        fill(poseStack, x - 1, y + height, x + width + 1, y + height + 1, color);
        fill(poseStack, x - 1, y, x, y + height, color);
        fill(poseStack, x + width, y, x + width + 1, y + height, color);
    }
}
