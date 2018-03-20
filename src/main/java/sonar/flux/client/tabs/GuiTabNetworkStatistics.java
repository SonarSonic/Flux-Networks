package sonar.flux.client.tabs;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;

public class GuiTabNetworkStatistics extends AbstractGuiTab {

	public GuiTabNetworkStatistics(TileFlux tile, List tabs) {
		super(tile, tabs);
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_STATS;
	}

	@Override
	public ResourceLocation getBackground() {
		return blank_flux_gui;
	}

}
