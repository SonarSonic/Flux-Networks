package sonar.flux.client.tabs;

import java.util.List;

import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
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
		//renderEnergyBar(14, 90 + 4, flux.storage.getEnergyStored(), flux.storage.getMaxEnergyStored(), midBlue, FontHelper.getIntFromColor(41, 94, 220));
		renderEnergyBar(14, 90 + 4, flux.storage.getEnergyStored(), flux.storage.getMaxEnergyStored(), colour, colour);
		IFluxCommon common = FluxNetworks.getClientCache().getNetwork(flux.networkID.getObject());
		renderEnergyBar(14, 134, common.getStatistics().network_energy, common.getStatistics().network_energy_capacity, colour, colour);
		FontHelper.text(FluxTranslate.LOCAL_BUFFER.t() + ": ", 14, 80 + 4, colour);
		FontHelper.text(FluxTranslate.NETWORK_BUFFER.t() + ": " + (common.getStatistics().network_energy != 0 ? +(flux.storage.getEnergyStored() * 100 / common.getStatistics().network_energy) + " %" : ""), 14, 124, colour);
	}

}
