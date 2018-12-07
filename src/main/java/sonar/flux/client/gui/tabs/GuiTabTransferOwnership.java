package sonar.flux.client.gui.tabs;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.buttons.FluxTextField;
import sonar.flux.client.gui.buttons.LargeButton;
import sonar.flux.connection.NetworkSettings;
import sonar.flux.network.PacketGeneralHelper;
import sonar.flux.network.PacketGeneralType;

import java.io.IOException;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabTransferOwnership extends GuiTabAbstract {

	public IFluxNetwork toChange;
	public SonarTextField owner;

	public GuiTabTransferOwnership(GuiTabIndexAdmin origin, List<EnumGuiTab> tabs, IFluxNetwork toChange) {
		super(tabs);
		this.setOrigin(origin);
		this.toChange = toChange;
	}

	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new LargeButton(this, FluxTranslate.CANCEL.t(), 0, getGuiLeft() + 60, getGuiTop() + 80, 68, 0));
		buttonList.add(new LargeButton(this, FluxTranslate.TRANSFER_OWNERSHIP.t(), 1, getGuiLeft() + 100, getGuiTop() + 80, 51, 0));

		owner = FluxTextField.create("", 12, getFontRenderer(), 12, 60, 152, 12).setBoxOutlineColour(NETWORK_COLOUR.getValue(toChange).getRGB());
		owner.setMaxStringLength(256);
		owner.setText("" + toChange.getSyncSetting(NetworkSettings.NETWORK_CACHED_NAME).getValue());

		fieldList.add(owner);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException{
		super.actionPerformed(button);
		switch(button.id){
		case 0:
			FMLCommonHandler.instance().showGuiScreen(origin);
			return;
		case 1:
			PacketGeneralHelper.sendPacketToServer(PacketGeneralType.SWITCH_OWNERSHIP, PacketGeneralHelper.createChangeNetworkOwner(NETWORK_ID.getValue(toChange), owner.getText()));
			FMLCommonHandler.instance().showGuiScreen(origin);
			return;
		}
	}
	

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		renderNetwork(NETWORK_NAME.getValue(toChange), NETWORK_ACCESS.getValue(toChange), NETWORK_COLOUR.getValue(toChange).getRGB(), true, 11, 8);
		FontHelper.textCentre(FluxTranslate.TRANSFER_OWNERSHIP_CONFIRM.t(), xSize, 40, NETWORK_COLOUR.getValue(toChange).getRGB());
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
