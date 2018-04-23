package sonar.flux.client;

import static net.minecraft.client.renderer.GlStateManager.color;

import net.minecraft.client.Minecraft;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.flux.FluxTranslate;
import sonar.flux.client.tabs.GuiTabNetworkConnections;

public class ConnectedBlocksButton extends ImageButton {
	public GuiTabNetworkConnections gui;

	public ConnectedBlocksButton(GuiTabNetworkConnections gui, int id, int x, int y) {
		super(id, x, y, AbstractGuiTab.large_buttons, 0, 34, 17, 17);
		this.gui = gui;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(FluxTranslate.SORTING_SHOW_CONNECTED.t() + ": " + gui.showConnections, x, y);
	}

	public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		if (visible) {
			color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = x >= this.x && y >= this.y && x < this.x + width + 1 && y < this.y + height + 1;
			mc.getTextureManager().bindTexture(texture);
			drawTexturedModalRect(this.x, this.y, textureX, gui.showConnections ? textureY : textureY + 17, sizeX, sizeY);
		}
	}
}