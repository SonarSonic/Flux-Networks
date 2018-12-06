package sonar.flux.client.gui.tabs;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.buttons.LargeButton;
import sonar.flux.network.PacketGeneralHelper;
import sonar.flux.network.PacketGeneralType;

import java.io.IOException;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabConfirmNetworkDeletion extends GuiTabAbstract {

	public IFluxNetwork toDelete;

	public GuiTabConfirmNetworkDeletion(GuiTabNetworkSelection origin, List<EnumGuiTab> tabs, IFluxNetwork toDelete) {
		super(tabs);
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
			PacketGeneralHelper.sendPacketToServer(PacketGeneralType.DELETE_NETWORK, PacketGeneralHelper.createNetworkDeletePacket(NETWORK_ID.getValue(toDelete)));
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
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.NETWORK_SELECTION;
	}

	@Override
	public ResourceLocation getBackground() {
		return scroller_flux_gui;
	}

}
