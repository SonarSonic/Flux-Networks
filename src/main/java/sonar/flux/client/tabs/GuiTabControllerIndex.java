package sonar.flux.client.tabs;

import java.io.IOException;
import java.util.List;

import net.minecraft.util.text.TextFormatting;
import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileController;

public class GuiTabControllerIndex extends GuiTabConnectionIndex<TileController, Object> {

	public GuiTabControllerIndex(TileController tile, List<GuiTab> tabs) {
		super(tile, tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		int colour = common.getNetworkColour().getRGB();
		IFluxController controller = flux;
		FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.SEND_MODE.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + controller.getSendMode().getDisplayName(), 8, 66 + 18, colour);
		FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.RECEIVE_MODE.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + controller.getReceiveMode().getDisplayName(), 8, 86 + 18, colour);
		FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.TRANSFER_MODE.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + controller.getTransferMode().getDisplayName() + (controller.getTransferMode().isBanned() ? TextFormatting.RED + " " + FluxTranslate.BANNED.t() : ""), 8, 106 + 18, colour);
		FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.TRANSMITTER_MODE.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + controller.getTransmitterMode().getDisplayName(), 8, 126 + 18, colour);
	}

	@Override
	public void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 66 + 18 && y - guiTop < 80 + 18) {
			flux.sendMode.setObject(IFluxController.PriorityMode.values()[flux.getSendMode().ordinal() + 1 < IFluxController.PriorityMode.values().length ? flux.getSendMode().ordinal() + 1 : 0]);
			SonarCore.sendPacketToServer(flux, 10);
		} else if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 86 + 18 && y - guiTop < 100 + 18) {
			flux.receiveMode.setObject(IFluxController.PriorityMode.values()[flux.getReceiveMode().ordinal() + 1 < IFluxController.PriorityMode.values().length ? flux.getReceiveMode().ordinal() + 1 : 0]);
			SonarCore.sendPacketToServer(flux, 11);
		} else if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 106 + 18 && y - guiTop < 120 + 18) {
			flux.transfer.setObject(IFluxController.TransferMode.values()[flux.getTransferMode().ordinal() + 1 < IFluxController.TransferMode.values().length ? flux.getTransferMode().ordinal() + 1 : 0]);
			SonarCore.sendPacketToServer(flux, 12);
		} else if (x - guiLeft > 5 && x - guiLeft < 165 && y - guiTop > 126 + 18 && y - guiTop < 140 + 18) {
			flux.transmitter.setObject(IFluxController.TransmitterMode.values()[flux.getTransmitterMode().ordinal() + 1 < IFluxController.TransmitterMode.values().length ? flux.getTransmitterMode().ordinal() + 1 : 0]);
			SonarCore.sendPacketToServer(flux, 13);
		}
	}

}
