package fluxnetworks.client.gui.button;

import fluxnetworks.api.EnumChargingTypes;
import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.basic.GuiDraw;
import fluxnetworks.client.gui.tab.GuiTabWireless;
import fluxnetworks.common.connection.NetworkSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class InventoryButton extends GuiButtonCore {

    public EnumChargingTypes chargeType;
    private int texX, texY;
    public GuiTabWireless host;

    public InventoryButton(EnumChargingTypes chargeType, GuiTabWireless host, int x, int y, int texX, int texY, int width, int height) {
        super(x, y, width, height, chargeType.ordinal());
        this.chargeType = chargeType;
        this.texX = texX;
        this.texY = texY;
        this.host = host;
        this.text = chargeType.getTranslatedName();
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, int guiLeft, int guiTop) {
        int colour = host.network.getSetting(NetworkSettings.NETWORK_COLOR);
        GlStateManager.color((colour >> 16 & 255) / 255.0F, (colour >> 8 & 255) / 255.0F, (colour & 255) / 255.0F, 1.0f);
        boolean hover = isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop);
        mc.getTextureManager().bindTexture(GuiDraw.INVENTORY);
        drawTexturedRectangular(x, y, texX, texY + height * (host.settings[id] ? 1 : 0), width, height);

        if(hover) {
            mc.fontRenderer.drawString(text, x + (width - mc.fontRenderer.getStringWidth(text)) / 2 + 1, y - 9, 0xFFFFFF);
        }

    }
}
