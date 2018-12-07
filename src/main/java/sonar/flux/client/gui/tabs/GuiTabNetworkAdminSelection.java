package sonar.flux.client.gui.tabs;

import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.EnumGuiTab;

import java.util.List;
import java.util.Map;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabNetworkAdminSelection extends GuiTabNetworkSelection {

    public GuiTabNetworkAdminSelection(List<EnumGuiTab> tabs) {
        super(tabs);
    }

    public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
        SelectionGrid grid = new SelectionGrid(this, 0, 11, 8, 154, 11, 1, 13);
        SonarScroller scroller = new SonarScroller(grid.xPos + (grid.gWidth * grid.eWidth), grid.yPos, grid.gHeight * grid.eHeight, 7);
        grids.put(grid, scroller);
    }


    @Override
    public void renderGridElement(int gridID, IFluxNetwork element, int x, int y, int slot) {
        renderNetwork(NETWORK_NAME.getValue(element), NETWORK_ACCESS.getValue(element), NETWORK_COLOUR.getValue(element).getRGB(), isSelectedNetwork(element), 0, 0);
        // delete button
        bindTexture(small_buttons);
        drawTexturedModalRect(154 - 12, 0, 48, 12, 10 + 1, 10 + 1);
    }
    @Override
    public EnumGuiTab getCurrentTab() {
        return EnumGuiTab.ADMIN_NETWORK_SELECTION;
    }
}
