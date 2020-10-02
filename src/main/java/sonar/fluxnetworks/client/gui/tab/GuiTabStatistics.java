package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.LineChart;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GuiTabStatistics extends GuiTabCore {

    public InvisibleButton redirectButton;

    private NetworkStatistics stats = network.getSetting(NetworkSettings.NETWORK_STATISTICS);
    private LineChart chart;
    private int timer = 0;
    private static CompoundNBT GUI_COLOR_TAG;
    private static ItemStack plug;
    private static ItemStack point;
    private static ItemStack storage;
    private static ItemStack controller;

    static {
        GUI_COLOR_TAG = new CompoundNBT();
        GUI_COLOR_TAG.putBoolean(FluxUtils.GUI_COLOR, true);
        plug = new ItemStack(RegistryBlocks.FLUX_PLUG);
        plug.setTag(GUI_COLOR_TAG);
        point = new ItemStack(RegistryBlocks.FLUX_POINT);
        point.setTag(GUI_COLOR_TAG);
        storage = new ItemStack(RegistryBlocks.BASIC_FLUX_STORAGE);
        storage.setTag(GUI_COLOR_TAG);
        controller = new ItemStack(RegistryBlocks.FLUX_CONTROLLER);
        controller.setTag(GUI_COLOR_TAG);
    }

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
            int color = network.getSetting(NetworkSettings.NETWORK_COLOR);
            screenUtils.renderNetwork(matrixStack, network.getSetting(NetworkSettings.NETWORK_NAME), color, 20, 8);

            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.PLUGS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxPlugCount, 12, 24, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.POINTS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxPointCount, 12, 36, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.STORAGES.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxStorageCount, 82, 24, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.CONTROLLERS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxControllerCount, 82, 36, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.INPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.energyInput, FluxUtils.TypeNumberFormat.COMPACT, network.getSetting(NetworkSettings.NETWORK_ENERGY), true), 12, 48, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.OUTPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.energyOutput, FluxUtils.TypeNumberFormat.COMPACT, network.getSetting(NetworkSettings.NETWORK_ENERGY), true), 12, 60, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.BUFFER.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.totalBuffer, FluxUtils.TypeNumberFormat.COMPACT, network.getSetting(NetworkSettings.NETWORK_ENERGY), false), 12, 72, color);
            font.drawString(matrixStack, TextFormatting.GRAY + FluxTranslate.ENERGY.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.totalEnergy, FluxUtils.TypeNumberFormat.COMPACT, network.getSetting(NetworkSettings.NETWORK_ENERGY), false), 12, 84, color);

            GlStateManager.scaled(0.75, 0.75, 0.75);
            drawCenteredString(matrixStack, font, FluxTranslate.AVERAGE_TICK.t() + ": " + stats.average_tick_micro + " " + "\u00B5" + "s/t", (int) ((xSize / 2) * (1 / 0.75)), (int) ((ySize - 2) * (1 / 0.75)), color);
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
                PacketHandler.CHANNEL.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_STATISTICS));
            }
            if (timer == 1) {
                chart.updateData(stats.energyChange);
            }
            timer++;
            timer %= 20;
        }
    }
}
