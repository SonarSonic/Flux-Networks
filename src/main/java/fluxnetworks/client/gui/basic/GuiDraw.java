package fluxnetworks.client.gui.basic;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


////ONLY RENDER METHODS & TEXTURES \\\\
public abstract class GuiDraw extends GuiContainer implements ITextBoxButton {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_bar.png");
    public static final ResourceLocation INVENTORY = new ResourceLocation(FluxNetworks.MODID, "textures/gui/inventory_configuration.png");
    public static final ResourceLocation BUTTONS = new ResourceLocation(FluxNetworks.MODID , "textures/gui/gui_button.png");

    public GuiDraw(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    protected void renderNetwork(String name, int color, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        GlStateManager.color(f, f1, f2);

        mc.getTextureManager().bindTexture(GUI_BAR);
        drawTexturedModalRect(x, y, 0, 0, 135, 12);
        fontRenderer.drawString(name, x + 4, y + 2, 0xffffff);

        GlStateManager.popMatrix();
    }

    protected void renderNavigationPrompt(String error, String prompt) {
        GlStateManager.pushMatrix();
        drawCenteredString(fontRenderer, error, xSize / 2, 16, 0x808080);
        GlStateManager.scale(0.625, 0.625, 0.625);
        drawCenteredString(fontRenderer, FluxTranslate.CLICK.t() + TextFormatting.AQUA + ' ' + prompt + ' ' + TextFormatting.RESET + FluxTranslate.ABOVE.t(), (int) (xSize / 2 * 1.6), (int) (26 * 1.6), 0x808080);
        GlStateManager.scale(1.6, 1.6, 1.6);
        GlStateManager.popMatrix();
    }

    protected void renderItemStack(ItemStack stack, int x, int y) {
        renderItemStack(stack, x, y, "");
    }

    protected void renderItemStack(ItemStack stack, int x, int y, String text) {
        GlStateManager.enableDepth();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;

        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, text);
        RenderHelper.disableStandardItemLighting();

        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
        GlStateManager.disableDepth();
    }

    public void drawColorRect(int x, int y, int height, int width, int color) {
        drawRect(x - 1, y - 1, x + width + 1, y, color);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, color);
        drawRect(x - 1, y, x, y + height, color);
        drawRect(x + width, y, x + width + 1, y + height, color);
    }

    public void drawRectWithBackground(int x, int y, int height, int width, int frameColor, int backColor) {
        drawRect(x - 1, y - 1, x + width + 1, y, frameColor);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, frameColor);
        //drawRect(x - 1, y, x, y + height, frameColor);
        //drawRect(x + width, y, x + width + 1, y + height, frameColor);
        drawRect(x, y, x + width, y + height, backColor);
    }

    protected void drawHoverTooltip(List<String> strings, int x, int y) {
        AtomicInteger maxLength = new AtomicInteger();
        strings.forEach(a -> maxLength.set(Math.max(fontRenderer.getStringWidth(a), maxLength.get())));
        drawRectWithBackground(x, y, strings.size() * 9 + 3, maxLength.get() + 4, 0x80ffffff, 0xc0000000);
        int i = 0;
        for(String s : strings) {
            fontRenderer.drawString(s, x + 2, y + 2 + 9 * i, 0xffffff);
            i++;
        }
    }

    protected void drawHoverTooltip(String text, int x, int y) {
        int maxLength = fontRenderer.getStringWidth(text);
        drawRect(x, y, x + maxLength + 4, y + 12, 0x80000000);
        fontRenderer.drawString(text, x + 2, y + 2, Color.GREEN.getRGB());
    }
}
