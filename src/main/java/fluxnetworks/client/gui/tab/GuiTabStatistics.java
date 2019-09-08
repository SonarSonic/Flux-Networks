package fluxnetworks.client.gui.tab;

import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.connection.NetworkStatistics;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiTabStatistics extends GuiTabCore {

    public NetworkStatistics stat = network.getSetting(NetworkSettings.NETWORK_STATISTICS);

    public GuiTabStatistics(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        fontRenderer.drawString(TextFormatting.GRAY + "Plugs: " + TextFormatting.RESET + stat.fluxPlugCount, 12, 28, network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(TextFormatting.GRAY + "Points: " + TextFormatting.RESET + stat.fluxPointCount, 12, 40, network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(TextFormatting.GRAY + "Storages: " + TextFormatting.RESET + stat.fluxStorageCount, 12, 52, network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(TextFormatting.GRAY + "Controllers: " + TextFormatting.RESET + stat.fluxControllerCount, 12, 64, network.getSetting(NetworkSettings.NETWORK_COLOR));
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(4).setMain();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
