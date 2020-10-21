package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.LineChart;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.misc.NumberFormatType;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.CNetworkUpdateMessage;
import net.minecraft.util.text.TextFormatting;

public class GuiTabStatistics extends GuiTabCore {

    public InvisibleButton redirectButton;

    private final NetworkStatistics stats = network.getStatistics();
    private LineChart chart;
    private int timer = 0;

    public GuiTabStatistics(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_STATISTICS;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (networkValid) {
            int color = network.getNetworkColor();
            screenUtils.renderNetwork(matrixStack, network.getNetworkName(), color, 20, 8);

            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.PLUGS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxPlugCount, 12, 24, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.POINTS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxPointCount, 12, 36, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.STORAGES.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxStorageCount, 82, 24, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.CONTROLLERS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxControllerCount, 82, 36, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.INPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.energyInput, NumberFormatType.COMPACT, EnergyType.FE, true), 12, 48, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.OUTPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.energyOutput, NumberFormatType.COMPACT, EnergyType.FE, true), 12, 60, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.BUFFER.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.totalBuffer, NumberFormatType.COMPACT, EnergyType.FE, false), 12, 72, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.ENERGY.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.totalEnergy, NumberFormatType.COMPACT, EnergyType.FE, false), 12, 84, color);

            GlStateManager.scaled(0.75, 0.75, 0.75);
            drawCenteredString(matrixStack, font, FluxTranslate.AVERAGE_TICK.t() + ": " + stats.averageTickMicro + " " + "\u00B5" + "s/t", (int) ((xSize / 2) * (1 / 0.75)), (int) ((ySize - 2) * (1 / 0.75)), color);
            GlStateManager.scaled(1 / 0.75, 1 / 0.75, 1 / 0.75);

        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
            addButton(redirectButton);
        }
    }

    @Override
    protected void drawBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        if (networkValid && chart != null) {
            chart.drawChart(minecraft, matrixStack);
            chart.updateHeight(partialTicks);
        }
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_STATISTICS, navigationTabs);

        if (networkValid) {
            chart = new LineChart(width / 2 - 48, height / 2 + 20, 50, 6, "s", "RF");
            chart.updateData(stats.energyChange);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (networkValid) {
            if (timer == 0) {
                NetworkHandler.INSTANCE.sendToServer(new CNetworkUpdateMessage(network.getNetworkID(), FluxConstants.TYPE_NET_STATISTICS));
            }
            if (timer == 1) {
                chart.updateData(stats.energyChange);
            }
            timer++;
            timer %= 20;
        }
    }
}
