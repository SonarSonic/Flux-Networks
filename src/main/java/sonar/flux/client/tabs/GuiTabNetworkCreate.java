package sonar.flux.client.tabs;

import java.util.List;

import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;

public class GuiTabNetworkCreate extends GuiTabNetworkEdit {

	public GuiTabNetworkCreate(TileFlux tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_CREATE;
	}

}