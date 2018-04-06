package sonar.flux.client.tabs;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.translate.Localisation;
import sonar.core.utils.SortingDirection;
import sonar.flux.FluxTranslate;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.ClientTransfer;
import sonar.flux.api.SortingType;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.ConnectionDirectionButton;
import sonar.flux.client.ConnectionSortingButton;
import sonar.flux.client.ConnectedBlocksButton;
import sonar.flux.client.GuiTab;
import sonar.flux.client.LargeButton;
import sonar.flux.common.tileentity.TileFlux;

public class GuiTabNetworkConnections extends GuiTabSelectionGrid<TileFlux, Object> {

	public static ChunkDisplayOptions chunk_display_option = ChunkDisplayOptions.BOTH;
	public static Map<ConnectionType, Boolean> canDisplay = new HashMap<>();
	public static SortingType sorting_type = SortingType.PRIORITY;
	public static SortingDirection sorting_dir = SortingDirection.UP;
	public static boolean showConnections = false;
	public SonarScroller connection_grid_scroller;
	public SelectionGrid connection_grid;
	public List<ClientFlux> selected = new ArrayList<>();
	

	static {
		for (ConnectionType type : ConnectionType.values()) {
			canDisplay.put(type, true);
		}
	}

	public GuiTabNetworkConnections(TileFlux tile, List tabs) {
		super(tile, tabs);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new ConnectionTypeButton(this, 0, ConnectionType.PLUG, FluxTranslate.PLUGS.t(), getGuiLeft() + 12, getGuiTop() + 142));
		buttonList.add(new ConnectionTypeButton(this, 1, ConnectionType.POINT, FluxTranslate.POINTS.t(), getGuiLeft() + 52, getGuiTop() + 142));
		buttonList.add(new ConnectionTypeButton(this, 2, ConnectionType.STORAGE, FluxTranslate.STORAGE.t(), getGuiLeft() + 92, getGuiTop() + 142));
		buttonList.add(new ConnectionTypeButton(this, 3, ConnectionType.CONTROLLER, FluxTranslate.CONTROLLERS.t(), getGuiLeft() + 132, getGuiTop() + 142));
		buttonList.add(new ChunkLoadedButton(this, 4, getGuiLeft() + 92, getGuiTop() + 120));
		buttonList.add(new ConnectionSortingButton(this, 5, getGuiLeft() + 11, getGuiTop() + 119));
		buttonList.add(new ConnectionDirectionButton(this, 6, getGuiLeft() + 12+16, getGuiTop() + 119));
		buttonList.add(new ConnectedBlocksButton(this, 6, getGuiLeft() + 51, getGuiTop() + 119));
		buttonList.add(new LargeButton(this, FluxTranslate.SORTING_CLEAR.t(), 7, getGuiLeft() + 52+16, getGuiTop() + 119, 68, 0));
	}

	public static enum ChunkDisplayOptions {
		BOTH(FluxTranslate.SORTING_BOTH), //
		LOADED(FluxTranslate.SORTING_LOADED), //
		UNLOADED(FluxTranslate.SORTING_UNLOADED);//

		Localisation message;

		ChunkDisplayOptions(Localisation message) {
			this.message = message;
		}

		public String getDisplayName() {
			return message.t();
		}

		public boolean canDisplay(IFlux flux) {
			switch (this) {
			case LOADED:
				return flux.isChunkLoaded();
			case UNLOADED:
				return !flux.isChunkLoaded();
			default:
				break;
			}
			return true;
		}
	}

	public static class ChunkLoadedButton extends GuiButton {

		public GuiTabNetworkConnections gui;

		protected ChunkLoadedButton(GuiTabNetworkConnections gui, int id, int x, int y) {
			super(id, x, y, 72, 15, "");
			this.gui = gui;
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x - 1, this.y - 1, this.x + 1 + this.width, this.y + this.height + 1, AbstractGuiTab.grey);
				switch (gui.chunk_display_option) {
				case BOTH:
					drawRect(this.x, this.y, this.x + this.width, this.y + this.height, AbstractGuiTab.midBlue);
					GlStateManager.color(1, 1, 1, 1);
					mc.getTextureManager().bindTexture(AbstractGuiTab.scroller_flux_gui);
					drawTexturedModalRect(this.x, this.y, 4, 227, this.width, this.height / 2);
					break;
				case LOADED:
					drawRect(this.x, this.y, this.x + this.width, this.y + this.height, AbstractGuiTab.midBlue);
					break;
				case UNLOADED:
					GlStateManager.color(1, 1, 1, 1);
					mc.getTextureManager().bindTexture(AbstractGuiTab.scroller_flux_gui);
					drawTexturedModalRect(this.x, this.y, 4, 227, this.width, this.height);
					break;
				default:
					break;
				}
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			gui.drawSonarCreativeTabHoveringText(FluxTranslate.CHUNK.t() + ": " + gui.chunk_display_option.getDisplayName(), x, y);
		}

	}

	public static class ConnectionTypeButton extends GuiButton {

		public GuiTabNetworkConnections gui;
		public ConnectionType type;
		public String typeName;

		protected ConnectionTypeButton(GuiTabNetworkConnections gui, int id, ConnectionType type, String name, int x, int y) {
			super(id, x, y, 32, 16, "");
			this.gui = gui;
			this.type = type;
			this.typeName = name;
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;

				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x - 1, this.y - 1, this.x + 32 + 1, this.y + 16 + 1, type.gui_colour);
				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x, this.y, this.x + 32, this.y + 16, Color.BLACK.getRGB());
				gui.drawNormalItemStack(type.getRepresentiveStack(), this.x, this.y);
				if (gui.canDisplay.get(type)) {
					mc.getTextureManager().bindTexture(AbstractGuiTab.small_buttons);
					GlStateManager.color(1, 1, 1, 1);
					this.drawTexturedModalRect(this.x + 3 + 16, this.y + 2, 130, 0, 11, 11);
				}
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			gui.drawSonarCreativeTabHoveringText(FluxTranslate.SHOW.t() + " " + typeName + ": " + gui.canDisplay.get(type), x, y);
		}

	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof ConnectionTypeButton) {
			ConnectionType type = ((ConnectionTypeButton) button).type;
			canDisplay.put(type, !canDisplay.get(type));
			connection_grid_scroller.currentScroll = 0;
			return;
		}
		if (button instanceof ChunkLoadedButton) {
			chunk_display_option = SonarHelper.incrementEnum(chunk_display_option, ChunkDisplayOptions.values());
			connection_grid_scroller.currentScroll = 0;
			return;
		}
		if (button instanceof ConnectionSortingButton) {
			sorting_type = SonarHelper.incrementEnum(sorting_type, SortingType.values());
			connection_grid_scroller.currentScroll = 0;
			return;
		}
		if (button instanceof ConnectionDirectionButton) {
			sorting_dir = SonarHelper.incrementEnum(sorting_dir, SortingDirection.values());
			connection_grid_scroller.currentScroll = 0;
			return;
		}
		if(button instanceof ConnectedBlocksButton){
			showConnections = !showConnections;
			connection_grid_scroller.currentScroll = 0;
			return;
		}
		if(button instanceof LargeButton){
			switch(button.id){
			case 7:
				chunk_display_option = ChunkDisplayOptions.BOTH;
				showConnections = false;
				canDisplay = new HashMap<>();
				for (ConnectionType type : ConnectionType.values()) {
					canDisplay.put(type, true);
				}
				break;
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (getGridList(0).isEmpty()) {
			if (common.isFakeNetwork()) {
				renderNavigationPrompt(FluxTranslate.ERROR_NO_CONNECTIONS.t(), FluxTranslate.GUI_TAB_NETWORK_SELECTION.t());
			} else if (common.getClientFluxConnection().isEmpty()) {
				FontHelper.textCentre(FluxTranslate.ERROR_WAITING_FOR_SERVER.t(), xSize, 10, Color.GRAY.getRGB());
			} else {
				FontHelper.textCentre(FluxTranslate.ERROR_NO_MATCHES.t(), xSize, 14, Color.GRAY.getRGB());
			}
		}
	}

	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
		connection_grid = new SelectionGrid(this, 0, 11, 8, 154, 18, 1, 6);
		connection_grid_scroller = new SonarScroller(connection_grid.xPos + (connection_grid.gWidth * connection_grid.eWidth), connection_grid.yPos, connection_grid.gHeight * connection_grid.eHeight, 7);
		grids.put(connection_grid, connection_grid_scroller);
	}

	@Override
	public void onGridClicked(int gridID, Object element, int x, int y, int pos, int button, boolean empty) {
		if (element instanceof ClientFlux) {
			if (selected.contains(element)) {
				selected.remove(element);
			} else {
				selected.add((ClientFlux) element);
			}
		}
	}

	@Override
	public void renderGridElement(int gridID, Object element, int x, int y, int slot) {
		if (element instanceof ClientFlux) {
			renderFlux((IFlux) element, selected.contains(element), 0, 0);
		} else if (element instanceof ClientTransfer) {
			renderFluxTransfer((ClientTransfer) element, x, y, common.getNetworkColour().getRGB());
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
		List<ClientFlux> gridList = new ArrayList<>();
		for (ClientFlux c : common.getClientFluxConnection()) {
			if (chunk_display_option.canDisplay(c) && canDisplay.get(c.getConnectionType())) {
				c.addToGuiList(gridList, true, false);
			}
		}
		sorting_type.sort(gridList, sorting_dir);
		if (showConnections) {
			List connectionlist = new ArrayList<>();
			for(ClientFlux flux : gridList){
				flux.addToGuiList(connectionlist, true, flux.connection_type == ConnectionType.PLUG || flux.connection_type == ConnectionType.POINT);
			}
			return connectionlist;
		}
		/*
		if (!selected.isEmpty()) {
			for (ClientFlux s : selected) {
				if (s.connection_type == ConnectionType.PLUG || s.connection_type == ConnectionType.POINT) {
					int index = gridList.indexOf(s);
					if (index != -1) {
						gridList.addAll(index + 1, s.getTransferHandler().getTransfers());
					}
				}
			}
		}
		*/

		return gridList;
	}

	public boolean isSelectedNetwork(ClientFlux network) {
		return network.getNetworkID() == getNetworkID();
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.CONNECTIONS;
	}

}
