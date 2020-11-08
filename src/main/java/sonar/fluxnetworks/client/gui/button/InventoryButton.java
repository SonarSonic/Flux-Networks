package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.tab.GuiTabWireless;

import javax.annotation.Nonnull;

public class InventoryButton extends GuiButtonCore {

    public WirelessType type;
    private final int texX;
    private final int texY;
    public GuiTabWireless host;

    public InventoryButton(@Nonnull WirelessType type, GuiTabWireless host, int x, int y, int texX, int texY, int width, int height) {
        super(x, y, width, height, type.ordinal());
        this.type = type;
        this.texX = texX;
        this.texY = texY;
        this.host = host;
        this.text = type.getTranslatedName();
    }

    @Override
    public void drawButton(Minecraft mc, MatrixStack matrixStack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int color = host.network.getNetworkColor();
        ScreenUtils.INSTANCE.setGuiColoring(color);
        boolean hover = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);

        mc.getTextureManager().bindTexture(ScreenUtils.INVENTORY);
        ScreenUtils.INSTANCE.blit(matrixStack, x, y, texX, texY + height * (type.isActivated(host.settings) ? 1 : 0), width, height);

        if (hover) {
            mc.fontRenderer.drawString(matrixStack, text, x + (width - mc.fontRenderer.getStringWidth(text)) / 2f + 1, y - 9, 0xFFFFFF);
        }
        ScreenUtils.INSTANCE.resetGuiColouring();
    }
}
