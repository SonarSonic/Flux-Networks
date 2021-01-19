package sonar.fluxnetworks.client.gui.basic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiButtonCore extends Gui {

    public boolean clickable = true;

    public int x;
    public int y;

    public int width;
    public int height;

    public int id;

    protected String text;

    public GuiButtonCore(int x, int y, int width, int height, int id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    private final void drawButton(Minecraft mc, int mouseX, int mouseY) {}

    public abstract void drawButton(Minecraft mc, int mouseX, int mouseY, int guiLeft, int guiTop);

    public void updateButton(float partialTicks, int mouseX, int mouseY) {}

    protected int getHoverState(boolean b) {
        return b ? 1 : 0;
    }

    public boolean isMouseHovered(Minecraft mc, int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX < this.x + this.width && mouseY < this.y + this.height && mouseY >= this.y;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void drawTexturedRectangular(double x, double y, double textureX, double textureY, double width, double height) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x + 0, y + height, zLevel).tex(textureX * f, (textureY + height) * f).endVertex();
        bufferbuilder.pos(x + width, y + height, zLevel).tex((textureX + width) * f, (textureY + height) * f).endVertex();
        bufferbuilder.pos(x + width, y + 0, zLevel).tex((textureX + width) * f, textureY * f).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).tex(textureX * f, textureY * f).endVertex();
        tessellator.draw();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
