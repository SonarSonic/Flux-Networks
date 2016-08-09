package sonar.flux.client;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.TextFormatting;
import sonar.core.SonarCore;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.EnergyStats;
import sonar.flux.api.FluxPlayer;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFluxCommon.AccessType;
import sonar.flux.api.IFluxController;
import sonar.flux.api.INetworkStatistics;
import sonar.flux.api.PlayerAccess;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.network.CommonNetworkCache;
import sonar.flux.network.PacketFluxButton;
import sonar.flux.network.PacketFluxButton.Type;

public abstract class GuiFlux extends GuiFluxBase {

	public EntityPlayer player;
	public IFluxCommon common = CommonNetworkCache.empty;

	// INDEX
	private SonarTextField fluxName;
	private SonarTextField priority;
	private SonarTextField limit;

	// NETWORK_SELECT
	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	private int changed;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;

	// NETWORK_CREATE
	private SonarTextField name;
	private SonarTextField r, g, b;
	private int currentColour = 0;
	private AccessType currentAccess = AccessType.PRIVATE;
	private boolean previewSelected = true;
	private boolean showFullPreview = true;
	public static int listSize = 10;
	public boolean disabledState = false;

	// PLAYERS
	private PlayerAccess playerAccess = PlayerAccess.USER;
	private FluxPlayer selectedPlayer = null;

	public GuiFlux(Container container, TileEntityFlux tile, EntityPlayer player) {
		super(container, tile);
		this.player = player;
	}

	public void onGuiClosed() {
		super.onGuiClosed();
		state = GuiState.INDEX;

	}

	@Override
	public void initGui() {
		common = FluxNetworks.cache.getNetwork(getNetworkID());
		selectedPlayer = null;
		Keyboard.enableRepeatEvents(true);
		this.xSize = state.x;
		this.ySize = state.y;
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scrollerLeft = this.guiLeft + 165;
		scrollerStart = this.guiTop + 8;
		scrollerEnd = scrollerStart + 123;
		scrollerWidth = 10;
		disabledState = false;

		this.buttonList.add(new NavigationButtons(GuiState.INDEX, -6, guiLeft + 2, guiTop - 15, 0, "network.nav.home"));
		this.buttonList.add(new NavigationButtons(GuiState.NETWORK_SELECT, -5, guiLeft + 20, guiTop - 15, 128, "network.nav.networks"));
		this.buttonList.add(new NavigationButtons(GuiState.CONNECTIONS, -4, guiLeft + 38, guiTop - 15, 64, "network.nav.config"));
		this.buttonList.add(new NavigationButtons(GuiState.NETWORK_STATS, -3, guiLeft + 56, guiTop - 15, 192, "network.nav.statistics"));
		this.buttonList.add(new NavigationButtons(GuiState.NETWORK_EDIT, -2, guiLeft + 74, guiTop - 15, 256, "network.edit"));
		this.buttonList.add(new NavigationButtons(GuiState.PLAYERS, -1, guiLeft + 92, guiTop - 15, 386, "network.players"));
		this.buttonList.add(new NavigationButtons(GuiState.NETWORK_CREATE, 0, guiLeft + 110, guiTop - 15, 320, "network.create"));

		int networkColour = common.getNetworkColour().getRGB();
		switch (state) {

		case INDEX:
			priority = new SonarTextField(0, this.fontRendererObj, 50, 46, 30, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
			priority.setMaxStringLength(3);
			priority.setText(String.valueOf(tile.getCurrentPriority()));

			limit = new SonarTextField(1, this.fontRendererObj, 110, 46, 58, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
			limit.setMaxStringLength(8);
			limit.setText(String.valueOf(tile.getTransferLimit()));

			fluxName = new SonarTextField(1, this.fontRendererObj, 38, 28, 130, 12).setBoxOutlineColour(networkColour);
			fluxName.setMaxStringLength(24);
			fluxName.setText(tile.getCustomName());
			// this.buttonList.add(new GuiButton(5, guiLeft + 5, guiTop + 140,
			// 80, 20, "Configure"));
			break;

		case NETWORK_SELECT:
			for (int i = 0; i < listSize; i++) {
				this.buttonList.add(new NetworkButton(10 + i, guiLeft + 7, guiTop + 8 + (i * 12)));
			}
			break;

		case NETWORK_CREATE:
			initEditFields(player.getName() + "'s" + " Network", colours[currentColour]);

			this.buttonList.add(new GuiButton(5, guiLeft + 5, guiTop + 140, 80, 20, "Reset"));
			this.buttonList.add(new GuiButton(6, guiLeft + 90, guiTop + 140, 80, 20, "Create"));
			break;

		case NETWORK_EDIT:
			if (!common.isFakeNetwork()) {
				initEditFields(common.getNetworkName(), common.getNetworkColour());

				this.buttonList.add(new GuiButton(5, guiLeft + 5, guiTop + 140, 80, 20, "Reset"));
				this.buttonList.add(new GuiButton(6, guiLeft + 90, guiTop + 140, 80, 20, "Save Changes"));
				this.currentAccess = common.getAccessType();
			} else {
				disabledState = true;
			}
			break;

		case CONNECTIONS:
			break;
		case NETWORK_STATS:
			if (common.isFakeNetwork()) {
				disabledState = true;
			}
			break;
		case PLAYERS:
			this.buttonList.add(new SelectButtons(1, guiLeft + 150, guiTop + 138, 136, "Add"));
			name = new SonarTextField(1, this.fontRendererObj, 14, 138, 130, 12).setBoxOutlineColour(networkColour);
			name.setMaxStringLength(24);
			name.setText("");
			if (!common.isFakeNetwork() && common.getAccessType() == AccessType.PRIVATE) {
				disabledState = true;
			}
			break;
		default:
			break;

		}
	}

	public void initEditFields(String networkName, CustomColour colour) {
		name = new SonarTextField(1, this.fontRendererObj, 38, 22, 130, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB());
		name.setMaxStringLength(24);
		name.setText(networkName);

		r = new SonarTextField(2, this.fontRendererObj, 56, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		r.setMaxStringLength(3);
		r.setText("" + colour.red);

		g = new SonarTextField(3, this.fontRendererObj, 96, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		g.setMaxStringLength(3);
		g.setText("" + colour.green);

		b = new SonarTextField(4, this.fontRendererObj, 136, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
		b.setMaxStringLength(3);
		b.setText("" + colour.blue);
	}

	public SonarTextField[] getFields() {
		switch (state) {
		case NETWORK_CREATE:
		case NETWORK_EDIT:
			return new SonarTextField[] { name, r, g, b };
		case INDEX:
			return new SonarTextField[] { priority, limit, fluxName };
		case PLAYERS:
			return new SonarTextField[] { name };
		default:
			break;
		}
		return new SonarTextField[0];
	}

	public int getSelectionSize() {
		switch (state) {
		case NETWORK_SELECT:
			return this.getNetworks().size();
		case CONNECTIONS:
			if (common == null) {
				return 0;
			}
			return common.getClientFluxConnection().size();
		case PLAYERS:
			return common.getPlayers().size();
		default:
			return 0;
		}
	}

	public int getNetworkPosition() {
		if (common.getNetworkName() == null) {
			return -1;
		}
		List<? extends IFluxCommon> networks = this.getNetworks();
		if (networks.isEmpty()) {
			return -1;
		}
		int start = (int) (networks.size() * this.currentScroll);
		int finish = Math.min(start + listSize, networks.size());
		for (int i = start; i < finish; i++) {
			if (networks.get(i) != null && isSelectedNetwork(networks.get(i))) {
				return i - start;
			}
		}
		return -1;
	}

	public boolean isSelectedNetwork(IFluxCommon network) {
		return network.getNetworkName().equals(common.getNetworkName()) && network.getNetworkID() == getNetworkID() && network.getCachedPlayerName().equals(common.getCachedPlayerName());
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (!disabledState) {
			for (SonarTextField field : this.getFields()) {
				field.drawTextBox();
			}
		}
		common = FluxNetworks.cache.getNetwork(getNetworkID());
		int networkColour = common.getNetworkColour().getRGB();

		switch (state) {
		case INDEX:
			FontHelper.text(FontHelper.translate("network.name") + ": ", 8, 30, networkColour);
			FontHelper.text(FontHelper.translate("point.priority") + ":", 6, 48, networkColour);
			FontHelper.text(FontHelper.translate("point.max") + ":", 84, 48, networkColour);
			FontHelper.text(FontHelper.translate(tile.getBlockType().getLocalizedName()), 20, 8, 0);

			renderNetwork(common, true, 11, 8);
			break;
		case NETWORK_SELECT:
			List<? extends IFluxCommon> networks = this.getNetworks();
			if (networks.isEmpty()) {
				renderNavigationPrompt("No available networks", "Create a New Network");
				break;
			}
			int start = (int) (networks.size() * this.currentScroll);
			int finish = Math.min(start + listSize, networks.size());
			for (int i = start; i < finish; i++) {
				if (networks.get(i) != null) {
					int xPos = 11;
					int yPos = 8 + (12 * i) - (12 * start);
					renderNetwork(networks.get(i), isSelectedNetwork(networks.get(i)), xPos, yPos);
				}
			}
			Minecraft.getMinecraft().getTextureManager().bindTexture(state.getBackground());
			break;
		case NETWORK_CREATE:
		case NETWORK_EDIT:
			if (this.disabledState) {
				renderNavigationPrompt("No network to edit", "Network Selection");
				break;
			} else {
				GL11.glPushMatrix();
				if (state == GuiState.NETWORK_CREATE)
					FontHelper.textCentre(FontHelper.translate("network.create"), xSize, 8, Color.GRAY.getRGB());
				else
					FontHelper.textCentre(FontHelper.translate("network.edit"), xSize, 8, Color.GRAY.getRGB());
				FontHelper.text(FontHelper.translate("network.name") + ": ", 8, 24, 0);
				FontHelper.text(FontHelper.translate("network.colour") + ": ", 8, 80, 0);
				FontHelper.text(TextFormatting.RED + FontHelper.translate("R:"), 46, 80, -1);
				FontHelper.text(TextFormatting.GREEN + FontHelper.translate("G:"), 86, 80, -1);
				FontHelper.text(TextFormatting.BLUE + FontHelper.translate("B:"), 126, 80, -1);
				int red = Integer.valueOf(r.getText().isEmpty() ? "0" : r.getText());
				int green = Integer.valueOf(g.getText().isEmpty() ? "0" : g.getText());
				int blue = Integer.valueOf(b.getText().isEmpty() ? "0" : b.getText());
				drawRect(55, 63 + 32, 165, 68 + 32 + 4, FontHelper.getIntFromColor(red, green, blue));
				FontHelper.text(FontHelper.translate("network.accessSetting") + ": " + TextFormatting.AQUA + FontHelper.translate(currentAccess.getName()), 8, 40, 0);
				FontHelper.text(FontHelper.translate("Preview") + ": ", 8, 96, 0);
				BasicFluxNetwork net = new BasicFluxNetwork(0, player.getGameProfile().getId(), name.getText().isEmpty() ? "Network Name" : name.getText(), new CustomColour(red, green, blue), currentAccess);
				if (this.showFullPreview) {
					this.renderNetworkInFull(net, previewSelected, 11, 110);
				} else {
					this.renderNetwork(net, previewSelected, 11, 110);
				}

				if (x - guiLeft > 55 && x - guiLeft < 165 && y - guiTop > 63 + 32 && y - guiTop < 68 + 32 + 4) {
					drawCreativeTabHoveringText(FontHelper.translate("network.nextColour"), x - guiLeft, y - guiTop);
				}
				if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 38 && y - guiTop < 52) {
					drawCreativeTabHoveringText(FontHelper.translate("network.changeSetting"), x - guiLeft, y - guiTop);
				}
				GL11.glPopMatrix();
			}
			break;
		case CONNECTIONS:
			if (common.isFakeNetwork()) {
				renderNavigationPrompt("No Connections Available", "Network Selection");
				break;
			}
			ArrayList<ClientFlux> connections = common.getClientFluxConnection();
			start = (int) (connections.size() * this.currentScroll);
			finish = Math.min(start + listSize, connections.size());
			ClientFlux selected = null;
			for (int i = start; i < finish; i++) {
				ClientFlux flux = connections.get(i);
				if (flux != null) {
					int posX = 11;
					int posY = 8 + (12 * i) - (12 * start);
					if (x > guiLeft + posX && x < guiLeft + posX + 154 && y >= guiTop + posY && y < guiTop + posY + 12) {
						selected = flux;
						renderFlux(flux, true, posX, posY);
					} else {
						renderFlux(flux, false, posX, posY);
					}
				}
			}
			Minecraft.getMinecraft().getTextureManager().bindTexture(state.getBackground());
			if (selected != null) {
				ArrayList<String> strings = new ArrayList<String>();
				strings.add(FontHelper.translate("flux.type") + ": " + TextFormatting.AQUA + selected.getConnectionType().toString());
				strings.add(TextFormatting.GRAY + selected.getCoords().toString());
				strings.add(FontHelper.translate("point.max") + ": " + TextFormatting.AQUA + selected.getTransferLimit());
				strings.add(FontHelper.translate("point.priority") + ": " + TextFormatting.AQUA + selected.getCurrentPriority());
				this.drawHoveringText(strings, x - guiLeft, y - guiTop);
			}

			break;
		case NETWORK_STATS:
			if (disabledState) {
				renderNavigationPrompt("No Statistics Available", "Network Selection");
				break;
			} else {
				renderNetwork(common, true, 11, 8);
				int colour = common.getNetworkColour().getRGB();
				INetworkStatistics stats = common.getStatistics();
				FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("plug.plugs") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.getPlugCount(), 12, 26, colour);
				FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("plug.points") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.getPointCount(), 12, 26 + 12 * 1, colour);

				EnergyStats energyStats = stats.getLatestStats();
				FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.energy.maxSent") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(energyStats.maxSent), 12, 26 + 12 * 2, colour);
				FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.energy.maxReceived") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(energyStats.maxReceived), 12, 26 + 12 * 3, colour);
				FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.energy.transfer") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(energyStats.transfer), 12, 26 + 12 * 4, colour);

				renderEnergyBar(14, 120, common.getEnergyAvailable(), common.getMaxEnergyStored(), colour, colour);
				FontHelper.text("Network Buffer ", 14, 110, colour);

			}
			break;
		case PLAYERS:
			if (common.isFakeNetwork()) {
				renderNavigationPrompt("No players can be added", "Network Selection");
				break;
			} else if (disabledState) {
				renderNavigationPrompt("Unavailable in Private Mode", "Edit Network");
				break;
			} else {
				ArrayList<FluxPlayer> players = common.getPlayers();
				FluxPlayer currentPlayer = null;
				if (!players.isEmpty()) {
					start = (int) (players.size() * this.currentScroll);
					finish = Math.min(start + listSize, players.size());
					for (int i = start; i < finish; i++) {
						if (players.get(i) != null) {
							int xPos = 11;
							int yPos = 8 + (12 * i) - (12 * start);

							drawRect(xPos, yPos, xPos + 154, yPos + 12, midBlue);

							Minecraft.getMinecraft().getTextureManager().bindTexture(GuiState.NETWORK_SELECT.getBackground());
							drawTexturedModalRect(xPos, yPos, 0,
									/* isSelected ? 178 : 166 */166, 154, 12);
							FontHelper.text(players.get(i).getCachedName(), xPos + 3, yPos + 2, Color.white.getRGB());
							mc.getTextureManager().bindTexture(buttons);
							this.drawTexturedModalRect(xPos + 154 - 12, yPos, 112 / 2, 0, 10 + 1, 10 + 1);

							if (x > guiLeft + xPos && x < guiLeft + xPos + 154 && y >= guiTop + yPos && y < guiTop + yPos + 12) {
								currentPlayer = players.get(i);
							}
						}
					}
				}
				selectedPlayer = currentPlayer;
				if (selectedPlayer != null) {
					List<String> strings = new ArrayList();
					if (x > guiLeft + 11 + 142 && x < guiLeft + 11 + 153) {
						strings.add(TextFormatting.RED + "Delete: " + selectedPlayer.getCachedName());
					} else {
						strings.add(TextFormatting.AQUA + "Config: " + FontHelper.translate(selectedPlayer.access.getName()));
						strings.add("Right click to change");
					}
					this.drawHoveringText(strings, x - guiLeft, y - guiTop);
				}
				Minecraft.getMinecraft().getTextureManager().bindTexture(state.getBackground());
			}
			break;

		default:
			break;
		}
		drawError(x - guiLeft, y - guiTop);
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		if (state.hasScrollBar()) {
			float lastScroll = currentScroll;
			int i = Mouse.getEventDWheel();

			if (i != 0 && this.needsScrollBars()) {
				int j = getSelectionSize() + 1;
				if (i > 0) {
					i = 1;
				}
				if (i < 0) {
					i = -1;
				}
				this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);
				if (this.currentScroll < 0.0F) {
					this.currentScroll = 0.0F;
				}
				if (this.currentScroll > 1.0F) {
					this.currentScroll = 1.0F;
				}
			}
		}
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		if (state.hasScrollBar()) {
			float lastScroll = currentScroll;
			boolean flag = Mouse.isButtonDown(0);

			if (!this.wasClicking && flag && x >= scrollerLeft && y >= scrollerStart && x < scrollerLeft + scrollerWidth && y < scrollerEnd) {
				this.isScrolling = this.needsScrollBars();
			}
			if (!flag) {
				this.isScrolling = false;
			}
			this.wasClicking = flag;

			if (this.isScrolling) {
				this.currentScroll = ((float) (y - scrollerStart) - 7.5F) / ((float) (scrollerEnd - scrollerStart) - 15.0F);
				if (this.currentScroll < 0.0F) {
					this.currentScroll = 0.0F;
				}
				if (this.currentScroll > 1.0F) {
					this.currentScroll = 1.0F;
				}
			}
			if (state == GuiState.NETWORK_SELECT) {
				for (GuiButton button : this.buttonList) {
					if (button instanceof NetworkButton) {
						if (button.isMouseOver()) {
							List<? extends IFluxCommon> networks = getNetworks();
							int start = (int) (networks.size() * this.currentScroll);
							int id = button.id - 10 + start;
							if (id < networks.size()) {
								IFluxCommon network = networks.get(id);
								ArrayList<String> strings = new ArrayList<String>();
								strings.add((FontHelper.translate("network.owner") + ": " + TextFormatting.AQUA + network.getCachedPlayerName()));
								strings.add((FontHelper.translate("network.accessSetting") + ": " + TextFormatting.AQUA + FontHelper.translate(network.getAccessType().getName())));
								this.drawHoveringText(strings, x, y);
							}
						}
					}
				}
			}
		}
	}

	protected void actionPerformed(GuiButton button) {
		if (changed == 1) {
			changed = 0;
			return;
		}
		if (button == null) {
			return;
		}
		if (button instanceof NavigationButtons) {
			switchState(((NavigationButtons) button).buttonState);
			this.reset();
			return;
		}
		if (this.disabledState) {
			return;
		}
		switch (state) {
		case INDEX:
			if (button.id == 0) {
				this.currentScroll = 0;
				switchState(GuiState.NETWORK_SELECT);
				if (!(tile instanceof IFluxController)) {
					// this.changed = 1;
				}
				this.reset();
				return;
			}
			if (button.id == 5) {
				switchState(GuiState.CONNECTIONS);
				SonarCore.sendPacketToServer(tile, 4);
				return;
			}
			break;
		case NETWORK_SELECT:
			if (button.id >= 10) {
				if (this.getNetworks() != null) {
					int start = (int) (this.getNetworks().size() * this.currentScroll);
					int network = start + button.id - 10;
					if (network < this.getNetworks().size()) {
						setNetwork(this.getNetworks().get(network));
					}
				}
			}
			break;
		case NETWORK_CREATE:
		case NETWORK_EDIT:
			switch (button.id) {
			case 5:
				resetCreateTab();
				break;
			case 6:
				if (!name.getText().isEmpty()) {
					if (state == GuiState.NETWORK_CREATE) {
						FluxNetworks.network.sendToServer(new PacketFluxButton(Type.CREATE_NETWORK, tile.getPos(), name.getText(), new CustomColour(r.getIntegerFromText(), g.getIntegerFromText(), b.getIntegerFromText()), currentAccess));
					} else {
						FluxNetworks.network.sendToServer(new PacketFluxButton(Type.EDIT_NETWORK, tile.getPos(), getNetworkID(), name.getText(), new CustomColour(r.getIntegerFromText(), g.getIntegerFromText(), b.getIntegerFromText()), currentAccess));
					}
					changed = 0;
					this.switchState(GuiState.NETWORK_SELECT);
					resetCreateTab();
					return;
				}
				break;
			}
			break;
		case PLAYERS:
			switch (button.id) {
			case 1:
				if (!name.getText().isEmpty()) {
					FluxNetworks.network.sendToServer(new PacketFluxButton(Type.ADD_PLAYER, tile.getPos(), getNetworkID(), name.getText(), PlayerAccess.USER));
					return;
				}
				break;
			}
		default:
			break;
		}
		// this.reset();
	}

	private void resetCreateTab() {
		name.setText("");
		currentColour = 0;
		currentAccess = AccessType.PRIVATE;
		reset();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (this.disabledState) {
			return;
		}
		for (SonarTextField field : getFields()) {
			field.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
		}
		switch (state) {
		case INDEX:
			if (mouseX - guiLeft > 5 && mouseX - guiLeft < 165 && mouseY - guiTop > 10 && mouseY - guiTop < 20) {
				this.currentScroll = 0;
				switchState(GuiState.NETWORK_SELECT);
				return;
			}
			break;
		case NETWORK_CREATE:
		case NETWORK_EDIT:
			if (mouseButton == 1) {
				name.setText("");
			}
			if (mouseX - guiLeft > 55 && mouseX - guiLeft < 165 && mouseY - guiTop > 63 + 32 && mouseY - guiTop < 68 + 32 + 4) {
				currentColour++;
				if (currentColour >= colours.length) {
					currentColour = 0;
				}
				CustomColour colour = colours[currentColour];
				r.setText("" + colour.red);
				g.setText("" + colour.green);
				b.setText("" + colour.blue);
			}
			if (mouseX - guiLeft > 5 && mouseX - guiLeft < 165 && mouseY - guiTop > 38 && mouseY - guiTop < 52) {
				currentAccess = AccessType.values()[currentAccess.ordinal() + 1 < AccessType.values().length ? currentAccess.ordinal() + 1 : 0];
			}
			if (mouseX - guiLeft > 11 && mouseX - guiLeft < 165 && mouseY - guiTop > 108 && mouseY - guiTop < 134) {
				this.showFullPreview = !showFullPreview;
			}
			break;
		case NETWORK_SELECT:
			break;
		case PLAYERS:
			FluxPlayer player = selectedPlayer;
			if (player != null && mouseX - guiLeft > 11 + 142 && mouseX - guiLeft < 11 + 153) {
				FluxNetworks.network.sendToServer(new PacketFluxButton(Type.REMOVE_PLAYER, tile.getPos(), getNetworkID(), player.id, player.access));
			} else if (mouseButton == 1) {
				FluxNetworks.network.sendToServer(new PacketFluxButton(Type.CHANGE_PLAYER, tile.getPos(), getNetworkID(), player.id, player.access.incrementAccess()));

			}
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(char c, int i) throws IOException {
		if (this.disabledState) {
			super.keyTyped(c, i);
			return;
		}
		for (SonarTextField field : getFields()) {
			if (field != null && field.isFocused()) {
				if (c == 13 || c == 27) {
					field.setFocused(false);
				} else {
					field.textboxKeyTyped(c, i);
					if (field == priority) {
						tile.priority.setObject(priority.getIntegerFromText());
						SonarCore.sendPacketToServer(tile, 1);
					} else if (field == limit) {
						tile.limit.setObject(limit.getLongFromText());
						SonarCore.sendPacketToServer(tile, 2);
					} else if (field == fluxName) {
						tile.customName.setObject(fluxName.getText());
						SonarCore.sendPacketToServer(tile, 3);
					}
				}
				return;
			}
		}
		if (state == GuiState.NETWORK_CREATE || state == GuiState.NETWORK_EDIT) {
			if (i == 1) {
				switchState(GuiState.INDEX);
				return;
			}
		} else {
			if (state == GuiState.NETWORK_SELECT && (c == '\b')) {
				FluxNetworks.network.sendToServer(new PacketFluxButton(Type.DELETE_NETWORK, tile.getPos(), getNetworkID()));
			}
			if (state != GuiState.INDEX && i == 1) {
				switchState(GuiState.INDEX);
				return;
			}
			super.keyTyped(c, i);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		if (state.hasScrollBar()) {
			drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176, 0, 10, 15);
		}
	}

	private boolean needsScrollBars() {
		if (state.hasScrollBar()) {
			return true;
		}
		if (getNetworks() == null)
			return false;
		if (getNetworks().size() <= 4)
			return false;
		return true;
	}
}
