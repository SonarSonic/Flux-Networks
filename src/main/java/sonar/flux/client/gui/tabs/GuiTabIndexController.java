package sonar.flux.client.gui.tabs;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;
import sonar.core.sync.SyncValueHandler;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.common.tileentity.TileController;

import java.io.IOException;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.NETWORK_COLOUR;

public class GuiTabIndexController extends GuiTabIndexConnection<Object> {

	public TileController controller;

	public GuiTabIndexController(List<EnumGuiTab> tabs) {
		super(tabs);
		controller = (TileController) FluxNetworks.proxy.getFluxTile();
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = NETWORK_COLOUR.getValue(common).getRGB();
		FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.WIRELESS_CHARGING.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FluxTranslate.translateToggle(controller.wireless_charging.getValue()), 8, 66 + 18, colour);
		GlStateManager.scale(0.75, 0.75, 0.75);
		GlStateManager.translate(3 * (1/0.75), (132) * (1/0.75), 0);
		FontHelper.text("Transfer Settings have been removed, there", 0, 0, colour);
		FontHelper.text("will be different settings soon, connections", 0, 12, colour);
		FontHelper.text("will now always be sorted by largest priority", 0, 24, colour);
	}

	@Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 66 + 18 && y - guiTop < 80 + 18) {
			SyncValueHandler.invertBoolean(controller.wireless_charging);
			SonarCore.sendPacketToServer(controller, 13);
		}
	}

}
