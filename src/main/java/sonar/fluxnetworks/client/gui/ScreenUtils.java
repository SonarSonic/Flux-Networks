package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.translate.FluxTranslate;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreenUtils extends AbstractGui {

    @Deprecated
    public static final ResourceLocation BACKGROUND = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    @Deprecated
    public static final ResourceLocation FRAME = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_bar.png");
    public static final ResourceLocation INVENTORY = new ResourceLocation(FluxNetworks.MODID, "textures/gui/inventory_configuration.png");
    @Deprecated
    public static final ResourceLocation BUTTONS = new ResourceLocation(FluxNetworks.MODID , "textures/gui/gui_button.png");

    public static final ScreenUtils INSTANCE = new ScreenUtils();

    protected Minecraft minecraft;
    protected ItemRenderer itemRenderer;
    protected FontRenderer font;
    {
        minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
        font = minecraft.fontRenderer;
    }

    public static float getRed(int colour){
        return (float) (colour >> 16 & 255) / 255.0F;
    }

    public static float getGreen(int colour){
        return (float) (colour >> 8 & 255) / 255.0F;
    }

    public static float getBlue(int colour){
        return (float) (colour & 255) / 255.0F;
    }

    public void setGuiColouring(int colour){
        RenderSystem.color3f(getRed(colour), getGreen(colour), getBlue(colour));
    }

    public void resetGuiColouring(){
        RenderSystem.color4f(1,1,1, 1);
    }

    public void renderNetwork(String name, int color, int x, int y) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        RenderSystem.color3f(f, f1, f2);

        Minecraft.getInstance().getTextureManager().bindTexture(GUI_BAR);
        blit(x, y, 0, 0, 135, 12, 256, 256);
        Minecraft.getInstance().fontRenderer.drawString(name, x + 4, y + 2, 0xffffff);

        RenderSystem.popMatrix();
    }

    public void renderItemStack(ItemStack stack, int x, int y) {
        renderItemStack(stack, x, y, "");
    }

    public void renderItemStack(ItemStack stack, int x, int y, String text) {
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200);
        this.itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = this.font;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, text);
        this.setBlitOffset(0);
        this.itemRenderer.zLevel = 0.0F;
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    public void drawColorRect(int x, int y, int height, int width, int color) {
        fill(x - 1, y - 1, x + width + 1, y, color);
        fill(x - 1, y + height, x + width + 1, y + height + 1, color);
        fill(x - 1, y, x, y + height, color);
        fill(x + width, y, x + width + 1, y + height, color);
    }

    public void drawRectWithBackground(int x, int y, int height, int width, int frameColor, int backColor) {
       // fill(x - 1, y - 1, x + width + 1, y, frameColor);
        //fill(x - 1, y + height, x + width + 1, y + height + 1, frameColor);
        //fill(x - 1, y, x, y + height, frameColor);
       // fill(x + width, y, x + width + 1, y + height, frameColor);
        fill(x, y, x + width, y + height, backColor);
    }

    public void drawHoverTooltip(List<String> strings, int x, int y) {
        AtomicInteger maxLength = new AtomicInteger();
        strings.forEach(a -> maxLength.set(Math.max(font.getStringWidth(a), maxLength.get())));
        drawRectWithBackground(x, y, strings.size() * 9 + 3, maxLength.get() + 4, 0x80ffffff, 0xc0000000);
        int i = 0;
        for(String s : strings) {
            font.drawString(s, x + 2, y + 2 + 9 * i, 0xffffff);
            i++;
        }
    }

    public void drawHoverTooltip(String text, int x, int y) {
        int maxLength = font.getStringWidth(text);
        fill(x, y, x + maxLength + 4, y + 12, 0x80000000);
        font.drawString(text, x + 2, y + 2, Color.GREEN.getRGB());
    }

}
