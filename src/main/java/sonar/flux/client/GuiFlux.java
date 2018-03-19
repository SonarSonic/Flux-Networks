package sonar.flux.client;

import java.awt.Color;
import java.io.IOException;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.common.tileentity.TileStorage;
import sonar.flux.connection.EmptyFluxNetwork;

public class GuiFlux extends GuiFluxBase {

	public EntityPlayer player;
	public IFluxCommon common = EmptyFluxNetwork.INSTANCE;
	public boolean disabledState;

	public GuiFlux(Container container, TileFlux tile, EntityPlayer player) {
		super(container, tile);
		this.player = player;
	}

	public void onGuiClosed() {
		super.onGuiClosed();
		state = GuiState.INDEX;
	}

	@Override
	public void initGui() {
		super.initGui();
		common = FluxNetworks.getClientCache().getNetwork(getNetworkID());
		Keyboard.enableRepeatEvents(true);
		this.xSize = state.x;
		this.ySize = state.y;
		this.mc.player.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		disabledState = false;

		int i = 0;
		for (GuiState state : GuiState.VALUES) {
			buttonList.add(new NavigationButtons(state, -i, guiLeft + 2 + 18 * i, guiTop - 15));
			i++;
		}
		state.init(this);
	}

	public boolean isSelectedNetwork(IFluxCommon network) {
		return network.getNetworkName().equals(common.getNetworkName()) && network.getNetworkID() == getNetworkID() && network.getCachedPlayerName().equals(common.getCachedPlayerName());
	}

	public void drawScreen(IFlux flux, ConnectionType type) {
		int colour = common.getNetworkColour().getRGB();
		FontHelper.text(GUI.NETWORK_NAME + ": ", 7, 30, colour);
		FontHelper.text(GUI.PRIORITY + ":", 7, 48, colour);
		FontHelper.text(GUI.MAX + ":", 87, 48, colour);
		FontHelper.text(GUI.IGNORE_LIMIT + ": " + TextFormatting.WHITE + tile.disableLimit.getObject().toString(), 7, 48 + 18, colour);
		// FontHelper.text(FontHelper.translate(tile.getBlockType().getLocalizedName()),
		// 20, 8, 0);
		renderNetwork(common.getNetworkName(), common.getAccessType(), common.getNetworkColour().getRGB(), true, 11, 8);

		switch (type) {
		case CONTROLLER:
			IFluxController controller = (IFluxController) flux;
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.sendMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(controller.getSendMode().getName()), 8, 66 + 18, colour);
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.receiveMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(controller.getReceiveMode().getName()), 8, 86 + 18, colour);
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.transferMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(controller.getTransferMode().getName()) + (controller.getTransferMode().isBanned() ? TextFormatting.RED + " BANNED" : ""), 8, 106 + 18, colour);
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.transmitterMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(controller.getTransmitterMode().getName()), 8, 126 + 18, colour);
			break;
		case PLUG:
			break;
		case POINT:
			break;
		case STORAGE:
			TileStorage entity = (TileStorage) flux;
			renderEnergyBar(14, 90 + 4, entity.storage.getEnergyStored(), entity.storage.getMaxEnergyStored(), midBlue, FontHelper.getIntFromColor(41, 94, 220));
			IFluxCommon common = FluxNetworks.getClientCache().getNetwork(tile.networkID.getObject());
			renderEnergyBar(14, 130 + 4, common.getEnergyAvailable(), common.getMaxEnergyStored(), colour, colour);
			FontHelper.text("Local Buffer: ", 14, 80 + 4, Color.DARK_GRAY.getRGB());
			FontHelper.text("Network Buffer: " + (common.getEnergyAvailable() != 0 ? +(entity.storage.getEnergyStored() * 100 / common.getEnergyAvailable()) + " %" : ""), 14, 120 + 4, colour);
			break;
		default:
			break;
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (!disabledState) {
			for (SonarTextField field : state.getFields(this)) {
				field.drawTextBox();
			}
		}
		common = FluxNetworks.getClientCache().getNetwork(getNetworkID());
		/// int networkColour = common.getNetworkColour().getRGB();

		state.draw(this, x, y);
		drawError(x - guiLeft, y - guiTop);
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		forScrollers(s -> s.handleMouse(state.needsScrollBars(), state.getSelectionSize(this)));
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		forScrollers(s -> s.drawScreen(x, y, state.needsScrollBars()));
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button instanceof NavigationButtons) {
				switchState(((NavigationButtons) button).buttonState);
				reset();
				return;
			}
			if (!disabledState)
				state.button(this, button);
		}
	}

	@Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (!disabledState) {
			for (SonarTextField field : state.getFields(this)) {
				field.mouseClicked(x - guiLeft, y - guiTop, mouseButton);
			}
			state.click(this, x, y, mouseButton);
		}
	}

	@Override
	public void keyTyped(char c, int i) throws IOException {
		if (disabledState) {
			super.keyTyped(c, i);
			return;
		}
		for (SonarTextField field : state.getFields(this)) {
			if (field != null && field.isFocused()) {
				if (c == 13 || c == 27) {
					field.setFocused(false);
				} else {
					field.textboxKeyTyped(c, i);
					state.textboxKeyTyped(this, field, c, i);
				}
				return;
			}
		}
		if (state.type(this, c, i)) {
			super.keyTyped(c, i);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		forScrollers(s -> drawTexturedModalRect(s.left, s.top + (int) ((float) (s.length - 17) * s.getCurrentScroll()), 176, 0, 10, 15));
	}

	public void forScrollers(Consumer<SonarScroller> action) {
		SonarScroller[] scrollers = state.getScrollers();
		for (SonarScroller scroller : scrollers) {
			if (scroller != null) {
				action.accept(scroller);
			}
		}
	}
}
