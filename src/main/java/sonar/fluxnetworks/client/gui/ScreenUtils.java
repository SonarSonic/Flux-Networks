package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ScreenUtils extends AbstractGui {

    @Deprecated
    public static final ResourceLocation BACKGROUND = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    @Deprecated
    public static final ResourceLocation FRAME = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    @Deprecated
    public static final ResourceLocation GUI_BAR = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_bar.png");
    @Deprecated
    public static final ResourceLocation BUTTONS = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_button.png");


    public static final ResourceLocation INVENTORY = new ResourceLocation(FluxNetworks.MODID, "textures/gui/inventory_configuration.png");

    public static final ScreenUtils INSTANCE = new ScreenUtils();

    protected Minecraft minecraft;
    protected ItemRenderer itemRenderer;
    protected FontRenderer font;

    {
        minecraft = Minecraft.getInstance();
        itemRenderer = minecraft.getItemRenderer();
        font = minecraft.fontRenderer;
    }

    public static float getRed(int colour) {
        return (float) (colour >> 16 & 255) / 255.0F;
    }

    public static float getGreen(int colour) {
        return (float) (colour >> 8 & 255) / 255.0F;
    }

    public static float getBlue(int colour) {
        return (float) (colour & 255) / 255.0F;
    }

    public void setGuiColoring(int colour) {
        RenderSystem.color3f(getRed(colour), getGreen(colour), getBlue(colour));
    }

    public void setGuiColoring(int colour, float alpha) {
        RenderSystem.color4f(getRed(colour), getGreen(colour), getBlue(colour), alpha);
    }

    public void resetGuiColouring() {
        RenderSystem.color4f(1, 1, 1, 1);
    }

    public void renderNetwork(MatrixStack matrixStack, String name, int color, int x, int y) {
        RenderSystem.enableBlend();

        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        RenderSystem.color3f(f, f1, f2);

        Minecraft.getInstance().getTextureManager().bindTexture(GUI_BAR);
        blit(matrixStack, x, y, 0, 0, 135, 12, 256, 256);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, name, x + 4, y + 2, 0xffffff);
    }

    public void renderItemStack(@Nonnull ItemStack stack, int x, int y) {
        RenderSystem.enableBlend();
        this.setBlitOffset(200);
        this.itemRenderer.zLevel = 200.0F;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.setBlitOffset(0);
        this.itemRenderer.zLevel = 0.0F;
    }

    /*public void drawColorRect(int x, int y, int height, int width, int color) {
        fill(x - 1, y - 1, x + width + 1, y, color);
        fill(x - 1, y + height, x + width + 1, y + height + 1, color);
        fill(x - 1, y, x, y + height, color);
        fill(x + width, y, x + width + 1, y + height, color);
    }*/

    public void drawRectWithBackground(MatrixStack matrixStack, int x, int y, int height, int width, int frameColor, int backColor) {
        fill(matrixStack, x - 1, y - 1, x + width + 1, y, frameColor);
        fill(matrixStack, x - 1, y + height, x + width + 1, y + height + 1, frameColor);
        //fill(matrixStack, x - 1, y, x, y + height, frameColor);
        //fill(matrixStack, x + width, y, x + width + 1, y + height, frameColor);
        fill(matrixStack, x, y, x + width, y + height, backColor);
    }

    public void drawHoverTooltip(MatrixStack matrixStack, List<String> strings, int x, int y) {
        int maxLength = 0;
        for (String s : strings) {
            maxLength = Math.max(font.getStringWidth(s), maxLength);
        }
        drawRectWithBackground(matrixStack, x, y, strings.size() * 9 + 3, maxLength + 4, 0x80ffffff, 0xc0000000);
        for (int i = 0; i < strings.size(); i++) {
            font.drawString(matrixStack, strings.get(i), x + 2, y + 2 + 9 * i, 0xffffff);
        }
    }

    /*public void drawHoverTooltip(String text, int x, int y) {
        int maxLength = font.getStringWidth(text);
        fill(matrixStack, x, y, x + maxLength + 4, y + 12, 0x80000000);
        font.drawString(matrixStack, text, x + 2, y + 2, Color.GREEN.getRGB());
    }*/

}
