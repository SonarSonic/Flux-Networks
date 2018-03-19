package sonar.flux.client.states;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiFluxBase;
import sonar.flux.client.GuiFluxBase.NetworkButton;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiStateNetworkSelect extends GuiState {

	public SonarScroller scroller;
	public int lastClickX;
	public static int listSize = 10;
	public int toDelete = -1;

	public GuiStateNetworkSelect() {
		super(GuiTypeMessage.NETWORK_SELECT, 176, 166, 128, "network.nav.networks");
	}

	@Override
	public void draw(GuiFlux flux, int x, int y) {
		List<? extends IFluxCommon> networks = flux.getNetworks();
		if (networks.isEmpty()) {
			flux.renderNavigationPrompt("No available networks", "Create a New Network");
			return;
		}
		int start = (int) (networks.size() * scroller.getCurrentScroll());
		int finish = Math.min(start + listSize, networks.size());
		for (int i = start; i < finish; i++) {
			IFluxCommon common = networks.get(i);
			if (common != null) {
				int xPos = 11;
				int yPos = 8 + 12 * i - 12 * start;
				flux.renderNetwork(common.getNetworkName(), common.getAccessType(), common.getNetworkColour().getRGB(), flux.isSelectedNetwork(networks.get(i)), xPos, yPos);
				flux.bindTexture(GuiFluxBase.buttons);
				flux.drawTexturedModalRect(154, yPos, 56, 0, 12, 12);
			}
		}
		flux.bindTexture(flux.getBackground());

		for (GuiButton button : flux.getButtonList()) {
			if (button instanceof NetworkButton && button.isMouseOver()) {
				start = (int) (networks.size() * scroller.getCurrentScroll());
				int id = button.id - 10 + start;
				if (id < networks.size()) {
					IFluxCommon network = networks.get(id);
					List<String> strings = Lists.newArrayList();
					if (x > flux.getGuiLeft() + 155) {
						if (network.getNetworkID() == toDelete) {
							strings.add(TextFormatting.RED + "ARE YOU SURE?");
							strings.add(TextFormatting.RED + "DELETE NETWORK");
						} else {
							strings.add(TextFormatting.RED + "DELETE NETWORK");
						}
					} else {
						strings.add(FontHelper.translate("network.owner") + ": " + TextFormatting.AQUA + network.getCachedPlayerName());
						strings.add(FontHelper.translate("network.accessSetting") + ": " + TextFormatting.AQUA + FontHelper.translate(network.getAccessType().getName()));
					}
					flux.drawHoveringText(strings, x - flux.getGuiLeft(), y - flux.getGuiTop());
				}
			}
		}

	}

	@Override
	public void init(GuiFlux flux) {
		toDelete=-1;
		scroller = new SonarScroller(flux.getGuiLeft() + 165, flux.getGuiTop() + 8, 123, 10);
		scroller.currentScroll = 0;
		for (int i = 0; i < listSize; i++) {
			flux.getButtonList().add(new NetworkButton(10 + i, flux.getGuiLeft() + 7, flux.getGuiTop() + 8 + i * 12));
		}
	}

	@Override
	public void button(GuiFlux flux, GuiButton button) {
		if (button instanceof NetworkButton && button.id >= 10) {
			List<? extends IFluxCommon> networks = flux.getNetworks();
			int start = (int) (networks.size() * scroller.getCurrentScroll());
			int network = start + button.id - 10;
			if (network < networks.size()) {
				IFluxCommon common = networks.get(network);
				if (lastClickX > flux.getGuiLeft() + 155) {
					int id = common.getNetworkID();
					if (id != -1) {
						if (toDelete == id) {
							PacketHelper.sendPacketToServer(PacketType.DELETE_NETWORK, flux.tile, PacketHelper.createNetworkDeletePacket(common.getNetworkID()));
							toDelete = -1;
						} else {
							toDelete = id;
						}
					}
				} else {
					flux.setNetwork(common);
				}
			}
		}

	}

	public boolean type(GuiFlux flux, char c, int i) {
		if (c == '\b') {
			PacketHelper.sendPacketToServer(PacketType.DELETE_NETWORK, flux.tile, PacketHelper.createNetworkDeletePacket(flux.getNetworkID()));
			return false;
		}
		return true;
	}

	public int getNetworkPosition(GuiFlux flux) {
		if (flux.common.getNetworkName() == null) {
			return -1;
		}
		List<? extends IFluxCommon> networks = flux.getNetworks();
		if (networks.isEmpty()) {
			return -1;
		}
		int start = (int) (networks.size() * scroller.getCurrentScroll());
		int finish = Math.min(start + listSize, networks.size());
		for (int i = start; i < finish; i++) {
			if (networks.get(i) != null && flux.isSelectedNetwork(networks.get(i))) {
				return i - start;
			}
		}
		return -1;
	}

	@Override
	public void click(GuiFlux flux, int x, int y, int mouseButton) {
		lastClickX = x;
	}

	@Override
	public SonarTextField[] getFields(GuiFlux flux) {
		return new SonarTextField[0];
	}

	@Override
	public SonarScroller[] getScrollers() {
		return new SonarScroller[] { scroller };
	}

	@Override
	public int getSelectionSize(GuiFlux flux) {
		return flux.getNetworks().size();
	}

}