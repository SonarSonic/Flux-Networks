package sonar.flux.client.gui.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.translate.Localisation;
import sonar.core.utils.SortingDirection;
import sonar.flux.FluxTranslate;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.ClientTransfer;
import sonar.flux.api.NetworkFluxFolder;
import sonar.flux.api.SortingType;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstractGrid;
import sonar.flux.client.gui.buttons.ConnectedBlocksButton;
import sonar.flux.client.gui.buttons.ConnectionDirectionButton;
import sonar.flux.client.gui.buttons.ConnectionSortingButton;
import sonar.flux.client.gui.buttons.LargeButton;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.CLIENT_CONNECTIONS;
import static sonar.flux.connection.NetworkSettings.NETWORK_FOLDERS;

public class GuiTabNetworkConnections extends GuiTabAbstractGrid<Object> {

	public static ChunkDisplayOptions chunk_display_option = ChunkDisplayOptions.BOTH;
	public static Map<ConnectionType, Boolean> canDisplay = new HashMap<>();
	public static SortingType sorting_type = SortingType.PRIORITY;
	public static SortingDirection sorting_dir = SortingDirection.UP;
	public static boolean showConnections = false;
	public SonarScroller connection_grid_scroller;
	public SelectionGrid connection_grid;
	public List<ClientFlux> selected = new ArrayList<>();
	public List<Integer> folder_ids = new ArrayList<>();
	

	static {
		for (ConnectionType type : ConnectionType.values()) {
			canDisplay.put(type, true);
		}
	}

	public GuiTabNetworkConnections(List tabs) {
		super(tabs);
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
		buttonList.add(new ConnectionDirectionButton(this, 6, getGuiLeft() + 28, getGuiTop() + 119));
		buttonList.add(new ConnectedBlocksButton(this, 6, getGuiLeft() + 51, getGuiTop() + 119));
		buttonList.add(new LargeButton(this, FluxTranslate.SORTING_CLEAR.t(), 7, getGuiLeft() + 68, getGuiTop() + 119, 68, 0));
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

		//// ADD VALID CONNECTIONS \\\\
		List<ClientFlux> gridList = new ArrayList<>();
		for (ClientFlux c : CLIENT_CONNECTIONS.getValue(common)) {
			if (chunk_display_option.canDisplay(c) && canDisplay.get(c.getConnectionType())) {
				c.addToGuiList(gridList, true, false);
			}
		}
		sorting_type.sort(gridList, sorting_dir);


		//// SORTING FOLDERS \\\\
		List list = new ArrayList<>();
		List<NetworkFluxFolder> folders = NETWORK_FOLDERS.getValue(common);
		folders.sort((folder1, folder2) -> SonarHelper.compareStringsWithDirection(folder1.name, folder2.name, SortingDirection.DOWN));

		for(NetworkFluxFolder folder : folders){
			list.add(folder);
			Iterator<ClientFlux> it = gridList.iterator();
			while(it.hasNext()){
				ClientFlux f = it.next();
				if(f.getFolderID()==folder.folderID){
					if(folder_ids.contains(f.getFolderID())) {
						list.add(f);
					}
					it.remove();
				}
			}
		}
		list.addAll(gridList);

		//// ADD CONNECTIONS TO THE LIST \\\\
		if (showConnections) {
			List connection_list = new ArrayList<>();
			for(Object obj : list){
				if(obj instanceof ClientFlux) {
					ClientFlux flux = (ClientFlux) obj;
					flux.addToGuiList(connection_list, true, flux.connection_type == ConnectionType.PLUG || flux.connection_type == ConnectionType.POINT);
				}else{
					connection_list.add(obj);
				}
			}
			return connection_list;
		}

		return gridList;
	}

	@Override
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.CONNECTIONS;
	}

	//// BUTTONS \\\\

	public enum ChunkDisplayOptions {
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
		public void drawButton(@Nonnull Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x - 1, this.y - 1, this.x + 1 + this.width, this.y + this.height + 1, grey);
				switch (chunk_display_option) {
					case BOTH:
						drawRect(this.x, this.y, this.x + this.width, this.y + this.height, midBlue);
						GlStateManager.color(1, 1, 1, 1);
						mc.getTextureManager().bindTexture(scroller_flux_gui);
						drawTexturedModalRect(this.x, this.y, 4, 227, this.width, this.height / 2);
						break;
					case LOADED:
						drawRect(this.x, this.y, this.x + this.width, this.y + this.height, midBlue);
						break;
					case UNLOADED:
						GlStateManager.color(1, 1, 1, 1);
						mc.getTextureManager().bindTexture(scroller_flux_gui);
						drawTexturedModalRect(this.x, this.y, 4, 227, this.width, this.height);
						break;
					default:
						break;
				}
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			gui.drawSonarCreativeTabHoveringText(FluxTranslate.CHUNK.t() + ": " + chunk_display_option.getDisplayName(), x, y);
		}

	}

	public static class ConnectionTypeButton extends GuiButton {

		public GuiTabNetworkConnections gui;
		public ConnectionType type;
		public String typeName;
		public ItemStack displayStack;

		protected ConnectionTypeButton(GuiTabNetworkConnections gui, int id, ConnectionType type, String name, int x, int y) {
			super(id, x, y, 32, 16, "");
			this.gui = gui;
			this.type = type;
			this.typeName = name;
			this.displayStack = type.getRepresentiveStack();
			NBTTagCompound colourTag = new NBTTagCompound();
			colourTag.setBoolean("gui_colour", true);
			displayStack.setTagCompound(colourTag);
		}

		@Override
		public void drawButton(@Nonnull Minecraft mc, int x, int y, float partialTicks) {
			if (this.visible) {
				this.hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;

				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x - 1, this.y - 1, this.x + 32 + 1, this.y + 16 + 1, type.gui_colour);
				GlStateManager.color(1, 1, 1, 1);
				drawRect(this.x, this.y, this.x + 32, this.y + 16, Color.BLACK.getRGB());
				gui.drawNormalItemStack(displayStack, this.x, this.y);
				if (canDisplay.get(type)) {
					mc.getTextureManager().bindTexture(small_buttons);
					GlStateManager.color(1, 1, 1, 1);
					this.drawTexturedModalRect(this.x + 3 + 16, this.y + 2, 24, 12, 11, 11);
				}
			}
		}

		public void drawButtonForegroundLayer(int x, int y) {
			gui.drawSonarCreativeTabHoveringText(FluxTranslate.SHOW.t() + " " + typeName + ": " + canDisplay.get(type), x, y);
		}

	}
}
