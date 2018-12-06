package sonar.flux.client.gui.tabs;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.buttons.LargeButton;
import sonar.flux.network.PacketGeneralHelper;
import sonar.flux.network.PacketGeneralType;

import java.io.IOException;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabNetworkDebug extends GuiTabAbstract {

	public GuiTabNetworkDebug(List tabs) {
		super(tabs);
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new LargeButton(this, FluxTranslate.DEBUG_CONNECTED_BLOCKS.t(), 0, getGuiLeft() + 12, getGuiTop() + 30, 0, 34));
		this.buttonList.add(new LargeButton(this, FluxTranslate.DEBUG_NETWORK_CONNECTIONS.t(), 1, getGuiLeft() + 12, getGuiTop() + 60, 0, 0));
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (common.isFakeNetwork()) {
			renderNavigationPrompt(FluxTranslate.ERROR_NO_CONNECTED_NETWORK.t(), FluxTranslate.GUI_TAB_NETWORK_SELECTION.t());
		} else {
			renderNetwork(NETWORK_NAME.getValue(common), NETWORK_ACCESS.getValue(common), NETWORK_COLOUR.getValue(common).getRGB(), true, 11, 8);
		}
		GlStateManager.scale(0.75, 0.75, 0.75);
		FontHelper.text("Reconnects all blocks attached", (int)(36 * (1 / 0.75)), (int)(32 * (1 / 0.75)), grey);
		FontHelper.text("to flux plugs/points", (int)(36 * (1 / 0.75)), (int)(40 * (1 / 0.75)), grey);

		FontHelper.text("Checks all flux connections and", (int)(36 * (1 / 0.75)), (int)(62 * (1 / 0.75)), grey);
		FontHelper.text("removes duplicates/errored tiles", (int)(36 * (1 / 0.75)), (int)(70 * (1 / 0.75)), grey);
		
		GlStateManager.scale(1 / 0.75, 1 / 0.75, 1 / 0.75);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		switch (button.id) {
		case 0:
			PacketGeneralHelper.sendPacketToServer(PacketGeneralType.DEBUG_CONNECTED_BLOCKS, PacketGeneralHelper.createResetConnectedBlocksPacket(getNetworkID()));
			break;
		case 1:
			PacketGeneralHelper.sendPacketToServer(PacketGeneralType.DEBUG_FLUX_CONNECTIONS, PacketGeneralHelper.createValidateConnectionsPacket(getNetworkID()));
			break;
		}
	}

	@Override
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.DEBUG;
	}

	@Override
	public ResourceLocation getBackground() {
		return blank_flux_gui;
	}

}