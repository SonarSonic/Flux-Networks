package sonar.flux.client.tabs;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.ClientTransfer;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;

public class GuiTabNetworkConnections extends GuiTabSelectionGrid<TileFlux, Object> {

	public static ChunkDisplayOptions chunk_display_option = ChunkDisplayOptions.BOTH;
	public static Map<ConnectionType, Boolean> canDisplay = new HashMap<>();

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
		buttonList.add(new ConnectionTypeButton(this, 0, ConnectionType.PLUG, GUI.PLUGS.toString(), getGuiLeft() + 12, getGuiTop() + 140));
		buttonList.add(new ConnectionTypeButton(this, 1, ConnectionType.POINT, GUI.POINTS.toString(), getGuiLeft() + 52, getGuiTop() + 140));
		buttonList.add(new ConnectionTypeButton(this, 2, ConnectionType.STORAGE, GUI.STORAGE.toString(), getGuiLeft() + 92, getGuiTop() + 140));
		buttonList.add(new ConnectionTypeButton(this, 3, ConnectionType.CONTROLLER, GUI.CONTROLLERS.toString(), getGuiLeft() + 132, getGuiTop() + 140));
		//buttonList.add(new ChunkLoadedButton(this, 4, getGuiLeft() + 120, getGuiTop() + 140));
	}

	public static enum ChunkDisplayOptions {
		BOTH, LOADED, UNLOADED;

		public String getDisplayString() {
			switch (this) {
			case BOTH:
				return "Show unloaded and loaded connections";
			case LOADED:
				return "Show only loaded connections";
			case UNLOADED:
				return "Show only unloaded connections";
			}
			return name();
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
			super(id, x, y, 16, 16, "");
			this.gui = gui;
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x, this.y, 16, 16, gui.common.getNetworkColour().getRGB());
				switch (gui.chunk_display_option) {
				case BOTH:
					drawRect(this.x + 1, this.y + 1, this.x + 16 - 1, this.y + 16 - 1, AbstractGuiTab.midBlue);
					GlStateManager.color(1, 1, 1, 1);
					mc.getTextureManager().bindTexture(AbstractGuiTab.scroller_flux_gui);
					drawTexturedModalRect(this.x + 1 + 7, this.y + 1, 4, 227, 16 - 1, 16 - 1);
					break;
				case LOADED:
					drawRect(this.x + 1, this.y + 1, this.x + 16 - 1, this.y + 16 - 1, AbstractGuiTab.midBlue);
					break;
				case UNLOADED:
					mc.getTextureManager().bindTexture(AbstractGuiTab.scroller_flux_gui);
					drawTexturedModalRect(this.x + 1, this.y + 1, 4, 227, 16 - 1, 16 - 1);
					break;
				default:
					break;
				}
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			gui.drawSonarCreativeTabHoveringText(gui.chunk_display_option.getDisplayString(), x, y);
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
				drawRect(this.x-1, this.y-1, this.x + 32+1, this.y + 16+1, type.gui_colour);
				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x, this.y, this.x + 32, this.y + 16, Color.BLACK.getRGB());
				gui.drawNormalItemStack(type.getDisplayStack(), this.x, this.y);
				if (gui.canDisplay.get(type)) {
					mc.getTextureManager().bindTexture(AbstractGuiTab.buttons);
					GlStateManager.color(1, 1, 1, 1);
					this.drawTexturedModalRect(this.x + 3 + 16, this.y + 2, 130, 0, 11, 11);
				}
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			gui.drawSonarCreativeTabHoveringText("Show " + typeName + ": " + gui.canDisplay.get(type), x, y);
		}

	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof ConnectionTypeButton) {
			ConnectionType type = ((ConnectionTypeButton) button).type;
			canDisplay.put(type, !canDisplay.get(type));
			return;
		}
		if (button instanceof ChunkLoadedButton) {
			SonarHelper.incrementEnum(chunk_display_option, ChunkDisplayOptions.values());
			return;
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (getGridList(0).isEmpty()) {
			if (common.isFakeNetwork()) {
				renderNavigationPrompt("No Connections Available", "Network Selection");
			} else if(common.getClientFluxConnection().isEmpty()){
				FontHelper.textCentre(FontHelper.translate("Waiting for server"), xSize, 10, Color.GRAY.getRGB());
			}
		}
	}

	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
		SelectionGrid grid = new SelectionGrid(this, 0, 11, 8, 154, 18, 1, 7);
		SonarScroller scroller = new SonarScroller(grid.xPos + (grid.gWidth * grid.eWidth), grid.yPos, grid.gHeight * grid.eHeight, 7);
		grids.put(grid, scroller);
	}

	@Override
	public void onGridClicked(int gridID, Object element, int x, int y, int pos, int button, boolean empty) {

	}

	@Override
	public void renderGridElement(int gridID, Object element, int x, int y, int slot) {
		if (element instanceof ClientFlux) {
			renderFlux((IFlux) element, true, 0, 0);
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
		// FIXME DO SORTING
		List gridList = new ArrayList<>();
		common.getClientFluxConnection().forEach(c -> {
			if (chunk_display_option.canDisplay(c) && canDisplay.get(c.getConnectionType())) {
				c.addToGuiList(gridList, true, false);
			}
		});
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
