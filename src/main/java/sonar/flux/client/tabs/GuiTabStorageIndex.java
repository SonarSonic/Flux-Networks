package sonar.flux.client.tabs;

import java.awt.Color;
import java.util.List;

import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileStorage;

public class GuiTabStorageIndex extends GuiTabConnectionIndex<TileStorage, Object> {

	public GuiTabStorageIndex(TileStorage tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = common.getNetworkColour().getRGB();
		renderEnergyBar(14, 90 + 4, flux.storage.getEnergyStored(), flux.storage.getMaxEnergyStored(), midBlue, FontHelper.getIntFromColor(41, 94, 220));
		IFluxCommon common = FluxNetworks.getClientCache().getNetwork(flux.networkID.getObject());
		renderEnergyBar(14, 130 + 4, common.getStatistics().network_energy, common.getStatistics().network_energy_capacity, colour, colour);
		FontHelper.text("Local Buffer: ", 14, 80 + 4, Color.DARK_GRAY.getRGB());
		FontHelper.text("Network Buffer: " + (common.getStatistics().network_energy != 0 ? +(flux.storage.getEnergyStored() * 100 / common.getStatistics().network_energy) + " %" : ""), 14, 120 + 4, colour);
	}

}
