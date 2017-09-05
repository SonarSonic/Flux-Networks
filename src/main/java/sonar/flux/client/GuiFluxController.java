package sonar.flux.client;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.SonarCore;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityController;

import java.io.IOException;

public class GuiFluxController extends GuiFlux {
	// public static final ResourceLocation bground = new ResourceLocation("FluxNetworks:textures/gui/fluxPlug.png");

	public TileEntityController entity;

	public GuiFluxController(EntityPlayer player, TileEntityController entity) {
		super(new ContainerFlux(player, entity, false), entity, player);
		this.entity = entity;
	}
	
	@Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (state == GuiState.INDEX) {
			if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 66 + 18 && y - guiTop < 80 + 18) {
				entity.sendMode.setObject(IFluxController.PriorityMode.values()[entity.getSendMode().ordinal() + 1 < IFluxController.PriorityMode.values().length ? entity.getSendMode().ordinal() + 1 : 0]);
				SonarCore.sendPacketToServer(entity, 10);
			} else if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 86 + 18 && y - guiTop < 100 + 18) {
				entity.receiveMode.setObject(IFluxController.PriorityMode.values()[entity.getReceiveMode().ordinal() + 1 < IFluxController.PriorityMode.values().length ? entity.getReceiveMode().ordinal() + 1 : 0]);
				SonarCore.sendPacketToServer(entity, 11);
			} else if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 106 + 18 && y - guiTop < 120 + 18) {
				entity.transfer.setObject(IFluxController.TransferMode.values()[entity.getTransferMode().ordinal() + 1 < IFluxController.TransferMode.values().length ? entity.getTransferMode().ordinal() + 1 : 0]);
				SonarCore.sendPacketToServer(entity, 12);
			} else if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 126 + 18 && y - guiTop < 140 + 18) {
				entity.transmitter.setObject(IFluxController.TransmitterMode.values()[entity.getTransmitterMode().ordinal() + 1 < IFluxController.TransmitterMode.values().length ? entity.getTransmitterMode().ordinal() + 1 : 0]);
				SonarCore.sendPacketToServer(entity, 13);
			}
		}
	}
}
