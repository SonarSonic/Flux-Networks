package sonar.flux.client.gui.tabs;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstractGrid;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketFluxItemNetwork;
import sonar.flux.network.PacketTileHelper;
import sonar.flux.network.PacketTileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabNetworkSelection extends GuiTabAbstractGrid<IFluxNetwork> {

	public GuiTabNetworkSelection(List<EnumGuiTab> tabs) {
		super(tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (getGridList(0).isEmpty()) {
			renderNavigationPrompt(FluxTranslate.ERROR_NO_NETWORKS.t(), FluxTranslate.GUI_TAB_NETWORK_CREATE.t());
		}
	}

	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
		SelectionGrid grid = new SelectionGrid(this, 0, 11, 8, 154, 11, 1, 13);
		SonarScroller scroller = new SonarScroller(grid.xPos + (grid.gWidth * grid.eWidth), grid.yPos, grid.gHeight * grid.eHeight, 7);
		grids.put(grid, scroller);
	}

	@Override
	public void onGridClicked(int gridID, IFluxNetwork element, int x, int y, int pos, int button, boolean empty) {
		if (element != null) {
			if (x - getGuiLeft() > 153) {
				FMLCommonHandler.instance().showGuiScreen(new GuiTabConfirmNetworkDeletion(this, tabs, element));
			} else if(!isSelectedNetwork(element)){
				TileFlux flux = FluxNetworks.proxy.getFluxTile();
				if(flux != null) {
					PacketTileHelper.sendPacketToServer(PacketTileType.SET_NETWORK, flux, PacketTileHelper.createNetworkSetPacket(element.getNetworkID()));
				}
				ItemStack stack = FluxNetworks.proxy.getFluxStack();
				if(stack != null && stack.getItem() instanceof IFluxItemGui){
					FluxNetworks.network.sendToServer(new PacketFluxItemNetwork(element.getNetworkID()));
					//stack.getOrCreateSubCompound().setInteger("id", element.getNetworkID());
				}
			}
		}
	}

	@Override
	public void renderGridElement(int gridID, IFluxNetwork element, int x, int y, int slot) {
		renderNetwork(NETWORK_NAME.getValue(element), NETWORK_ACCESS.getValue(element), NETWORK_COLOUR.getValue(element).getRGB(), isSelectedNetwork(element), 0, 0);
		// delete button
		bindTexture(small_buttons);
		drawTexturedModalRect(154 - 12, 0, 48, 12, 10 + 1, 10 + 1);
	}

	@Override
	public void renderElementToolTip(int gridID, IFluxNetwork element, int x, int y) {
		List<String> strings = new ArrayList<>();
		if (x > 153) {
			strings.add(TextFormatting.RED + FluxTranslate.DELETE.t() + ": " + NETWORK_NAME.getValue(element));
		} else {
			strings.add(FluxTranslate.NETWORK_OWNER.t() + ": " + TextFormatting.AQUA + NETWORK_CACHED_NAME.getValue(element));
			strings.add(FluxTranslate.ACCESS_SETTING.t() + ": " + TextFormatting.AQUA + NETWORK_ACCESS.getValue(element).getDisplayName());
		}
		drawHoveringText(strings, x, y);
	}

	@Override
	public List getGridList(int gridID) {
		return FluxNetworks.getClientCache().getAllNetworks();
	}

	public boolean isSelectedNetwork(IFluxNetwork network) {
		return network.getNetworkID() == getNetworkID();
	}

	@Override
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.NETWORK_SELECTION;
	}

}