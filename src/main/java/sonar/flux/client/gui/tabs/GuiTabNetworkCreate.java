package sonar.flux.client.gui.tabs;

import sonar.flux.client.gui.EnumGuiTab;

import java.util.List;

public class GuiTabNetworkCreate extends GuiTabNetworkEdit {

	public GuiTabNetworkCreate(List<EnumGuiTab> tabs) {
		super(tabs);
	}

	@Override
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.NETWORK_CREATE;
	}

}