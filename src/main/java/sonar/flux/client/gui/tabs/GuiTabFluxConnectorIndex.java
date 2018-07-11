package sonar.flux.client.gui.tabs;

import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.ClientTransfer;
import sonar.flux.api.energy.internal.IFluxTransfer;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.common.tileentity.TileFluxConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiTabFluxConnectorIndex extends GuiTabConnectionIndex<TileFluxConnector, Object> {

	public GuiTabFluxConnectorIndex(TileFluxConnector tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
		SelectionGrid grid = new SelectionGrid(this, 0, 11, 86, 154, 18, 1, 4);
		SonarScroller scroller = new SonarScroller(grid.xPos + (grid.gWidth * grid.eWidth), grid.yPos, grid.gHeight * grid.eHeight, 7);
		scroller.renderScroller = true;
		grids.put(grid, scroller);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	public void onGridClicked(int gridID, Object element, int x, int y, int pos, int button, boolean empty) {}

	@Override
	public void renderGridElement(int gridID, Object element, int x, int y, int slot) {
		if (element instanceof ClientFlux) {
			renderFlux((IFlux) element, true, 0, 0);
		} else if (element instanceof ClientTransfer) {
			renderFluxTransfer((ClientTransfer) element, x, y, getNetworkColour());
		}
	}

	@Override
	public void renderElementToolTip(int gridID, Object element, int x, int y) {
		List<String> strings = null;
		if (element instanceof ClientFlux) {
			strings = getTextLines((ClientFlux) element);
		} else if (element instanceof ClientTransfer) {
			strings = getTextLines((ClientTransfer) element);
		}
		if (strings != null && !strings.isEmpty()) {
			drawHoveringText(strings, x, y);
		}
	}

	@Override
	public List getGridList(int gridID) {
		List<IFluxTransfer> gridList = new ArrayList<>();
		flux.getClientFlux().addToGuiList(gridList, true, true);
		return gridList;
	}

}
