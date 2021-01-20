package sonar.fluxnetworks.client.gui.button;

import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiDraw;
import sonar.fluxnetworks.client.gui.tab.GuiTabWireless;
import sonar.fluxnetworks.api.network.NetworkSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class InventoryButton extends GuiButtonCore {

    public WirelessType chargeType;
    private int texX, texY;
    public GuiTabWireless host;

    public InventoryButton(WirelessType chargeType, GuiTabWireless host, int x, int y, int texX, int texY, int width, int height) {
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
