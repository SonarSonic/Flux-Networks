package sonar.flux.client.gui.tabs;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.connection.transfer.stats.NetworkStatistics;

import java.util.List;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabNetworkStatistics extends GuiTabAbstract {

	public GuiTabNetworkStatistics(List tabs) {
		super(tabs);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (common.isFakeNetwork()) {
			renderNavigationPrompt(FluxTranslate.ERROR_NO_STATISTICS.t(), FluxTranslate.GUI_TAB_NETWORK_SELECTION.t());
		} else {
			renderNetwork(NETWORK_NAME.getValue(common), NETWORK_ACCESS.getValue(common), NETWORK_COLOUR.getValue(common).getRGB(), true, 11, 8);
			NetworkStatistics stats = NETWORK_STATISTICS.getValue(common);
			int rgb = getNetworkColour();
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.PLUGS.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_plug_count, 12, 26, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.POINTS.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_point_count, 12, 26 + 12, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.STORAGE.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_storage_count, 12, 26 + 12 * 2, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.CONTROLLERS.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + stats.flux_controller_count, 12, 26 + 12 * 3, rgb);

			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.TOTAL_INPUT.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(NETWORK_ENERGY_TYPE.getValue(common), stats.total_energy_added), 12, 26 + 12 * 4, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.TOTAL_OUTPUT.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(NETWORK_ENERGY_TYPE.getValue(common), stats.total_energy_removed), 12, 26 + 12 * 5, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.TOTAL_STORAGE_CHANGE.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(NETWORK_ENERGY_TYPE.getValue(common), stats.network_energy_change), 12, 26 + 12 * 6, rgb);
			FontHelper.text(TextFormatting.DARK_GRAY + FluxTranslate.TOTAL_NETWORK_TRANSFER.t() + TextFormatting.DARK_GRAY + ": " + TextFormatting.RESET + FontHelper.formatOutput(NETWORK_ENERGY_TYPE.getValue(common), stats.total_energy_added - stats.total_energy_removed), 12, 26 + 12 * 7, rgb);

			FontHelper.text(FluxTranslate.NETWORK_BUFFER.t(), 14, 124, rgb);
			renderEnergyBar(14, 134, stats.network_energy, stats.network_energy_capacity, rgb, rgb);
		}
	}

	@Override
	public EnumGuiTab getCurrentTab() {
		return EnumGuiTab.NETWORK_STATISTICS;
	}

	@Override
	public ResourceLocation getBackground() {
		return blank_flux_gui;
	}

}
