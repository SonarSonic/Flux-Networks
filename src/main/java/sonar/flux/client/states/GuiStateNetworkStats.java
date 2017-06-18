package sonar.flux.client.states;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.network.EnergyStats;
import sonar.flux.api.network.INetworkStatistics;
import sonar.flux.api.tiles.IFlux.ConnectionType;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;

public class GuiStateNetworkStats extends GuiState {

	public GuiStateNetworkStats() {
		super(GuiTypeMessage.NETWORK_STATS, 176, 166, 192, "network.nav.statistics");
	}

	@Override
	public void draw(GuiFlux flux, int x, int y) {
		if (flux.disabledState) {
			flux.renderNavigationPrompt("No Statistics Available", "Network Selection");
			return;
		} else {
			flux.renderNetwork(flux.common.getNetworkName(), flux.common.getAccessType(), flux.common.getNetworkColour().getRGB(), true, 11, 8);
			int rgb = flux.common.getNetworkColour().getRGB();
			INetworkStatistics stats = flux.common.getStatistics();
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.PLUGS.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.getConnectionCount(ConnectionType.PLUG), 12, 26, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.POINTS.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.getConnectionCount(ConnectionType.POINT), 12, 26 + 12 * 1, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.STORAGE.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.getConnectionCount(ConnectionType.STORAGE), 12, 26 + 12 * 2, rgb);

			EnergyStats energyStats = stats.getLatestStats();
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.MAX_SENT.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(energyStats.maxSent), 12, 26 + 12 * 3, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.MAX_RECEIVE.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(energyStats.maxReceived), 12, 26 + 12 * 4, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.TRANSFER.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(energyStats.transfer), 12, 26 + 12 * 5, rgb);

			flux.renderEnergyBar(14, 120, flux.common.getEnergyAvailable(), flux.common.getMaxEnergyStored(), rgb, rgb);
			FontHelper.text("Network Buffer ", 14, 110, rgb);

		}
	}

	@Override
	public void init(GuiFlux flux) {
		if (flux.common.isFakeNetwork()) {
			flux.disabledState = true;
		}
	}

	@Override
	public void button(GuiFlux flux, GuiButton button) {}

	@Override
	public void click(GuiFlux flux, int x, int y, int mouseButton) {}

	@Override
	public SonarTextField[] getFields(GuiFlux flux) {
		return new SonarTextField[0];
	}

	@Override
	public int getSelectionSize(GuiFlux flux) {
		return 0;
	}

}
