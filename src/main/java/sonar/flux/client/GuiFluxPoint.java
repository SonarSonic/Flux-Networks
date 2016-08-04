package sonar.flux.client;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityPoint;

public class GuiFluxPoint extends GuiFlux {
	public static final ResourceLocation bground = new ResourceLocation("FluxNetworks:textures/gui/fluxPoint.png");

	public TileEntityPoint entity;

	private GuiTextField priority, transfer;

	public GuiFluxPoint(EntityPlayer player, TileEntityPoint entity) {
		super(new ContainerFlux(player, entity, false), entity, player);
		this.entity = entity;
	}

	/*
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		/*
		if (!(state == GuiState.INDEX)) {
			priority.drawTextBox();
			transfer.drawTextBox();
			FontHelper.text(FontHelper.translate(entity.getBlockType().getLocalizedName()), 20, 8, 0);
			FontHelper.text(FontHelper.translate("point.priority") + ":", 6, 28, 0);
			FontHelper.text(FontHelper.translate("point.max") + ":", 84, 28, 0);
			if (entity.networkName.getObject().equals("NETWORK") || entity.networkName.getObject().isEmpty()) {
				FontHelper.textCentre(FontHelper.translate("network.notConnected"), xSize, 47, 0);
			} else {
				FontHelper.textCentre(entity.networkName + ": " + getNetworkType(AccessType.PRIVATE), xSize, 47, 0);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		/*
		if (!(state == GuiState.INDEX)) {
			priority = new GuiTextField(0, this.fontRendererObj, 50, 26, 30, 12);
			priority.setMaxStringLength(3);
			priority.setText(String.valueOf(entity.priority.getObject()));

			transfer = new GuiTextField(1, this.fontRendererObj, 110, 26, 58, 12);
			transfer.setMaxStringLength(8);
			transfer.setText(String.valueOf(entity.limit.getObject()));
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (!(state == GuiState.INDEX)) {
			priority.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			transfer.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
		}
	}

	@Override
	public void keyTyped(char c, int i) {
		if (state == GuiState.INDEX) {
			if (priority.isFocused()) {
				if (c == 13 || c == 27) {
					priority.setFocused(false);
				} else {
					FontHelper.addDigitsToString(priority, c, i);
					final String order = priority.getText();
					if (order.isEmpty() || order == "" || order == null) {
						// FluxNetworks.network.sendToServer(new PacketFluxButton(String.valueOf(0), entity.getPos(), 1));
					} else {
						// FluxNetworks.network.sendToServer(new PacketFluxButton(order, entity.getPos(), 1));
					}
					if (order.isEmpty() || order == "" || order == null) {
						entity.priority.setObject(0);
					} else {
						entity.priority.setObject(Integer.valueOf(order));
					}
				}
			} else if (transfer.isFocused()) {
				if (c == 13 || c == 27) {
					transfer.setFocused(false);
				} else {
					FontHelper.addDigitsToString(transfer, c, i);
					final String order = transfer.getText();

					if (order.isEmpty() || order == "" || order == null) {
						// FluxNetworks.network.sendToServer(new PacketFluxButton(String.valueOf(0), entity.getPos(), 2));
					} else {
						// FluxNetworks.network.sendToServer(new PacketFluxButton(order, entity.getPos(), 2));
					}

					if (order.isEmpty() || order == "" || order == null) {
						entity.limit.setObject((long) 0);
					} else {
						entity.limit.setObject(Long.valueOf(order));
					}
				}
			}
		} else {
			super.keyTyped(c, i);
		}
		
	}
		*/
}
