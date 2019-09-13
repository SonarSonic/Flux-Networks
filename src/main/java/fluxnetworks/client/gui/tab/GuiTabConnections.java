package fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketConnectionRequest;
import fluxnetworks.common.network.PacketNetworkUpdate;
import fluxnetworks.common.network.PacketUpdateRequest;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiTabConnections extends GuiTabPages<IFluxConnector> {

    public List<IFluxConnector> batchConnections = new ArrayList<>();

    private int timer = 3;

    public GuiTabConnections(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 18;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 16;
        elementWidth = 144;
        PacketHandler.network.sendToServer(new PacketUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_CONNECTIONS));
    }

    @Override
    protected void onElementClicked(IFluxConnector element, int mouseButton) {

    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(!networkValid) {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED, FluxTranslate.TAB_SELECTION);
        }
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);

    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(3).setMain();
    }

    @Override
    public void renderElement(IFluxConnector element, int x, int y) {
        GlStateManager.pushMatrix();
        drawColorRect(x, y, elementHeight, elementWidth, network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        renderItemStack(element.getDisplayStack(), x, y);
        if(element.isChunkLoaded()) {
            fontRenderer.drawString(element.getCustomName(), x + 20, y + 1, 0xffffff);
            GlStateManager.scale(0.625, 0.625, 0.625);
            fontRenderer.drawString(getTransferInfo(element.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), element.getChange()), (int) ((x + 20) * 1.6), (int) ((y + 10) * 1.6), 0xffffff);
            GlStateManager.scale(1.6, 1.6, 1.6);
        } else {
            fontRenderer.drawString(element.getCustomName(), x + 20, y + 4, 0x808080);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(IFluxConnector element, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        drawHoverTooltip(getFluxInfo(element), mouseX + 4, mouseY - 16);
        GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        if(timer == 4) {
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
        }
        if(timer % 5 == 0) {
            PacketHandler.network.sendToServer(new PacketConnectionRequest.ConnectionRequestMessage(network.getNetworkID(), current.stream().map(IFluxConnector::getCoords).collect(Collectors.toList())));
        }
        timer++;
        timer %= 100;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(IFluxConnector::isChunkLoaded).reversed().thenComparing(IFluxConnector::getConnectionType).thenComparing(p -> -p.getPriority()));
        refreshCurrentPage();
    }
}
