package sonar.flux.client;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.IFluxController;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityController;

public class GuiFluxController extends GuiFlux {
	// public static final ResourceLocation bground = new ResourceLocation("FluxNetworks:textures/gui/fluxPlug.png");

	public TileEntityController entity;

	public GuiFluxController(EntityPlayer player, TileEntityController entity) {
		super(new ContainerFlux(player, entity, false), entity, player);
		this.entity = entity;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (state == GuiState.INDEX) {

			int colour = common.getNetworkColour().getRGB();
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.sendMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(entity.getSendMode().getName()), 8, 66 + 18, colour);
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.receiveMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(entity.getReceiveMode().getName()), 8, 86 + 18, colour);
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.transferMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(entity.getTransferMode().getName()) + (entity.getTransferMode().isBanned() ? TextFormatting.RED + " BANNED" : ""), 8, 106 + 18, colour);
			FontHelper.text(TextFormatting.DARK_GRAY + FontHelper.translate("network.transmitterMode") + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.translate(entity.getTransmitterMode().getName()), 8, 126 + 18, colour);

			/* FontHelper.text(FontHelper.translate("network.sendMode") + ": " + TextFormatting.AQUA + FontHelper.translate(entity.getSendMode().getName()), 8, 66, 0); FontHelper.text(FontHelper.translate("network.receiveMode") + ": " + TextFormatting.AQUA + FontHelper.translate(entity.getReceiveMode().getName()), 8, 86, 0); FontHelper.text(FontHelper.translate("network.transferMode") + ": " + TextFormatting.AQUA + FontHelper.translate(entity.getTransferMode().getName()), 8, 106, 0); FontHelper.text(FontHelper.translate("network.transmitterMode") + ": " + TextFormatting.AQUA + FontHelper.translate(entity.getTransmitterMode().getName()), 8, 126, 0); */
		}
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
