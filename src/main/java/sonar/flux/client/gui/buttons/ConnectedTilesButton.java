package sonar.flux.client.gui.buttons;

import net.minecraft.client.Minecraft;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.tabs.GuiTabNetworkConnections;

import static net.minecraft.client.renderer.GlStateManager.color;

public class ConnectedTilesButton extends ImageButton {
	public GuiTabNetworkConnections gui;

	public ConnectedTilesButton(GuiTabNetworkConnections gui, int id, int x, int y) {
		super(id, x, y, GuiTabAbstract.large_buttons, 17, 34, 17, 17);
		this.gui = gui;
	}

	public void drawButtonForegroundLayer(int x, int y) {
		gui.drawSonarCreativeTabHoveringText(!GuiTabNetworkConnections.show_disconnected ? FluxTranslate.SORTING_CONNECTED_TILES.t() : FluxTranslate.SORTING_DISCONNECTED_TILES.t(), x, y);
	}

	public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
		if (visible) {
			color(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = x >= this.x && y >= this.y && x < this.x + width + 1 && y < this.y + height + 1;
			mc.getTextureManager().bindTexture(texture);
			drawTexturedModalRect(this.x, this.y, textureX, !GuiTabNetworkConnections.show_disconnected ? textureY : textureY + 17, sizeX, sizeY);
		}
	}
}