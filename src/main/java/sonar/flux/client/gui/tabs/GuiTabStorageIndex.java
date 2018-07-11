package sonar.flux.client.gui.tabs;

import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.common.tileentity.TileStorage;
import sonar.flux.connection.transfer.stats.NetworkStatistics;

import java.util.List;

import static sonar.flux.connection.NetworkSettings.NETWORK_STATISTICS;

public class GuiTabStorageIndex extends GuiTabConnectionIndex<TileStorage, Object> {

	public GuiTabStorageIndex(TileStorage tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = getNetworkColour();
		NetworkStatistics stats = NETWORK_STATISTICS.getValue(common);
		renderEnergyBar(14, 94, flux.storage.getEnergyStored(), flux.storage.getMaxEnergyStored(), colour, colour);
		renderEnergyBar(14, 134, stats.network_energy, stats.network_energy_capacity, colour, colour);
		FontHelper.text(FluxTranslate.LOCAL_BUFFER.t() + ": " + (stats.network_energy != 0 ? +((int)((((double)flux.getEnergyStored() / flux.getMaxEnergyStored())*100D))) + " %" : ""), 14, 84, colour);
		FontHelper.text(FluxTranslate.NETWORK_BUFFER.t() + ": " + (stats.network_energy != 0 ? +((int)((((double)stats.network_energy / stats.network_energy_capacity)*100D))) + " %" : ""), 14, 124, colour);
	}

}
