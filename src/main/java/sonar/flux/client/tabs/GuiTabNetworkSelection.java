package sonar.flux.client.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiTabNetworkSelection extends GuiTabSelectionGrid<TileFlux, IFluxCommon> {

	public GuiTabNetworkSelection(TileFlux tile, List tabs) {
		super(tile, tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (getGridList(0).isEmpty()) {
			renderNavigationPrompt("No available networks", "Create a New Network");
		}
	}
	
	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
		SelectionGrid grid = new SelectionGrid(this, 0, 11, 8, 154, 11, 1, 13);
		SonarScroller scroller = new SonarScroller(grid.xPos + (grid.gWidth * grid.eWidth), grid.yPos, grid.gHeight * grid.eHeight, 7);
		grids.put(grid, scroller);
	}

	@Override
	public void onGridClicked(int gridID, IFluxCommon element, int x, int y, int pos, int button, boolean empty) {
		if (element !=null && !isSelectedNetwork(element)) {
			PacketHelper.sendPacketToServer(PacketType.SET_NETWORK, flux, PacketHelper.createNetworkSetPacket(element.getNetworkID()));
		}
	}

	@Override
	public void renderGridElement(int gridID, IFluxCommon element, int x, int y, int slot) {
		renderNetwork(element.getNetworkName(), element.getAccessType(), element.getNetworkColour().getRGB(), isSelectedNetwork(element), 0, 0);
	}

	@Override
	public void renderElementToolTip(int gridID, IFluxCommon element, int x, int y) {
		List<String> strings = new ArrayList<>();
		strings.add(FontHelper.translate("network.owner") + ": " + TextFormatting.AQUA + element.getCachedPlayerName());
		strings.add(FontHelper.translate("network.accessSetting") + ": " + TextFormatting.AQUA + FontHelper.translate(element.getAccessType().getName()));
		drawHoveringText(strings, x, y);
	}

	@Override
	public List getGridList(int gridID) {
		return FluxNetworks.getClientCache().getAllNetworks();
	}

	public boolean isSelectedNetwork(IFluxCommon network) {
		return network.getNetworkID() == getNetworkID();
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_SELECTION;
	}

}