package sonar.flux.client.tabs;

import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import sonar.core.helpers.FontHelper;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.transfer.stats.NetworkStatistics;

public class GuiTabNetworkStatistics extends AbstractGuiTab {

	public GuiTabNetworkStatistics(TileFlux tile, List tabs) {
		super(tile, tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (common.isFakeNetwork()) {
			renderNavigationPrompt("No Statistics Available", "Network Selection");
		} else {
			renderNetwork(common.getNetworkName(), common.getAccessType(), common.getNetworkColour().getRGB(), true, 11, 8);
			NetworkStatistics stats = common.getStatistics();
			int rgb = common.getNetworkColour().getRGB();
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.PLUGS.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_plug_count, 12, 26, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.POINTS.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_point_count, 12, 26 + 12, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.STORAGE.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_storage_count, 12, 26 + 12 * 2, rgb);

			FontHelper.text(TextFormatting.DARK_GRAY + GUI.TOTAL_INPUT.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(stats.total_energy_added), 12, 26 + 12 * 3, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.TOTAL_OUTPUT.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(stats.total_energy_removed), 12, 26 + 12 * 4, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.TOTAL_STORAGE_CHANGE.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(stats.network_energy_change - stats.total_energy_removed), 12, 26 + 12 * 5, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + GUI.TOTAL_NETWORK_TRANSFER.toString() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(stats.total_energy_added - stats.total_energy_removed), 12, 26 + 12 * 6, rgb);

			FontHelper.text("Network Buffer ", 14, 120, rgb);
			renderEnergyBar(14, 130, stats.network_energy, stats.network_energy_capacity, rgb, rgb);
		}
	}

	@Override
	public GuiTab getCurrentTab() {
		return GuiTab.NETWORK_STATS;
	}

	@Override
	public ResourceLocation getBackground() {
		return blank_flux_gui;
	}

}
