package sonar.flux.client.tabs;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SelectionGrid;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.client.GuiTab;
import sonar.flux.client.SmallButton;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiTabNetworkPlayers extends GuiTabSelectionGrid<TileFlux, FluxPlayer> {

	public SonarTextField playerName;
	// public FluxPlayer selectedPlayer;

	public GuiTabNetworkPlayers(TileFlux tile, List tabs) {
		super(tile, tabs);
	}

	@Override
	public void initGui() {
		super.initGui();
		int networkColour = common.getNetworkColour().getRGB();
		buttonList.add(new SmallButton(this, 1, getGuiLeft() + 150, getGuiTop() + 138, 136, "Add"));
		playerName = new SonarTextField(1, getFontRenderer(), 14, 138, 130, 12).setBoxOutlineColour(networkColour);
		playerName.setMaxStringLength(24);
		playerName.setText("");
		fieldList.add(playerName);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (getGridList(0).isEmpty()) {
			if (common.isFakeNetwork()) {
				renderNavigationPrompt("No players can be added", "Network Selection");
			} else if (disabled) {
				renderNavigationPrompt("Unavailable in Private Mode", "Edit Network");
			}
		}
	}

	@Override
	public void addGrids(Map<SelectionGrid, SonarScroller> grids) {
		SelectionGrid grid = new SelectionGrid(this, 0, 11, 8, 154, 11, 1, 11);
		SonarScroller scroller = new SonarScroller(grid.xPos + (grid.gWidth * grid.eWidth), grid.yPos, grid.gHeight * grid.eHeight, 7);
		grids.put(grid, scroller);
	}

	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		switch (button.id) {
		case 1:
			if (!playerName.getText().isEmpty()) {
				PacketHelper.sendPacketToServer(PacketType.ADD_PLAYER, flux, PacketHelper.createAddPlayerPacket(flux.getNetworkID(), playerName.getText(), PlayerAccess.USER));
				return;
			}
			break;
		}
	}

	@Override
	public void onGridClicked(int gridID, FluxPlayer element, int x, int y, int pos, int button, boolean empty) {
		if (element != null) {
			if (x - getGuiLeft() > 153) {
				if (element.access != PlayerAccess.OWNER)
					PacketHelper.sendPacketToServer(PacketType.REMOVE_PLAYER, flux, PacketHelper.createRemovePlayerPacket(flux.getNetworkID(), element.id, PlayerAccess.USER));
			} else if (button == 1) {
				PacketHelper.sendPacketToServer(PacketType.CHANGE_PLAYER, flux, PacketHelper.createChangePlayerPacket(flux.getNetworkID(), element.id, element.access));
			}
		}
	}

	@Override
	public void renderGridElement(int gridID, FluxPlayer element, int x, int y, int slot) {
		PlayerAccess access = element.getAccess();
		boolean isOwner = common.getCachedPlayerName().equals(element.getCachedName());
		Gui.drawRect(0, 0, 0 + 154, 0 + 12, access.canDelete() || isOwner ? Color.lightGray.getRGB() : access.canEdit() ? colours[7].getRGB() : !access.canConnect() ? colours[4].getRGB() : lightBlue);

		bindTexture(getBackground());
		drawTexturedModalRect(0, 0, 0, 166, 154, 12);
		FontHelper.text(element.getCachedName(), 0 + 3, 0 + 2, Color.white.getRGB());
		bindTexture(small_buttons);
		drawTexturedModalRect(0 + 154 - 12, 0, 112 / 2, 0, 10 + 1, 10 + 1);
	}

	@Override
	public void renderElementToolTip(int gridID, FluxPlayer element, int x, int y) {
		List<String> strings = new ArrayList<>();
		boolean isOwner = common.getCachedPlayerName().equals(element.getCachedName());
		if (x > 153) {
			strings.add(TextFormatting.RED + "Delete: " + element.getCachedName());
		} else {
			strings.add(TextFormatting.AQUA + "Config: " + FontHelper.translate(isOwner ? PlayerAccess.OWNER.getName() : element.access.getName()));
			strings.add("Right click to change");
		}
		drawHoveringText(strings, x, y);
	}

	@Override
	public void startToolTipRender(int gridID, FluxPlayer selection, int x, int y) {
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		renderElementToolTip(gridID, selection, x, y);
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	public List getGridList(int gridID) {
		this.common = FluxNetworks.getClientCache().getNetwork(getNetworkID());
		return common.getPlayers();
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.PLAYERS;
	}

}
