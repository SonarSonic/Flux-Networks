package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.tabs.GuiTabNetworkConnections;

import static net.minecraft.client.renderer.GlStateManager.color;

public class ConnectNetworkButton extends LargeButton {
    public GuiTabNetworkConnections gui;

    public ConnectNetworkButton(GuiTabNetworkConnections gui, int id, int x, int y) {
        super(gui, "", id, x, y, 34, 0);
        this.gui = gui;
    }

    public void drawButtonForegroundLayer(int x, int y) {
        gui.drawSonarCreativeTabHoveringText(GuiTabNetworkConnections.show_disconnected ? FluxTranslate.CONNECT_SELECTED.t() : FluxTranslate.DISCONNECT_SELECTED.t(), x, y);
    }

    public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
        if (visible) {
            color(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = x >= this.x && y >= this.y && x < this.x + width + 1 && y < this.y + height + 1;
            mc.getTextureManager().bindTexture(texture);
            drawTexturedModalRect(this.x, this.y, textureX, GuiTabNetworkConnections.show_disconnected ? textureY : textureY + 34, sizeX, sizeY);
        }
    }

}
