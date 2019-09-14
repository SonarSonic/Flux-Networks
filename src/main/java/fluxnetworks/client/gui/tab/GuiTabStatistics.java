package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxTranslate;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.connection.NetworkStatistics;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketNetworkUpdate;
import fluxnetworks.common.network.PacketNetworkUpdateRequest;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class GuiTabStatistics extends GuiTabCore {

    public NetworkStatistics stats = network.getSetting(NetworkSettings.NETWORK_STATISTICS);
    private int timer = 1;

    public GuiTabStatistics(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        if(networkValid) {
            PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_STATISTICS));
        }
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.PLUGS.t() + ": " + TextFormatting.RESET + stats.fluxPlugCount, 12, 28, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.POINTS.t() + ": " + TextFormatting.RESET + stats.fluxPointCount, 12, 40, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.STORAGES.t() + ": " + TextFormatting.RESET + stats.fluxStorageCount, 12, 52, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(TextFormatting.GRAY + FluxTranslate.CONTROLLERS.t() + ": " + TextFormatting.RESET + stats.fluxControllerCount, 12, 64, network.getSetting(NetworkSettings.NETWORK_COLOR));
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
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
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer == 0) {
            PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_STATISTICS));
        }
        timer++;
        timer %= 20;
    }
}
