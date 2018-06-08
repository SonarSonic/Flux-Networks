package sonar.flux.client.gui.tabs;

import sonar.flux.client.gui.GuiTab;
import sonar.flux.common.tileentity.TileFlux;

import java.util.List;

public class GuiTabNetworkCreate extends GuiTabNetworkEdit {

	public GuiTabNetworkCreate(TileFlux tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_CREATE;
	}

}