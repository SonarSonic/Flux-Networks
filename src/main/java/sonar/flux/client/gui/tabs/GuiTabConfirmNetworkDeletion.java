package sonar.flux.client.gui.tabs;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.GuiAbstractTab;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.client.gui.buttons.LargeButton;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

import java.io.IOException;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabConfirmNetworkDeletion extends GuiAbstractTab<TileFlux> {

	public IFluxNetwork toDelete;

	public GuiTabConfirmNetworkDeletion(TileFlux tile, IFluxNetwork toDelete, GuiTabNetworkSelection origin, List<GuiTab> tabs) {
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
			PacketHelper.sendPacketToServer(PacketType.DELETE_NETWORK, flux, PacketHelper.createNetworkDeletePacket(NETWORK_ID.getValue(toDelete)));
			FMLCommonHandler.instance().showGuiScreen(origin);
			return;
		}
	}
	

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		renderNetwork(NETWORK_NAME.getValue(toDelete), NETWORK_ACCESS.getValue(toDelete), NETWORK_COLOUR.getValue(toDelete).getRGB(), true, 11, 60);
		FontHelper.textCentre(FluxTranslate.NETWORK_CONFIRM_DELETE.t(), xSize, 40, NETWORK_COLOUR.getValue(toDelete).getRGB());
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
