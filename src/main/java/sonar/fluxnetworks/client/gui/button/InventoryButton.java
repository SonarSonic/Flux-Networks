package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.api.network.ChargingType;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.tab.GuiTabWireless;

public class InventoryButton extends GuiButtonCore {

    public ChargingType chargeType;
    private int texX, texY;
    public GuiTabWireless host;

    public InventoryButton(ChargingType chargeType, GuiTabWireless host, int x, int y, int texX, int texY, int width, int height) {
        super(x, y, width, height, chargeType.ordinal());
        this.chargeType = chargeType;
        this.texX = texX;
        this.texY = texY;
        this.host = host;
        this.text = chargeType.getTranslatedName();
    }

    public void drawButton(Minecraft mc, MatrixStack matrixStack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        int color = host.network.getNetworkColor();
        ScreenUtils.INSTANCE.setGuiColouring(color);
        boolean hover = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);

        mc.getTextureManager().bindTexture(ScreenUtils.INVENTORY);
        ScreenUtils.INSTANCE.blit(matrixStack, x, y, texX, texY + height * (chargeType.isActivated(host.settings) ? 1 : 0), width, height);

        if (hover) {
            mc.fontRenderer.drawString(matrixStack, text, x + (width - mc.fontRenderer.getStringWidth(text)) / 2f + 1, y - 9, 0xFFFFFF);
        }
        ScreenUtils.INSTANCE.resetGuiColouring();
    }
}
