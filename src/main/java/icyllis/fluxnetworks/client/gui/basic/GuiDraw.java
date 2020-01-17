package icyllis.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import icyllis.fluxnetworks.system.FluxNetworks;
import icyllis.fluxnetworks.system.misc.FluxTranslate;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GuiDraw extends ContainerScreen implements ITextBoxButton {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_bar.png");
    public static final ResourceLocation INVENTORY = new ResourceLocation(FluxNetworks.MODID, "textures/gui/inventory_configuration.png");
    public static final ResourceLocation BUTTONS = new ResourceLocation(FluxNetworks.MODID , "textures/gui/gui_button.png");

    public GuiDraw(Container screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    protected void renderNetwork(String name, int color, int x, int y) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        RenderSystem.color3f(f, f1, f2);

        minecraft.getTextureManager().bindTexture(GUI_BAR);
        blit(x, y, 0, 0, 135, 12);
        font.drawString(name, x + 4, y + 2, 0xffffff);

        RenderSystem.popMatrix();
    }

    protected void renderNavigationPrompt(String error, String prompt) {
        RenderSystem.pushMatrix();
        drawCenteredString(font, error, xSize / 2, 16, 0x808080);
        RenderSystem.scalef(0.625f, 0.625f, 0.625f);
        drawCenteredString(font, FluxTranslate.CLICK + TextFormatting.AQUA + ' ' + prompt + ' ' + TextFormatting.RESET + FluxTranslate.ABOVE, (int) (xSize / 2 * 1.6), (int) (26 * 1.6), 0x808080);
        RenderSystem.scalef(1.6f, 1.6f, 1.6f);
        RenderSystem.popMatrix();
    }

    protected void renderItemStack(ItemStack stack, int x, int y) {
        renderItemStack(stack, x, y, "");
    }

    protected void renderItemStack(ItemStack stack, int x, int y, String text) {

        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200);
        this.itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = this.font;

        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y, text);

        this.setBlitOffset(0);
        this.itemRenderer.zLevel = 0.0F;
        RenderSystem.disableDepthTest();
    }

    public void drawColorRect(int x, int y, int height, int width, int color) {
        fill(x - 1, y - 1, x + width + 1, y, color);
        fill(x - 1, y + height, x + width + 1, y + height + 1, color);
        fill(x - 1, y, x, y + height, color);
        fill(x + width, y, x + width + 1, y + height, color);
    }

    public void drawRectWithBackground(int x, int y, int height, int width, int frameColor, int backColor) {
        fill(x - 1, y - 1, x + width + 1, y, frameColor);
        fill(x - 1, y + height, x + width + 1, y + height + 1, frameColor);
        //fill(x - 1, y, x, y + height, frameColor);
        //fill(x + width, y, x + width + 1, y + height, frameColor);
        fill(x, y, x + width, y + height, backColor);
    }

    protected void drawHoverTooltip(List<String> strings, int x, int y) {
        AtomicInteger maxLength = new AtomicInteger();
        strings.forEach(a -> maxLength.set(Math.max(font.getStringWidth(a), maxLength.get())));
        drawRectWithBackground(x, y, strings.size() * 9 + 3, maxLength.get() + 4, 0x80ffffff, 0xc0000000);
        int i = 0;
        for(String s : strings) {
            font.drawString(s, x + 2, y + 2 + 9 * i, 0xffffff);
            i++;
        }
    }

    protected void drawHoverTooltip(String text, int x, int y) {
        int maxLength = font.getStringWidth(text);
        fill(x, y, x + maxLength + 4, y + 12, 0x80000000);
        font.drawString(text, x + 2, y + 2, Color.GREEN.getRGB());
    }
}
