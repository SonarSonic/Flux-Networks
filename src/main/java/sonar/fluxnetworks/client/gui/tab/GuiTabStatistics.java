package sonar.fluxnetworks.client.gui.tab;

/*import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.energy.EnergyType;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.LineChart;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;
import sonar.fluxnetworks.common.network.C2SNetMsg;

import javax.annotation.Nonnull;

public class GuiTabStatistics extends GuiTabCore {

    public InvisibleButton redirectButton;

    private final NetworkStatistics stats = network.getStatistics();
    private LineChart chart;
    private int timer = 0;

    public GuiTabStatistics(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_STATISTICS;
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
                    EnergyType.usageCompact(stats.energyInput), 12, 48, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.OUTPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    EnergyType.usageCompact(stats.energyOutput), 12, 60, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.BUFFER.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    EnergyType.storageCompact(stats.totalBuffer), 12, 72, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.ENERGY.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    EnergyType.storageCompact(stats.totalEnergy), 12, 84, color);

            matrixStack.push();
            matrixStack.scale(0.75f, 0.75f, 1);
            drawCenterText(matrixStack, FluxTranslate.AVERAGE_TICK.t() + ": " + stats.averageTickMicro + " \u00b5s/t",
                    ((xSize / 2f) * (1 / 0.75f)), ((ySize - 2f) * (1 / 0.75f)), color);
            matrixStack.pop();

        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
            addButton(redirectButton);
        }
    }

    @Override
    protected void drawBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        if (networkValid && chart != null) {
            chart.drawChart(minecraft, matrixStack, partialTicks);
        }
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_STATISTICS, navigationTabs);

        if (networkValid) {
            chart = new LineChart(width / 2 - 48, height / 2 + 20, 50, NetworkStatistics.CHANGE_COUNT, "s", EnergyType.FE.getStorageSuffix());
            chart.updateData(stats.energyChange);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (networkValid) {
            if (timer == 0) {
                C2SNetMsg.requestNetworkUpdate(network, FluxConstants.TYPE_NET_STATISTICS);
            }
            if (timer == 1) {
                chart.updateData(stats.energyChange);
            }
            timer++;
            timer %= 20;
        }
    }
}*/
