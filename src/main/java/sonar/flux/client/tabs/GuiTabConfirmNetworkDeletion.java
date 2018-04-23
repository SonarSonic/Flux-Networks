package sonar.flux.client.tabs;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GuiTab;
import sonar.flux.client.LargeButton;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiTabConfirmNetworkDeletion extends AbstractGuiTab<TileFlux> {

	public IFluxCommon toDelete;

	public GuiTabConfirmNetworkDeletion(TileFlux tile, IFluxCommon toDelete, GuiTabNetworkSelection origin, List<GuiTab> tabs) {
		super(tile, tabs);
		this.setOrigin(origin);
		this.toDelete = toDelete;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(new LargeButton(this, FluxTranslate.CANCEL.t(), 0, getGuiLeft() + 60, getGuiTop() + 80, 68, 0));
		buttonList.add(new LargeButton(this, FluxTranslate.NETWORK_DELETE.t(), 1, getGuiLeft() + 100, getGuiTop() + 80, 51, 0));
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException{
		super.actionPerformed(button);
		switch(button.id){
		case 0:
			FMLCommonHandler.instance().showGuiScreen(origin);
			return;
		case 1:
			PacketHelper.sendPacketToServer(PacketType.DELETE_NETWORK, flux, PacketHelper.createNetworkDeletePacket(toDelete.getNetworkID()));
			FMLCommonHandler.instance().showGuiScreen(origin);
			return;
		}
	}
	

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		renderNetwork(toDelete.getNetworkName(), toDelete.getAccessType(), toDelete.getNetworkColour().getRGB(), true, 11, 60);
		FontHelper.textCentre(FluxTranslate.NETWORK_CONFIRM_DELETE.t(), xSize, 40, toDelete.getNetworkColour().getRGB());
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_SELECTION;
	}

	@Override
	public ResourceLocation getBackground() {
		return scroller_flux_gui;
	}

}
