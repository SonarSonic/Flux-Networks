package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;

public abstract class GuiButtonCore extends Screen {

    public boolean clickable = true;

    public int x;
    public int y;

    public int width;
    public int height;

    public int id;

    protected String text;

    public GuiButtonCore(int x, int y, int width, int height, int id) {
        super(new StringTextComponent("ButtonCore"));
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

    public void drawTexturedRectangular(double x, double y, float textureX, float textureY, float width, float height) {
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        double zLevel = 0;
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
        GlStateManager.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.color4f(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }


    public void accurateBlit(double left, double top, int uv_left, int uvTop, double width, double height) {
        int texX = 256;
        int texY = 256;
        accurateBlit(left, left + width, top, top + height, (uv_left + 0.0F) / (float)texX, (uv_left + (float)width) / (float)texX, (uvTop + 0.0F) / (float)texY, (uvTop + (float)height) / (float)texY);
    }

    public static void accurateBlit(double left, double right, double bottom, double top, float uvLeft, float uvTop, float uvRight, float uvBottom) {

        double z = 0;
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(left, top, z).tex(uvLeft, uvBottom).endVertex();
        builder.pos(right, top, z).tex(uvTop, uvBottom).endVertex();
        builder.pos(right, bottom, z).tex(uvTop, uvRight).endVertex();
        builder.pos(left, bottom, z).tex(uvLeft, uvRight).endVertex();
        builder.finishDrawing();
        RenderSystem.enableAlphaTest();
        WorldVertexBufferUploader.draw(builder);
    }
}
