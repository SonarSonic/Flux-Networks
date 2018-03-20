package sonar.flux.client.tabs;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiTabNetworkConnections extends GuiTabSelectionGrid<ClientFlux> {

	public GuiTabNetworkConnections(TileFlux tile, List tabs) {
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
	public void onGridClicked(int gridID, ClientFlux element,int x, int y,  int pos, int button, boolean empty) {
		if (isSelectedNetwork(element)) {
			PacketHelper.sendPacketToServer(PacketType.SET_NETWORK, flux, PacketHelper.createNetworkSetPacket(element.getNetworkID()));
		}
	}

	@Override
	public void renderGridElement(int gridID, ClientFlux element, int x, int y, int slot) {
		renderFlux(element, true, 0, 0);		
		bindTexture(buttons);
		drawTexturedModalRect(0, 170, 56, 0, 12, 12);
	}

	@Override
	public void renderElementToolTip(int gridID, ClientFlux element, int x, int y) {
		List<String> strings = Lists.newArrayList();
		if (element.coords.getBlockPos().equals(flux.getPos())){
			strings.add(TextFormatting.GREEN + "THIS CONNECTION!");
		}
		strings.add(FontHelper.translate("flux.type") + ": " + TextFormatting.AQUA + element.getConnectionType().toString());
		strings.add(TextFormatting.GRAY + element.getCoords().toString());
		strings.add(GUI.MAX + ": " + TextFormatting.AQUA + (element.getTransferLimit() == Long.MAX_VALUE ? "NO LIMIT" : element.getTransferLimit()));
		strings.add(GUI.PRIORITY + ": " + TextFormatting.AQUA + element.getCurrentPriority());
		drawHoveringText(strings, x, y);
	}

	@Override
	public void startToolTipRender(int gridID, ClientFlux selection, int x, int y) {
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		renderElementToolTip(gridID, selection, x, y);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	public List getGridList(int gridID) {
		return common.getClientFluxConnection();
	}

	public boolean isSelectedNetwork(ClientFlux network) {
		return network.getNetworkID() == getNetworkID();
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.CONNECTIONS;
	}

}
