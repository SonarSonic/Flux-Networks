package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxTranslate;
import fluxnetworks.client.gui.LineChart;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.connection.NetworkStatistics;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketNetworkUpdate;
import fluxnetworks.common.network.PacketNetworkUpdateRequest;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class GuiTabStatistics extends GuiTabCore {

    private NetworkStatistics stats = network.getSetting(NetworkSettings.NETWORK_STATISTICS);
    private LineChart chart;
    private int timer = 0;

    public GuiTabStatistics(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.PLUGS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxPlugCount, 12, 28, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.POINTS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxPointCount, 12, 40, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.STORAGES.t() + TextFormatting.GRAY +  ": " +
                    TextFormatting.RESET + stats.fluxStorageCount, 82, 28, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.CONTROLLERS.t() + TextFormatting.GRAY + ": " +
                    TextFormatting.RESET + stats.fluxControllerCount, 82, 40, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.INPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.energyInput, FluxUtils.TypeNumberFormat.COMPACT, "RF/t"), 12, 52, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.OUTPUT.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.energyOutput, FluxUtils.TypeNumberFormat.COMPACT, "RF/t"), 12, 64, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.BUFFER.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.totalBuffer, FluxUtils.TypeNumberFormat.COMPACT, "RF"), 12, 76, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.ENERGY.t() + TextFormatting.GRAY + ": " + TextFormatting.RESET +
                    FluxUtils.format(stats.totalEnergy, FluxUtils.TypeNumberFormat.COMPACT, "RF"), 12, 88, network.getSetting(NetworkSettings.NETWORK_COLOR));
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
        if(networkValid && chart != null) {
            chart.drawChart(mc);
            chart.updateHeight(partialTicks);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(4).setMain();

        if(networkValid) {
            chart = new LineChart(width / 2 - 48, height / 2 + 24, 52, 6, "s", "RF");
            chart.updateData(stats.energyChange);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(networkValid) {
            if (timer == 0) {
                PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_STATISTICS));
            }
            if (timer == 1) {
                chart.updateData(stats.energyChange);
            }
            timer++;
            timer %= 20;
        }
    }
}
