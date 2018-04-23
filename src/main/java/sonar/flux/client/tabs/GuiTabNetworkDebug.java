package sonar.flux.client.tabs;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GuiTab;
import sonar.flux.client.LargeButton;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiTabNetworkDebug extends AbstractGuiTab {

	public GuiTabNetworkDebug(TileFlux tile, List tabs) {
		super(tile, tabs);
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new LargeButton(this, "Debug Connected Blocks", 0, getGuiLeft() + 12, getGuiTop() + 30, 0, 34));
		this.buttonList.add(new LargeButton(this, "Debug Network Connections", 1, getGuiLeft() + 12, getGuiTop() + 60, 0, 0));
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int rgb = common.getNetworkColour().getRGB();
		if (common.isFakeNetwork()) {
			renderNavigationPrompt(FluxTranslate.ERROR_NO_CONNECTED_NETWORK.t(), FluxTranslate.GUI_TAB_NETWORK_SELECTION.t());
		} else {
			renderNetwork(common.getNetworkName(), common.getAccessType(), common.getNetworkColour().getRGB(), true, 11, 8);
		}
		GlStateManager.scale(0.75, 0.75, 0.75);
		FontHelper.text("Reconnects all blocks attached", (int)(36 * (1 / 0.75)), (int)(32 * (1 / 0.75)), grey);
		FontHelper.text("to flux plugs/points", (int)(36 * (1 / 0.75)), (int)(40 * (1 / 0.75)), grey);

		FontHelper.text("Checks all flux connections and", (int)(36 * (1 / 0.75)), (int)(62 * (1 / 0.75)), grey);
		FontHelper.text("removes duplicates/errored tiles", (int)(36 * (1 / 0.75)), (int)(70 * (1 / 0.75)), grey);
		
		GlStateManager.scale(1 / 0.75, 1 / 0.75, 1 / 0.75);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		switch (button.id) {
		case 0:
			PacketHelper.sendPacketToServer(PacketType.DEBUG_CONNECTED_BLOCKS, flux, PacketHelper.createResetConnectedBlocksPacket());
			break;
		case 1:
			PacketHelper.sendPacketToServer(PacketType.DEBUG_FLUX_CONNECTIONS, flux, PacketHelper.createValidateConnectionsPacket());
			break;
		}
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.DEBUG;
	}

	@Override
	public ResourceLocation getBackground() {
		return blank_flux_gui;
	}

}